/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package module.analyse;

import ij.*;
import ij.io.*;
import ij.process.*;
import ij.util.*;
import java.awt.Rectangle;
import module.wad.xml.AnalyseModuleConfigFile;
import module.wad.xml.AnalyseModuleInputFile;
import module.wad.xml.AnalyseModuleResultFile;
import module.wad.xml.ResultsChar;
import module.wad.xml.ResultsFloat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class Analyse {

    private ImagePlus IPL_image;    // the imagej image variable
    private ImageProcessor ip;      // the ImageProcessor variable to manipulate the pixels   
    private Log log = LogFactory.getLog(Analyse.class);
    
    
    public Analyse(AnalyseModuleInputFile inputfile, AnalyseModuleConfigFile configfile, AnalyseModuleResultFile resultfile){
        
        // selecteer de files die we willen analyseren (in dit voorbeeld de serie die "SAVE SCREEN" bevat)
        String analyseFile = "";
        
        log.debug("patientid   = "+ inputfile.getPatient().getId());
        log.debug("patientnaam = "+ inputfile.getPatient().getName());
        log.debug("studydescription = "+ inputfile.getPatient().getStudy().getDescription());
        log.debug("aantal series = "+ inputfile.getPatient().getStudy().getSeriesList().size());
        
        for (int i=0; i<inputfile.getPatient().getStudy().getSeriesList().size();i++) {
            if ( inputfile.getPatient().getStudy().getSeries(i).getDescription().contains("SAVE SCREEN") ) {
                log.debug("Seriesdescription van serie " + (i+1) + " bevat 'SAVE SCREEN'");
                
                for (int j=0;j<inputfile.getPatient().getStudy().getSeries(i).getInstanceList().size();j++) {
                    analyseFile = inputfile.getPatient().getStudy().getSeries(i).getInstance(j).getFilename();
                    log.debug("Serie " + (i+1) + ", Instance " + (j+1) + " " + analyseFile);
                    
                }
            }
        }
        
        
        // gewenste files inlezen, analyse uitvoeren en resultaten toevoegen aan results.xml
        ReadFile(analyseFile); 
        
        // doe iets met de file (hier: lees de image-hoogte en patientnaam uit)
        ip = IPL_image.getProcessor();    //get access to the pixels
        log.debug("   Height = " + ip.getHeight());
        Rectangle roi = ip.getRoi();
        log.debug("   PatientName (0010,0010) = " + DicomTools.getTag(IPL_image, "0010,0010"));
        
        ResultsFloat hoogte = new ResultsFloat();
        hoogte.setVolgnummer("1");
        hoogte.setWaarde(Integer.toString(ip.getHeight()));
        hoogte.setEenheid("pixels");
        resultfile.add(hoogte);
        
        // voeg een resultaat toe aan de resultfile
        ResultsChar patientnaam = new ResultsChar();
        patientnaam.setVolgnummer("2");
        patientnaam.setWaarde(DicomTools.getTag(IPL_image, "0010,0010"));
        resultfile.add(patientnaam);
    }

    
    private void ReadFile(String filename){
        IPL_image = IJ.openImage(filename);
    }
    

    
}
