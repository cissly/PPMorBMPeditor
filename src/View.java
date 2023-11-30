import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Stack;

public class View {

    private Doc document;
    public JPanel mainF;

    private JScrollPane block;
    private JScrollPane code;
    private JScrollPane stack= new JScrollPane();
    private JScrollPane mStack= new JScrollPane();
    private JScrollPane result= new JScrollPane();
    private JLabel tagblock = new JLabel("BLOCK");
    private JLabel tagCode = new JLabel("U-Code");
    private JLabel tagStack = new JLabel("CPU-Stack");
    private JLabel tagMStack = new JLabel("Memory-Stack");
    private JLabel tagResult = new JLabel("Result");
    private JButton oneStep = new JButton("한단계씩 실행");
    private JButton outPut= new JButton("출력");
    private JButton Start = new JButton("실행");
    private JButton input = new JButton("입력");
    private DefaultTableModel codemodel;
    private DefaultTableModel blockmodel;
    private DefaultTableModel stackmodel;
    private DefaultTableModel stackMmodel;
    private GridBagConstraints c=new GridBagConstraints();

    private DefaultTableCellRenderer render;

    public View(Doc d) {
        String[] header = {"Now","LABEL", "명령어", "인자1", "인자2", "인자3"};
        String[] header1 = {"No.","DATA"};
        String[] stackhead = {"Data"};
        stackmodel = new DefaultTableModel(stackhead,0);
        stackMmodel = new DefaultTableModel(stackhead,0);
        codemodel=new DefaultTableModel(header,0);
        blockmodel=new DefaultTableModel(header1,0);
        JTable table= new JTable(codemodel);
        code = new JScrollPane(table);
        JTable blocktable = new JTable(blockmodel);
        block = new JScrollPane(blocktable);
        JTable stacktable= new JTable(stackmodel);
        stack = new JScrollPane(stacktable);
        JTable stackMtable= new JTable(stackMmodel);
        mStack = new JScrollPane(stackMtable);
        document = d;
        mainF = new JPanel();
        mainF.setLayout(new GridBagLayout());
        c.fill=GridBagConstraints.BOTH;
        input.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clear();
                String temp = load();
                String[][] lindData = document.lineReader(temp);
                UCODESet(lindData);
                mainF.invalidate();
            }
        });
        Start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                document.turnOneF();
                document.process();
            }
        });
        oneStep.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                document.turnOne();
                document.process();
            }
        });
        gbAdd(tagblock,0 ,0, 1, 1,0.2,0.03);
        gbAdd(block,0,1,1,5,0.2,1);
        gbAdd(tagCode,1 ,0, 1, 1,0.7,0.03);
        gbAdd(tagStack, 2,0, 1,1, 0.15,0.03);
        gbAdd(tagMStack, 3,0, 1,1, 0.15,0.03);
        gbAdd(code,1 ,1, 1, 1,0.7,0.6);
        gbAdd(stack, 2,1, 1,1, 0.15,0.6);
        gbAdd(mStack, 3,1, 1,1, 0.15,0.6);
        gbAdd(tagResult,1,2,1,1, 0.7,0.03);
        gbAdd(result, 1,3,1,3,0.7,0.09);
        gbAdd(input, 2,2,2,1,0.3,0.03);
        gbAdd(outPut, 2,3,2,1,0.3,0.03);
        gbAdd(Start, 2,4,2,1,0.3,0.03);
        gbAdd(oneStep, 2,5,2,1,0.3,0.03);
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
        for (String[] datum : data) {
            codemodel.addRow(datum);
        }
    }
    public void stackAdd(int a)
    {
        String ab = Integer.toString(a);
        String[] temp = {ab};
        stackmodel.addRow(temp);
        stackmodel.fireTableDataChanged();
    }

    public void stackPopDel()
    {
        int a =stackmodel.getRowCount();
        stackmodel.removeRow(a-1);
        stackmodel.fireTableDataChanged();
    }

    public void mstackAdd(int a)
    {
        String ab = Integer.toString(a);
        String[] temp = {ab};
        stackMmodel.addRow(temp);
        stackMmodel.fireTableDataChanged();
    }
    public void blockAdd(int block, int offset, int a)
    {
        String _block =  Integer.toString(block);
        String _offset =  Integer.toString(offset);
        String data =  Integer.toString(a);
        String[] temp = {_block+"/"+_offset,data};
        blockmodel.addRow(temp);
        blockmodel.fireTableDataChanged();
    }
    public void blockDel(int size)
    {
        for(int i = 0; i < size; i++)
        {
            blockmodel.removeRow(blockmodel.getRowCount()-1);
        }
        blockmodel.fireTableDataChanged();
    }


    public void blockInput(int block, int offset, int data) {
        for (int i = 0; i < blockmodel.getRowCount(); i++)
        {
            String temp = Integer.toString(block) + "/" + Integer.toString(offset);
            String blocktemp = (String) blockmodel.getValueAt(i, 0);
            if(temp.equals(blocktemp))
            {
                temp = Integer.toString(data);
                blockmodel.setValueAt(temp,i,1);
            }
        }
    }

    public void blockSet(int block, int offset, int size)
    {
        int j = offset;
        int hit = 0;
        for (int i = 0;(hit < size) && (i < blockmodel.getRowCount()); i++)
        {
            String nul = "";
            String blocktemp = (String) blockmodel.getValueAt(i, 0);
            if(nul.equals(blocktemp))
            {
                nul = Integer.toString(block) + "/" + Integer.toString(j++);
                hit++;
                blockmodel.setValueAt(nul,i,0);
            }
            else if(blocktemp.substring(0, 1).equals(Integer.toString(block))) {
                if (blockmodel.getValueAt(i, 0).equals("")) {
                    blockmodel.removeRow(blockmodel.getRowCount() - 1);
                }
            }
        }
    }
    public void blockSet1(int size) {
        for (int i = 0; i < size; i++) {
            String[] temp = {""};
            blockmodel.addRow(temp);
        }
    }

    public int blockFind(int block, int offset)
    {
        for(int i = 0; i < blockmodel.getRowCount(); i++)
        {
            String temp = (String) blockmodel.getValueAt(i,0);
            if(temp.equals(block+"/"+offset))
            {
                return i+1;
            }
        }
        return -1;
    }

    public Pair blockInAddr(int Addr, int temp)
    {
        blockmodel.setValueAt(temp,Addr,1);
        String pos = (String) blockmodel.getValueAt(Addr,0);
        String[] spos = pos.split("/");
        return new Pair(Integer.parseInt(spos[0]),Integer.parseInt(spos[1]));
    }
    public void nowPos(int prev,int now)
    {
        codemodel.setValueAt("",prev,0);
        codemodel.setValueAt("NOW",now,0);
        codemodel.fireTableDataChanged();
    }
    private void clear()
    {
        document.clear();
        removeall(codemodel);
        removeall(blockmodel);
        removeall(stackmodel);
        removeall(stackMmodel);
    }

    private void removeall(DefaultTableModel model)
    {
        for(int i = 0; i < model.getRowCount();)
        {
            model.removeRow(i);
        }
        model.fireTableRowsDeleted(0, model.getRowCount());
    }
}

