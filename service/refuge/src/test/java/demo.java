import java.text.SimpleDateFormat;
import java.util.Date;

public class demo {

    public static void main(String[] args) {
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        while (true){
            //Date date = new Date(System.currentTimeMillis());
            System.out.println(new Date().getTime());
        }
    }
}
