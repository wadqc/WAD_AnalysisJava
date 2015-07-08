/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad_sr;
import com.pixelmed.dicom.*;
/**
 *
 * @author Bart
 */
public class AccumulatedXrayDose {
public String plane;
public String doseAreaProductTotal;
public Float doseRpTotal;
public Float fluoroDoseAreaProductTotal;
public Float fluoroDoseRp;
public Float totalFluoroTime;
public Float acquisitionDoseAreaProductTotal;
public Float acquisitionDoseRpTotal;
public Float totalAcquisitionTime;
public Integer totalNumberOfRadiographicImages;
public Float accumulatedAverageGlandularDose;
public String referencePointDefinition;
public String laterality;
public String dapUnit;


public AccumulatedXrayDose(){
this.plane=null;
this.doseAreaProductTotal=null;
this.doseRpTotal=null;
this.fluoroDoseAreaProductTotal=null;
this.fluoroDoseRp=null;
this.totalFluoroTime=null;
this.acquisitionDoseAreaProductTotal=null;
this.acquisitionDoseRpTotal=null;
this.totalAcquisitionTime=null;
this.totalNumberOfRadiographicImages=null;
this.referencePointDefinition=null;
this.accumulatedAverageGlandularDose=null;
this.laterality = null;
this.dapUnit = null;
}

public void parseAccumulatedXrayDose(ContentItem root) throws DicomException{
    
    //get plane form coded concept
    AttributeList acqPlane = root.getNamedChild("DCM","113764").getAttributeList();
    String acqPlaneCode = CodedSequenceItem.getSingleCodedSequenceItemOrNull(acqPlane, TagFromName.ConceptCodeSequence).getCodeValue();
    if (acqPlaneCode.equals("113622")){
        this.plane="S";
    } else if (acqPlaneCode.equals("113620")) {
        this.plane="A";
    } else if (acqPlaneCode.equals("113621")) {
        this.plane="B";
    } else if (acqPlaneCode.equals("113890")) {
        this.plane="X";
    }

    ContentItem dapTot = root.getNamedChild("DCM","113722"); 
    
    if (dapTot != null){
        dapUnit=((ContentItemFactory.NumericContentItem)dapTot).getUnits().getCodeValue();
        System.out.println(dapUnit);
        switch (dapUnit) {
            case "Gym2":
            case "Gy.m2":
                this.doseAreaProductTotal= dapTot.getSingleStringValueOrNull();
                break;
            case "bla":
                break;
            default:
                throw new DicomException("Error parsing DAP, incorrect units");
        }
    } 

    ContentItem drpTot = root.getNamedChild("DCM","113725");
    if (drpTot!=null){
        String drpTotUnits = ((ContentItemFactory.NumericContentItem)drpTot).getUnits().getCodeValue(); 
        if (drpTotUnits.equals("Gy")){
            this.doseRpTotal=Float.parseFloat(drpTot.getSingleStringValueOrNull());
        } else{
            throw new DicomException("Error Parsing dose reference point total, incorrect units");
        }
    }

    ContentItem fDoseTot = root.getNamedChild("DCM", "113726");
    if(fDoseTot!=null){
        String fDoseUnits = ((ContentItemFactory.NumericContentItem)fDoseTot).getUnits().getCodeValue();
        switch (fDoseUnits) {
            case "Gym2":
            case "Gy.m2":
                this.fluoroDoseAreaProductTotal=Float.parseFloat(fDoseTot.getSingleStringValueOrNull());
                break;
            default:
                throw new DicomException("Error Parsing fluore DAP, incorrect units");
        }
    }

    ContentItem fDoseRp = root.getNamedChild("DCM", "113728");
    if(fDoseRp!=null){
        String fDoseRpUnits = ((ContentItemFactory.NumericContentItem)fDoseRp).getUnits().getCodeValue();
        if(fDoseRpUnits.equals("Gy")){
            this.fluoroDoseRp=Float.parseFloat(fDoseRp.getSingleStringValueOrNull());
        }else{
            throw new DicomException("Error Parsing fluora dose reference point, incorrect units");
        }
    }

    ContentItem fTime = root.getNamedChild("DCM", "113730");
    if(fTime!=null){
        String fTimeUnits = ((ContentItemFactory.NumericContentItem)fTime).getUnits().getCodeValue();
        if (fTimeUnits.equals("s")){
            this.totalFluoroTime=Float.parseFloat(fTime.getSingleStringValueOrNull());
    
        } else {
            throw new DicomException("Error Parsing fluoro time, incorrect units");
        }
    }

    ContentItem acqDap = root.getNamedChild("DCM", "113727");
    if(acqDap!=null){
        String acqDapUnits = ((ContentItemFactory.NumericContentItem)acqDap).getUnits().getCodeValue();
        switch (acqDapUnits) {
            case "Gym2":
            case "Gy.m2":
                this.acquisitionDoseAreaProductTotal=Float.parseFloat(acqDap.getSingleStringValueOrNull());
                break;
            default:
                throw new DicomException("Error Parsing acquisition DAP, incorrect units");
        }
    }

    ContentItem acqDsRp = root.getNamedChild("DCM", "113729");
    
    if(acqDsRp!=null){
        String acqDsRpUnits = ((ContentItemFactory.NumericContentItem)acqDsRp).getUnits().getCodeValue();
        if (acqDsRpUnits.equals("Gy")){
            this.acquisitionDoseRpTotal=Float.parseFloat(acqDsRp.getSingleStringValueOrNull());
        } else {
            throw new DicomException("Error Parsing acquisition dose at reference point, incorrect units");
        }
    }
    
    ContentItem totAcqTime = root.getNamedChild("DCM", "113855");
    if(totAcqTime!=null){
        String totAcqTimeUnits = ((ContentItemFactory.NumericContentItem)totAcqTime).getUnits().getCodeValue();
        if(totAcqTimeUnits.equals("s")){
            this.totalAcquisitionTime=Float.parseFloat(totAcqTime.getSingleStringValueOrNull());
        } else {
            throw new DicomException("Error Parsing acquisition acquisition time, incorrect units");
        }
    }
    
    String nIm = root.getSingleStringValueOrNullOfNamedChild("DCM", "113731");
    if(nIm!=null){
        this.totalNumberOfRadiographicImages=Integer.parseInt(nIm);
    }

    this.referencePointDefinition = root.getSingleStringValueOrNullOfNamedChild("DCM", "113780");

    ContentItem accGlanDs = root.getNamedChild("DCM", "111637");
    if (accGlanDs!=null){
        String accGlanDsUnit = ((ContentItemFactory.NumericContentItem)accGlanDs).getUnits().getCodeValue();
        if(accGlanDsUnit.equals("dGy")){
        this.accumulatedAverageGlandularDose=Float.parseFloat(accGlanDs.getSingleStringValueOrNull());
        }else {
            throw new DicomException("Error Parsing average glandular dose, incorrect units");
        }
        
    }

    ContentItem ltIt = root.getNamedChild("SRT","G-C171");
    if (ltIt!=null){
        AttributeList ltLs = ltIt.getAttributeList(); 
        String lateralityCode = CodedSequenceItem.getSingleCodedSequenceItemOrNull(ltLs, TagFromName.ConceptCodeSequence).getCodingSchemeDesignator()+
        ":" + CodedSequenceItem.getSingleCodedSequenceItemOrNull(ltLs, TagFromName.ConceptCodeSequence).getCodeValue();
        this.laterality=ConceptCodes.getConceptName(lateralityCode);
    }
}

public boolean checkForDb(){
    boolean status = false;
    
    if (this.plane==null){
        return status;
    }
    if ((this.doseAreaProductTotal!=null)&&(this.accumulatedAverageGlandularDose!=null)){
        return status;
    }
    if(this.doseAreaProductTotal!=null){
        if (this.acquisitionDoseAreaProductTotal==null){
            return status;
        }
        if (this.totalAcquisitionTime==null){
            return status;
        }
        if ((this.doseRpTotal!=null)&&(this.referencePointDefinition==null)){
            return status;
        }
    } else if(this.accumulatedAverageGlandularDose!=null) {       
        if (this.laterality==null){
            return status;
        }             
    } else {
        return status;
    }
    return true;
}


}