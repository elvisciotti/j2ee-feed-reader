package twi518.userXmlFactory;
import java.util.*;
/**
 * controlla che la stringa dell'username sia ammissibile
 * @author Elvis
 */
public class StringControl {
    
    /**
     * Creates a new instance of StringControl
     */
    public StringControl() {}
    public boolean goodString(String str){
        boolean b = true;
        char[] stringa = str.toCharArray();
        for(int i=0; i<stringa.length; i++){
            if ((stringa[i]== '�')||
                    (stringa[i]== '�')||
                    (stringa[i]== '�')||
                    (stringa[i]== '�')||
                    (stringa[i]== ' ')||
                    (stringa[i]== '�'))
                b = false;
        }
        return b;
    }
}
