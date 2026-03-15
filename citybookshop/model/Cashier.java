package model;

public class Cashier extends User {

    public Cashier(String username, String password) {
        super(username, password, "Cashier");
    }

    @Override
    public void showRole() {
        System.out.println("Cashier Access");
    }
}