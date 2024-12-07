package view;

import controller.UserController;
import java.awt.*;
import javax.swing.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private UserController userController;

    public LoginFrame() {
        userController = new UserController();
        initComponents();
    }

    private void initComponents() {
        setTitle("E-Commerce Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Username Label
        JLabel usernameLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(usernameLabel, gbc);

        // Username Field
        usernameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(usernameField, gbc);

        // Password Label
        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(passwordLabel, gbc);

        // Password Field
        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(passwordField, gbc);

        // Login Button
        JButton loginButton = new JButton("Login");
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(loginButton, gbc);

        // Action Listener untuk Login
        loginButton.addActionListener(e -> performLogin());
    }

    private void performLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.equals("kasir") && password.equals("kasir123")) {
            CashierDashboardFrame cashierDashboard = new CashierDashboardFrame();
            cashierDashboard.setVisible(true); // Tampilkan CashierDashboardFrame
            dispose(); // Tutup login frame
        } else {
            BuyerDashboardFrame buyerDashboard = new BuyerDashboardFrame();
        buyerDashboard.setVisible(true); // Tampilkan BuyerDashboardFrame
        dispose(); // Tutup login frame
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}