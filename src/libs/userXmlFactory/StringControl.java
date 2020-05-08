package twi518.userXmlFactory;

import java.util.*;

public class StringControl {
    public StringControl() {
    }

    public boolean goodString(String str) {
        boolean b = true;
        char[] stringa = str.toCharArray();
        for (int i = 0; i < stringa.length; i++) {
            if ((stringa[i] == '�') ||
                    (stringa[i] == '�') ||
                    (stringa[i] == '�') ||
                    (stringa[i] == '�') ||
                    (stringa[i] == ' ') ||
                    (stringa[i] == '�'))
                b = false;
        }
        return b;
    }
}
