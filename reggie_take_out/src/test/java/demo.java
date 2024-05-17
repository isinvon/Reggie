import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

/**
 * @author: Lin
 * @Date: 2023-04-22 15:02
 **/
public class demo {
    @Test
    public void test1() {
        String path = "";
        File file = new File(path);
        if (file.exists()) {
            System.out.println("1:"+file.exists());
        }
        //}else {
        //    System.out.println(file.exists());
        //}

        if (!file.exists()) {
            System.out.println("2:"+file.exists());
        }
        //}else {
        //    System.out.println(file.exists());
        //}
        //System.out.println(s.substring(s.lastIndexOf(".")));
    }
}
