import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Dashboard extends JFrame {
    String user;
    JLabel balanceLabel;

    public Dashboard(String username) {
        user = username;
        setTitle("Dashboard - " + user);
        setSize(600, 500);
        setLayout(null);
        getContentPane().setBackground(new Color(54, 69, 79)); // Charcoal Grey

        JLabel title = new JLabel("Welcome, " + user + "!");
        title.setBounds(160, 20, 300, 30);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(new Color(178, 255, 204)); // Seafoam Green
        add(title);

        JButton checkBalBtn = new JButton("Check Balance");
        JButton depositBtn = new JButton("Deposit");
        JButton withdrawBtn = new JButton("Withdraw");
        JButton historyBtn = new JButton("Transaction History");

        checkBalBtn.setBounds(200, 70, 200, 40);
        depositBtn.setBounds(200, 130, 200, 40);
        withdrawBtn.setBounds(200, 190, 200, 40);
        historyBtn.setBounds(200, 250, 200, 40);

        JButton[] buttons = {checkBalBtn, depositBtn, withdrawBtn, historyBtn};
        for (JButton btn : buttons) {
            btn.setFont(new Font("SansSerif", Font.PLAIN, 16));
            btn.setBackground(new Color(178, 255, 204));
            btn.setForeground(Color.BLACK);
            add(btn);
        }

        // 🔘 Logout Button
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBounds(250, 400, 100, 35);
        logoutBtn.setBackground(new Color(255, 102, 102));
        logoutBtn.setForeground(Color.BLACK);
        add(logoutBtn);

        logoutBtn.addActionListener(e -> {
            dispose(); // close dashboard
            LoginPage.main(null); // return to login
        });

        balanceLabel = new JLabel("");
        balanceLabel.setBounds(180, 320, 300, 30);
        balanceLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        balanceLabel.setForeground(Color.WHITE);
        add(balanceLabel);

        checkBalBtn.addActionListener(e -> checkBalance());
        depositBtn.addActionListener(e -> updateBalance(true));
        withdrawBtn.addActionListener(e -> updateBalance(false));
        historyBtn.addActionListener(e -> showHistory());

        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    void checkBalance() {
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT balance FROM users WHERE username=?");
            ps.setString(1, user);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double bal = rs.getDouble("balance");
                balanceLabel.setText("Current Balance: ₹" + bal);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            balanceLabel.setText("Error fetching balance.");
        }
    }

    void updateBalance(boolean isDeposit) {
        String input = JOptionPane.showInputDialog("Enter amount:");
        if (input == null || input.trim().isEmpty()) return;

        try {
            double amount = Double.parseDouble(input);
            if (amount <= 0) throw new NumberFormatException();

            try (Connection con = DBConnection.getConnection()) {
                con.setAutoCommit(false);

                PreparedStatement ps1 = con.prepareStatement("SELECT balance FROM users WHERE username=?");
                ps1.setString(1, user);
                ResultSet rs = ps1.executeQuery();

                if (rs.next()) {
                    double current = rs.getDouble("balance");
                    double newBal = isDeposit ? current + amount : current - amount;

                    if (newBal < 0) {
                        JOptionPane.showMessageDialog(null, "Insufficient Balance!");
                        return;
                    }

                    PreparedStatement ps2 = con.prepareStatement("UPDATE users SET balance=? WHERE username=?");
                    ps2.setDouble(1, newBal);
                    ps2.setString(2, user);
                    ps2.executeUpdate();

                    PreparedStatement ps3 = con.prepareStatement(
                        "INSERT INTO transactions (username, type, amount) VALUES (?, ?, ?)"
                    );
                    ps3.setString(1, user);
                    ps3.setString(2, isDeposit ? "deposit" : "withdraw");
                    ps3.setDouble(3, amount);
                    ps3.executeUpdate();

                    con.commit();
                    JOptionPane.showMessageDialog(null, (isDeposit ? "Deposited ₹" : "Withdrawn ₹") + amount);
                    checkBalance();
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Enter a valid number.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Transaction failed.");
            e.printStackTrace();
        }
    }

    void showHistory() {
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                "SELECT type, amount, timestamp FROM transactions WHERE username=? ORDER BY timestamp DESC"
            );
            ps.setString(1, user);
            ResultSet rs = ps.executeQuery();

            StringBuilder history = new StringBuilder("Transaction History:\n\n");

            while (rs.next()) {
                history.append(rs.getString("type").toUpperCase())
                        .append(" ₹").append(rs.getDouble("amount"))
                        .append(" on ").append(rs.getTimestamp("timestamp")).append("\n");
            }

            JOptionPane.showMessageDialog(null, history.length() > 25 ? history.toString() : "No transactions found!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Failed to fetch history.");
        }
    }
}
