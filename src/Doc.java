import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Doc {

    String filePath;
    public Doc()
    {
    }

    public boolean Check(String target) // 파일이 유코드인지 확인후 맞다면 true를 반환한다.
    {

        if((target.length()-4)==target.indexOf(".uco")) return true;
        else return false;
    }

    public String[] lineReader(String path)  {
        if(!Check(path))
        {
            return null;
        }
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(path));
        }
    }


}
