
import javax.swing.*;


public class Screen {
    private JPanel jPanel;
    private JLabel label1;

    public static void Main(String[] args) {
        JFrame frame = new JFrame("Screen");
        frame.setContentPane(new Screen().jPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }


}
