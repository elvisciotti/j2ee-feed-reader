package twi518.conf;

public class Config {
    private static final String SERVERADD = "si-tomcat.csr.unibo.it";
    private static final String APPDIR = "/usr/java/homeaccess/nethomes/STUDENTI/elvis.ciotti/application";
    private static final String WEBDIR = "/usr/java/homeaccess/nethomes/STUDENTI/elvis.ciotti/public_webapp";
    private static final int WEBPORT = 8080;
    private static final String CONTEXTADD = "~elvis.ciotti";
    private static final int APPPORT = 2000 + 518;

    public static String getSERVERADD() {
        return SERVERADD;
    }

    public static int getWEBPORT() {
        return WEBPORT;
    }

    public static String getCONTEXTADD() {
        return CONTEXTADD;
    }

    public static String getWEBDIR() {
        return WEBDIR;
    }

    public static int getAPPPORT() {
        return APPPORT;
    }

    public static String getAPPDIR() {
        return APPDIR;
    }
}