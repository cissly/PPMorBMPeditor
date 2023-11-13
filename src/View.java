import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

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
    private DefaultTableModel model;
    private GridBagConstraints c=new GridBagConstraints();

    public View(Doc d) {
        String[] header = {"LABEL", "명령어", "인자1", "인자2", "인자3"};
        model=new DefaultTableModel(header,0);
        JTable table= new JTable(model);
        code = new JScrollPane(table);
        document = d;
        mainF = new JPanel();
        mainF.setLayout(new GridBagLayout());
        c.fill=GridBagConstraints.BOTH;
        input.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String temp = load();
                String[][] lindData = document.lineReader(temp);
                UCODESet(lindData);
                mainF.invalidate();
            }
        });
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
    public String load() { // 입력을 받기위한 창을 띄우고 입력할 파일의 절대경로를 가져오는 함수
        JDialog dialog = new JDialog();

        // 파일 선택 다이얼로그 생성
        JFileChooser fileChooser = new JFileChooser(".");
        //fileChooser.setCurrentDirectory(new File(System.getProperty("")));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("U-Code", "uco"));

        int result = fileChooser.showOpenDialog(dialog);
        if (result != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        File loadedFile = fileChooser.getSelectedFile();
        String fileName = loadedFile.getAbsolutePath();
        return fileName;
    }
    private void UCODESet(String[][] data)
    {
        for(int i = 0; i< data.length; i++)
        {
            model.addRow(data[i]);
        }

    }

}

