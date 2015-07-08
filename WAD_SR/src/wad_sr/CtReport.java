/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad_sr;
import com.pixelmed.dicom.*;
import java.util.ArrayList;
import java.util.Iterator;
//import org.apache.log4j.Logger;
import java.text.ParseException;
/**
 *
 * @author Bart
 */
public class CtReport {
//<editor-fold defaultstate="collapsed" desc="field definitions">
public Integer numberOfIrradiationEvents;
public String ctDoseLengthProductTotal;
public Float ctEffectiveDoseTotal;
public String doseUnits;
public String dlpUnit;
public ArrayList<CtIrradiationEvent> irradiationEvents;
public PatientStudySeriesData generalInfo;
//private static final Logger log = Logger.getLogger(CtReport.class);
//</editor-fold>
    
public CtReport(){
this.numberOfIrradiationEvents = null;
this.doseUnits = null;
this.dlpUnit = null;
this.ctDoseLengthProductTotal = null;
this.ctEffectiveDoseTotal = null;
this.irradiationEvents = new ArrayList<CtIrradiationEvent>();
this.generalInfo =  new PatientStudySeriesData("CT");
}

public void parseSR(ContentItem root)throws DicomException, ParseException{
    // method to read the a structured report representet in the ContentItem into a CtReport object
        
    //parse general debug
    //log.debug("Parsing Patient, Series, Study Information");
    this.generalInfo.parsePatientStudySeriesData(root);   
    
    //read the CT report fields              
    ContentItem accumulatedDoseData = root.getNamedChild("DCM", "113811");
    
    ContentItem ctDlp=accumulatedDoseData.getNamedChild("DCM", "113813");
    if (ctDlp!=null){
        this.dlpUnit=((ContentItemFactory.NumericContentItem)ctDlp).getUnits().getCodeValue();
        if(this.dlpUnit.equals("mGycm")){
            this.ctDoseLengthProductTotal = ctDlp.getSingleStringValueOrNull();
        } else {
            throw new DicomException("Incorrect unit parsing dose length product");
        }
    }
    
    String nEvents=accumulatedDoseData.getSingleStringValueOrNullOfNamedChild("DCM", "113812");
    if(nEvents!=null){
        this.numberOfIrradiationEvents = Integer.parseInt(nEvents);   
    }
    
    ContentItem effDos = root.getNamedChild("DCM", "113839");
    if(effDos!=null){
        this.doseUnits=((ContentItemFactory.NumericContentItem)effDos).getUnits().getCodeValue();
        if(this.doseUnits.equals("mSv")){
            this.ctEffectiveDoseTotal =Float.parseFloat(effDos.getSingleStringValueOrNull());  
        } else {
            throw new DicomException("Incorrect unit parsing effective dose");
        }
        
    }

    AttributeList list = root.getAttributeList();
    SequenceAttribute srAttributes = (SequenceAttribute)list.get(new AttributeTag("(0x0040,0xa730)") );
    
        
    //Iterate through the content sequence of the SR content sequence to get irradiation events
    Iterator i = ((SequenceAttribute)srAttributes).iterator();
    int j=1;
    while (i.hasNext()) {

       SequenceItem item = (SequenceItem)i.next();
       AttributeList contentList = item.getAttributeList();
        
       //Get the conceptCodeSequence
       SequenceAttribute conceptSeq = (SequenceAttribute)(contentList.get(new AttributeTag("(0x0040,0xa043)")));
       CodedSequenceItem code = new CodedSequenceItem(conceptSeq.getItem(0).getAttributeList());
         
       //Get irradiationevents
       if (code.getCodeValue().equals("113819")){
            StructuredReport ct = new StructuredReport(contentList);
            ContentItem wortel = (ContentItem)ct.getRoot();     
            CtIrradiationEvent ctEvent = new CtIrradiationEvent();
            //log.debug("Parsing CT irradiation event "+j);
            j++;
            ctEvent.parseCtIrradiationEvent(wortel);
            this.irradiationEvents.add(ctEvent);
        }
    }
}

}

