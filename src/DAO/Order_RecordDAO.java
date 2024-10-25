// 5시 18분 1차 변화
package DAO;

import Common.Common;
import VO.Order_RecordVO;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Order_RecordDAO {

    static Connection conn = null;
    static Statement stmt = null;
    static PreparedStatement psmt = null;
    static ResultSet rs = null;

    Scanner sc = new Scanner(System.in);
    boolean isSuccess = false;

    //주문 내역 출력을 위해 list 생성
    public static List<Order_RecordVO> Order_RecordSelect(String Id) {
        List<Order_RecordVO> list = new ArrayList<>(0);

        try {
            String query = "SELECT * FROM ORDER_RECORD WHERE USER_ID = ? ORDER BY ORDER_CODE";
            conn = Common.getConnection();
            psmt = conn.prepareStatement(query);
            psmt.setString(1, Id);

            // ORDER_RECORD 보여주기
            // ORDER_CODE 기준 오름차순 정렬 (기본)

            rs = psmt.executeQuery();

            while (rs.next()) {
                String orderCode = rs.getString("ORDER_CODE");
                String orderList = rs.getString("ORDER_LIST");
                Timestamp orderTime = rs.getTimestamp("ORDER_TIME");
                int orderPrice = rs.getInt("ORDER_PRICE");
                String userId = rs.getString("USER_ID");
                String storeId = rs.getString("STORE_ID");
                Order_RecordVO vo = new Order_RecordVO(orderCode, orderList, orderTime, orderPrice, userId, storeId);
                list.add(vo);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            Common.close(rs);
            Common.close(psmt);
            Common.close(conn);
        }
        return list;
    }

    // 고객의 주문 정보 출력
    public void userOrderList() {
        String sql = "SELECT * FROM ORDER_RECORD WHERE USER_ID = ?";

        try {
            conn = Common.getConnection();
            psmt = conn.prepareStatement(sql);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
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

    public static void orderRecordInsert(String storeId, String orderList, int price, String userId) {
        String sql = "INSERT INTO ORDER_RECORD VALUES (?, ?, ?, ?, ?, ?)";
        Timestamp timestamp = new Timestamp(new java.util.Date().getTime());
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String timeStr = format.format(timestamp);

        try {
            conn = Common.getConnection();
            psmt = conn.prepareStatement(sql);
            psmt.setString(1, (storeId + timeStr));
            psmt.setString(2, orderList);
            psmt.setTimestamp(3, timestamp);
            psmt.setInt(4, price);
            psmt.setString(5, userId);
            psmt.setString(6, storeId);
            int rows = psmt.executeUpdate();

            System.out.println(rows+"개 행 삽입");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            Common.close(psmt);
            Common.close(conn);
        }
    }

}
