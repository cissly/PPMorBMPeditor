import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Doc {

    String filePath;
    public static void main(String[] args)
    {
        JFrame ff = new JFrame("aaa");
        ff.setContentPane(new View().main);
        ff.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ff.setSize(500,500);
        ff.setVisible(true);
    }

    public boolean Check(String target) // 파일이 유코드인지 확인후 맞다면 true를 반환한다.
    {
        if((target.length()-4)==target.indexOf(".uco")) return true;
        else return false;
    }

    public ArrayList<String> lineReader(String path)  { // 라인을 한개씩 읽어 Arraylist에 넣는 작업
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
                // do something...
                result.add(input);
                input = br.readLine();
            }
        }
        catch(FileNotFoundException e) {}
        catch(IOException e) {}

        return result;
    }


}
