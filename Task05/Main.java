
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// Task 05 - Currency Converter
public class Main extends JFrame {

    JTextField amountField;
    JLabel resultLabel;

    // Sample exchange rate
    final double RATE = 0.012;

    Main() {

        setTitle("Task 05 - Currency Converter");
        setSize(500, 350);
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel(
            "CURRENCY CONVERTER",
            JLabel.CENTER
        );

        title.setFont(new Font("Arial", Font.BOLD, 22));

        add(title, BorderLayout.NORTH);

        // Input panel
        JPanel inputPanel = new JPanel(new FlowLayout());

        inputPanel.add(new JLabel("Enter Amount (INR):"));

        amountField = new JTextField(12);

        inputPanel.add(amountField);

        add(inputPanel, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        JButton button1 = new JButton("Separate Listener");
        JButton button2 = new JButton("Inner Class");
        JButton button3 = new JButton("Anonymous Class");
        JButton button4 = new JButton("Lambda Expression");

        buttonPanel.add(button1);
        buttonPanel.add(button2);
        buttonPanel.add(button3);
        buttonPanel.add(button4);

        add(buttonPanel, BorderLayout.SOUTH);

        // Result label
        resultLabel = new JLabel(
            "Conversion result will appear here.",
            JLabel.CENTER
        );

        add(resultLabel, BorderLayout.WEST);

        // 1. Separate Listener Class
        button1.addActionListener(
            new SeparateListener(this)
        );

        // 2. Inner Class
        button2.addActionListener(
            new InnerListener()
        );

        // 3. Anonymous Inner Class
        button3.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                convertCurrency();
            }
        });

        // 4. Lambda Expression
        button4.addActionListener(
            e -> convertCurrency()
        );

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Currency conversion method
    void convertCurrency() {

        try {

            double rupees = Double.parseDouble(
                amountField.getText()
            );

            if (rupees < 0) {
                JOptionPane.showMessageDialog(
                    this,
                    "Please enter a positive amount."
                );
                return;
            }

            double dollars = rupees * RATE;

            resultLabel.setText(
                String.format(
                    "INR %.2f = USD $%.2f",
                    rupees,
                    dollars
                )
            );

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                this,
                "Please enter a valid number."
            );
        }
    }

    // Inner Class Listener
    class InnerListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            convertCurrency();
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> new Main());
    }
}

// Separate Listener Class
class SeparateListener implements ActionListener {

    Main converter;

    SeparateListener(Main converter) {
        this.converter = converter;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        converter.convertCurrency();
    }
}

