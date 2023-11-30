import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class positionNode
{
    private positionNode prevNode;
    private int nowLine;
    private int varNum;
    private int blockNum;
    private positionNode nextNode;

    public void setNowLine(int a)
    {
        nowLine = a;
    }
    public int getNowLine()
    {
        return nowLine;
    }
    public positionNode(positionNode prev, int line)
    {
        prevNode = prev;
        nowLine = line;
    }

    public positionNode()
    {

    }

    public void setVarNum(int varNum) {
        this.varNum = varNum;
    }

    public int getVarNum() {
        return varNum;
    }

    public positionNode getPrevNode() {
        return prevNode;
    }

    public void next(positionNode next)
    {
        nextNode = next;
    }

    public void delNext()
    {
        nextNode = null;
    }
    public void setBlockNum(int a){blockNum = a; }
    public int getBlockNum(){return blockNum;}
}

class Pair {
    private int block;
    private int offset;

    public Pair(int b, int o)
    {
        this.block = b;
        this.offset = o;
    }

    public int getOffset()
    {
        return offset;
    }

    public int getBlock(){
        return block;
    }
}

public class Doc {
    private static View v;

    private String[][] sndarr;

    private HashMap<String, Integer> labelMap = new HashMap<>();// 라벨의 위치를 확인하는 해쉬맵
    private HashMap<String, Integer> oprUse = new HashMap<>(); // 명령어의 사용횟수를 파악하는 해쉬맵

    private List<String> programStruct = List.of(new String[]{"nop", "bgn", "sym", "end"});
    private List<String> functionOpr= List.of(new String[]{"proc","ret","ldp","push","call"});
    private List<String> inoutProOpr = List.of(new String[]{"read", "write", "lf"});
    private List<String> dataMove = List.of(new String[]{"lod","lda","ldc","str","ldi","sti"});
    private List<String> unary = List.of(new String[]{"not","neg","inc","dec","dup"});
    private List<String> binary = List.of(new String[]{"add","sub","mult","div","mod","gt","lt","ge","le","eq","ne","and","or","swp"});
    private List<String> flowOpr = List.of(new String[]{"ujp","tjp","fjp"});
    private List<String> operator = new ArrayList<>();

    private Stack<Integer> stack = new Stack<>();
    private Stack<Integer> mstack = new Stack<>();
    private positionNode now = new positionNode();
    private HashMap<String,Integer> blocks = new HashMap<String, Integer>();

    private boolean oneStep = false;

    private int pc = 0;
    public Doc()
    {
        operator.addAll(programStruct);
        operator.addAll(functionOpr);
        operator.addAll(inoutProOpr);
        operator.addAll(dataMove);
        operator.addAll(unary);
        operator.addAll(binary);
        operator.addAll(flowOpr);
        now.setBlockNum(1);
    }

    public void addView (View temp)
    {
        v = temp;
    }
    public boolean Check(String target) // 파일이 유코드인지 확인후 맞다면 true를 반환한다.
    {
        if((target.length()-4)==target.indexOf(".uco")) return true;
        else return false;
    }

    public void turnOne()
    {
        oneStep = true;
    }
    public void turnOneF()
    {
        oneStep = false;
    }
    public String[][] lineReader(String path)  { // 라인을 한개씩 읽어 Arraylist에 넣는 작업
        ArrayList<String> result = new ArrayList<>();
        if(!Check(path))
        {
            return null;
        }
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String input = br.readLine();
            while (input != null) {
                result.add(input);
                input = br.readLine();
            }
        }
        catch(FileNotFoundException e) {}
        catch(IOException e) {}

