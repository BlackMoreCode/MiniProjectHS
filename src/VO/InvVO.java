package VO;

import java.util.Objects;

public class InvVO {
    private String menuName;
    private String storeId;
    private int price;
    private int stock;
    private String descr;

    public InvVO(String menuName, String storeId, int price, int stock, String descr) {
        this.menuName = menuName;
        this.storeId = storeId;
        setPrice(price); // 세터를 사용해서 검사?
        setStock(stock); // 세터를 사용해서 검사?
        this.descr = descr;
    }

    public InvVO() {
    }

    public String getMenuName() {
        return menuName;
    }

    public String getStoreId() {
        return storeId;
    }

    public int getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public void setStock(int stock) {
        if (stock < 0) {
            throw new IllegalArgumentException("재고는 음수일 수 없습니다..");
        }
        this.stock = stock;
    }

    public void setPrice(int price) {
        if (price < 0) {
            throw new IllegalArgumentException("가격은 음수일 수 없습니다.");
        }
        this.price = price;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    @Override // 오버라이딩 처리.
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InvVO)) return false;
        InvVO invVO = (InvVO) o;
        return Objects.equals(menuName, invVO.menuName) &&
                Objects.equals(storeId, invVO.storeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(menuName, storeId);
    }
}
