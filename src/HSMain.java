import DAO.*;
import VO.Acc_InfoVO;
import VO.Inv_OrderVO;
import VO.Order_RecordVO;

import Common.Session;

import java.util.List;
import java.util.Scanner;

public class HSMain {
    public static void main(String[] args) {
        menuSelect();
    }

    public static void menuSelect() {
        Scanner sc = new Scanner(System.in);
        Acc_InfoDAO aiDAO = new Acc_InfoDAO();
        StoreDAO sDAO = new StoreDAO();
        InvDAO invDAO = new InvDAO();

        boolean isLoggedIn = false;
        boolean isAdminLoggedIn = false;
        boolean isHQLoggedIn = false;
        boolean storeCapital = false;

        String userId =""; // 유저 id
        String adminId = ""; // 점주 id
        String hqId = ""; // 본사 id

        while (true) {
            System.out.println("버거집에 오신 것을 환영합니다.");
            System.out.println("이용을 위해서는 로그인을 해야합니다. 회원이 아니라면 가입해주세요 :)");

            System.out.print("[1]로그인 [2]회원가입 [3]프로그램 종료 [4]ADMIN 로그인 [5]HQ 로그인 : ");
            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    System.out.print("아이디 : ");
                    userId = sc.next();
                    System.out.print("비밀번호 : ");
                    String userPw = sc.next();
                    boolean user = aiDAO.accInfoCheck(userId, userPw);
                    if (user) {
                        System.out.println("로그인 성공!");
                        isLoggedIn = true;
                        Session.loggedInUserId = userId; // Set the user ID in session
                        Session.userRole = "customer"; // Set role for customer
                        break;
                    } else {
                        System.out.println("회원권한이 맞지 않는 경우에도 리다이렉트 됩니다.");
                        System.out.println("ADMIN/HQ 유저는 해당 로그인 페이지에서 시도해주세요");
                        System.out.println("혹은, 아이디 또는 비밀번호를 확인해주세요.");
                        continue;
                    }
                case 2:
                    Acc_InfoDAO.Acc_InfoInsert();
                    break;
                case 3:
                    System.out.println("프로그램을 종료합니다");
                    return;
                case 4:
                    System.out.print("아이디 : ");
                    adminId = sc.next();
                    System.out.print("비밀번호 : ");
                    String adminPw = sc.next();
                    boolean admin = aiDAO.accInfoAdminCheck(adminId, adminPw);
                    if (admin) {
                        System.out.println("ADMIN 로그인 성공!");
                        Session.loggedInUserId = adminId;  // Save the admin ID to the session
                        Session.storeId = aiDAO.adminStore(adminId);
                        Session.isAdminLoggedIn = true;    // Set the admin login flag
                        Session.userRole = "admin"; // Set role for admin
                        isAdminLoggedIn = true;            // Keep the flag in the local method as well (if needed)
                        break;
                    } else {
                        System.out.println("회원권한이 맞지 않는 경우에도 리다이렉트 됩니다.");
                        System.out.println("HQ 및 일반 유저는 해당 로그인 페이지에서 시도해주세요");
                        System.out.println("혹은, 아이디 또는 비밀번호를 확인해주세요.");
                        continue;
                    }
                case 5:
                    System.out.print("아이디 : ");
                    hqId = sc.next();
                    System.out.print("비밀번호 : ");
                    String hqPw = sc.next();
                    boolean hq = aiDAO.accInfoHQCheck(hqId, hqPw);
                    if (hq) {
                        System.out.println("HQ 로그인 성공!");
                        isHQLoggedIn = true;
                        Session.userRole = "hq"; // Set role for HQ
                        break;
                    } else {
                        System.out.println("회원권한이 맞지 않는 경우에도 리다이렉트 됩니다.");
                        System.out.println("ADMIN 및 일반 유저는 해당 로그인 페이지에서 시도해주세요");
                        System.out.println("혹은, 아이디 또는 비밀번호를 확인해주세요.");
                        continue;
                    }
                default :
                    System.out.println("메뉴를 잘 못 선택하셨습니다.");
            }
            if (isLoggedIn || isAdminLoggedIn || isHQLoggedIn) break;
        }