        String[] fstarr = new String[result.size()];
        result.toArray(fstarr);
        sndarr = lineSegement(fstarr);
        return sndarr;
    }

    public String[][] lineSegement(String[] Paragraph) // 라인별로 하나하나씩 분석및 분류하여 2차원배열로 작성하는 함수
    {
        int size = Paragraph.length;
        String[][] result = new String[size][6];

        for(int i = 0; i < size; i++)
        {
            String label = Paragraph[i].substring(0,11);
            label = label.trim();
            if(!label.equals(""))
            {
                result[i][1] = label;
                labelMap.put(label,i);
            }

            String sub = Paragraph[i].substring(11);
            String[] temp = sub.split(" ");


            for(int j = 0; (j < 4) && (j < temp.length); j++)
            {
                if((j == 0 )&&temp[j].equals("bgn"))
                {
                    pc = i;
                    result[i][0] = "NOW";
                }
                if((j == 0) && !isOpr(temp[j])) // 명령어 자리에 명령어가 있지 않음
                {
                    return null;
                }
                if(temp[j].equals("%"))
                {
                    break;
                }
                result[i][j+2] = temp[j];
            }
        }
        return result;
    }

    private boolean isOpr(String temp)
    {
        if(operator.contains(temp))
        {

            return  true;
        }
        return false;
    }

    public String process() // 명령어의 종류를 인식하고 분류하여 작동시키는 함수
    {
        int prev = pc;
        String result = new String();
        boolean go = true;
        while(go)
        {
            try{
                int k = oprUse.get(sndarr[pc][2]); //명령어의 사용회수를 체크하는 맵에 추가
                oprUse.replace(sndarr[pc][2],++k);
            }
            catch (NullPointerException e)
            {
                oprUse.put(sndarr[pc][2],0);
            }

            if(programStruct.contains(sndarr[pc][2]))// 프로그램 구성 명령어 인식 및 처리
            {
                go = programStructProcess(sndarr[pc]);
                pc++;
            }
            else if (functionOpr.contains(sndarr[pc][2]))
            {
                pc = functionOprProcess(sndarr[pc],pc);
            }
            else if (dataMove.contains(sndarr[pc][2]))
            {
                dataMoveProcess(sndarr[pc]);
                pc++;
            }
            else if (unary.contains(sndarr[pc][2]))
            {
                unaryProcess(sndarr[pc]);
                pc++;
            }
            else if (binary.contains(sndarr[pc][2]))
            {
                binaryProcess(sndarr[pc]);
                pc++;
            }
            else if (flowOpr.contains(sndarr[pc][2]))
            {
                pc = flowOprProcess(sndarr[pc]);
            }
            if(pc < 0)
            {
                JOptionPane.showConfirmDialog(v.mainF,"오류가 발생하였습니다.");
                return null;
            }
            if(oneStep)
            {
                go = false;
            }

        }
        v.nowPos(prev,pc);
        return result;
    }

    private void dataMoveProcess(String[] strings)
    {
        int temp;
        int block;
        int offset;
        switch(strings[2])
        {
            case "lod":
                temp = blocks.get(strings[3]+strings[4]);
                stack.push(temp);
                v.stackAdd(temp);
                break;
            case "lda":
                block = Integer.parseInt(strings[3]);
                offset = Integer.parseInt(strings[4]);
                stack.push(v.blockFind(block,offset));
                v.stackAdd(stack.peek());
                break;
            case "ldc":
                temp = Integer.parseInt(strings[3]);
                stack.push(temp);
                v.stackAdd(temp);
                break;
            case "str":
                block = Integer.parseInt(strings[3]);
                offset = Integer.parseInt(strings[4]);
                temp = stack.pop();
                v.stackPopDel();
                String inputkey = strings[3] + strings[4];
                blocks.put(inputkey,temp);
                v.blockInput(block,offset,temp);
                break;
            case "ldi":
                break;
            case "sti":
                int data = stack.pop();
                int Addr = stack.pop();
                Pair pos = v.blockInAddr(Addr,data);
                break;
        }
    }

    private void unaryProcess(String[] strings)// 단항 연산자
    {
        switch(strings[2])
        {
            case "not":
                if(stack.pop() == -1) stack.push(0);
                else if(stack.pop() == 0) stack.push(-1);
                v.stackPopDel();
                v.stackAdd(stack.peek());
                break;
            case "neg":
                stack.push(-stack.pop());
                v.stackPopDel();
                v.stackAdd(stack.peek());
                break;
            case "inc":
                stack.push(stack.pop()+1);
                v.stackPopDel();
                v.stackAdd(stack.peek());
                break;
            case "dec":
                stack.push(stack.pop()-1);
                v.stackPopDel();
                v.stackAdd(stack.peek());
                break;
            case "dup":
                stack.push(stack.peek());
                v.stackAdd(stack.peek());
                break;
        }
    }

    private void binaryProcess(String[] strings) //이항 연산자
    {
        int first = stack.pop();
        v.stackPopDel();
        int second = stack.pop();
        v.stackPopDel();
        switch(strings[2])
        {
            case "add":
                stack.push(second + first);
                v.stackAdd(stack.peek());
                break;
            case "sub":
                stack.push(second - first);
                v.stackAdd(stack.peek());
                break;
            case "mult":
                stack.push(second * first);
                v.stackAdd(stack.peek());
                break;
            case "div":
                stack.push(second / first);
                v.stackAdd(stack.peek());
                break;
            case "mod":
                stack.push(second % first);
                v.stackAdd(stack.peek());
                break;
            case "gt":
                if(first < second)
                {
                    stack.push(-1);
                }
                else
                {
                    stack.push(0);
                }
                v.stackAdd(stack.peek());
                break;
            case "lt":
                if(first > second)
                {
                    stack.push(-1);
                }
                else
                {
                    stack.push(0);
                }
                v.stackAdd(stack.peek());
                break;
            case "ge":
                if(first <= second)
                {
                    stack.push(-1);
                }
                else
                {
                    stack.push(0);
                }
                v.stackAdd(stack.peek());
                break;
            case "le":
                if(first >= second)
                {
                    stack.push(-1);
                }
                else
                {
                    stack.push(0);
                }
                v.stackAdd(stack.peek());
                break;
            case "eq":
                if(first == second)
                {
                    stack.push(-1);
                }
                else
                {
                    stack.push(0);
                }
                v.stackAdd(stack.peek());
                break;
            case "ne":
                if(first != second)
                {
                    stack.push(-1);
                }
                else
                {
                    stack.push(0);
                }
                v.stackAdd(stack.peek());
                break;
            case "and":
                if(first == second & (first==-1))
                {
                    stack.push(-1);
                }
                else
                {
                    stack.push(0);
                }
                v.stackAdd(stack.peek());
                break;
            case "or":
                if(first == 0 & second == 0)
                {
                    stack.push(0);
                }
                else
                {
                    stack.push(-1);
                }
                v.stackAdd(stack.peek());
                break;
            case "swp":
                stack.push(first);
                v.stackAdd(stack.peek());
                stack.push(second);
                v.stackAdd(stack.peek());
                break;
        }
    }

    private int flowOprProcess(String[] strings) //흐름 제어 명령어 처리함수
    {
        switch(strings[2])
        {
            case "ujp":
                return labelMap.get(strings[3]);
            case "tjp":
                v.stackPopDel();
                if(stack.pop() == -1 )

                    return labelMap.get(strings[3]);
            case "fjp":
                v.stackPopDel();
                if(stack.pop() == 0 )
                    return labelMap.get(strings[3]);
                else
                {
                    return pc+1;
                }
        }
        return -1;
    }

    private boolean programStructProcess(String[] strings) //프로그램 구성 명령어 처리함수
    {
        switch(strings[2])
        {
            case "nop":
                //아무것도 안하기
                break;
            case "bgn":
                v.blockSet1(Integer.parseInt(strings[3]));
                break;
            case "sym":
                if(mstack.isEmpty()) {
                    v.blockSet(Integer.parseInt(strings[3]), Integer.parseInt(strings[4]), Integer.parseInt(strings[5]));
                }
                else
                {
                    v.blockSet(Integer.parseInt(strings[3]), Integer.parseInt(strings[4]), Integer.parseInt(strings[5]));
                    v.blockInput(Integer.parseInt(strings[3]), Integer.parseInt(strings[4]),mstack.peek());
                    blocks.replace(strings[3]+strings[4],mstack.pop());
                    v.mstackPopDel();
                }
                break;
            case "end"://어셈블리 프로그램의 끝을 나타내는 코드
                v.buttonOff();
                return false;
        }
        return true;
    }

    private int functionOprProcess(String[] strings, int pc) // 함수 정의 및 호출 명령어 처리함수 미완성!!!!!!!!!!!!!!!!!
    {
        switch(strings[2])
        {
            case "proc":
                int temp = Integer.parseInt(strings[3]);
                now.setVarNum(temp);
                v.blockSet1(Integer.parseInt(strings[3]));
                return pc+1;
            case "ret":
                now = now.getPrevNode();
                now.delNext();
                temp = now.getNowLine();
                temp++;
                return temp;
            case "ldp":// 함수 호출 준비 명령 하는일 설정 없음
                return pc+1;
            case "push":
                mstack.push(stack.pop());
                v.stackPopDel();
                v.mstackAdd(mstack.peek());
                return pc+1;
            case "call":
                if(inoutProOpr.contains(strings[3]))
                {
                    systemCall(strings[3]);
                    return pc+1;
                }
                positionNode node = new positionNode(now,pc);
                now.setNowLine(pc);
                now.next(node);
                now = node;
                now.setBlockNum(now.getPrevNode().getBlockNum()+1);
                return labelMap.get(strings[3]);
        }
        return pc;
    }

    private void systemCall(String call)
    {
        switch (call)
        {
            case "lf":
                v.lf();
                break;
            case "write":
                v.write(mstack.pop());
                v.mstackPopDel();
                break;
            case "read":
                String temp = JOptionPane.showInputDialog("숫자를 입력하세요");
                int a = mstack.pop();
                v.mstackPopDel();
                Pair dpair = v.blockInAddr(a,Integer.parseInt(temp));
                blocks.put(""+dpair.getBlock()+dpair.getOffset(),Integer.parseInt(temp));
                break;
        }
    }
    public void clear()
    {
        stack.clear();
       mstack.clear();
       now = new positionNode();
       now.setBlockNum(1);
       blocks.clear();
    }
}
