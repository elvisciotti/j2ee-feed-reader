package twi518.conf;

/**
 * @author Elvis
 */
public class Config {
   
    /*** PARAMETRI SI-TOMCAT  ******/
    private static final String SERVERADD = "si-tomcat.csr.unibo.it"; /* indirizzo DNS del server */
    private static final String APPDIR = "/usr/java/homeaccess/nethomes/STUDENTI/elvis.ciotti/application"; /* directory di deploy progetto application */
    private static final String WEBDIR = "/usr/java/homeaccess/nethomes/STUDENTI/elvis.ciotti/public_webapp"; /* directory di deploy progetto web */ 
    
    /********** PARAMETRI LOCALHOST **********/
    //private static final String SERVERADD = "localhost";
    //private static final String WEBDIR = "e:\\documenti\\UNIV\\twi\\progetto\\elvis-ugo\\public_webapp\\web";
    //private static final String APPDIR = "e:\\documenti\\UNIV\\twi\\progetto\\elvis-ugo\\application";
    //private static final boolean DEBUG = true;
  
    /******  PARAMETRI COMUNI *****/
    private static final int WEBPORT = 8080; /* numero di porta der server web tomcat */
    private static final String CONTEXTADD = "~elvis.ciotti"; /* indirizzo del vostro context */
    private static final int APPPORT = 2000 + 518;    
     
    public static String getSERVERADD() {return SERVERADD;}
    public static int getWEBPORT() {return WEBPORT;}
    public static String getCONTEXTADD() {return CONTEXTADD;}         
    public static String getWEBDIR() {return WEBDIR;}
    public static int getAPPPORT() {return APPPORT;}
    public static String getAPPDIR() {return APPDIR;}

}