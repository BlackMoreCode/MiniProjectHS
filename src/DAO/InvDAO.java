package DAO;

import Common.Common;
import VO.InvVO;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class InvDAO {
    Connection conn = null;
    Statement stmt = null;
    PreparedStatement psmt = null;
    ResultSet rs = null;
    Scanner sc = new Scanner(System.in);

    public InvDAO() {
        sc = new Scanner(System.in);
    }

    public List<InvVO> invSelect() {
        List<InvVO> list = new ArrayList<>();
        try {
            conn = Common.getConnection(); // 오라클DB 연결
            stmt = conn.createStatement(); // Statement 생성
            String query = "SELECT * FROM INV"; //..다만 이건 하드코딩이다..
            // executeQuery: select 문과 같이 결과값이 여러 개의 레코드로 반환되는 경우 사용
            rs = stmt.executeQuery(query); // ResultSet: 여러 행의 결과값을 받아서 반복자(iterator)를 제공
            // next() : 현재 행에서 한 행 앞으로 이동
            // previous(): 현재 행에서 한 행 뒤로 이동
            // first() : 첫번째 행으로 이동
            // last() : 마지막 행으로 이동
            while (rs.next()) {
                String menuName = rs.getString("MENU_NAME");
                String storeId = rs.getString("STORE_ID");
                int price = rs.getInt("PRICE");
                int stock = rs.getInt("STOCK");
                String descr = rs.getString("DESCR");
                InvVO vo = new InvVO(menuName, storeId, price, stock, descr);
                list.add(vo);
            }
            Common.close(rs);
            Common.close(stmt);
            Common.close(conn);

        } catch (Exception e) {
            System.out.println("EMP 조회 실패");
        }
        return list;
    }

    public boolean invInsert(InvVO vo) {
        String sql = "INSERT INTO INV(MENU_NAME, STORE_ID, PRICE, STOCK, DESCR)";

        try {
            conn = Common.getConnection();
            psmt = conn.prepareStatement(sql);
            psmt.setString(1, vo.getMenuName());
            psmt.setString(2, vo.getStoreId());
            psmt.setInt(3, vo.getPrice());
            psmt.setInt(4, vo.getStock());
            psmt.setString(5, vo.getDescr());
            int rst = psmt.executeUpdate(); // INSERT, UPDATE, DELETE에 해당하는 함수
            System.out.println("INSERT 결과로 영향 받는 행의 갯수 : " + rst);
            return true; // 원래는 반환값 받는거 처리해야한다.. 쿼리문에 대한 성공실패만 판정. 이 부분에 대한 변경은 추후 논의
        } catch (Exception e) {
            System.out.println("INSERT 실패");
            return false;
        } finally {
            Common.close(rs);   // 이 부분 필요한가?
            Common.close(stmt);
            Common.close(conn);
        }
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
            Common.close(rs);   // 이 부분 필요한가?
            Common.close(stmt);
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
            Common.close(stmt);
            Common.close(conn);
        }
    }

    public void empSelectResult(List<InvVO> list) {
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
