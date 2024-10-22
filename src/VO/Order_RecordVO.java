package VO;

import java.sql.Date;

public class Order_RecordVO {
    private String orderCode;
    private String orderList;
    private Date orderTime; // LocalDateTime 을 대신 써야할지 아직 고민중.
    private int orderPrice;
    private String userId;
    private String storeId;

    public Order_RecordVO(String orderCode, String orderList, Date orderTime, int orderPrice, String userId, String storeId) {
        this.orderCode = orderCode;
        this.orderList = orderList;
        this.orderTime = orderTime;
        this.orderPrice = orderPrice;
        this.userId = userId;
        this.storeId = storeId;
    }

    public Order_RecordVO() {
    }

    public String getOrderCode() {
        return orderCode;
    }

    public String getOrderList() {
        return orderList;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public int getOrderPrice() {
        return orderPrice;
    }

    public String getUserId() {
        return userId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public void setOrderList(String orderList) {
        this.orderList = orderList;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public void setOrderPrice(int orderPrice) {
        if (orderPrice < 0) {   // null은 허용 안하니까 조건은 음수일 수 없다로만 설정.
            throw new IllegalArgumentException("제품 가격은 음수일 수 없습니다.");
        }
        this.orderPrice = orderPrice;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }
}
