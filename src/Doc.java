import javax.swing.*;
import java.io.*;
import java.util.*;

//함수가 호출될경우 돌아갈값과 변수들의 저장을 설정하기 위해 만들어진 클래스 positionNode
// 각인자의 getter setter가 만들어져 있습니다.
class positionNode
{
    private positionNode prevNode;
    private int nowLine;
    private String Labelname;
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

    public void setLabelname(String temp) {
        this.Labelname = temp;
    }

    public String getLabelname() {
        return Labelname;
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

//한번에 2개의 값을 돌려받기 위해 만들어진 클래스
// Getter만 만들어져 있습니다.
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
    private List<String> flowOpr = List.of(new String[]{"ujp","tjp","fjp"}); // 흐름제어 명령어 모음
    private List<String> operator = new ArrayList<>();

    private Stack<Integer> stack = new Stack<>(); // CPU스택
    private Stack<Integer> mstack = new Stack<>(); // 메모리스택
    private positionNode now = new positionNode(); // 함수의 정보를 나타내는 포지션노드
    private HashMap<String,Integer> blocks = new HashMap<String, Integer>(); // 블럭

    private ArrayList<String> labellist = new ArrayList<>(); // 어떤 라벨이 실제로 사용되었는지 보여주는 배열
    private boolean oneStep = false; //한단계씩 진행할것인지 알려주는 변수
    private String output = "";

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
        return (target.length() - 4) == target.indexOf(".uco");
    }

    public void turnOne()
    {
        oneStep = true;
    } //한단계씩 진행 켜기
    public void turnOneF()
    {
        oneStep = false;
    } //한단계씩 진행 끄기
    public String[][] lineReader(String path)  // 라인을 한개씩 읽어 Arraylist에 넣는 작업
    {
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
            if(!label.isEmpty())
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
        return operator.contains(temp);
    } // 명령어인지 구분하는 함수

    public void clear() // 새로운 코드를 읽기전 이전 정보를 비우는 함수
    {
        stack.clear();
        mstack.clear();
        now = new positionNode();
        now.setBlockNum(1);
        blocks.clear();
    }

    private int popStackAndDel()  // CPU 스택의 Pop 및 뷰에 반영
    {
        int value = stack.pop();
        v.stackPopDel();
        return value;
    }

    private int popMStackAndDel() // 메모리 스택의 Pop 및 뷰에 반영
    {
        int value = mstack.pop();
        v.mstackPopDel();
        return value;
    }

    private void pushAndAddToView(int value)  //CPU 스택에 Push 및 뷰에 반영
    {
        stack.push(value);
        v.stackAdd(value);
    }

    private void mpushAndAddToView(int i)  //메모리 스택에 Push 및 뷰에 반영
    {
        mstack.push(i);
        v.mstackAdd(i);
    }

    private void blockRetunRemove(String labelname) //함수를 리턴할때 그 함수안에 만들어진 블럭들을 모두 삭제하는 함수 및 뷰에 반영
    {
        ArrayList<String> keyArray = new ArrayList<>(blocks.keySet());
        for (String key : keyArray) {
            int leng = now.getLabelname().length();
            if(key.length() < leng)
            {
                continue;
            }
            String a= key.substring(key.length()-leng);
            if (a.equals(labelname))
            {
                blocks.remove(key);
            }
        }
    }

