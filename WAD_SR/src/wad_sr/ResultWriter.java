/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package wad_sr;

import java.io.File;
//import java.util.logging.Level;
// import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author bornsc
 */
public class ResultWriter {
       
    private String fileName;
    private DocumentBuilderFactory docBuilderFactory;
    private DocumentBuilder docBuilder;
    private Document doc;
    private Element rootElement;
    private Element resultElement;
    private Element volgNummer;
    private Element type;
    private Element niveau;
    private Element waarde;
    private Element grootheid;
    private Element eenheid;
    private Element omschrijving;
    private Element grensAcceptabelOnder;
    private Element grensAcceptabelBoven;
    private Element grensKritischOnder;
    private Element grensKritischBoven;
            
    public ResultWriter(String fn)
    {
        try {
            this.fileName = fn;
            
            docBuilderFactory = DocumentBuilderFactory.newInstance();
            docBuilder = docBuilderFactory.newDocumentBuilder();
            doc = docBuilder.newDocument();
            
            rootElement = doc.createElement("WAD");
            doc.appendChild(rootElement);
            
            
        } catch (ParserConfigurationException ex) {
            //Logger.getLogger(ResultWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    public void setFileName(String fn)
    {
        this.fileName = fn;
    }
    
    public void writeResults(Results results)
    {
        resultElement = doc.createElement("results");
        
        volgNummer = doc.createElement("volgnummer");
        volgNummer.appendChild(doc.createTextNode("1"));
        
        type = doc.createElement("type");
        type.appendChild(doc.createTextNode(results.type));
        
        niveau = doc.createElement("niveau");
        niveau.appendChild(doc.createTextNode("1"));
        
        waarde = doc.createElement("waarde");
        waarde.appendChild(doc.createTextNode(results.value));
        
        grootheid = doc.createElement("grootheid");
        grootheid.appendChild(doc.createTextNode(results.quantity));
        
        eenheid = doc.createElement("eenheid");
        eenheid.appendChild(doc.createTextNode(results.unit));
        
        omschrijving = doc.createElement("omschrijving");
        omschrijving.appendChild(doc.createTextNode(results.description));
        
        grensAcceptabelOnder = doc.createElement("grens_acceptabel_onder");
        grensAcceptabelOnder.appendChild(doc.createTextNode("0"));
        
        grensAcceptabelBoven = doc.createElement("grens_acceptabel_boven");
        grensAcceptabelBoven.appendChild(doc.createTextNode("0"));
        
        grensKritischOnder = doc.createElement("grens_kritisch_onder");
        grensKritischOnder.appendChild(doc.createTextNode("0"));
        
        grensKritischBoven = doc.createElement("grens_kritisch_boven");
        grensKritischBoven.appendChild(doc.createTextNode("0"));
        
        resultElement.appendChild(volgNummer);
        resultElement.appendChild(type);
        resultElement.appendChild(niveau);
        resultElement.appendChild(waarde);
        resultElement.appendChild(grootheid);
        resultElement.appendChild(eenheid);
        resultElement.appendChild(omschrijving);
        resultElement.appendChild(grensAcceptabelOnder);
        resultElement.appendChild(grensAcceptabelBoven);
        resultElement.appendChild(grensKritischOnder);
        resultElement.appendChild(grensKritischBoven);
        
        rootElement.appendChild(resultElement);
    }
    
    public void writeFile()
    {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", 2);
            
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            
            DOMSource source = new DOMSource(doc);
            
            StreamResult result = new StreamResult(new File(fileName));
            
            // Output to console for testing
            //treamResult result = new StreamResult(System.out);

            transformer.transform(source, result);
            
        } catch (TransformerConfigurationException ex) {
            //Logger.getLogger(ResultWriter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            //Logger.getLogger(ResultWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
