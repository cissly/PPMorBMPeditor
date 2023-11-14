import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Doc {

    private String filePath;
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
        String result = new String();
        int i = 0;
        boolean go = true;
        while(go)
        {
            if(programStruct.contains(sndarr[i][1]))
            {

            }
            else if (functionOpr.contains(sndarr[i][1]))
            {
                functionOprprocess(sndarr[i]);
            }
            else if (inoutProOpr.contains(sndarr[i][1]))
            {

            }
            else if (dataMove.contains(sndarr[i][1]))
            {

            }
            else if (unary.contains(sndarr[i][1]))
            {

            }
            else if (binary.contains(sndarr[i][1]))
            {

            }
            else if (flowOpr.contains(sndarr[i][1]))
            {

            }
        }
        return result;
    }

    private void functionOprprocess(String[] strings) {
        switch(strings[1])
        {
            case "proc":
                int temp = Integer.parseInt(strings[2]);
                v.blockAdd(temp);
                break;
            case "ret":
                break;
            case "push":
                break;
        }
    }
}
