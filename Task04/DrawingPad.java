
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Main extends Frame {

    DrawingArea drawingArea;

    Main() {

        setTitle("Task 04 - Event Handling Drawing Pad");
        setSize(600, 450);
        setLayout(new BorderLayout());

        drawingArea = new DrawingArea();

        add(drawingArea, BorderLayout.CENTER);

        Label instruction = new Label(
            "Click anywhere inside the drawing area to create dots.",
            Label.CENTER
        );

        add(instruction, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    class DrawingArea extends Canvas {

        ArrayList<Point> dots = new ArrayList<>();

        DrawingArea() {

            setBackground(Color.WHITE);

            addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {

                    dots.add(new Point(e.getX(), e.getY()));

                    repaint();
                }
            });
        }

        @Override
        public void paint(Graphics g) {

            g.setColor(Color.BLUE);

            for (Point point : dots) {

                g.fillOval(
                    point.x - 5,
                    point.y - 5,
                    10,
                    10
                );
            }
        }
    }

    public static void main(String[] args) {

        new Main();
    }
}
