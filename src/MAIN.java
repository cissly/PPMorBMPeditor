import javax.swing.*;

public class MAIN {

    public static void main(String[] args)
    {
        Doc d = new Doc();
        View v = new View(d);
        JFrame ff = new JFrame("aaa");
        ff.setContentPane(v.mainF);
        ff.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ff.setSize(500,500);
        ff.setVisible(true);
    }
}
