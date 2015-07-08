/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad_sr;

import java.io.IOException;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;

/**
 *
 * @author Bart
 */
public class ReaderCfg {

    private String location;
    private String port;
    private String user;
    private String password;
    private String database;
        
    public ReaderCfg(){
        location="localhost";
        port="3306";
        user="juser";
        password="jpword";
        database="rdsr";
    }
    
   
    public void loadXml(String fn)throws Exception{
        Document xml;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try{
            DocumentBuilder db = dbf.newDocumentBuilder();
            xml=db.parse(fn);
            
            Element content =xml.getDocumentElement();
            
            this.database=getTextValue(this.database, content,"database");
            this.location=getTextValue(this.location,content,"location");
            this.password=getTextValue(this.password,content,"password");
            this.port=getTextValue(this.port,content,"port");
            this.user=getTextValue(this.user,content, "user");
        } 
        catch (ParserConfigurationException pce) {
            throw new Exception("Could not load config",pce);
        } 
        catch (SAXException se) {
            throw new Exception("Could not load config",se);
        } 
        catch (IOException ioe) {
            throw new Exception("Could not load config",ioe);
        }   
    }

       
    public String getLocation() {
        return location;
    }

    public String getPort() {
        return port;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getDatabase() {
        return database;
    }
    
    private String getTextValue(String def, Element doc, String tag) {
    String value = def;
    NodeList nl;
    nl = doc.getElementsByTagName(tag);
    if (nl.getLength() > 0 && nl.item(0).hasChildNodes()) {
        value = nl.item(0).getFirstChild().getNodeValue();
    }
    return value;
}    
}
