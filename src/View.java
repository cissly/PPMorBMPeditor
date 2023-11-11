import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class View {
    private JButton INPUT;
    public JPanel main;
    private JButton NEXT;
    private JScrollPane UCODE;
    private JScrollPane STACK;
    private JScrollPane RESULT;
    private JButton OUTPUT;


    public View() {
        new JScrollPane(UCODE, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        UCODE.add(new JLabel("zz"));
        INPUT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,"hello world");
            }
        });
    }


}
