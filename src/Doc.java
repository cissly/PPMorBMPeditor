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
    private positionNode nextNode;

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
}

class Pair {
    private int block;
    private int data;

    public Pair(int x) {
        this.block = x;
    }

    public void set(int x)
    {
        this.data = x;
    }

    public int getBlock(){
        return block;
    }

    public int getData(){
        return data;
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
    private List<String> unary = List.of(new String[]{"not","neg","ind","dec","dup"});
    private List<String> binary = List.of(new String[]{"add","sub","mult","div","mod","gt","lt","ge","le","eq","ne","and","or","swp"});
    private List<String> flowOpr = List.of(new String[]{"ujp","tjp","fjp"});
    private List<String> operator = new ArrayList<>();

    private Stack<Integer> stack = new Stack<>();
    private positionNode now = new positionNode();
    private ArrayList<Pair> blocks = new ArrayList<>();
    public Doc()
    {
        operator.addAll(programStruct);
        operator.addAll(functionOpr);
        operator.addAll(inoutProOpr);
        operator.addAll(dataMove);
        operator.addAll(unary);
        operator.addAll(binary);
        operator.addAll(flowOpr);
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
        String[][] result = new String[size][5];

        for(int i = 0; i < size; i++)
        {
            String label = Paragraph[i].substring(0,11);
            label = label.trim();
            if(!label.equals(""))
            {
                result[i][0] = label;
                labelMap.put(label,i);
            }

            String sub = Paragraph[i].substring(11);
            String[] temp = sub.split(" ");

            try{
                int k = oprUse.get(temp[0]);
                oprUse.replace(temp[0],++k);
            }
            catch (NullPointerException e)
            {
                oprUse.put(temp[0],0);
            }
            for(int j = 0; (j < 4) && (j < temp.length); j++)
            {
                if((j == 0) && !isOpr(temp[j]))
                {
                    return null;
                }
                if(temp[j].equals("%"))
                {
                    break;
                }
                result[i][j+1] = temp[j];
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
        positionNode root = new positionNode();

        String result = new String();
        int pc = 0;
        boolean go = true;
        while(go)
        {
            if(programStruct.contains(sndarr[pc][1]))// 프로그램 구성 명령어 인식 및 처리
            {
                go = programStructProcess(sndarr[pc]);
            }
            else if (functionOpr.contains(sndarr[pc][1]))
            {
                functionOprProcess(sndarr[pc],pc);
            }
            else if (dataMove.contains(sndarr[pc][1]))
            {
                dataMoveProcess(sndarr[pc]);
            }
            else if (unary.contains(sndarr[pc][1]))
            {
                unaryProcess(sndarr[pc]);
            }
            else if (binary.contains(sndarr[pc][1]))
            {
                binaryProcess(sndarr[pc]);
            }
            else if (flowOpr.contains(sndarr[pc][1]))
            {
                pc = flowOprProcess(sndarr[pc]);
            }
            if(pc > 0)
            {
                JOptionPane.showConfirmDialog(v.mainF,"오류가 발생하였습니다.");
            }
        }
        return result;
    }

    private void dataMoveProcess(String[] strings)
    {
        switch(strings[1])
        {
            case "lod":

                break;
            case "lda":
                break;
            case "ldc":
                break;
            case "str":
                break;
            case "ldi":
                break;
            case "sti":
                break;
        }
    }

    private void unaryProcess(String[] strings)// 단항 연산자
    {
        switch(strings[1])
        {
            case "not":
                if(stack.pop() == -1) stack.push(0);
                else if(stack.pop() == 0) stack.push(-1);
                break;
            case "neg":
                stack.push(-stack.pop());
                break;
            case "inc":
                stack.push(stack.pop()+1);
                break;
            case "dec":
                stack.push(stack.pop()-1);
                break;
            case "dup":
                stack.push(stack.peek());
                break;
        }
    }

    private void binaryProcess(String[] strings) //이항 연산자
    {
        int first = stack.pop();
        int second = stack.pop();
        switch(strings[1])
        {
            case "add":
                stack.push(second + first);
                break;
            case "sub":
                stack.push(second - first);
                break;
            case "mult":
                stack.push(second * first);
                break;
            case "div":
                stack.push(second / first);
                break;
            case "mod":
                stack.push(second % first);
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
                break;
            case "swp":
                stack.push(first);
                stack.push(second);
                break;
        }
    }

    private int flowOprProcess(String[] strings) //흐름 제어 명령어 처리함수
    {
        switch(strings[1])
        {
            case "ujp":
                return labelMap.get(strings[2]);
            case "tjp":
                if(stack.pop() == -1 )
                    return labelMap.get(strings[2]);
            case "fjp":
                if(stack.pop() == 0 )
                    return labelMap.get(strings[2]);
        }
        return -1;
    }

    private boolean programStructProcess(String[] strings) //프로그램 구성 명령어 처리함수
    {
        switch(strings[1])
        {
            case "nop":
                //아무것도 안하기
                break;
            case "bgn":
                int temp = Integer.parseInt(strings[2]);
                v.blockAdd(temp);
                break;
            case "sym":
                //실제로는 사용되지 않고 인간의 이해를 돕는 코드
                break;
            case "end"://어셈블리 프로그램의 끝을 나타내는 코드
                return false;
        }
        return true;
    }

    private void functionOprProcess(String[] strings, int pc) // 함수 정의 및 호출 명령어 처리함수 미완성!!!!!!!!!!!!!!!!!
    {
        switch(strings[1])
        {
            case "proc":
                int temp = Integer.parseInt(strings[2]);
                v.blockAdd(temp);
                now.setVarNum(temp);
                break;
            case "ret":
                v.blockDel(now.getVarNum());
                now = now.getPrevNode();
                now.delNext();
                break;
            case "push":
                break;
            case "call":
                positionNode node = new positionNode(now,pc);
                now.next(node);
                now = node;
                break;
        }
    }
}
