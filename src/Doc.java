import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Doc {

    private String filePath;
    private static View v;

    private String[][] sndarr;

    private HashMap<String,Integer> labelMap = new HashMap<>();
    private String[] operator = {"nop","bgn","sym","end","proc","ret","ldp","push","call","lod","lda","ldc","str","ldi","sti","not","neg","inc","dec","dup","add","sub","mult","div","mod","gt","lt","ge","le","eq","ne","and","or","swp","ujp","tjp","fjp"};

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
        String[][] sndarr = lineSegement(fstarr);
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
        List<String> strList = new ArrayList<>(Arrays.asList(operator));
        if(strList.contains(temp))
        {
            return  true;
        }
        return false;
    }


}
