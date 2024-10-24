package VO;

public class StoreVO {
    private String storeId; // 지점이름
    private int sales; // 매출 현황
    private int capital; // 지점 가용 계좌(자본금)

    public StoreVO(String storeId,int sales,int capital) {
        this.storeId = storeId;
        this.sales = sales;
        this.capital = capital;
    }

    public StoreVO(String storeId, int capital) {
        this.storeId = storeId;
        this.capital = capital;
    }

    public StoreVO(int sales) {
        this.sales = sales;
    }

    public String getStoreId() {
        return storeId;
    }

    public int getSales() {
        return sales;
    }

    public int getCapital() {
        return capital;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public void setSales(int sales) {
        this.sales = sales;
    }

    public void setCapital(int capital) {
        this.capital = capital;
    }
}
