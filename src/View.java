import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class View {
    private JButton button1;
    private JPanel main;

    public static void main(String[] args)
    {
        JFrame ff = new JFrame("aaa");
        ff.setContentPane(new View().main);
        ff.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ff.setSize(500,500);
        ff.setVisible(true);
    }

    public View() {
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,"hello world");
            }
        });
    }


}
