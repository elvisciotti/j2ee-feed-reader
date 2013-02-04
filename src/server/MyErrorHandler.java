package twi518;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
/**
 * @author Elvis
 */
public class MyErrorHandler implements ErrorHandler {
    
    // throw SAXException for fatal errors
    public void fatalError( SAXParseException exception )
    throws SAXException {
        throw exception;
    }
    
    /** se validating a false non dovrebbe partire*/
    public void error( SAXParseException e )
    throws SAXParseException {
       throw e;
       // System.err.println( "ERRORE SAX: " + e.getMessage() );
    }
    
    // print any warnings
    public void warning( SAXParseException err )
    throws SAXParseException {
        System.err.println( "Warning: " + err.getMessage() );
    }
}

