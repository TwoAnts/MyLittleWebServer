package hzy.func;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateFunc {
    
    private static SimpleDateFormat dateFormat = null;
    
    public static String getDateStr(Date date){
        if(null == dateFormat){
            dateFormat = new SimpleDateFormat("EEE,d MMM yyyy hh:mm:ss", Locale.ENGLISH);
        }
        return dateFormat.format(date) + " GMT";
    }

}
