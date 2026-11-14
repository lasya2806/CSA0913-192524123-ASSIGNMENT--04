
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class LibrarySearchScreen extends JFrame {

    JTextField searchField;
    JComboBox<String> category;
    JTable table;
    JLabel status;

    DefaultTableModel model;

    LibrarySearchScreen() {

        setTitle("Campus Library Search");
        setSize(700, 450);
        setLayout(new BorderLayout(10, 10));

        // TOP PANEL
        JPanel topPanel = new JPanel(new BorderLayout());

        JLabel title = new JLabel(
                "CAMPUS LIBRARY SEARCH",
                JLabel.CENTER
        );

        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        topPanel.add(title, BorderLayout.NORTH);

        // SEARCH PANEL
        JPanel searchPanel = new JPanel(new FlowLayout());

        searchField = new JTextField(20);

        category = new JComboBox<>(
                new String[]{"All", "Computer Science",
                        "Mathematics", "Science", "English"}
        );

        JButton searchButton = new JButton("Search");

        searchPanel.add(new JLabel("Search Book:"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Category:"));
        searchPanel.add(category);
        searchPanel.add(searchButton);

        topPanel.add(searchPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // RESULTS TABLE
        String[] columns = {
                "Book ID", "Book Name", "Category", "Author"
        };

        model = new DefaultTableModel(columns, 0);

        model.addRow(new Object[]{
                "101", "Java Programming",
                "Computer Science", "James Gosling"
        });

        model.addRow(new Object[]{
                "102", "Mathematics",
                "Mathematics", "John Smith"
        });

        model.addRow(new Object[]{
                "103", "Physics",
                "Science", "David Hall"
        });

        model.addRow(new Object[]{
                "104", "English Grammar",
                "English", "David Green"
        });

        table = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(table);

        add(scrollPane, BorderLayout.CENTER);

        // BOTTOM PANEL
        JPanel bottomPanel = new JPanel(new BorderLayout());

        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton prevButton = new JButton("Prev");
        JButton nextButton = new JButton("Next");
        JButton reserveButton = new JButton("Reserve");

        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(reserveButton);

        status = new JLabel("Welcome! Search for your books.");

        bottomPanel.add(buttonPanel, BorderLayout.NORTH);
        bottomPanel.add(status, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        // SEARCH ACTION
        searchButton.addActionListener(e -> {

            String search = searchField.getText().toLowerCase();
            String selectedCategory =
                    category.getSelectedItem().toString();

            boolean found = false;

            for (int i = 0; i < model.getRowCount(); i++) {

                String book = model.getValueAt(i, 1).toString()
                        .toLowerCase();

                String bookCategory =
                        model.getValueAt(i, 2).toString();

                boolean matchesSearch =
                        book.contains(search);

                boolean matchesCategory =
                        selectedCategory.equals("All")
                        || selectedCategory.equals(bookCategory);

                if (matchesSearch && matchesCategory) {
                    table.setRowSelectionInterval(i, i);
                    status.setText("Book found: " + book);
                    found = true;
                    break;
                }
            }

            if (!found) {
                table.clearSelection();
                status.setText("No matching book found.");
            }
        });

        // PREVIOUS BUTTON
        prevButton.addActionListener(e -> {
            int row = table.getSelectedRow();

            if (row > 0) {
                table.setRowSelectionInterval(row - 1, row - 1);
            } else {
                status.setText("No previous book available.");
            }
        });

        // NEXT BUTTON
        nextButton.addActionListener(e -> {
            int row = table.getSelectedRow();

            if (row < model.getRowCount() - 1) {
                table.setRowSelectionInterval(row + 1, row + 1);
            } else {
                status.setText("No next book available.");
            }
        });

        // RESERVE BUTTON
        reserveButton.addActionListener(e -> {

            int row = table.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(
                        this, "Please select a book first!"
                );
            } else {
                String bookName =
                        model.getValueAt(row, 1).toString();

                JOptionPane.showMessageDialog(
                        this,
                        "Book Reserved Successfully!\n" + bookName
                );

                status.setText("Reserved: " + bookName);
            }
        });

        // WINDOW CLOSING
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(
                () -> new LibrarySearchScreen()
        );
    }
}

