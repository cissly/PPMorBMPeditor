import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class View {

    private Doc document;
    public JPanel mainF;

    private JScrollPane code = new JScrollPane();
    private JScrollPane stack= new JScrollPane();
    private JScrollPane result= new JScrollPane();

    private JLabel tagCode = new JLabel("U-Code");
    private JLabel tagStack = new JLabel("Stack");
    private JLabel tagResult = new JLabel("Result");

    private JButton oneStep = new JButton("한단계씩 실행");
    private JButton outPut= new JButton("출력");
    private JButton Start = new JButton("실행");
    private JButton input = new JButton("입력");
    private GridBagConstraints c=new GridBagConstraints();

    public View(Doc d) {
        document = d;
        mainF = new JPanel();
        mainF.setLayout(new GridBagLayout());
        c.fill=GridBagConstraints.BOTH;
        gbAdd(tagCode,0 ,0, 1, 1,0.7,0.03);
        gbAdd(tagStack, 1,0, 1,1, 0.3,0.03);
        gbAdd(code,0 ,1, 1, 1,0.7,0.6);
        gbAdd(stack, 1,1, 1,1, 0.3,0.6);
        gbAdd(tagResult,0,2,1,1, 0.7,0.03);
        gbAdd(result, 0,3,1,3,0.7,0.09);
        gbAdd(input, 1,2,1,1,0.3,0.03);
        gbAdd(outPut, 1,3,1,1,0.3,0.03);
        gbAdd(Start, 1,4,1,1,0.3,0.03);
        gbAdd(oneStep, 1,5,1,1,0.3,0.03);
    }
    public void gbAdd(Component temp, int x, int y, int w, int h, double k, double t) {

        c.gridx = x;
        c.gridy = y;
        c.gridwidth  = w;
        c.gridheight = h;
        c.weightx = k;
        c.weighty = t;

        mainF.add(temp,c);
    }


}

