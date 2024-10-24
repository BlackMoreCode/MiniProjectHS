package DAO;

import Common.Common;
import Common.Common.Util;
import VO.Acc_InfoVO;
import VO.InvVO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Acc_InfoDAO {
    Connection conn = null;
    Statement stmt = null;
    PreparedStatement psmt = null;
    ResultSet rs = null;
    Scanner sc = new Scanner(System.in);

    public Acc_InfoDAO() {
        sc = new Scanner(System.in);
    }

    // 회원 정보 조회 (전체 조회; 중복 체크만을 위험이므로 비밀번호랑, 유저권한 및 지점 정보를 제외하고 불러온다)
    public List<Acc_InfoVO> Acc_InfoSelect() {
        List<Acc_InfoVO> accInfo = new ArrayList<>();
        try {
            conn = Common.getConnection(); // 오라클DB 연결
            stmt = conn.createStatement(); // Statement 생성
            String query = "SELECT USER_ID, USER_NAME FROM ACC_INFO"; //추후 수정 가능
            // executeQuery: select 문과 같이 결과값이 여러 개의 레코드로 반환되는 경우 사용
            rs = stmt.executeQuery(query); // ResultSet: 여러 행의 결과값을 받아서 반복자(iterator)를 제공
            while (rs.next()) { // 아이디 & 이름만 불러오니까 필요 없는건 주석처리 혹은 제거 필ㅇ?
                String userId = rs.getString("USER_ID");
//                String userPw = rs.getString("USER_PW");
                String userName = rs.getString("USER_NAME");
                String userPhone = rs.getString("USER_PHONE");
                Date joinDate = rs.getDate("JOIN_DATE");
                // int authLv = ??
//                String storeId = rs.getString("STORE_ID");
                Acc_InfoVO vo = new Acc_InfoVO(userId, userName, userPhone, joinDate); // 개별 생성자 하나 더 만듬, 해당 2개만 보는
                accInfo.add(vo);
            }
            Common.close(rs);
            Common.close(stmt);
            Common.close(conn);

        } catch (Exception e) {
            System.out.println("회원정보 조회 실패");
        }
        return accInfo;
    }

    // 로그인 체크 로직 => 아이디 / 비밀번호 검사. 회원정보 수정 / 탈퇴시에도 이 로직 이용?
    public boolean accInfoCheck (String userId, String userPw) {
        boolean isMember = false;
        try {
            conn = Common.getConnection();
            String sql = "SELECT COUNT(*) FROM ACC_INFO WHERE USER_ID = ? AND USER_PW = ?";
            psmt = conn.prepareStatement(sql); //createStement 랑 prepareStatement의 차이를 공부해야한다.
            psmt.setString(1, userId);
            psmt.setString(2, userPw);
            rs = psmt.executeQuery();
            if(rs.next()) {
                if(rs.getInt("COUNT(*)") == 1) {
                    isMember = true;
                }
            }
        } catch(Exception e) {
            System.out.println("로그인 실패!");
            System.out.println(e.getMessage());
        }
        Common.close(rs);
        Common.close(psmt);
        Common.close(conn);
        return isMember;
    }


    // 회원 가입을 한다 = ACC_INFO 테이블에 추가한다 = INSERT 처리다?
    // 회원 가입을 위해서는 희망 아이디, 비밀번호, 연락처를 기입. 가입일시, 유저레벨(AUTH_LV)은 자동으로 부여. STORE_ID 역시 입력하지 않는다.
    public String Acc_InfoInsert(InvVO vo) {
        // 회원정보 불러오기; Acc_InfoSelect 참조.
        List<Acc_InfoVO> accInfo = Acc_InfoSelect();
        System.out.println("가입을 위해 회원 정보를 입력해주세요!");
        // 회원 정보 입력 시작
        // 유저 아이디
        String userId;
        while(true) {
            Util ut = new Util();

            System.out.print("아이디 : ");
            userId = sc.next();
            String check = userId;

            // 중복 체크; 스트림 객체로 변환한 뒤 메서드 체이닝으로 각각 체크. filter(), findAn(), orElse() 사용.
            if(accInfo.stream().filter(n -> check.equals(n.getUserId())).findAny().orElse(null) != null) {
                System.out.println("이미 사용중인 아이디 입니다.");
            }else if (!ut.checkInputNumAndAlphabet(userId)) System.out.println("영문과 숫자 조합만 사용해주세요.");
            else if (userId.length() < 5) System.out.println("ID는 5자 이상 입력해주세요");
            else if (userId.length() > 20) System.out.println("ID는 20자 이하로 입력해주세요");
            else break;
        }
        // 유저 비밀번호
        // 정규식, Pattern, Matcher 클래스 동원한다
        String userPw ;
        while(true) {
            System.out.print("비밀번호(8자 이상 20자 이하) : ");
            userPw = sc.next();

            // Pattern compile : 주어진 정규식들을 Pattern 객체로 컴파일 처리. 즉 합당한 비밀번호가 뭔지에 대한 규칙을 제시
            // ^ : 문자열의 시작
            // (?=.*[a-zA-Z]) = 최소 한 글자가 문자인가 체크
            // (?=.*\\d) : 최소 비밀번호 한자리가 0~9 사이 숫자인가 (if there is at least one digit (0-9))
            // (?=.*\\W) : 특수문자가 하나 포함되어 있는가 (e.g. !@#$%^&*)
            // .{8,20} : 비밀번호 문자열이 8~20자 사이의 문자인지
            // $ : 문자열의 끝
            Pattern passPattern1 = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,20}$");
            // Matcher : userPw를 passPattern1 에 대조해서 검사하는 Matcher 객체를 생성
            // userPw가 검증되어야할 문자열 변수로 판단될 것이다.
            Matcher passMatcher1 = passPattern1.matcher(userPw);

            if(userPw.length() < 8) System.out.println("비밀번호는 8자 이상 입력해주세요");
            else if (userPw.getBytes().length > 20) System.out.println("비밀번호는 20자 이하 영문자와 &제외 특수문자로 입력해주세요");
            else if (!passMatcher1.find()) System.out.println("비밀번호는 영문자, 숫자, 특수기호만 사용 할 수 있습니다.");
            else if (userPw.indexOf('&') >= 0) System.out.println("&는 비밀번호로 사용할수 없습니다.");
            else break;
        }

        // 이름 입력
        System.out.print("이름 : ");
        String userName = sc.next();

        // 연락처 입력  - 13자리만 허용 (비워둘 수 없습니다 → 자동적용)
        String userPhone;
        while(true) {
            System.out.println("연락처 : ");
            userPhone = sc.next();
            String check = userPhone;
            //중복 체크; 스트림 객체로 변환한 뒤 메서드 체이닝으로 각각 체크. filter(), findAn(), orElse() 사용.
            if(accInfo.stream().filter(n -> check.equals(n.getUserPhone())).findAny().orElse(null) != null) {
                System.out.println("이미 사용중인 번호 입니다.");
            }
            else if (userPhone.length() != 13) System.out.print("전화번호는 (-)포함 13글자로 작성하세요.");
            else break;
        }

        // 필요 값들 전부 입력 완료시 ACC_INFO 테이블에 자료 추가
        String sql = "INSERT INTO ACC_INFO(USER_ID, USER_PW, USER_NAME, USER_PHONE, JOIN_DATE, AUTH_LV) VALUES (?, ?, ?, ?, SYSDATE, ?)";

        try {
            conn = Common.getConnection();
            psmt = conn.prepareStatement(sql);
            psmt.setString(1, userId);
            psmt.setString(2, userPw);
            psmt.setString(3, userName);
            psmt.setString(4, userPhone);
            psmt.setInt(5, 3); // 새로 가입하는 소비자 유저는 무조건 3 처리.
            int rst = psmt.executeUpdate(); // INSERT, UPDATE, DELETE에 해당하는 함수
            System.out.println("INSERT 결과로 영향 받는 행의 갯수 : " + rst);

        } catch (Exception e) {
            System.out.println("INSERT 실패");
        } finally {
            Common.close(psmt);
            Common.close(conn);
        }
        System.out.println("회원가입이 완료되었습니다. 메인메뉴로 이동합니다.");
        return userId;
    }


    public boolean invDelete(InvVO vo) {
        String sql = "DELETE FROM INV WHERE MENU_NAME = ?";

        try {
            conn = Common.getConnection();
            psmt = conn.prepareStatement(sql);
            psmt.setString(1, vo.getMenuName());
            int rst = psmt.executeUpdate(); // INSERT, UPDATE, DELETE에 해당하는 함수
            System.out.println("DELETE 결과로 영향 받는 행의 갯수 : " + rst);
            return true; // 원래는 반환값 받는거 처리해야한다.. 쿼리문에 대한 성공실패만 판정. 이 부분에 대한 변경은 추후 논의
        } catch (Exception e) {
            System.out.println("DELETE 실패");
            return false;
        } finally {
            Common.close(psmt);
            Common.close(conn);
        }
    }

    public boolean invUpdate(InvVO vo) {
        String sql = "UPDATE INV SET PRICE = ?, STOCK = ?, DESCR = ? WHERE MENU_NAME = ?";

        try {
            conn = Common.getConnection();
            psmt = conn.prepareStatement(sql);
            psmt.setString(1, vo.getMenuName());
//            psmt.setString(2, vo.getStoreId()); // 이 부분은 수정 안할거니까 필요 없지 않나?
            psmt.setInt(3, vo.getPrice());
            psmt.setInt(4, vo.getStock());
            psmt.setString(5, vo.getDescr());
            int rst = psmt.executeUpdate(); // INSERT, UPDATE, DELETE에 해당하는 함수
            System.out.println("UPDATE 결과로 영향 받는 행의 갯수 : " + rst);
            return true; // 원래는 반환값 받는거 처리해야한다.. 쿼리문에 대한 성공실패만 판정. 이 부분에 대한 변경은 추후 논의
        } catch (Exception e) {
            System.out.println("UPDATE 실패");
            return false;
        } finally {
            Common.close(psmt);
            Common.close(conn);
        }
    }



    public void invSelectResult(List<InvVO> list) {
        System.out.println("--------------------------------------------------------");
        System.out.println("                재고 정보");
        System.out.println("--------------------------------------------------------");
        for(InvVO e : list) {
            System.out.print(e.getMenuName() + " ");
            System.out.print(e.getStoreId() + " ");
            System.out.print(e.getPrice() + " ");
            System.out.print(e.getStock() + " ");
            System.out.print(e.getDescr() + " ");
            System.out.println();
        }
        System.out.println("--------------------------------------------------------");
    }
}