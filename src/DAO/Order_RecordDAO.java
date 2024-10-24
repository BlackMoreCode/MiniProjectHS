package DAO;

import Common.Common;
import VO.Order_RecordVO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Order_RecordDAO {

    static Connection conn = null;
    static Statement stmt = null;
    PreparedStatement pstmt = null;
    static ResultSet rs = null;

    Scanner sc = new Scanner(System.in);
    boolean isSuccess = false;

    //주문 내역 출력을 위해 list 생성
    public static List<Order_RecordVO> Order_RecordSelect() {
        List<Order_RecordVO> list = new ArrayList<>(0);

        try {
            conn = Common.getConnection();
            stmt = conn.createStatement();

            // ORDER_RECORD 보여주기
            // ORDER_CODE 기준 오름차순 정렬 (기본)
            String query = "SELECT * FROM ORDER_RECORD ORDER BY ORDER_CODE";
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                String orderCode = rs.getString("ORDER_CODE");
                String orderList = rs.getString("ORDER_LIST");
                Date orderTime = rs.getDate("ORDER_TIME");
                int orderPrice = rs.getInt("ORDER_PRICE");
                String userId = rs.getString("USER_ID");
                String storeId = rs.getString("STORE_ID");
                Order_RecordVO vo = new Order_RecordVO(orderCode, orderList, orderTime, orderPrice, userId, storeId);
                list.add(vo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Common.close(rs);
            Common.close(stmt);
            Common.close(conn);
        }
        return list;
    }

    // 주문 정보 출력
    public static void Order_RecordSelectResult(List<Order_RecordVO> list) {
        System.out.println("----------- 주문 정보 ------------");

        for (Order_RecordVO e : list) {
            System.out.print(e.getOrderCode() + " ");
            System.out.print(e.getOrderList() + " ");
            System.out.print(e.getOrderTime() + " ");
            System.out.print(e.getOrderPrice() + " ");
            System.out.print(e.getUserId() + " ");
            System.out.print(e.getStoreId());
            System.out.println();
        }
        System.out.println("---------------------------------");
    }

}
