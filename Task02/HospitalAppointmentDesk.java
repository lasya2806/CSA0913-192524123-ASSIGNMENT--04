
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class Main extends JFrame {

    JTextField patientId;
    JComboBox<String> department;
    JComboBox<String> doctor;
    JComboBox<String> timeSlot;
    JLabel status;
    JButton bookButton;

    Map<String, String[]> doctors = new HashMap<>();

    boolean bookingInProgress = false;

    Main() {

        setTitle("Hospital Appointment Desk");
        setSize(450, 350);
        setLayout(new GridLayout(6, 2, 10, 10));

        doctors.put("Cardiology",
                new String[]{"Dr. Kumar", "Dr. Priya"});

        doctors.put("Neurology",
                new String[]{"Dr. Ravi", "Dr. Anitha"});

        doctors.put("Orthopedics",
                new String[]{"Dr. Suresh", "Dr. Meena"});

        doctors.put("Dermatology",
                new String[]{"Dr. Arun", "Dr. Divya"});

        add(new JLabel("Patient ID:"));
        patientId = new JTextField();
        add(patientId);

        add(new JLabel("Department:"));

        department = new JComboBox<>(
                new String[]{"Select Department",
                        "Cardiology", "Neurology",
                        "Orthopedics", "Dermatology"});

        add(department);

        add(new JLabel("Doctor:"));

        doctor = new JComboBox<>();
        doctor.addItem("Select Doctor");
        add(doctor);

        add(new JLabel("Time Slot:"));

        timeSlot = new JComboBox<>(
                new String[]{"Select Time",
                        "10:00 AM", "11:00 AM",
                        "12:00 PM", "2:00 PM",
                        "3:00 PM"});

        add(timeSlot);

        bookButton = new JButton("Book Appointment");
        add(bookButton);

        JButton clearButton = new JButton("Clear");
        add(clearButton);

        status = new JLabel("Please enter appointment details");
        add(status);

        department.addActionListener(e -> {
            String selected =
                    (String) department.getSelectedItem();

            doctor.removeAllItems();

            if (doctors.containsKey(selected)) {
                for (String d : doctors.get(selected)) {
                    doctor.addItem(d);
                }
            } else {
                doctor.addItem("Select Doctor");
            }
        });

        bookButton.addActionListener(e -> bookAppointment());

        clearButton.addActionListener(e -> {
            patientId.setText("");
            department.setSelectedIndex(0);
            timeSlot.setSelectedIndex(0);
            doctor.removeAllItems();
            doctor.addItem("Select Doctor");
            status.setText("Fields cleared");
            bookingInProgress = false;
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {

                if (bookingInProgress) {
                    int choice = JOptionPane.showConfirmDialog(
                            Main.this,
                            "Booking is in progress. Close window?",
                            "Confirm Exit",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (choice == JOptionPane.YES_OPTION) {
                        dispose();
                    }
                } else {
                    dispose();
                }
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    void bookAppointment() {

        bookingInProgress = true;

        String id = patientId.getText().trim();
        String dept = (String) department.getSelectedItem();
        String doc = (String) doctor.getSelectedItem();
        String time = (String) timeSlot.getSelectedItem();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter Patient ID");
            bookingInProgress = false;
            return;
        }

        if (department.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select Department");
            bookingInProgress = false;
            return;
        }

        if (doctor.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select Doctor");
            bookingInProgress = false;
            return;
        }

        if (timeSlot.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select Time Slot");
            bookingInProgress = false;
            return;
        }

        JOptionPane.showMessageDialog(this,
                "Appointment Booked Successfully!\n\n"
                + "Patient ID: " + id
                + "\nDepartment: " + dept
                + "\nDoctor: " + doc
                + "\nTime: " + time);

        status.setText("Appointment Booked Successfully");
        bookingInProgress = false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main());
    }
}

