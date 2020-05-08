package twi518.userXmlFactory;

import org.w3c.dom.*;
import org.w3c.dom.Element;
import org.xml.sax.*;
import twi518.conf.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.*;
import java.io.*;
import java.util.*;



public class UserXml {
    private static Config conf = new Config();
    private final String USER_XML_PATH = new String(conf.getWEBDIR() + "/xml/utenti.xml");
    private final String REG_USER = "utente";
    private final String USER = "username";
    private final String PWD = "password";
    private final String NOME = "nome";
    private final String COGNOME = "cognome";
    private final String DTD_PATH = new String(conf.getWEBDIR() + "/xml-types/utenti.dtd");
    public Crypting crypting = new Crypting();
    public String errore = "[" + USER_XML_PATH + "]<br>[" + DTD_PATH + "]<br>";
    Node root;
    Element nodoRoot;
    private Document document;

    
    public UserXml() {
        synchronized (this) {
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setValidating(true);
                DocumentBuilder builder = factory.newDocumentBuilder();
                //builder.setErrorHandler( new MyErrorHandler());
                File f = new File(USER_XML_PATH);
                document = builder.parse(f);
                root = document.getDocumentElement();
            } catch (FileNotFoundException fne) {
                errore += fne.toString();
            } catch (SAXParseException spe) {
                errore += spe.toString();
            } catch (SAXException se) {
                errore += se.toString();
            } catch (Exception e) {
                errore += e.toString();
            }
            errore += "noderootvalue=root.getNodeValue()<br>" + root.getNodeValue();
        }
    }

    
    public String[] findUser(String user, String pwd) {
        errore += "findUser con " + user + "-" + pwd;
        String[] user_data = new String[2];
        String u, p, pwd_crypt = crypting.getMd5(pwd);
        NamedNodeMap attributi;
        if (root.getNodeType() == Node.ELEMENT_NODE) {
            Element utenti_registrati = (Element) root;
            NodeList utenti = utenti_registrati.getElementsByTagName(REG_USER);
            if (utenti.getLength() != 0) {
                int i = 0;
                do {
                    attributi = utenti.item(i).getAttributes();
                    u = attributi.getNamedItem(USER).getNodeValue();
                    p = attributi.getNamedItem(PWD).getNodeValue();
                    if ((u.compareTo(user) == 0) && (p.compareTo(pwd_crypt) == 0)) {
                        user_data[0] = attributi.getNamedItem(COGNOME).getNodeValue();
                        user_data[1] = attributi.getNamedItem(NOME).getNodeValue();
                        break;
                    } else {
                        user_data[0] = "0";
                    }
                    i++;
                }
                while (i < utenti.getLength());
            }
        }
        return user_data;
    }

    
    public boolean doubleUsername(String user) {
        System.out.print("cerco user");
        String u;
        boolean b = false;
        NamedNodeMap attributi;
        if (root.getNodeType() == Node.ELEMENT_NODE) {
            Element utenti_registrati = (Element) root;
            NodeList utenti = utenti_registrati.getElementsByTagName(REG_USER);
            if (utenti.getLength() != 0) {
                int i = 0;
                do {
                    attributi = utenti.item(i).getAttributes();
                    u = attributi.getNamedItem(USER).getNodeValue();
                    if (u.compareTo(user) == 0) {
                        b = true;
                        break;
                    }
                    i++;
                }
                while (i < utenti.getLength());
            }
        }
        return b;
    }

    
    public synchronized void addUser(String user, String pwd, String nome, String cognome) {
        System.out.print("aggiungo user");
        Element reg_user = document.createElement(REG_USER);
        reg_user.setAttribute(NOME, nome);
        reg_user.setAttribute(COGNOME, cognome);
        reg_user.setAttribute(USER, user);
        reg_user.setAttribute(PWD, new Crypting().getMd5(pwd));
        root.appendChild(reg_user);
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, DTD_PATH);
            document.normalize();
            transformer.transform(new DOMSource(document), new StreamResult(new FileWriter(USER_XML_PATH)));
        } catch (TransformerConfigurationException e) {
            System.out.println();
        } catch (TransformerException e) {
            System.out.println();
        } catch (FileNotFoundException e) {
            System.out.println();
        } catch (IOException e) {
            System.out.println();
        }
    }

    
    public synchronized void modifyUser(String user, String pwd, String nome, String cognome) {
        String u, pwd_crypt = new Crypting().getMd5(pwd);
        NamedNodeMap attributi;
        if (root.getNodeType() == Node.ELEMENT_NODE) {
            Element utenti_registrati = (Element) root;
            NodeList utenti = utenti_registrati.getElementsByTagName(REG_USER);
            if (utenti.getLength() != 0) {
                int i = 0;
                do {
                    attributi = utenti.item(i).getAttributes();
                    u = attributi.getNamedItem(USER).getNodeValue();
                    if ((u.compareTo(user) == 0)) {
                        root.removeChild(utenti.item(i));
                        addUser(user, pwd, nome, cognome);
                        break;
                    }
                    i++;
                }
                while (i < utenti.getLength());
            }
        }
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            document.normalize();
            transformer.transform(new DOMSource(document), new StreamResult(new FileWriter(USER_XML_PATH)));
        } catch (TransformerConfigurationException e) {
            System.out.println(e);
        } catch (TransformerException e) {
            System.out.println(e);
        } catch (FileNotFoundException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    
    public String getUserName(String user) {
        String u, result = new String();
        NamedNodeMap attributi;
        if (root.getNodeType() == Node.ELEMENT_NODE) {
            Element utenti_registrati = (Element) root;
            NodeList utenti = utenti_registrati.getElementsByTagName(REG_USER);
            if (utenti.getLength() != 0) {
                int i = 0;
                do {
                    attributi = utenti.item(i).getAttributes();
                    u = attributi.getNamedItem(USER).getNodeValue();
                    if (u.compareTo(user) == 0) {
                        result = attributi.getNamedItem(NOME).getNodeValue();
                        ;
                        break;
                    }
                    i++;
                }
                while (i < utenti.getLength());
            }
        }
        return result;
    }

    
    public String getUserSurname(String user) {
        String u, result = new String();
        NamedNodeMap attributi;
        if (root.getNodeType() == Node.ELEMENT_NODE) {
            Element utenti_registrati = (Element) root;
            NodeList utenti = utenti_registrati.getElementsByTagName(REG_USER);
            if (utenti.getLength() != 0) {
                int i = 0;
                do {
                    attributi = utenti.item(i).getAttributes();
                    u = attributi.getNamedItem(USER).getNodeValue();
                    if (u.compareTo(user) == 0) {
                        result = attributi.getNamedItem(COGNOME).getNodeValue();
                        ;
                        break;
                    }
                    i++;
                }
                while (i < utenti.getLength());
            }
        }
        return result;
    }
}
