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
public class ProjectionXrayReport {

//private static final Logger log = Logger.getLogger(ProjectionXrayReport.class);

public String procedureReported;    
public Boolean biplane;
public ArrayList<AccumulatedXrayDose> accumulatedDose;
public ArrayList<XrayIrradiationEvent> irradiationEvents;
public PatientStudySeriesData generalInfo;
    
public ProjectionXrayReport(){
    this.procedureReported = null;
    //this.deviceObserverUid =null;
    //this.deviceObserverManufacturer=null;
    //this.deviceObserverModelName=null;
    //this.deviceObserverSerialNumber=null;
    //this.deviceObserverPhysicalLocation = null;
    this.biplane=null;
    this.accumulatedDose=new ArrayList<AccumulatedXrayDose>();
    this.irradiationEvents=new ArrayList<XrayIrradiationEvent>();
    this.generalInfo=new PatientStudySeriesData("Projection X-ray");
}
    
public void ParseSr(ContentItem root) throws ParseException,DicomException{

    //log.debug("Parsing Patient, Series, Study Data");
    this.generalInfo.parsePatientStudySeriesData(root);

    ContentItem procRepIt= root.getNamedChild("DCM", "121058");
    if (procRepIt!=null){
        AttributeList procRepLs =procRepIt.getAttributeList();  
        String procedureReportedCode=CodedSequenceItem.getSingleCodedSequenceItemOrNull(procRepLs, TagFromName.ConceptCodeSequence).getCodingSchemeDesignator()+
                ":"+CodedSequenceItem.getSingleCodedSequenceItemOrNull(procRepLs, TagFromName.ConceptCodeSequence).getCodeValue();
        this.procedureReported=ConceptCodes.getConceptName(procedureReportedCode);
        if (this.procedureReported==null){
            //log.warn("Unknown Procedure Code IN RDSR: COncept Code = "+procedureReportedCode );
            throw new DicomException("Unknown Procedure Reported in Projection X-ray report: Should be Mammography OR Projection X-ray");
        } 

    }

    //Get contentsequence from contentitem
    AttributeList list = root.getAttributeList();
    SequenceAttribute srAttributes = (SequenceAttribute)list.get(new AttributeTag("(0x0040,0xa730)") );


    //Iterate through the content sequence of the SR content sequence
    Iterator i = ((SequenceAttribute)srAttributes).iterator();
    while (i.hasNext()) {

       SequenceItem item = (SequenceItem)i.next();
       AttributeList contentList = item.getAttributeList();

       //Get the conceptCodeSequence
       SequenceAttribute conceptSeq = (SequenceAttribute)(contentList.get(new AttributeTag("(0x0040,0xa043)")));
       CodedSequenceItem code = new CodedSequenceItem(conceptSeq.getItem(0).getAttributeList());

       //Get accumulated dose and irradiation events
       if (code.getCodeValue().equals("113702")){
           StructuredReport dose = new StructuredReport(contentList);
           ContentItem wortel = (ContentItem)dose.getRoot();
           AccumulatedXrayDose xDose = new AccumulatedXrayDose();
           xDose.parseAccumulatedXrayDose(wortel);
           //log.debug("Parsing Accumulated Dose data");
           this.accumulatedDose.add(xDose);
       } else if (code.getCodeValue().equals("113706")){
           StructuredReport event = new StructuredReport(contentList);
           ContentItem wortel = (ContentItem)event.getRoot();
           XrayIrradiationEvent xEvent = new XrayIrradiationEvent();
           //log.debug("Parsing irradiationevent");
           xEvent.parseXrayIrradiationEvent(wortel);
           this.irradiationEvents.add(xEvent);
       }
       if (this.accumulatedDose.size()>1){
           this.biplane=true;
       } else {this.biplane=false;}
    }  
}
  


}
