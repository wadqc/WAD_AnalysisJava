/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package module.wad.xml;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
//import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
//import wad.logger.LoggerWrapper;



public class AnalyseModuleConfigFile {
   
    private Log log = LogFactory.getLog(AnalyseModuleConfigFile.class);
    File analyseModuleConfigFile;
    Document dom;
    
    public AnalyseModuleConfigFile(String filename){
        
        if(!filename.isEmpty()) {
            analyseModuleConfigFile = new File(filename);
            if(analyseModuleConfigFile.isFile()) {
                parseXmlFile(analyseModuleConfigFile);
                parseDocument();
            }
        }
    }
   
    
   private void parseXmlFile(File inputFile){
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        
        try{
            DocumentBuilder db = dbf.newDocumentBuilder();
            
            dom = db.parse(inputFile);
            
            // FIXME: waarvoor is deze nodig???
            dom.getDocumentElement().normalize ();
            
        } catch (SAXException ex) {
            log.error(ex);
            //LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{AnalyseModuleInputFile.class.getName(), ex});
        } catch (IOException ex) {
            log.error(ex);
            //LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{AnalyseModuleInputFile.class.getName(), ex});
        } catch (ParserConfigurationException ex) {
            log.error(ex);
            //LoggerWrapper.myLogger.log(Level.SEVERE, "{0} {1}", new Object[]{AnalyseModuleInputFile.class.getName(), ex});
        }    
    }
    
   
    private void parseDocument(){     
        
        // lees variabelen uit de config file (maak hiervoor private members aan binnen deze class)
            
        //Element docElement = dom.getDocumentElement();
        //System.out.println("Root element " + dom.getDocumentElement().getNodeName());
            
        //NodeList datasources = dom.getElementsByTagName("datasources"); 
            
            /* voorbeeld voor config.xml tbv WAD_Services:
            NodeList firstLocalDatasource = dom.getElementsByTagName("DCM4CHEE-db");            
            Node fstNode = firstLocalDatasource.item(0);
            
            if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
                Element fstElmnt = (Element) fstNode;
                NodeList fstNmElmntLst = fstElmnt.getElementsByTagName("connection-url");
                Element fstNmElmnt = (Element) fstNmElmntLst.item(0);
                NodeList fstNm = fstNmElmnt.getChildNodes();
                //System.out.println("connection-url : "  + ((Node) fstNm.item(0)).getNodeValue());
                NodeList scndNmElmntLst = fstElmnt.getElementsByTagName("user-name");
                Element scndNmElmnt = (Element) scndNmElmntLst.item(0);
                NodeList scndNm = scndNmElmnt.getChildNodes();
                //System.out.println("user-name : " + ((Node) scndNm.item(0)).getNodeValue());
                NodeList thrdNmElmntLst = fstElmnt.getElementsByTagName("password");
                Element thrdNmElmnt = (Element) thrdNmElmntLst.item(0);
                NodeList thrdNm = thrdNmElmnt.getChildNodes();
                //System.out.println("password : " + ((Node) thrdNm.item(0)).getNodeValue());
                NodeList lstNmElmntLst = fstElmnt.getElementsByTagName("driver-class");
                Element lstNmElmnt = (Element) lstNmElmntLst.item(0);
                NodeList lstNm = lstNmElmnt.getChildNodes();
                //System.out.println("driver-class : " + ((Node) lstNm.item(0)).getNodeValue());
                
                
                dbParams.connectionURL = nodeToString(fstNm.item(0));
                String[] temp = dbParams.connectionURL.split("/"); 
                dbParams.databasename = temp[3];
                dbParams.username = nodeToString(scndNm.item(0));
                dbParams.password = nodeToString(thrdNm.item(0));
                dbParams.driverclass = nodeToString(lstNm.item(0));
                
                //System.out.println("dbname : " + dbParams.databasename);
                
                
            }
           
            NodeList metaData = dom.getElementsByTagName("metadata");            
            Node scndNode = metaData.item(0);
            
            if (scndNode.getNodeType() == Node.ELEMENT_NODE) {
                Element fstElmnt = (Element) fstNode;
                NodeList lstNmElmntLst = fstElmnt.getElementsByTagName("type-mapping");
                Element lstNmElmnt = (Element) lstNmElmntLst.item(0);
                NodeList lstNm = lstNmElmnt.getChildNodes();
                //System.out.println("type : " + ((Node) lstNm.item(0)).getNodeValue());
                
                dbParams.type = nodeToString(lstNm.item(0));
            }
            */
    }    
   
/*    
    private static String nodeToString(Node node) {
        StringWriter sw = new StringWriter();
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            t.transform(new DOMSource(node), new StreamResult(sw));
        } catch (TransformerException te) {
            //System.out.println("nodeToString Transformer Exception");
        }
        return sw.toString();
    }
*/    

/*    
    public static String readFileElement(String ElementName){
        try{
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse (new File("../../config.xml"));
            
            Element docElement = doc.getDocumentElement();
            
            NodeList nlFilepath = docElement.getElementsByTagName("filepath");
            
            if (nlFilepath != null && nlFilepath.getLength()>0) {
                for (int i = 0 ; i < nlFilepath.getLength();i++) {
                    Element elFilepath = (Element)nlFilepath.item(i);
                    return getTextValue(elFilepath, ElementName);
                }
            }            
        }catch (ParserConfigurationException pce) {
            
        }catch (SAXException se) {
            
        }catch (IOException ioe){
            
        }
        return null;
    }
    
    public static String readSettingsElement(String ElementName){
        try{
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse (new File("../../config.xml"));
            
            Element docElement = doc.getDocumentElement();
            
            NodeList nlFilepath = docElement.getElementsByTagName("settings");
            
            if (nlFilepath != null && nlFilepath.getLength()>0) {
                for (int i = 0 ; i < nlFilepath.getLength();i++) {
                    Element elFilepath = (Element)nlFilepath.item(i);
                    return getTextValue(elFilepath, ElementName);
                }
            }            
        }catch (ParserConfigurationException pce) {
            
        }catch (SAXException se) {
            
        }catch (IOException ioe){
            
        }
        return null;
    }
  
*/
   
    private static String getTextValue(Element ele, String tagName) {
        String textVal = null;
	NodeList nl = ele.getElementsByTagName(tagName);
	if(nl != null && nl.getLength() > 0) {
            Element el = (Element)nl.item(0);
            if(el != null && el.getFirstChild() != null) {
                textVal = el.getFirstChild().getNodeValue();
            } else {
                textVal = "";
            }
        }
	return textVal;
    }
    
}
