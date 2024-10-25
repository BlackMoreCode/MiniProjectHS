package DAO;

import Common.Common;
import Customer.SetMenu;
import Customer.SingleMenu;
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
    String storeId;

    List<SetMenu> setCart = new ArrayList<>();
    List<SingleMenu> singleCart = new ArrayList<>();

    public void choiceStore() { // 지점 설정 메서드
        List<String> lst = new ArrayList<>();
        int i = 1;
        int storeIdx;

        try {
            conn = Common.getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT DISTINCT STORE_ID FROM INV";
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                lst.add(rs.getString("STORE_ID"));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.printf("%20s", "점포 목록");
        for (String e : lst) {
            System.out.printf("[%d] %s \n", i++, e);
        }
        System.out.println("주문 지점을 선택 해 주세요 : ");
        storeIdx = sc.nextInt() - 1;

        this.storeId = lst.get(storeIdx);
    }

    public void cusOrder() { // 주문 관련 메서드
        List<InvVO> set = new ArrayList<>();
        List<InvVO> burger = new ArrayList<>();
        List<InvVO> side = new ArrayList<>();
        List<InvVO> drink = new ArrayList<>();

        String query = "SELECT i.MENU_NAME, i.PRICE, o.DESCR, o.CATEGORY FROM INV i JOIN INV_ORDER o " +
                "ON i.MENU_NAME = o.MENU_NAME WHERE STORE_ID = ?";

        try {
            conn = Common.getConnection();
            psmt = conn.prepareStatement(query);
            psmt.setString(1, this.storeId);
            rs = psmt.executeQuery();

            while (rs.next()) {
                String menuName = rs.getString("MENU_NAME");
                int price = rs.getInt("PRICE");
                String descr = rs.getString("DESCR");
                String cat = rs.getString("CATEGORY");

                InvVO vo = new InvVO(menuName, price, descr, cat);
                if (vo.getCategory().equals("버거")) {
                    burger.add(vo);
                    set.add(vo);
                } else if (vo.getCategory().equals("사이드")) {
                    side.add(vo);
                } else {
                    drink.add(vo);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


        while (true) {
            int i = 1;
            System.out.print("주문할 메뉴의 분류를 선택 해 주세요 [1]세트, [2]단품, [3]사이드, [4]음료 [9] 주문종료");
            int cat = sc.nextInt();
            sc.nextLine();

            switch (cat) {
                case 1:
                    System.out.printf("%20s", "세트 메뉴 목록 \n");
                    for (InvVO e : set) {
                        System.out.printf("[%d] %s 세트, %s의 세트메뉴, %d원 부터\n", i++, e.getMenuName(), e.getMenuName(), e.getPrice()); // 가격 설정 필요(버거가격+감튀+콜라가 기본가격)
                    }
                    System.out.print("메뉴를 선택 해 주세요 : ");
                    int b = sc.nextInt() - 1;
                    sc.nextLine();
                    i = 1;
                    System.out.print("사이드 선택 : ");
                    for (InvVO e : side) {
                        System.out.printf("[%d] %s, %s \n", i++, e.getMenuName(), e.getDescr());
                    }
                    int s = sc.nextInt() - 1;
                    i = 1;
                    System.out.print("음료 선택 : ");
                    for (InvVO e : drink) {
                        System.out.printf("[%d] %s, %s \n", i++, e.getMenuName(), e.getDescr());
                    }
                    int d = sc.nextInt() - 1;
                    System.out.print("수량 선택 : ");
                    int cnt = sc.nextInt();
                    sc.nextLine();
                    setCart.add(new SetMenu(
                            new SingleMenu(burger.get(b).getMenuName(), burger.get(b).getPrice()),
                            new SingleMenu(side.get(s).getMenuName(), side.get(s).getPrice()),
                            new SingleMenu(drink.get(d).getMenuName(), drink.get(d).getPrice()),
                            cnt
                    ));
                    break;
                case 2:
                    menuSelect(burger, "버거");
                    break;
                case 3:
                    menuSelect(side, "사이드");
                    break;
                case 4:
                    menuSelect(drink, "음료");
                    break;
                case 9:
                    System.out.println("메뉴 주문을 종료합니다.");
                    return;
                default:
                    System.out.println("메뉴 분류를 다시 선택 해 주세요.");
            }


        }

    }

    // 장바구니 내부 동작
    public void inCart() {
        int n;
        int change;

        while (true) {
            int i = 1;
            for (SingleMenu e : singleCart) {
                System.out.printf("[%d] %s, %d개, %d원\n", i++, e.getName(), e.getCount(), e.getPrice() * e.getCount());
            }
            for (SetMenu e : setCart) {
                System.out.printf("[%d] %s 세트, %d개, %d원\n", i++, e.getBurger().getName(), e.getCount(), e.getPrice() * e.getCount());
                System.out.printf("     ㄴ 사이드 : %s\n", e.getSide().getName());
                System.out.printf("     ㄴ 음료 : %s\n", e.getDrink().getName());
            }

            System.out.println("=".repeat(20));
            System.out.print("[1]메뉴 수량 변경, [2]전체 취소, [3] 결제 [4] 이전화면: ");
            int sel = sc.nextInt();
            sc.nextLine();

            switch (sel) {
                case 1:
                    System.out.print("수량을 변경할 메뉴를 선택 해 주세요 : ");
                    n = sc.nextInt() - 1;
                    sc.nextLine();
                    if (n >= singleCart.size()) {
                        n = n - singleCart.size();
                        System.out.printf("%s 세트의 현재 수량 %d개 \n", setCart.get(n).getBurger().getName(), setCart.get(n).getCount());
                        System.out.print("증가, 감소시킬 개수를 입력 해 주세요 : ");
                        change = sc.nextInt();
                        sc.nextLine();
                        if (change >= setCart.get(n).getCount()) {
                            System.out.println("메뉴를 제거하였습니다.");
                            setCart.remove(n);
                        } else {
                            setCart.get(n).changeCount(change);
                        }
                        break;
                    } else {
                        System.out.printf("%s 의 현재 수량 %d개 \n", singleCart.get(n).getName(), singleCart.get(n).getCount());
                        System.out.print("증가, 감소시킬 개수를 입력 해 주세요 : ");
                        change = sc.nextInt();
                        sc.nextLine();
                        if (change >= singleCart.get(n).getCount()) {
                            System.out.println("메뉴를 제거하였습니다.");
                            singleCart.remove(n);
                        }
                        break;
                    }
                case 2:
                    System.out.print("정말로 전체 취소 하시겠습니까?(y/n) : ");
                    String yn = sc.nextLine();
                    if (yn.equalsIgnoreCase("y")) {
                        System.out.println("장바구니를 비웁니다.");
                        singleCart.clear();
                        setCart.clear();
                        return;
                    } else {
                        System.out.println("취소하지 않습니다.");
                        break;
                    }
                case 3:
                    int totalPrice = 0;
                    for (SingleMenu e : singleCart) {
                        totalPrice += e.getPrice();
                    }
                    for (SetMenu e : setCart) {
                        totalPrice += e.getPrice();
                    }
                    System.out.printf("총 가격 : %d원", totalPrice);
                    paymentUpdate();

                    // 추후 결제정보 리턴하여 다른 객체로 넘겨주기
                    singleCart.clear();
                    setCart.clear();
                    return;
                case 4:
                    System.out.println("이전 화면으로 돌아갑니다.");
                    return;
            }
        }
    }

    // 결제 시 재고 감소
    public void paymentUpdate() {
        String sqlSingle = "UPDATE INV SET STOCK = STOCK - ? WHERE STORE_ID = ? AND MENU_NAME = ?";
        String sqlSet = "UPDATE INV SET STOCK = STOCK - ? WHERE STORE_ID = ? AND MENU_NAME IN (?, ?, ?)";
        int tuple = 0;

        try {
            conn = Common.getConnection();
            psmt = conn.prepareStatement(sqlSingle);
            for (SingleMenu e : singleCart) {
                psmt.setInt(1, e.getCount());
                psmt.setString(2, storeId);
                psmt.setString(3, e.getName());
                System.out.println(e.getName() + " : " + e.getCount() + "개 감소");
                tuple += psmt.executeUpdate();
            }
            psmt = conn.prepareStatement(sqlSet);
            for (SetMenu e : setCart) {
                psmt.setInt(1, e.getCount());
                psmt.setString(2, storeId);
                psmt.setString(3, e.getBurger().getName());
                psmt.setString(4, e.getSide().getName());
                psmt.setString(5, e.getDrink().getName());
                System.out.printf("%s, %s, %s : %d개씩 감소\n", e.getBurger().getName(), e.getSide().getName(), e.getDrink().getName(), e.getCount());
                tuple += psmt.executeUpdate();
            }
        } catch (Exception e) {
            System.out.println("Error in paymentUpdate : " + e.getMessage());
        }
    }


    public void menuSelect(List<InvVO> menu, String cat) {
        System.out.printf("%20s\n", cat + " 목록");
        int i = 1;
        for (InvVO e : menu) {
            System.out.printf("[%d] %s , %s, %d\n", i++, e.getMenuName(), e.getDescr(), e.getPrice());
        }
        System.out.print("메뉴를 선택 해 주세요 : ");
        int select = sc.nextInt() - 1;
        sc.nextLine();
        System.out.print("수량을 선택 해 주세요 : ");
        int count = sc.nextInt();
        sc.nextLine();

        // 장바구니에 추가
        boolean isExist = singleCart.stream().anyMatch(singleMenu -> singleMenu.getName().equals(menu.get(select).getMenuName()));
        if (isExist) {
            for (SingleMenu e : singleCart) {
                if (e.getName().equals(menu.get(select).getMenuName())) {
                    e.increaseCount(count); // 수량 증가
                    break;
                }
            }
        } else {
            singleCart.add(new SingleMenu(menu.get(select).getMenuName(), menu.get(select).getPrice(), count));
        }
    }

    public void ownerOrder() {
        int capital = CapitalCheck();

        List<InvVO> vo = orderInvCheck();
        List<InvVO> burger = new ArrayList<>();
        List<InvVO> side = new ArrayList<>();
        List<InvVO> drink = new ArrayList<>();

        String sqlOrder = "UPDATE INV SET STOCK = STOCK + ? WHERE MENU_NAME = ? AND STORE_ID = ?";
        String sqlCapital = "UPDATE STORE SET CAPITAL = CAPITAL - ? WHERE STORE_ID = ?";

        for (InvVO e : vo) {
            switch (e.getCategory()) {
                case "버거":
                    burger.add(e);
                    break;
                case "사이드":
                    side.add(e);
                    break;
                case "음료":
                    drink.add(e);
                    break;
            }
        }

        while (true) {
            System.out.println("발주를 넣을 메뉴의 분류 선택 [1] 버거, [2] 사이드, [3] 음료, [9] 종료");
            int sel = sc.nextInt();
            sc.nextLine();

            List<InvVO> selectedCategory = null;
            switch (sel) {
                case 1:
                    selectedCategory = burger;
                    break;
                case 2:
                    selectedCategory = side;
                    break;
                case 3:
                    selectedCategory = drink;
                    break;
                case 9:
                    return;
                default:
                    System.out.println("잘못된 선택입니다. 다시 선택하세요.");
                    continue;
            }

            if (selectedCategory != null && !selectedCategory.isEmpty()) {
                displayMenu(selectedCategory);

                System.out.print("발주할 메뉴를 선택하세요. : ");
                int idx = sc.nextInt() - 1;
                sc.nextLine();
                System.out.print("메뉴의 수량을 입력하세요 : ");
                int cnt = sc.nextInt();
                sc.nextLine();

                if (selectedCategory.get(idx).getPrice() * cnt <= capital) {
                    try (Connection conn = Common.getConnection()) {
                        conn.setAutoCommit(false);

                        updateInventory(conn, sqlOrder, cnt, selectedCategory.get(idx).getMenuName());
                        updateCapital(conn, sqlCapital, cnt * selectedCategory.get(idx).getPrice());

                        conn.commit();
                        System.out.println("주문이 정상적으로 완료되었습니다.");
                    } catch (SQLException e) {
                        System.out.println("Error during update: " + e.getMessage());
                        try {
                            if (conn != null) {
                                conn.rollback();
                                System.out.println("Changes rolled back.");
                            }
                        } catch (SQLException rollbackEx) {
                            rollbackEx.printStackTrace();
                        }
                    }
                } else {
                    System.out.println("가용액이 부족합니다.");
                }
            } else {
                System.out.println("선택한 카테고리에 메뉴가 없습니다.");
            }
        }
    }

    private void displayMenu(List<InvVO> items) {
        int i = 1;
        for (InvVO e : items) {
            System.out.printf("[%d] %s %d원\n", i++, e.getMenuName(), e.getPrice());
        }
    }

    private void updateInventory(Connection conn, String sqlOrder, int cnt, String menuName) {
        try (PreparedStatement psmt = conn.prepareStatement(sqlOrder)) {
            psmt.setInt(1, cnt);
            psmt.setString(2, menuName);
            psmt.setString(3, storeId);
            psmt.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void updateCapital(Connection conn, String sqlCapital, int amount) {
        try (PreparedStatement psmt = conn.prepareStatement(sqlCapital)) {
            psmt.setInt(1, amount);
            psmt.setString(2, storeId);
            psmt.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public int CapitalCheck() {
        String sqlCap = "SELECT CAPITAL FROM STORE WHERE STORE_ID = ?";
        try {
            conn = Common.getConnection();
            psmt = conn.prepareStatement(sqlCap);
            psmt.setString(1, storeId);
            rs = psmt.executeQuery();
            rs.next();

            return rs.getInt("CAPITAL");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public List<InvVO> orderInvCheck() {
        List<InvVO> vo = new ArrayList<>();
        String sqlInv = "SELECT MENU_NAME, PRICE, CATEGORY FROM INV_ORDER";

        try {
            conn = Common.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlInv);

            while (rs.next()) {
                String name = rs.getString("MENU_NAME");
                int price = rs.getInt("PRICE");
                String cat = rs.getString("CATEGORY");

                vo.add(new InvVO(name, cat, price));
            }
            return vo;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void invCheck(String storeId) {
        List<InvVO> vo = new ArrayList<>();

        // SQL query
        String sql = "SELECT i.MENU_NAME, i.STOCK, o.CATEGORY FROM INV i JOIN INV_ORDER o " +
                "ON i.MENU_NAME = o.MENU_NAME WHERE STORE_ID = ?";

        try {
            // Debug output to check the storeId value
            System.out.println("Current storeId: " + storeId);

            conn = Common.getConnection();
            psmt = conn.prepareStatement(sql);
            psmt.setString(1, storeId); // Bind storeId value
            rs = psmt.executeQuery();

            // Fetching inventory data
            while (rs.next()) {
                String menuName = rs.getString("MENU_NAME");
                int stock = rs.getInt("STOCK");
                String category = rs.getString("CATEGORY");
                vo.add(new InvVO(menuName, stock, category));
            }

            // Debug output to check list size
            System.out.println("Inventory List Size: " + vo.size());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            Common.close(rs);
            Common.close(psmt);
            Common.close(conn);
        }

        // Menu selection logic remains the same
        while (true) {
            System.out.print("재고를 확인할 메뉴의 분류 선택 [1]버거, [2]사이드, [3]음료 [9]뒤로가기 : ");
            int sel = sc.nextInt();
            sc.nextLine();
            switch (sel) {
                case 1:
                    for (InvVO e : vo) {
                        if (e.getCategory().equals("버거")) {
                            System.out.println(e.getMenuName() + " : " + e.getStock() + "개");
                        }
                    }
                    break;
                case 2:
                    for (InvVO e : vo) {
                        if (e.getCategory().equals("사이드")) {
                            System.out.println(e.getMenuName() + " : " + e.getStock() + "개");
                        }
                    }
                    break;
                case 3:
                    for (InvVO e : vo) {
                        if (e.getCategory().equals("음료")) {
                            System.out.println(e.getMenuName() + " : " + e.getStock() + "개");
                        }
                    }
                    break;
                case 9:
                    return;
            }
        }
    }

}