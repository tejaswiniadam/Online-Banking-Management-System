import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RegisterPage {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Register Page");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(new Color(54, 69, 79)); // Charcoal Grey
        frame.setLayout(null);

        JLabel heading = new JLabel("Create Account", SwingConstants.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 24));
        heading.setForeground(new Color(178, 255, 204)); // Seafoam Green
        heading.setBounds(180, 20, 250, 30);
        frame.add(heading);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Arial", Font.BOLD, 18));
        userLabel.setBounds(130, 100, 120, 25);
        frame.add(userLabel);

        JTextField userField = new JTextField();
        userField.setBounds(250, 100, 200, 30);
        frame.add(userField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.WHITE);
        passLabel.setFont(new Font("Arial", Font.BOLD, 18));
        passLabel.setBounds(130, 150, 120, 25);
        frame.add(passLabel);

        JPasswordField passField = new JPasswordField();
        passField.setBounds(250, 150, 200, 30);
        frame.add(passField);

        JButton registerBtn = new JButton("Register");
        registerBtn.setBounds(160, 220, 100, 40);
        registerBtn.setBackground(new Color(178, 255, 204));
        registerBtn.setForeground(Color.BLACK);
        frame.add(registerBtn);

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(340, 220, 100, 40);
        backBtn.setBackground(new Color(178, 255, 204));
        backBtn.setForeground(Color.BLACK);
        frame.add(backBtn);

        registerBtn.addActionListener(e -> {
            String username = userField.getText();
            String password = String.valueOf(passField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter username and password!");
                return;
            }

            try {
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
                ps.setString(1, username);
                ps.setString(2, password);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(frame, "Registered successfully!");
                frame.dispose();
                LoginPage.main(null);
            } catch (SQLIntegrityConstraintViolationException ex) {
                JOptionPane.showMessageDialog(frame, "Username already exists.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Registration failed.");
            }
        });

        backBtn.addActionListener(e -> {
            frame.dispose();
            LoginPage.main(null);
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
