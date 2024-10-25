package DAO;

import Common.Common;
import VO.Acc_InfoVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static DAO.Acc_InfoDAO.Acc_InfoSelect;
import static DAO.Acc_InfoDAO.psmt;

public class MyPageDAO {
    static Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    static Scanner sc = new Scanner(System.in);

    Acc_InfoDAO aiDAO = new Acc_InfoDAO();

    // ================ 필요 메서드 ===================

    //회원 정보 수정
    public static void membUpdate(Acc_InfoVO vo, String userId) {
        // 회원정보 불러오기; Acc_InfoSelect 참조.
        List<Acc_InfoVO> accInfo = Acc_InfoSelect();

        //비밀번호 수정 시
        String userPw = "";
        while (true) {
            System.out.println("비밀번호를 바꾸지 않을 경우 NO를 입력해주세요");
            System.out.print("변경할 비밀번호(8자 이상 20자 이하) : ");
            userPw = sc.next();

            // Check for "no" first
            if (userPw.equalsIgnoreCase("no")) {
                userPw = vo.getUserPw(); // Revert to existing password
                break; // Exit the loop since we are keeping the existing password
            }
            // Validate the new password
            if (userPw.getBytes().length > 20) {
                System.out.println("비밀번호는 20자 이하 영문자와 &제외 특수문자로 입력해주세요");
            } else if (userPw.length() < 8) {
                System.out.println("비밀번호는 8자 이상 입력해주세요");
            } else {
                // Check for invalid characters and pattern
                Pattern passPattern1 = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,20}$");
                Matcher passMatcher1 = passPattern1.matcher(userPw);

                if (!passMatcher1.find()) {
                    System.out.println("비밀번호는 영문자, 숫자, 특수기호의 조합으로만 사용 할 수 있습니다.");
                } else if (userPw.indexOf('&') >= 0) {
                    System.out.println("&는 비밀번호로 사용할수 없습니다.");
                } else {
                    break; // Valid password, exit the loop
                }
            }

        }

        // 연락처 수정시
        String userPhone = "";
        while(true) {
            System.out.println("연락처를 바꾸지 않을 경우 NO를 입력해주세요");
            System.out.print("변경할 핸드폰 번호를 입력하세요 : ");
            userPhone = sc.next();
            String check = userPhone;

            // no 입력시 그대로 유지
            if (userPhone.equalsIgnoreCase("NO")) { // // 위에서 언급한 NO 입력시 연락처 수정 안함 처리.
                userPhone = vo.getUserPhone();
                break;
            }

            //중복 체크
            // 회원정보 리스트에 스트림으로 필터를 걸어서 하나라도 일치하는게 있다면 값 반환 없으면 null 반환
            if (userPhone.length() != 13) {
                System.out.print("전화번호는 (-)포함 13글자로 작성하세요.");
            } else if (accInfo.stream().filter(n -> check.equals(n.getUserPhone())).findAny().orElse(null) != null) {
                System.out.println("이미 사용중인 번호 입니다.");
            } else break;
        }

        String sql = "";
        try {
            conn = Common.getConnection();
            if (userPw.equals(vo.getUserPw())) { // Keeping the old password
                sql = "UPDATE ACC_INFO SET USER_PHONE = ? WHERE USER_ID = ?";
                psmt = conn.prepareStatement(sql);
                psmt.setString(1, userPhone);
                psmt.setString(2, userId);
            } else { // Updating password and phone number
                sql = "UPDATE ACC_INFO SET USER_PW = ?, USER_PHONE = ? WHERE USER_ID = ?";
                psmt = conn.prepareStatement(sql);
                psmt.setString(1, userPw);
                psmt.setString(2, userPhone);
                psmt.setString(3, userId);
            }
            psmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("회원정보 수정 실패: " + e.getMessage());
        } finally {
            Common.close(psmt);
            Common.close(conn);
        }
        System.out.println("회원정보 수정이 완료되었습니다.");
    }

    // 회원 탈퇴의 경우
    public static boolean membDelete(String userId) {
        Connection conn = null;
        PreparedStatement psmt = null;

        String sql = "DELETE FROM ACC_INFO WHERE USER_ID = ?";

        try {
            conn = Common.getConnection();
            psmt = conn.prepareStatement(sql);
            psmt.setString(1, userId); // Use the userId parameter to set the query
            int rst = psmt.executeUpdate(); // Execute the delete statement
            System.out.println("DELETE 결과로 영향 받는 행의 갯수 : " + rst);
            return rst > 0; // Return true if at least one row was affected
        } catch (Exception e) {
            System.out.println("DELETE 실패: " + e.getMessage());
            return false; // Return false in case of an exception
        } finally {
            Common.close(psmt);
            Common.close(conn);
        }
    }
}
