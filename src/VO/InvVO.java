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

//    @Override // 오버라이딩 처리.
//    public boolean equals(Object o) {
//        if (this == o) return true; // Reference Equality Check ; 두개의 참조가 같은지 체크한다. 메모리상 같은지 확인; 같으면 논리적으로 동일함.
//        if (!(o instanceof InvVO)) return false;    // 타입 검사; o 가 InvVO의 인스턴스인지 체크. 다른 타입의 객체를 비교안하게 하는 안전망
//        InvVO invVO = (InvVO) o; // 필드에 접근할 수 있게 해줌?
//        return Objects.equals(menuName, invVO.menuName) &&  //눌 값이 있나 비교 체크?
//                Objects.equals(storeId, invVO.storeId);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(menuName, storeId);
//    }
}
