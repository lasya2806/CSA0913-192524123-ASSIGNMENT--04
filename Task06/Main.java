
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Main extends JFrame {

    JTextField studentId, studentName, searchField;
    JComboBox<EventItem> eventBox;
    JTable table;
    DefaultTableModel model;
    JLabel status;

    final int PAGE_SIZE = 5;
    int page = 0;

    RegistrationDAO dao = new RegistrationDAO();

    Main() {

        setTitle("Campus Event Registration System");
        setSize(900, 550);
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel(
            "CAMPUS EVENT REGISTRATION SYSTEM",
            JLabel.CENTER
        );
        title.setFont(new Font("Arial", Font.BOLD, 22));

        studentId = new JTextField(12);
        studentName = new JTextField(12);
        eventBox = new JComboBox<>();

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder("Registration"));

        form.add(new JLabel("Student ID:"));
        form.add(studentId);

        form.add(new JLabel("Student Name:"));
        form.add(studentName);

        form.add(new JLabel("Select Event:"));
        form.add(eventBox);

        JButton register = new JButton("Register");
        JButton filter = new JButton("Search");
        JButton refresh = new JButton("Refresh");
        JButton export = new JButton("Export CSV");
        JButton previous = new JButton("Previous");
        JButton next = new JButton("Next");

        searchField = new JTextField(12);

        JPanel actions = new JPanel(new FlowLayout());
        actions.add(register);
        actions.add(new JLabel("Search:"));
        actions.add(searchField);
        actions.add(filter);
        actions.add(refresh);
        actions.add(export);

        JPanel top = new JPanel(new BorderLayout());
        top.add(title, BorderLayout.NORTH);
        top.add(form, BorderLayout.CENTER);
        top.add(actions, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);

        model = new DefaultTableModel(
            new String[]{
                "Registration ID",
                "Student ID",
                "Student Name",
                "Event",
                "Date",
                "Venue"
            }, 0
        ) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        status = new JLabel("Ready.");

        JPanel bottom = new JPanel();
        bottom.add(previous);
        bottom.add(next);
        bottom.add(status);

        add(bottom, BorderLayout.SOUTH);

        register.addActionListener(e -> registerStudent());

        filter.addActionListener(e -> {
            page = 0;
            loadRegistrations();
        });

        refresh.addActionListener(e -> {
            searchField.setText("");
            page = 0;
            loadRegistrations();
        });

        previous.addActionListener(e -> {
            if (page > 0) {
                page--;
                loadRegistrations();
            }
        });

        next.addActionListener(e -> {
            if (model.getRowCount() == PAGE_SIZE) {
                page++;
                loadRegistrations();
            }
        });

        export.addActionListener(e -> exportCSV());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        loadEvents();
        loadRegistrations();

        setVisible(true);
    }

    void loadEvents() {

        new SwingWorker<List<EventItem>, Void>() {

            protected List<EventItem> doInBackground()
                    throws Exception {
                return dao.getEvents();
            }

            protected void done() {
                try {
                    eventBox.removeAllItems();

                    for (EventItem event : get()) {
                        eventBox.addItem(event);
                    }

                } catch (Exception ex) {
                    showError(ex);
                }
            }

        }.execute();
    }

    void registerStudent() {

        String id = studentId.getText().trim();
        String name = studentName.getText().trim();
        EventItem event = (EventItem) eventBox.getSelectedItem();

        if (id.isEmpty() || name.isEmpty() || event == null) {
            JOptionPane.showMessageDialog(
                this, "Please enter all details."
            );
            return;
        }

        new SwingWorker<Void, Void>() {

            protected Void doInBackground() throws Exception {
                dao.register(id, name, event.id);
                return null;
            }

            protected void done() {
                try {
                    get();

                    JOptionPane.showMessageDialog(
                        Main.this, "Registration Successful!"
                    );

                    studentId.setText("");
                    studentName.setText("");

                    page = 0;
                    loadRegistrations();

                } catch (Exception ex) {
                    showError(ex);
                }
            }

        }.execute();
    }

    void loadRegistrations() {

        String search = searchField.getText().trim();
        int offset = page * PAGE_SIZE;

        new SwingWorker<List<Registration>, Void>() {

            protected List<Registration> doInBackground()
                    throws Exception {
                return dao.getRegistrations(
                    search, PAGE_SIZE, offset
                );
            }

            protected void done() {
                try {
                    List<Registration> data = get();

                    model.setRowCount(0);

                    for (Registration r : data) {
                        model.addRow(new Object[]{
                            r.id,
                            r.studentId,
                            r.studentName,
                            r.eventName,
                            r.date,
                            r.venue
                        });
                    }

                    status.setText(
                        "Page " + (page + 1) +
                        " | Records: " + data.size()
                    );

                } catch (Exception ex) {
                    showError(ex);
                }
            }

        }.execute();
    }

    void exportCSV() {

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("registrations.csv"));

        if (chooser.showSaveDialog(this)
                != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try (FileWriter writer =
                new FileWriter(chooser.getSelectedFile())) {

            for (int c = 0; c < model.getColumnCount(); c++) {
                writer.write(csv(model.getColumnName(c)) + ",");
            }

            writer.write("\n");

            for (int r = 0; r < model.getRowCount(); r++) {
                for (int c = 0; c < model.getColumnCount(); c++) {
                    writer.write(
                        csv(String.valueOf(model.getValueAt(r, c)))
                    );

                    if (c < model.getColumnCount() - 1) {
                        writer.write(",");
                    }
                }
                writer.write("\n");
            }

            JOptionPane.showMessageDialog(
                this, "CSV exported successfully!"
            );

        } catch (Exception ex) {
            showError(ex);
        }
    }

    String csv(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    void showError(Exception ex) {
        Throwable cause = ex;

        while (cause.getCause() != null) {
            cause = cause.getCause();
        }

        JOptionPane.showMessageDialog(
            this,
            cause.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main());
    }
}

