/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad_sr;

import com.pixelmed.dicom.*;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
// import org.apache.log4j.Logger;


/**
 *
 * @author Bart
 */
public class DoseReport {
    public Results results;
    private CtReport ctReport;
    private ProjectionXrayReport projectionXrayReport;
    private String srType;
    //private static final Logger log = Logger.getLogger(DoseReport.class);
    
    public DoseReport(){
            this.results = new Results();
            this.srType=null;
            this.projectionXrayReport=null;
            this.ctReport=null;
    }
    
    public void readFromFile(String fn) throws IOException, DicomException, Exception {
        FileInputStream fis = null;
        DicomInputStream stream = null;
        try{
            //Read dicom SR and convert to contentitem
            fis = new FileInputStream(fn);
            stream = new DicomInputStream(new BufferedInputStream(fis));
            AttributeList list = new AttributeList();
            list.read(stream);      
            
            //check if list is SR
            if (!Attribute.getSingleStringValueOrEmptyString(list,TagFromName.Modality).trim().equals("SR")){
                //log.error("read File is not a SR Document: "+Attribute.getSingleStringValueOrEmptyString(list,TagFromName.Modality));
                throw new DicomException("read file is not a SR Document");
            }
            if(!Attribute.getSingleStringValueOrEmptyString(list,TagFromName.SOPClassUID).trim().equals("1.2.840.10008.5.1.4.1.1.88.67")){
                //log.error("read File is not a SR Dose report, SOP Class found: "+Attribute.getSingleStringValueOrEmptyString(list,TagFromName.SOPClassUID));
                throw new DicomException("read File is not a SR Dose report, SOP class not found");
            }

            //convert to ContentItem to facilitate CodeValue searches
            StructuredReport srDoc = new StructuredReport(list);
            ContentItem root = (ContentItem)srDoc.getRoot();


            //get type of structured report
            String code;
            try{
                AttributeList typeList = root.getNamedChild("DCM","121058").getAttributeList();
                code = CodedSequenceItem.getSingleCodedSequenceItemOrNull(typeList, TagFromName.ConceptCodeSequence).getCodeValue();
            }
            catch(NullPointerException Ex){
                throw new DicomException("Nullpointer exception: Cannot find concept code: DCM, 121058, X-ray Radiation Dose Report: not a valid Dose report"); 
            }

            //parse report in correct object
            if (code.equals("P5-08000")){
                this.srType = "CT";
                //log.debug("Parsing "+fn+"as a CT Dose report");
                this.ctReport= new CtReport();
                this.ctReport.parseSR(root);
                this.results.type = "float";
                this.results.quantity = "DLP";
                this.results.unit = this.ctReport.dlpUnit;
                this.results.description = this.ctReport.generalInfo.studyDescription;
                this.results.value = this.ctReport.ctDoseLengthProductTotal;
                  
                
            } else if (code.equals("P5-40010")||code.equals("113704")){
                this.srType ="Projection X-ray";
                //log.debug("Parsing " + fn+ "as a projection x-ray report");
                this.projectionXrayReport = new ProjectionXrayReport();
                this.projectionXrayReport.ParseSr(root);
                this.results.type = "float";
                this.results.quantity = "DAP";
                this.results.unit = this.projectionXrayReport.accumulatedDose.get(0).dapUnit;
                this.results.description = this.projectionXrayReport.generalInfo.studyDescription;
                this.results.value = this.projectionXrayReport.accumulatedDose.get(0).doseAreaProductTotal; 
                
                System.out.println(this.results.type);
                System.out.println(this.results.quantity);
                System.out.println(this.results.unit);
                System.out.println(this.results.description);
                System.out.println(this.results.value);
                
            } else{
                //log.error("Unknown Dose Report type"+code);  
                throw new DicomException("Unknown Dose report Type:"+code);                
            }
            
        }
        finally{
            //close all resources
            if(fis!=null){
                fis.close();
            }
            if(stream!=null){
                stream.close();
            }
        }
        
    } 
   
}
