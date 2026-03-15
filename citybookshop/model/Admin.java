package model;

public class Admin extends User {

    public Admin(String username, String password) {
        super(username, password, "Admin");
    }

    @Override
    public void showRole() {
        System.out.println("Admin Access");
    }
}