        while (isLoggedIn) { // 소비자 로그인
            System.out.println("내 정보 페이지");


            System.out.print("[1]주문하기 [2]장바구니 [3]주문내역 확인 [4]회원정보 수정 [5]회원탈퇴 [6]종료하기 : ");
            int choice = sc.nextInt();
            switch(choice) {
                case 1:
                    invDAO.choiceStore();
                    invDAO.cusOrder();
                    break;
                case 2:
                    invDAO.inCart(userId);
                    break;
                case 3:
                    List<Order_RecordVO> list = Order_RecordDAO.Order_RecordSelect(userId);
                    Order_RecordDAO.Order_RecordSelectResult(list);
                    break;
                case 4:
                    MyPageDAO.membUpdate(new Acc_InfoVO(), userId);
                    break;
                case 5:
                    MyPageDAO.membDelete(Session.loggedInUserId); // Use session to get user ID
                    System.out.println("회원탈퇴 처리 되었습니다");
                    isLoggedIn = false; // Set login flag to false
                    Session.clear(); // Clear session data
                    menuSelect(); // Redirect to menu
                    break;
                case 6:
                    System.out.println("로그아웃 합니다");
                    isLoggedIn = false;
                    break;
                default :
                    System.out.println("메뉴를 잘 못 선택하셨습니다.");
            }
        }

        while (isAdminLoggedIn) { // 점주로 로그인
            System.out.println("ADMIN 로그인 페이지");
            System.out.print("항목 선택 [1]발주 [2]재고확인 [3]매출현황 [4]매장계좌 [5]로그아웃 : ");
            int choice = sc.nextInt();
            switch(choice) {
                case 1: // 발주
                    invDAO.ownerOrder(Session.storeId);
                    break;
                case 2: // 재고확인
                    invDAO.invCheck(Session.storeId);
                    break;
                case 3:
                    sDAO.slSelectResult(sDAO.slSelect(sDAO.getSlStoreId(adminId)));
                    break;
                case 4:
                    storeCapital = true; // Set the flag to enter the store capital menu
                    break;
                case 5:
                    System.out.println("로그아웃 합니다");
                    Session.isAdminLoggedIn = false;  // Reset session
                    Session.loggedInUserId = null;     // Clear session data
                    isAdminLoggedIn = false;           // Reset local flag
                    menuSelect();
                    break;
                default:
                    System.out.println("메뉴를 잘 못 선택하셨습니다.");
            }

            while (storeCapital) { // 매장계좌
                System.out.print("항목 선택 [1]계좌 입금 [2]계좌 잔액 현황 [3]뒤로가기 : ");
                int choice2 = sc.nextInt();
                switch(choice2) {
                    case 1:
                        boolean isSuccess = sDAO.cpCharge(sDAO.cpChargeInput(), sDAO.getCpCStoreId(adminId));
                        if (isSuccess)
                            System.out.println("계좌에 금액이 송금되었습니다..");
                        else
                            System.out.println("계좌에 송금이 실패했습니다.");
                        break;
                    case 2:
                        sDAO.cpSelectResult(sDAO.cpSelect(sDAO.getCpSStoreId(adminId)));
                        break;
                    case 3: // Case for going back to the previous menu
                        storeCapital = false; // Set the flag to false to exit the loop
                        break; // Exit the current while loop and go back
                    default:
                        System.out.println("잘못 입력 하셨습니다.");
                }
            }
        }


        while (isHQLoggedIn) { // 본사 로그인시
            System.out.println("HQ 로그인 페이지");
            System.out.print("[1]메뉴조회 [2]메뉴추가 [3]메뉴수정 [4]메뉴삭제 [5]회원정보 조회 [6]로그아웃 : ");
            int choice = sc.nextInt();
            switch(choice) {
                case 1:
                    List<Inv_OrderVO> list = Inv_OrderDAO.Inv_OrderSelect();
                    Inv_OrderDAO.Inv_OrderSelectResult(list);
                    break;
                case 2:
                    boolean isSuccess2 = Inv_OrderDAO.Inv_OrderInsert();
                    if (isSuccess2) System.out.println("메뉴 등록 성공");
                    else System.out.println("메뉴 등록 실패");
                    break;
                case 3:
                    isSuccess2 = Inv_OrderDAO.Inv_OrderUpdate();
                    if (isSuccess2) System.out.println("메뉴 수정 성공");
                    else System.out.println("메뉴 수정 실패");
                    break;
                case 4:
                    isSuccess2 = Inv_OrderDAO.Inv_OrderDelete();
                    if (isSuccess2) System.out.println("메뉴 삭제 성공");
                    else System.out.println("메뉴 삭제 실패");
                    break;
                case 5:
                    List<Acc_InfoVO> accList = Acc_InfoDAO.Acc_InfoSelect();
                    Acc_InfoDAO.accInfoSelectResult(accList);
                    break;
                case 6:
                    System.out.println("로그아웃 합니다");
                    isHQLoggedIn = false;
                    break;
                default:
                    System.out.println("메뉴를 잘못 선택하셨습니다.");
            }
        }
    }
}