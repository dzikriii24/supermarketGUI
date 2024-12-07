package controller;

import model.User;
import java.util.ArrayList;
import java.util.List;

public class UserController {
    private List<User> users;

    public UserController() {
        users = new ArrayList<>();
        // Inisialisasi default user
        users.add(new User("kasir", "kasir123", "KASIR"));
        users.add(new User("pembeli", "pembeli123", "PEMBELI"));
    }

    public boolean authenticateUser(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && 
                user.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    public String getUserRole(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user.getRole();
            }
        }
        return null;
    }

    public void addUser(String username, String password, String role) {
        User newUser = new User(username, password, role);
        users.add(newUser);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }
}