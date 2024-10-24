import DAO.*;
import VO.Acc_InfoVO;
import VO.Order_RecordVO;

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
        Order_RecordDAO orDAO = new Order_RecordDAO();
        InvDAO invDAO = new InvDAO();

        boolean isLoggedIn = false;
        boolean isAdminLoggedIn = false;
        boolean isHQLoggedIn = false;
        String userId = ""; // 유저 id
        String adminId = ""; // 점주 id
        String hqId = ""; // 본사 id

        while (true) {
            System.out.println("버거집에 오신 것을 환영합니다.");
            System.out.println("이용을 위해서는 로그인을 해야합니다. 회원이 아니라면 가입해주세요 :)");
            System.out.print("[1]로그인 [2]회원가입 [3]프로그램 종료 [4]회원정보 조회 [5]ADMIN 로그인 [6]HQ 로그인 : ");
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
                    List<Acc_InfoVO> list = Acc_InfoDAO.Acc_InfoSelect();
                    Acc_InfoDAO.accInfoSelectResult(list);
                    break;
                case 5:
                    System.out.print("아이디 : ");
                    adminId = sc.next();
                    System.out.print("비밀번호 : ");
                    String adminPw = sc.next();
                    boolean admin = aiDAO.accInfoAdminCheck(adminId, adminPw);
                    if (admin) {
                        System.out.println("ADMIN 로그인 성공!");
                        isAdminLoggedIn = true;
                        break;
                    } else {
                        System.out.println("회원권한이 맞지 않는 경우에도 리다이렉트 됩니다.");
                        System.out.println("HQ 및 일반 유저는 해당 로그인 페이지에서 시도해주세요");
                        System.out.println("혹은, 아이디 또는 비밀번호를 확인해주세요.");
                        continue;
                    }
                case 6:
                    System.out.print("아이디 : ");
                    hqId = sc.next();
                    System.out.print("비밀번호 : ");
                    String hqPw = sc.next();
                    boolean hq = aiDAO.accInfoHQCheck(hqId, hqPw);
                    if (hq) {
                        System.out.println("HQ 로그인 성공!");
                        isHQLoggedIn = true;
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

        while (isLoggedIn) {
            System.out.println("내 정보 페이지");
            System.out.print("[1]주문하기 [2]주문내역 확인 [3]회원정보 수정 [4]회원탈퇴 [5]종료하기 : ");
            int choice = sc.nextInt();
            switch(choice) {
                case 1:
                    invDAO.choiceStore();
                    invDAO.cusOrder();
                    invDAO.inCart();
                    break;
                case 2:
                    List<Order_RecordVO> list = Order_RecordDAO.Order_RecordSelect();
                    Order_RecordDAO.Order_RecordSelectResult(list);
                    break;
                case 3:
                    MyPageDAO.membUpdate(new Acc_InfoVO(), userId);
                    break;
                case 4:
                    MyPageDAO.membDelete(new Acc_InfoVO());
                    System.out.println("회원탈퇴 처리 되었습니다");
                    isLoggedIn = false;
                    break;
                case 5:
                    System.out.println("로그아웃 합니다");
                    isLoggedIn = false;
                    break;
                default :
                    System.out.println("메뉴를 잘 못 선택하셨습니다.");
            }
        }

        while (isAdminLoggedIn) {
            System.out.println("ADMIN 로그인 페이지");

            System.out.print("[1]계좌 송금 [4]로그아웃 : ");
            int choice = sc.nextInt();
            switch(choice) {
                case 1:
//                    dao.cpCharge(StoreDAO.cpChargeInput());
                    boolean isSuccess = sDAO.cpCharge(StoreDAO.cpChargeInput());
                    if (isSuccess) System.out.println("계좌에 금액이 송금되었습니다..");
                    else System.out.println("계좌에 송금이 실패했습니다.");
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    System.out.println("로그아웃 합니다");
                    isAdminLoggedIn = false;
                    break;
                default :
                    System.out.println("메뉴를 잘 못 선택하셨습니다.");
            }
        }

        while (isHQLoggedIn) {
            System.out.println("HQ 로그인 페이지");
            System.out.print("");
            int choice = sc.nextInt();
            switch(choice) {
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    System.out.println("로그아웃 합니다");
                    isHQLoggedIn = false;
                    break;
                default :
                    System.out.println("메뉴를 잘 못 선택하셨습니다.");
            }
        }
    }
}