    public void resultMaker()
    {
        String result = "-------------------result--------------------";
        String count = "------------------oprCount-------------------";
        String end = "---------------------END---------------------";
        ArrayList<String> opr = new ArrayList<>(oprUse.keySet());
        ArrayList<String> resultArray = new ArrayList<>();
        String filePath = "output.txt";
        for (String s : opr) {
            resultArray.add(String.format("%-6s : %-4d", s, oprUse.get(s)));
        }

        try {
            FileWriter fileWriter = new FileWriter(filePath);

            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write(result);

            bufferedWriter.write(output);

            bufferedWriter.write(count);

            for(String s : resultArray)
            {
                bufferedWriter.write(s);
            }
            bufferedWriter.write(end);
            bufferedWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String process() // 명령어의 종류를 인식하고 분류하여 작동시키는 함수
    {
        int prev = pc;
        String result = null;
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
            else if (functionOpr.contains(sndarr[pc][2]))// 함수정의 및 호출 명령어 인식후 처리함수에 넘기기
            {
                pc = functionOprProcess(sndarr[pc],pc);
            }
            else if (dataMove.contains(sndarr[pc][2]))// 데이터 이동 명령어 인식후 처리함수에 넘기기
            {
                dataMoveProcess(sndarr[pc]);
                pc++;
            }
            else if (unary.contains(sndarr[pc][2])) // 단항연산자 명령어 인식후 처리함수에 넘기기
            {
                unaryProcess(sndarr[pc]);
                pc++;
            }
            else if (binary.contains(sndarr[pc][2])) // 이항연산자 명령어 인식후 처리함수에 넘기기
            {
                binaryProcess(sndarr[pc]);
                pc++;
            }
            else if (flowOpr.contains(sndarr[pc][2])) //흐름제어 명령어 인식후 처리함수에 넘기기
            {
                pc = flowOprProcess(sndarr[pc]);
            }
            if(pc < 0) // 오류를 처리하기위해 만들어진 if문
            {
                JOptionPane.showConfirmDialog(v.mainF,"오류가 발생하였습니다.");
                return null;
            }
            if(oneStep) // 한단계식 실행을 시킬때 중지시킬 if문
            {
                go = false;
            }

        }
        v.nowPos(prev,pc);
        return result;
    }

    private void dataMoveProcess(String[] strings)// 데이터 이동 연산자 처리함수
    {
        int temp;
        int block;
        int offset;
        switch(strings[2])
        {
            case "lod":
                try {
                    temp = blocks.get(strings[3] + strings[4] + now.getLabelname());
                }
                catch(NullPointerException e)
                {
                    temp = blocks.get(strings[3] + strings[4] + "null");
                }
                pushAndAddToView(temp);
                break;
            case "lda":
                block = Integer.parseInt(strings[3]);
                offset = Integer.parseInt(strings[4]);
                pushAndAddToView(v.blockFind(block,offset, now.getLabelname()));
                break;
            case "ldc":
                temp = Integer.parseInt(strings[3]);
                pushAndAddToView(temp);
                break;
            case "str":
                block = Integer.parseInt(strings[3]);
                offset = Integer.parseInt(strings[4]);
                temp = popStackAndDel();
                String inputkey = strings[3] + strings[4] + now.getLabelname();
                blocks.put(inputkey,temp);
                v.blockInput(block,offset,temp, now.getLabelname());
                break;
            case "ldi":
                int a = popStackAndDel();
                a = v.blockIndex(a);
                pushAndAddToView(a);
                break;
            case "sti":
                int data = popStackAndDel();
                int Addr = popStackAndDel();
                Pair pos = v.blockInAddr(Addr,data,now.getLabelname());
                if(blocks.containsKey(""+pos.getBlock()+ pos.getOffset()+now.getLabelname()))
                {
                    blocks.replace(""+pos.getBlock()+ pos.getOffset()+now
                            .getLabelname(),data);
                }
                else
                {
                    blocks.put(""+pos.getBlock()+ pos.getOffset()+now.getLabelname(),data);
                }
                break;
        }
    }

    private void unaryProcess(String[] strings)// 단항 연산자 처리함수
    {
        switch(strings[2])
        {
            case "not":
                if(popStackAndDel() == -1) pushAndAddToView(0);
                else if(popStackAndDel() == 0) pushAndAddToView(-1);
                break;
            case "neg":
                pushAndAddToView(-popStackAndDel());
                break;
            case "inc":
                pushAndAddToView(popStackAndDel()+1);
                break;
            case "dec":
                pushAndAddToView(popStackAndDel()-1);
                break;
            case "dup":
                pushAndAddToView(stack.peek());
                break;
        }
    }

    private void binaryProcess(String[] strings) //이항 연산자 처리함수
    {
        int first = popStackAndDel();
        int second = popStackAndDel();
        switch(strings[2])
        {
            case "add":
                pushAndAddToView(second + first);
                break;
            case "sub":
                pushAndAddToView(second - first);
                break;
            case "mult":
                pushAndAddToView(second * first);
                break;
            case "div":
                pushAndAddToView(second / first);
                break;
            case "mod":
                pushAndAddToView(second % first);
                break;
            case "gt":
                if(first < second)
                {
                    pushAndAddToView(-1);
                }
                else
                {
                    pushAndAddToView(0);
                }
                break;
            case "lt":
                if(first > second)
                {
                    pushAndAddToView(-1);
                }
                else
                {
                    pushAndAddToView(0);
                }
                break;
            case "ge":
                if(first <= second)
                {
                    pushAndAddToView(-1);
                }
                else
                {
                    pushAndAddToView(0);
                }
                break;
            case "le":
                if(first >= second)
                {
                    pushAndAddToView(-1);
                }
                else
                {
                    pushAndAddToView(0);
                }
                break;
            case "eq":
                if(first == second)
                {
                    pushAndAddToView(-1);
                }
                else
                {
                    pushAndAddToView(0);
                }
                break;
            case "ne":
                if(first != second)
                {
                    pushAndAddToView(-1);
                }
                else
                {
                    pushAndAddToView(0);
                }
                break;
            case "and":
                if(first == second & (first==-1))
                {
                    pushAndAddToView(-1);
                }
                else
                {
                    pushAndAddToView(0);
                }
                break;
            case "or":
                if(first == 0 & second == 0)
                {
                    pushAndAddToView(0);
                }
                else
                {
                    pushAndAddToView(-1);
                }
                break;
            case "swp":
                pushAndAddToView(first);
                pushAndAddToView(second);
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
                if(popStackAndDel() == -1 )
                    return labelMap.get(strings[3]);
            case "fjp":
                if(popStackAndDel() == 0 )
                    return labelMap.get(strings[3]);
                else
                    return pc+1;

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
                    v.blockSet(Integer.parseInt(strings[3]), Integer.parseInt(strings[4]), Integer.parseInt(strings[5]),now.getLabelname());
                }
                else
                {
                    v.blockSet(Integer.parseInt(strings[3]), Integer.parseInt(strings[4]), Integer.parseInt(strings[5]),now.getLabelname());
                    v.blockInput(Integer.parseInt(strings[3]), Integer.parseInt(strings[4]),mstack.peek(),now.getLabelname());
                    blocks.put(strings[3]+strings[4]+now.getLabelname(),popMStackAndDel());
                }
                break;
            case "end"://어셈블리 프로그램의 끝을 나타내는 코드
                v.buttonOff();
                return false;
        }
        return true;
    }

