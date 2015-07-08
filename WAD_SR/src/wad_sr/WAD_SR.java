/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package wad_sr;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.*;
// import org.apache.log4j.*;
import org.w3c.dom.*;
import org.xml.sax.*;

/**
 *
 * @author bornsc
 */
public class WAD_SR {
    
    // private static final Logger log = Logger.getLogger(WAD_SR.class);
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
            // TODO code application logic here
        
            // PropertyConfigurator.configure("C:\\xampp\\htdocs\\WAD-IQC\\uploads\\analysemodule\\WAD_SR\\log4j.properties");
            
            String inputFn = args[0];
            
            try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse (new File(inputFn));
            
            Element root = doc.getDocumentElement();
            String fileName  = doc.getElementsByTagName("filename").item(0).getTextContent();
            String resultName = doc.getElementsByTagName("analysemodule_output").item(0).getTextContent();

            DoseReport report = new DoseReport();
            ResultWriter resultWriter = new ResultWriter(resultName);
            
            try{
            report.readFromFile(fileName);
            resultWriter.writeResults(report.results);
            resultWriter.writeFile();
            }
            catch(Exception ex){
            //log.error(fileName + "error reading file");
            // log.error(ex.getLocalizedMessage());
            System.exit(2);
            }
            
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            
        }
    }
    
}
