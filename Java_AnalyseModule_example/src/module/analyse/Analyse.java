/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package module.analyse;

import com.pixelmed.dicom.*;
import com.pixelmed.display.SourceImage;
import module.wad.xml.AnalyseModuleConfigFile;
import module.wad.xml.AnalyseModuleInputFile;
import module.wad.xml.AnalyseModuleResultFile;
import module.wad.xml.ResultsChar;
import module.wad.xml.ResultsFloat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class Analyse {

    private static final AttributeList list = new AttributeList();
    private final Log log = LogFactory.getLog(Analyse.class);
    
    
    public Analyse(AnalyseModuleInputFile inputfile, AnalyseModuleConfigFile configfile, AnalyseModuleResultFile resultfile){
        
        log.debug("patientid   = "+ inputfile.getPatient().getId());
        log.debug("patientnaam = "+ inputfile.getPatient().getName());
        log.debug("studydescription = "+ inputfile.getPatient().getStudy().getDescription());
        log.debug("aantal series = "+ inputfile.getPatient().getStudy().getSeriesList().size());
        
        // neem de eerste instance van de eerste serie van de eerste studie
        String analyseFile = inputfile.getPatient().getStudy().getSeries(0).getInstance(0).getFilename();
        
        // gewenste files inlezen, analyse uitvoeren en resultaten toevoegen aan results.xml
        String dicomFilePath = analyseFile;
        System.out.println("Analyzing:" + dicomFilePath);
        
        try {
            list.read(dicomFilePath);
            SourceImage img = new com.pixelmed.display.SourceImage(list);
            OtherWordAttribute pixelAttribute = (OtherWordAttribute)(list.get(TagFromName.PixelData));
            short[] data = pixelAttribute.getShortValues();
            int max=0;
            for (int i = 1; i < data.length; i++) {
               if (data[i] > max) {
                    max = data[i];
               }
            }
            System.out.println("Patient naam = " + Attribute.getDelimitedStringValuesOrEmptyString(list,TagFromName.PatientName));
            System.out.println("Hoogte plaatje = " + img.getHeight());
            System.out.println("Maximale pixelwaarde = " + max);
            
            
            // doe iets met de file (hier: lees de image-hoogte en patientnaam uit)
                
            ResultsFloat hoogte = new ResultsFloat();
            hoogte.setVolgnummer("1");
            hoogte.setNiveau("2");
            hoogte.setOmschrijving("hoogte van de dicom-image");
            hoogte.setGrootheid("hoogte");
            hoogte.setWaarde(Integer.toString(img.getHeight()));
            hoogte.setEenheid("pixels");
            resultfile.add(hoogte);

            ResultsFloat maxpixel = new ResultsFloat();
            maxpixel.setVolgnummer("2");
            maxpixel.setNiveau("1");
            maxpixel.setOmschrijving("maxpixel");
            maxpixel.setGrootheid("pixelwaarde");
            maxpixel.setWaarde(Integer.toString(max));
            maxpixel.setEenheid("a.u.");
            resultfile.add(maxpixel);
            
            // voeg een resultaat toe aan de resultfile
            ResultsChar patientnaam = new ResultsChar();
            patientnaam.setVolgnummer("3");
            patientnaam.setNiveau("2");
            patientnaam.setOmschrijving("Patient naam");
            patientnaam.setWaarde(Attribute.getDelimitedStringValuesOrEmptyString(list,TagFromName.PatientName));
            resultfile.add(patientnaam);
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
    }
    
}
