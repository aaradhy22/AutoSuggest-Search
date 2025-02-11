package AutoSuggestion.withdatabase;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.ArrayList;

public class AutoSuggestApp {
    private static DefaultListModel<String> listModel;
    private static JList<String> suggestionList;
    private static Connection connection;

    public static void main(String[] args) {
        connectDatabase();
        SwingUtilities.invokeLater(AutoSuggestApp::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Auto Suggest Search");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        JTextField searchField = new JTextField(20);
        listModel = new DefaultListModel<>();
        suggestionList = new JList<>(listModel);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { SwingUtilities.invokeLater(() -> updateSuggestions(searchField.getText())); }
            public void removeUpdate(DocumentEvent e) { SwingUtilities.invokeLater(() -> updateSuggestions(searchField.getText())); }
            public void changedUpdate(DocumentEvent e) {}
        });

        suggestionList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    searchField.setText(suggestionList.getSelectedValue());
                }
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(searchField, BorderLayout.NORTH);
        panel.add(new JScrollPane(suggestionList), BorderLayout.CENTER);

        frame.add(panel);
        frame.setVisible(true);
    }

    private static void updateSuggestions(String query) {
        listModel.clear();
        if (query.trim().isEmpty()) return;

        try {
            if (connection == null || connection.isClosed()) {
                System.err.println("Database connection is not established.");
                return;
            }
            PreparedStatement stmt = connection.prepareStatement("SELECT name FROM contacts WHERE name LIKE ? LIMIT 10");
            stmt.setString(1, query + "%");
            ResultSet rs = stmt.executeQuery();

            ArrayList<String> suggestions = new ArrayList<>();
            while (rs.next()) {
                suggestions.add(rs.getString("name"));
            }
            listModel.addAll(suggestions);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void connectDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");  // Ensure Driver is Loaded
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/your_database?useSSL=false&allowPublicKeyRetrieval=true",
                    "root",
                    "Radhe@22#"
            );
            System.out.println("Connected to database successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found. Add MySQL Connector/J.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}