    private int functionOprProcess(String[] strings, int pc) // 함수 정의 및 호출 명령어 처리함수
    {
        int temp;
        switch(strings[2])
        {
            case "proc":
                v.blockSet1(Integer.parseInt(strings[3]));
                return pc+1;
            case "ret":
                v.blockDel(now.getLabelname());
                blockRetunRemove(now.getLabelname());
                now = now.getPrevNode();
                now.delNext();
                temp = now.getNowLine();
                temp++;
                return temp;
            case "ldp":// 함수 호출 준비 명령 하는일 설정 없음
                return pc+1;
            case "push":
                mpushAndAddToView(popStackAndDel());
                return pc+1;
            case "call":
                if(inoutProOpr.contains(strings[3]))
                {
                    systemCall(strings[3]);
                    return pc+1;
                }
                String Labelname = strings[3];
                int i = 1;
                while(labellist.contains(Labelname))
                {
                    Labelname = strings[3] + i;
                    i++;
                }
                now.setNowLine(pc);
                positionNode node = new positionNode(now,pc);
                node.setLabelname(Labelname);
                labellist.add(Labelname);
                now.next(node);
                now = node;
                now.setBlockNum(now.getPrevNode().getBlockNum()+1);
                return labelMap.get(strings[3]);
        }
        return pc;
    }

    private void systemCall(String call) // 입출력처리 함수
    {
        switch (call)
        {
            case "lf":
                v.lf();
                break;
            case "write":
                int out =popMStackAndDel();
                v.write(out);
                output += out;
                break;
            case "read":
                String temp = JOptionPane.showInputDialog("숫자를 입력하세요");
                int a = popMStackAndDel();
                Pair dpair = v.blockInAddr(a,Integer.parseInt(temp),now.getLabelname());
                blocks.put(""+dpair.getBlock()+dpair.getOffset()+now.getLabelname(),Integer.parseInt(temp));
                break;
        }
    }
}
