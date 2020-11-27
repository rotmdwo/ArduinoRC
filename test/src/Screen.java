import javax.swing.*;

public class Screen {
    private JPanel jPanel;

    public static void main(String args[]) {
        JFrame frame = new JFrame("Screen");
        frame.setContentPane(new Screen().jPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
