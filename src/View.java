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

    // 상호작용을 위한 DOC클래스
    private Doc document;
    public JPanel mainF;


    //
    //테이블에 보여주는 코드가 길어질때 확장을 위한 스크롤 클래스들
    //
    private JScrollPane block;
    private JScrollPane code;
    private JScrollPane stack;
    private JScrollPane mStack;
    private JScrollPane result;

    //
    //각 뷰들의 이름을 보여주기위한 라벨들
    //
    private JLabel tagblock = new JLabel("BLOCK");
    private JLabel tagCode = new JLabel("U-Code");
    private JLabel tagStack = new JLabel("CPU-Stack");
    private JLabel tagMStack = new JLabel("Memory-Stack");
    private JLabel tagResult = new JLabel("Result");
    
    
    //
    // 버튼들
    //
    private JButton oneStep = new JButton("한단계씩 실행");
    private JButton outPut= new JButton("출력");
    private JButton Start = new JButton("실행");
    private JButton input = new JButton("입력");
    
    
    //
    // 2개의 스택과 하나의 블럭 하나의 코드테이블에 실시간으로 수정사항을 올리기위해 사용되는 클래스들 모음
    //
    private DefaultTableModel codemodel;
    private DefaultTableModel blockmodel;
    private DefaultTableModel stackmodel;
    private DefaultTableModel stackMmodel;
    
    //결과값을 보여주는 라벨
    private JLabel resultLabel;
    
    //위치조절 세부사항 세팅을 위한 클래스
    private GridBagConstraints c=new GridBagConstraints();

    public View(Doc d) //뷰의 기본세팅을 담당하는 생성자
    {
        makeStack();
        makeCodeTableAndResult();
        makeBlock();

        document = d;
        mainF = new JPanel();
        mainF.setLayout(new GridBagLayout());
        c.fill=GridBagConstraints.BOTH;


        //위치조절
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
        actionAdder();
        clear();
    }
    private void makeStack() // CPU스택과 메모리스택을 만드는 함수
    {        
        String[] stackhead = {"Data"};
        //CPU스택
        stackmodel = new DefaultTableModel(stackhead,0);
        JTable stacktable= new JTable(stackmodel);
        stack = new JScrollPane(stacktable);
        //메모리 스택
        stackMmodel = new DefaultTableModel(stackhead,0);
        JTable stackMtable= new JTable(stackMmodel);
        mStack = new JScrollPane(stackMtable);
    }
    private void makeCodeTableAndResult() // 코드테이블과 결과창을 만드는 함수
    {
        String[] header = {"Now","LABEL", "명령어", "인자1", "인자2", "인자3"};
        codemodel=new DefaultTableModel(header,0);
        JTable table= new JTable(codemodel);
        code = new JScrollPane(table);
        resultLabel = new JLabel();
        result = new JScrollPane(resultLabel);
    }
    private void makeBlock() //Block객체를 만드는 함수
    {
        String[] header1 = {"No.","DATA"};
        blockmodel=new DefaultTableModel(header1,0);
        JTable blocktable = new JTable(blockmodel);
        block = new JScrollPane(blocktable);
    }
    private void actionAdder() // 버튼에 이벤트를 추가하는 함수
    {
        input.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clear();
                String temp = load();
                String[][] lindData = document.lineReader(temp);
                UCODESet(lindData);
                mainF.invalidate();
                buttonOn();
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
        outPut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                document.resultMaker();
            }
        });
    }
    private void buttonOn() // 버튼을 활성화 시키는 함수
    {
        Start.setEnabled(true);
        oneStep.setEnabled(true);
    }
    public void buttonOff() // 버튼을 비활성화 시키는 함수 편의에 따라 출력버튼은 활성화 시켰음
    {
        Start.setEnabled(false);
        oneStep.setEnabled(false);
        outPut.setEnabled(true);
    }
    private void gbAdd(Component temp, int x, int y, int w, int h, double k, double t) // 위치설정을 도와주는 함수
    {

        c.gridx = x;
        c.gridy = y;
        c.gridwidth  = w;
        c.gridheight = h;
        c.weightx = k;
        c.weighty = t;

        mainF.add(temp,c);
    }
    public String load()// 입력을 받기위한 창을 띄우고 입력할 파일의 절대경로를 가져오는 함수
    {
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
    private void clear() //새로운 입력을 받기전 모든 뷰의 정보를 지우는 함수
    {
        document.clear();
        resultLabel.setText("");
        removeall(codemodel);
        removeall(blockmodel);
        removeall(stackmodel);
        removeall(stackMmodel);
        buttonOff();
        outPut.setEnabled(false);
    }
    private void removeall(DefaultTableModel model) // 테이블의 정보를 지우는 함수
    {
        for(int i = 0; i < model.getRowCount();)
        {
            model.removeRow(i);
        }
        model.fireTableRowsDeleted(0, model.getRowCount());
    }
    public void nowPos(int prev,int now) // 코드테이블의 어디부분을 가리키고 있는지 보여주는 함수
    {
        codemodel.setValueAt("",prev,0);
        codemodel.setValueAt("NOW",now,0);
        codemodel.fireTableDataChanged();
    }
    private void UCODESet(String[][] data) // 입력으로 들어온코드를 코드 테이블에 올리는 함수
    {
        for (String[] datum : data) {
            codemodel.addRow(datum);
        }
    }
    public void stackAdd(int a) // 뷰의 CPU스택에 PUSH
    {
        String ab = Integer.toString(a);
        String[] temp = {ab};
        stackmodel.addRow(temp);
        stackmodel.fireTableDataChanged();
    }
    public void stackPopDel() // 뷰의 CPU스택의 POP
    {
        int a =stackmodel.getRowCount();
        stackmodel.removeRow(a-1);
        stackmodel.fireTableDataChanged();
    }
    public void mstackAdd(int a) // 뷰의 메모리 스택의 PUSH
    {
        String ab = Integer.toString(a);
        String[] temp = {ab};
        stackMmodel.addRow(temp);
        stackMmodel.fireTableDataChanged();
    }
    public void mstackPopDel() // 뷰의 메모리 스택의 POP
    {
        int a =stackMmodel.getRowCount();
        stackMmodel.removeRow(a-1);
        stackMmodel.fireTableDataChanged();
    }

    //
    //블럭을 제어하기 위해 만들어진 함수들 모음
    //
    public void blockInput(int block, int offset, int data, String Label) // 특정 블럭에 값을 넣는 함수
    {
        for (int i = 0; i < blockmodel.getRowCount(); i++)
        {
            String temp = Integer.toString(block) + "/" + Integer.toString(offset) +"/" +Label;
            String blocktemp = (String) blockmodel.getValueAt(i, 0);
            if(temp.equals(blocktemp))
            {
                temp = Integer.toString(data);
                blockmodel.setValueAt(temp,i,1);
            }
        }
    }
    public void blockSet(int block, int offset, int size, String Label) // 함수정의 및 호출에서 블럭의 공간만 설정된 부분에 블럭의 번호를 넣는 함수
    {
        int j = offset;
        int hit = 0;
        for (int i = 0;(hit < size) && (i < blockmodel.getRowCount()); i++)
        {
            String nul = "";
            String blocktemp = (String) blockmodel.getValueAt(i, 0);
            if(nul.equals(blocktemp))
            {
                nul = Integer.toString(block) + "/" + Integer.toString(j++) + "/" + Label;
                hit++;
                blockmodel.setValueAt(nul,i,0);
            }
        }
    }
    public void blockSet1(int size) // 블럭의 공간만 설정하는 함수
    {
        for (int i = 0; i < size; i++) 
        {
            String[] temp = {""};
            blockmodel.addRow(temp);
        }
    }
    public int blockFind(int block, int offset,String Label) // 특정 함수(라벨)에서 만든 블럭을 찾는 함수 라벨에서 호출하지 않았다면 전역변수에서 찾는다
    {
        int sub = -1;
        for(int i = 0; i < blockmodel.getRowCount(); i++)
        {
            String temp = (String) blockmodel.getValueAt(i,0);
            if(temp.equals(block+"/"+offset+"/"+Label))
            {
                return i+1;
            }
            else if (temp.equals(block+"/"+offset+"/"+"null"))
            {
                sub =  i+1;
            }
        }
        return sub;
    }
    public int blockIndex(int a) // 블럭의 순서를 받아 그 순서에 맞는 블럭의 값을 받아오는 함수
    {
        String temp = (String) blockmodel.getValueAt(a-1,1);
        return Integer.parseInt(temp);
    }
    public Pair blockInAddr(int Addr, int temp,String Label) //블럭의 순서를 받아 그 순서에 값을 입력하는 함수
    {
        blockmodel.setValueAt(Integer.toString(temp),Addr-1,1);
        String pos = (String) blockmodel.getValueAt(Addr-1,0);
        String[] spos = pos.split("/");
        return new Pair(Integer.parseInt(spos[0]),Integer.parseInt(spos[1]));
    }
    public void blockDel(String labelname) // 특정 라벨의 이름을 가지고 있는 블럭을 삭제하는 함수
    {
        for(int i = 0; i < blockmodel.getRowCount(); i ++)
        {
            String temp = (String) blockmodel.getValueAt(i,0);
            String[] abc = temp.split("/");
            if(abc[2].equals(labelname))
            {
                blockmodel.removeRow(i);
                i--;
            }
        }
    }
    //
    //끝
    //

    public void lf()
    {
        resultLabel.setText(resultLabel.getText()+"\n");
    } // 시스템함수중 줄바꿈을 결과창에 보여주기 위한 함수
    public void write(int data)
    {
        resultLabel.setText(resultLabel.getText()+data);
    } // 시스템함수중 출력을 결과창에 보여주기 위한 함수
}

