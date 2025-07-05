import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginPage {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Login Page");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(new Color(54, 69, 79)); // Charcoal Grey
        frame.setLayout(null);

        JLabel heading = new JLabel("Hey User", SwingConstants.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 24));
        heading.setForeground(new Color(178, 255, 204)); // Seafoam Green
        heading.setBounds(200, 20, 200, 30);
        frame.add(heading);

        JLabel subHeading = new JLabel("Welcome to online banking services", SwingConstants.CENTER);
        subHeading.setFont(new Font("Arial", Font.BOLD, 20));
        subHeading.setForeground(Color.WHITE);
        subHeading.setBounds(100, 60, 400, 30);
        frame.add(subHeading);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.BOLD, 18));
        userLabel.setForeground(Color.WHITE);
        userLabel.setBounds(130, 120, 120, 25);
        frame.add(userLabel);

        JTextField userField = new JTextField();
        userField.setBounds(250, 120, 200, 30);
        frame.add(userField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.BOLD, 18));
        passLabel.setForeground(Color.WHITE);
        passLabel.setBounds(130, 170, 120, 25);
        frame.add(passLabel);

        JPasswordField passField = new JPasswordField();
        passField.setBounds(250, 170, 200, 30);
        frame.add(passField);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(340, 230, 100, 40);
        loginBtn.setBackground(new Color(178, 255, 204));
        loginBtn.setForeground(Color.BLACK);
        frame.add(loginBtn);

        JButton registerBtn = new JButton("Register");
        registerBtn.setBounds(160, 230, 100, 40);
        registerBtn.setBackground(new Color(178, 255, 204));
        registerBtn.setForeground(Color.BLACK);
        frame.add(registerBtn);

        loginBtn.addActionListener(e -> {
            String username = userField.getText();
            String password = String.valueOf(passField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter username and password!");
                return;
            }

            try {
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username=? AND password=?");
                ps.setString(1, username);
                ps.setString(2, password);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    JOptionPane.showMessageDialog(frame, "Login successful!");
                    frame.dispose();
                    new Dashboard(username);
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid credentials!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        registerBtn.addActionListener(e -> {
            frame.dispose();
            RegisterPage.main(null);
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