// EVENT MODEL
class EventItem {

    int id;
    String name;

    EventItem(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String toString() {
        return name;
    }
}

// REGISTRATION MODEL
class Registration {

    int id;
    String studentId, studentName, eventName, date, venue;

    Registration(
        int id, String studentId, String studentName,
        String eventName, String date, String venue
    ) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.eventName = eventName;
        this.date = date;
        this.venue = venue;
    }
}

// DAO LAYER
class RegistrationDAO {

    private final String URL =
        "jdbc:mysql://localhost:3306/campus_events";

    private final String USER = "root";

    // Replace with your MySQL password
    private final String PASSWORD = "YOUR_PASSWORD";

    Connection connect() throws SQLException {
        return DriverManager.getConnection(
            URL, USER, PASSWORD
        );
    }

    List<EventItem> getEvents() throws SQLException {

        List<EventItem> list = new ArrayList<>();

        String sql = "SELECT * FROM events";

        try (
            Connection con = connect();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                list.add(new EventItem(
                    rs.getInt("event_id"),
                    rs.getString("event_name")
                ));
            }
        }

        return list;
    }

    void register(
        String studentId, String studentName, int eventId
    ) throws SQLException {

        String sql =
            "INSERT INTO registrations " +
            "(student_id, student_name, event_id) " +
            "VALUES (?, ?, ?)";

        try (
            Connection con = connect();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, studentId);
            ps.setString(2, studentName);
            ps.setInt(3, eventId);
            ps.executeUpdate();
        }
    }

    List<Registration> getRegistrations(
        String search, int limit, int offset
    ) throws SQLException {

        List<Registration> list = new ArrayList<>();

        String sql =
            "SELECT r.registration_id, r.student_id, " +
            "r.student_name, e.event_name, e.event_date, e.venue " +
            "FROM registrations r " +
            "JOIN events e ON r.event_id = e.event_id " +
            "WHERE r.student_id LIKE ? " +
            "OR r.student_name LIKE ? " +
            "OR e.event_name LIKE ? " +
            "ORDER BY r.registration_id DESC " +
            "LIMIT ? OFFSET ?";

        try (
            Connection con = connect();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            String value = "%" + search + "%";

            ps.setString(1, value);
            ps.setString(2, value);
            ps.setString(3, value);
            ps.setInt(4, limit);
            ps.setInt(5, offset);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    list.add(new Registration(
                        rs.getInt("registration_id"),
                        rs.getString("student_id"),
                        rs.getString("student_name"),
                        rs.getString("event_name"),
                        rs.getString("event_date"),
                        rs.getString("venue")
                    ));
                }
            }
        }

        return list;
    }
}

