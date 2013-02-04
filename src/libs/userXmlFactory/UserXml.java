package twi518.userXmlFactory;

import twi518.conf.*;
/** Classe per gestire operazioni su file utenti.xml
 * @author Elvis
 */
import java.io.*;
import java.util.*;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import org.w3c.dom.Element;
import org.xml.sax.*;

import javax.xml.parsers.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;


/**
 * @author Elvis
 */
public class UserXml {
    
    private Document document;
    Node root;
    Element nodoRoot;
    private static Config conf = new Config();
    public Crypting crypting = new Crypting();
    private final String USER_XML_PATH = new String(conf.getWEBDIR() +  "/xml/utenti.xml");
    private final String REG_USER = "utente";
    private final String USER = "username";
    private final String PWD = "password";
    private final String NOME = "nome";
    private final String COGNOME = "cognome";
    
    
    
    private final String DTD_PATH = new String(conf.getWEBDIR() + "/xml-types/utenti.dtd");
    
    public String errore = "["+USER_XML_PATH+"]<br>["+DTD_PATH+"]<br>";
    
    /** Creates a new instance of UserXml */
    public  UserXml() {
        synchronized(this){
           
            try{
                 DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setValidating(true);
                DocumentBuilder builder = factory.newDocumentBuilder();
                //builder.setErrorHandler( new MyErrorHandler());
                    File f = new File(USER_XML_PATH);
                document = builder.parse(f);
                root = document.getDocumentElement();
            } catch(FileNotFoundException fne){
                //System.err.println(" File ("+USER_XML_PATH+") not found ");
                errore += fne.toString();
//System.exit(1);
            } catch(SAXParseException spe){
                //System.err.println(" Parse Error: " + spe.getMessage());
                errore += spe.toString();
//System.exit(1);
            } catch(SAXException se){
                errore += se.toString();
                //se.printStackTrace();
            } catch(Exception e){
                errore += e.toString();
                //e.printStackTrace();
            }
            
            errore += "noderootvalue=root.getNodeValue()<br>" + root.getNodeValue();
        }
    }
    /**
     *  Cerca il matching fra nome utente e password
     *	@param user Username da cercare
     *  @param pwd Password da cercare
     *	@return Un vettore v di 3 stringhe che contiene la stringa "0" v[0]
     *	se non c'� match, il nome e il cognome dell'utente in v[1] e v[2] altrimenti
     */
    public String[] findUser(String user, String pwd){
        errore += "findUser con " + user + "-" + pwd;
        String[] user_data = new String[2];
        
        String u, p, pwd_crypt = crypting.getMd5(pwd);
        NamedNodeMap attributi;
        if(root.getNodeType() == Node.ELEMENT_NODE){
            Element utenti_registrati = (Element) root;
            NodeList utenti = utenti_registrati.getElementsByTagName(REG_USER);
            if(utenti.getLength() != 0){
                int i = 0;
                do{
                    attributi = utenti.item(i).getAttributes();
                    u = attributi.getNamedItem(USER).getNodeValue();
                    p = attributi.getNamedItem(PWD).getNodeValue();
                    if((u.compareTo(user)==0)&&(p.compareTo(pwd_crypt)==0)){
                        user_data[0]=attributi.getNamedItem(COGNOME).getNodeValue();
                        user_data[1]=attributi.getNamedItem(NOME).getNodeValue();
                        break;
                    }else{user_data[0]="0";}
                    i++;
                }
                while(i < utenti.getLength());
            }
        }
        return user_data;
    }
    /**
     *  Controlla se l'username � gi� stato utilizzato
     *	@param user Username da cercare
     *	@return True se � gi� presente, false altrimenti
     */
    public boolean doubleUsername(String user){
        System.out.print("cerco user");
        String u;
        boolean b = false;
        NamedNodeMap attributi;
        if(root.getNodeType() == Node.ELEMENT_NODE){
            Element utenti_registrati = (Element)root;
            NodeList utenti = utenti_registrati.getElementsByTagName(REG_USER);
            if(utenti.getLength() != 0){
                int i = 0;
                do{
                    attributi = utenti.item(i).getAttributes();
                    u = attributi.getNamedItem(USER).getNodeValue();
                    //System.out.println(u+"=="+user);
                    if(u.compareTo(user) == 0){
                        b = true;
                        break;
                    }
                    i++;
                }
                while(i < utenti.getLength());
            }
        }
        return b;
    }
    /**
     *  Aggiunge un nuovo utente
     *	@param user Username dell'utente da inserire
     *  @param pwd Password dell'utente da inserire
     *	@param nome Nome dell'utente da inserire
     *	@param cognome Cognome dell'utente da inserire
     */
    public synchronized void addUser(String user, String pwd, String nome, String cognome){
        System.out.print("aggiungo user");
        Element reg_user = document.createElement(REG_USER);
        reg_user.setAttribute(NOME,nome);
        reg_user.setAttribute(COGNOME,cognome);
        reg_user.setAttribute(USER,user);
        reg_user.setAttribute(PWD,new Crypting().getMd5(pwd));
        root.appendChild(reg_user);
        try{
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            
            //  ...definisce propriet� document...
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,   DTD_PATH    );
            
            
            document.normalize();
            transformer.transform(new DOMSource(document), new StreamResult(new FileWriter(USER_XML_PATH)));
        } catch(TransformerConfigurationException e){
            System.out.println();} catch(TransformerException e){
                System.out.println();} catch(FileNotFoundException e){
                    System.out.println();} catch(IOException e){
                        System.out.println();}
        
    }
    
    /** modifica dati utente  */
    public synchronized void modifyUser(String user, String pwd, String nome, String cognome){
        String u, pwd_crypt = new Crypting().getMd5(pwd);
        NamedNodeMap attributi;
        if(root.getNodeType() == Node.ELEMENT_NODE){
            Element utenti_registrati = (Element) root;
            NodeList utenti = utenti_registrati.getElementsByTagName(REG_USER);
            if(utenti.getLength() != 0){
                int i = 0;
                do{
                    attributi = utenti.item(i).getAttributes();
                    u = attributi.getNamedItem(USER).getNodeValue();
                    if((u.compareTo(user) == 0)){
                        root.removeChild(utenti.item(i));
                        addUser(user, pwd, nome, cognome);
                        break;
                    }
                    i++;
                }
                while(i < utenti.getLength());
            }
        }
        try{
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            document.normalize();
            transformer.transform(new DOMSource(document), new StreamResult(new FileWriter(USER_XML_PATH)));
        } catch(TransformerConfigurationException e){
            System.out.println(e);} catch(TransformerException e){
                System.out.println(e);} catch(FileNotFoundException e){
                    System.out.println(e);} catch(IOException e){
                        System.out.println(e);}
    }
    
    /** ritorna nome utente  */
    public String getUserName(String user){
        String u, result = new String();
        NamedNodeMap attributi;
        if(root.getNodeType() == Node.ELEMENT_NODE){
            Element utenti_registrati = (Element) root;
            NodeList utenti = utenti_registrati.getElementsByTagName(REG_USER);
            if(utenti.getLength() != 0){
                int i = 0;
                do{
                    attributi = utenti.item(i).getAttributes();
                    u = attributi.getNamedItem(USER).getNodeValue();
                    if(u.compareTo(user)==0){
                        result = attributi.getNamedItem(NOME).getNodeValue();;
                        break;
                    }
                    i++;
                }
                while(i < utenti.getLength());
            }
        }
        return result;
    }
    
    /** ritorna cognome utente  */
    public String getUserSurname(String user){
        String u, result = new String();
        NamedNodeMap attributi;
        if(root.getNodeType() == Node.ELEMENT_NODE){
            Element utenti_registrati = (Element) root;
            NodeList utenti = utenti_registrati.getElementsByTagName(REG_USER);
            if(utenti.getLength() != 0){
                int i = 0;
                do{
                    attributi = utenti.item(i).getAttributes();
                    u = attributi.getNamedItem(USER).getNodeValue();
                    if(u.compareTo(user)==0){
                        result = attributi.getNamedItem(COGNOME).getNodeValue();;
                        break;
                    }
                    i++;
                }
                while(i < utenti.getLength());
            }
        }
        return result;
    }
}
