/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad_sr;
import com.pixelmed.dicom.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
//import org.apache.log4j.Logger;

/**
 *
 * @author Bart
 */
public class XrayIrradiationEvent {
public String plane;
public Timestamp dateTimeStarted;
public String IrradiationEventType;
public String acquisitionProtocol;
public String anatomicalStructure;
public String laterality;
public String referencePointDefinition;
public String irradiationEventUid;
public Float doseAreaProduct;
public Float averageGlandularDose;
public Float doseRp;
public Float entranceExposureAtRp;
public Float positionerPrimaryAngle;
public Float positionerSecondaryAngle;
public Float positionerPrimaryEndAngle;
public Float positionerSecondaryEndAngle;
public Float columnAngulation;
public Float collimatedFieldArea;
public String fluoroMode;
public Float pulseRate;
public Integer numberOfPulses;
public Float exposureTime;
public Float focalSpotSize;
public Float irradiationDuration;
public Float averageXrayTubeCurrent;
public Float avarageKv;
public String patientOrientation;
public String patientOrientationModifier;
public String targetRegion;
public ArrayList <XrayFilter> xrayFilters;
public ArrayList <XrayPulse> xrayPulses;
//private static final Logger log = Logger.getLogger(XrayIrradiationEvent.class);       

public XrayIrradiationEvent(){
this.plane = null;
this.dateTimeStarted = null;
this.IrradiationEventType = null;
this.acquisitionProtocol = null;
this.anatomicalStructure=null;
this.laterality=null;
this.referencePointDefinition = null;
this.irradiationEventUid = null;
this.doseAreaProduct= null;
this.averageGlandularDose= null;
this.doseRp= null;
this.entranceExposureAtRp= null;
this.positionerPrimaryAngle= null;
this.positionerSecondaryAngle= null;
this.positionerPrimaryEndAngle= null;
this.positionerSecondaryEndAngle= null;
this.columnAngulation= null;
this.collimatedFieldArea= null;
this.fluoroMode= null;
this.pulseRate= null;
this.numberOfPulses= null;
this.exposureTime= null;
this.focalSpotSize= null;
this.irradiationDuration = null;
this.averageXrayTubeCurrent=null;
this.avarageKv=null;
this.patientOrientation=null;
this.patientOrientationModifier=null;
this.targetRegion =null;
this.xrayFilters = new ArrayList<XrayFilter>();
this.xrayPulses = new ArrayList<XrayPulse>();
}    

public void parseXrayIrradiationEvent(ContentItem root) throws DicomException, ParseException{
   
    //parse plane code concept
    ContentItem acqPlaneIt = root.getNamedChild("DCM","113764");
    if (acqPlaneIt!=null){
    AttributeList acqPlaneLs = acqPlaneIt.getAttributeList();    
    String planeCode = CodedSequenceItem.getSingleCodedSequenceItemOrNull(acqPlaneLs, TagFromName.ConceptCodeSequence).getCodingSchemeDesignator()+
            ":"+CodedSequenceItem.getSingleCodedSequenceItemOrNull(acqPlaneLs, TagFromName.ConceptCodeSequence).getCodeValue();
    this.plane=ConceptCodes.getConceptName(planeCode);
    }
    
    ContentItem testTime=root.getNamedChild("DCM","111526");
    if (testTime!=null){
        String dtAtt= testTime.getAttributeList().get(new AttributeTag("(0x0040,0xa120)")).getSingleStringValueOrNull();
                  
        //never tested the timezone (z) component
        String format="yyyyMMddhhmmss.SSSSSSz";
        format=format.substring(0, dtAtt.trim().length());
        SimpleDateFormat tDate= new SimpleDateFormat(format);
          
        Date test=tDate.parse(dtAtt);
        this.dateTimeStarted = new Timestamp(test.getTime());
    }
    
    //parse eventype concept code 
    ContentItem eventTypeIt=root.getNamedChild("DCM", "113721");
    if(eventTypeIt!=null){
        AttributeList eventTypeLs=eventTypeIt.getAttributeList();
        String IrradiationEventTypeCode = CodedSequenceItem.getSingleCodedSequenceItemOrNull(eventTypeLs, TagFromName.ConceptCodeSequence).getCodingSchemeDesignator()+
            ":"+CodedSequenceItem.getSingleCodedSequenceItemOrNull(eventTypeLs, TagFromName.ConceptCodeSequence).getCodeValue();    
        this.IrradiationEventType=ConceptCodes.getConceptName(IrradiationEventTypeCode);
    }
    
    this.acquisitionProtocol = root.getSingleStringValueOrNullOfNamedChild("DCM", "125203");
    
    this.anatomicalStructure= root.getSingleStringValueOrNullOfNamedChild("SRT", "T-D0005");
        
    //parse laterality cocept code
    ContentItem latIt=root.getNamedChild("SRT", "G-C171");
    if(latIt!=null){
        AttributeList latLs=latIt.getAttributeList();
        String lateralityCode = CodedSequenceItem.getSingleCodedSequenceItemOrNull(latLs, TagFromName.ConceptCodeSequence).getCodingSchemeDesignator()+
            ":"+CodedSequenceItem.getSingleCodedSequenceItemOrNull(latLs, TagFromName.ConceptCodeSequence).getCodeValue();    
        this.laterality=ConceptCodes.getConceptName(lateralityCode);
    }
    
    //parse ref pont as text (can als be code but ignored)
    this.referencePointDefinition = root.getSingleStringValueOrNullOfNamedChild("DCM", "113780");
        
    this.irradiationEventUid = root.getSingleStringValueOrNullOfNamedChild("DCM", "113769");
    
    ContentItem dap = root.getNamedChild("DCM", "122130");
    
    if (dap != null){
        String dapUnit=((ContentItemFactory.NumericContentItem)dap).getUnits().getCodeValue();
        System.out.println(dapUnit);
        switch (dapUnit) {
            case "Gym2":
            case "Gy.m2":
                this.doseAreaProduct= Float.parseFloat(dap.getSingleStringValueOrNull());
                break;
            case "bla":
                break;
            default:
                throw new DicomException("Error parsing DAP, incorrect units");
        }
    }
    
    ContentItem glandDose = root.getNamedChild("DCM", "111631");
    if (glandDose != null){
        String glanDoseUnit=((ContentItemFactory.NumericContentItem)glandDose).getUnits().getCodeValue();
        if(glanDoseUnit.equals("dGy")){
            this.averageGlandularDose= Float.parseFloat(glandDose.getSingleStringValueOrNull());
        } else {
            throw new DicomException("Error parsing glandular dose, incorrect units");
        }
    }
    
    ContentItem doseRef =root.getNamedChild("DCM", "113738");
    if (doseRef != null) {
        String doseRefUnit=((ContentItemFactory.NumericContentItem)doseRef).getUnits().getCodeValue();
        if(doseRefUnit.equals("Gy")){
            this.doseRp = Float.parseFloat(doseRef.getSingleStringValueOrNull());
        } else {
            throw new DicomException("Error parsing dose at reference point, incorrect units");
        }
    }
    
    ContentItem entrExp = root.getNamedChild("DCM", "111636");
    if (entrExp != null){
        String entrExpUnit=((ContentItemFactory.NumericContentItem)doseRef).getUnits().getCodeValue();
        if(entrExpUnit.equals("mGy")){
            this.entranceExposureAtRp= Float.parseFloat(entrExp.getSingleStringValueOrNull());
        }else {
            throw new DicomException("Error parsing entrance exposure at reference point, incorrect units");
        }
    }
    
    ContentItem ppAngle = root.getNamedChild("DCM", "112011");
    if (ppAngle!= null){
        String ppAngleUnit=((ContentItemFactory.NumericContentItem)ppAngle).getUnits().getCodeValue();
        if(ppAngleUnit.equals("deg")){
            this.positionerPrimaryAngle= Float.parseFloat(ppAngle.getSingleStringValueOrNull());
        } else{
            throw new DicomException("Error parsing positioner primary angle, incorrect units");
        }
    }
    
    ContentItem psAngle = root.getNamedChild("DCM", "112012");
    if (psAngle != null){
        String psAngleUnit=((ContentItemFactory.NumericContentItem)psAngle).getUnits().getCodeValue();
        if(psAngleUnit.equals("deg")){
            this.positionerSecondaryAngle= Float.parseFloat(psAngle.getSingleStringValueOrNull());
        } else{
            throw new DicomException("Error parsing positioner secondary angle, incorrect units");
        }
    }
    
    ContentItem ppeAngle = root.getNamedChild("DCM", "113739");
    if (ppeAngle!= null){
        String ppeAngleUnit=((ContentItemFactory.NumericContentItem)ppeAngle).getUnits().getCodeValue();
        if(ppeAngleUnit.equals("deg")){
            this.positionerPrimaryEndAngle= Float.parseFloat(ppeAngle.getSingleStringValueOrNull());
        }else{
            throw new DicomException("Error parsing positioner primary end angle, incorrect units");
        }
    }
    
    ContentItem pseAngle = root.getNamedChild("DCM", "113740");
    if (pseAngle!=null){
        String pseAngleUnit=((ContentItemFactory.NumericContentItem)pseAngle).getUnits().getCodeValue();
        if(pseAngleUnit.equals("deg")){
            this.positionerSecondaryEndAngle= Float.parseFloat(pseAngle.getSingleStringValueOrNull());
        }else{
            throw new DicomException("Error parsing positioner secondary end angle, incorrect units");
        }
    }
    
    ContentItem cAng = root.getNamedChild("DCM", "113770");
    if (cAng!=null){
        String cAngUnit=((ContentItemFactory.NumericContentItem)cAng).getUnits().getCodeValue();
        if(cAngUnit.equals("deg")){
            this.columnAngulation= Float.parseFloat(cAng.getSingleStringValueOrNull());
        }else{
            throw new DicomException("Error parsing column angulation, incorrect units");
        }
    }
    
    String cfArea = root.getSingleStringValueOrNullOfNamedChild("DCM", "113790");
    if (cfArea!=null){this.collimatedFieldArea= Float.parseFloat(cfArea);}
    
    //parse fluoro mode concept code
    ContentItem modeIt = root.getNamedChild("DCM", "113732");
    if (modeIt!= null){
        AttributeList modeLs = modeIt.getAttributeList();
        String fluoroModeCode = CodedSequenceItem.getSingleCodedSequenceItemOrNull(modeLs, TagFromName.ConceptCodeSequence).getCodingSchemeDesignator()+
            ":"+CodedSequenceItem.getSingleCodedSequenceItemOrNull(modeLs, TagFromName.ConceptCodeSequence).getCodeValue();  
        this.fluoroMode=ConceptCodes.getConceptName(fluoroModeCode);
    }
    
    String pRate = root.getSingleStringValueOrNullOfNamedChild("DCM", "113791");
    if (pRate !=null){this.pulseRate=Float.parseFloat(pRate);}
    
    String nPulses = root.getSingleStringValueOrNullOfNamedChild("DCM", "113768");
    if (nPulses!=null){this.numberOfPulses=Integer.parseInt(nPulses);}
    
    String expTime = root.getSingleStringValueOrNullOfNamedChild("DCM", "113735");
    if(expTime!=null){this.exposureTime=Float.parseFloat(expTime);}
    
    String fSpotSize = root.getSingleStringValueOrNullOfNamedChild("DCM", "113766");
    if (fSpotSize!=null){this.focalSpotSize=Float.parseFloat(fSpotSize);}
    
    String iDuration=root.getSingleStringValueOrNullOfNamedChild("DCM", "113742");
    if(iDuration!=null){this.irradiationDuration=Float.parseFloat(iDuration);}
    
    String avTubeCurr=root.getSingleStringValueOrNullOfNamedChild("DCM","113767");
    if(avTubeCurr!=null){this.averageXrayTubeCurrent=Float.parseFloat(avTubeCurr);}
    
    ContentItem orient = root.getNamedChild("DCM", "113743");
    if (orient!=null){
        AttributeList orientLs=orient.getAttributeList();    
        String orientCode = CodedSequenceItem.getSingleCodedSequenceItemOrNull(orientLs, TagFromName.ConceptCodeSequence).getCodingSchemeDesignator()+
            ":"+CodedSequenceItem.getSingleCodedSequenceItemOrNull(orientLs, TagFromName.ConceptCodeSequence).getCodeValue();
        this.patientOrientation=ConceptCodes.getConceptName(orientCode);
        ContentItem orientMod = orient.getNamedChild("DCM", "113744");
        if (orientMod!=null){
            AttributeList orientModLs=orientMod.getAttributeList();    
            String orientModCode = CodedSequenceItem.getSingleCodedSequenceItemOrNull(orientModLs, TagFromName.ConceptCodeSequence).getCodingSchemeDesignator()+
                ":"+CodedSequenceItem.getSingleCodedSequenceItemOrNull(orientModLs, TagFromName.ConceptCodeSequence).getCodeValue();
            this.patientOrientationModifier=ConceptCodes.getConceptName(orientModCode);        
        }
    }
    
    this.targetRegion=root.getSingleStringValueOrNullOfNamedChild("DCM", "123014");
    
    //get number of pulses to parse
    int pulseIterations = 1;        
    if(this.numberOfPulses!=null){pulseIterations=this.numberOfPulses;}
    
    //get individiual items of pulse iteratively
    //<editor-fold>
    
    ContentItem kvIt = (ContentItem)root.getNamedChild("DCM", "113733");
    ContentItem currentIt = (ContentItem)root.getNamedChild("DCM", "113734");
    ContentItem widthIt = (ContentItem)root.getNamedChild("DCM", "113793");
    ContentItem exposureIt = (ContentItem)root.getNamedChild("DCM", "113736");
    
    //initiate default index
    int kvIndex=-1;
    int currentIndex=-1;
    int widthIndex=-1;
    int exposureIndex=-1;
    
    //get index of items ifnot null
    if(kvIt!=null){
        kvIndex = root.getIndex(kvIt);
    }
    if(currentIt!=null){
        currentIndex=root.getIndex(currentIt);
    }    
    if(widthIt!=null){
        widthIndex = root.getIndex(widthIt);
    }
    if(exposureIt!=null){
        exposureIndex = root.getIndex(exposureIt);
    }
    
    //get next item in tree
    for (int i=0;i<pulseIterations;i++){
        XrayPulse thisPulse = new XrayPulse();
        //check if is present and get value
        if(kvIndex!=-1){
            kvIndex=kvIndex+i;
            kvIt = (ContentItem)root.getChildAt(kvIndex);
            if (kvIt.getConceptNameCodeValue().equals("113733")){                       
                String kvString = kvIt.getSingleStringValueOrNull();
                if (kvString!=null){
                    thisPulse.kvp=Float.parseFloat(kvString);
                } 
            } else {
                //if wrong concept code then not kv item this parameter can be ignored next iteration
                kvIndex=-1;
            }
        }
        
        if(currentIndex!=-1){
            currentIndex=currentIndex+i;
            currentIt = (ContentItem)root.getChildAt(currentIndex);
                       
            if (currentIt.getConceptNameCodeValue().equals("113734")){
                String currentString = currentIt.getSingleStringValueOrNull();
                
                if (currentString!=null){
                    thisPulse.xrayTubeCurrent=Float.parseFloat(currentString);
                }
            } else {currentIndex=-1;}
        }
        
        if(widthIndex !=-1){
            widthIndex=widthIndex+i;
            widthIt = (ContentItem)root.getChildAt(widthIndex);

            if (widthIt.getConceptNameCodeValue().equals("113793")){
                String widthString = widthIt.getSingleStringValueOrNull();
                if (widthString!=null){
                    thisPulse.pulseWidth=Float.parseFloat(widthString);
                }
            } else {widthIndex=-1;}
        }
        
        if(exposureIndex!=-1){
            exposureIndex=exposureIndex+i;
            exposureIt = (ContentItem)root.getChildAt(exposureIndex);


            if (exposureIt.getConceptNameCodeValue().equals("113736")){
                String exposureString = exposureIt.getSingleStringValueOrNull();
                if (exposureString!=null){
                    thisPulse.exposure=Float.parseFloat(exposureString);
                }
            } else {exposureIndex=-1;}        
        }
        
        
        if ((kvIndex==-1)&&(currentIndex==-1)&&(widthIndex==-1)&&(exposureIndex==-1)){
            
            break;
        }
        //log.debug("Parsing pulse "+(i+1));
        this.xrayPulses.add(thisPulse);
    }
    
    //Calculate the avarage Kv
    Iterator avKvIt=this.xrayPulses.iterator();
    Float sumKv=new Float(0);
    int count=0;
    XrayPulse tempPulse;
    while(avKvIt.hasNext()){
      tempPulse=(XrayPulse)avKvIt.next();
      if(tempPulse.kvp!=null){
        count++;
        sumKv+=tempPulse.kvp;
      }
    }
    if(sumKv!=0){
      this.avarageKv=sumKv/count;
    }
    //</editor-fold>
    
    //get index of filter and check if more are present
    int filterInd = root.getIndex(root.getNamedChild("DCM", "113771"));
    
    while(((ContentItem)root.getChildAt(filterInd)).getConceptNameCodeValue().equals("113771")){
        ContentItem filterItem = (ContentItem)root.getChildAt(filterInd);
        XrayFilter filterObject = new XrayFilter();
        //log.debug("Parsing X-rayFilter");
        filterObject.parseXrayFilter(filterItem);
        this.xrayFilters.add(filterObject);
        
        filterInd++;
    }                
}

public boolean checkForDb(){
    boolean status = true;
    if (this.plane==null){
        status=false;
        //log.warn("Invalid SR Document: Plane in Irradiation Event unknown");
    }
    if(this.IrradiationEventType==null){
        status=false;
        //log.warn("Invalid SR Document: Irradiation Event Type in Irradiation Event unknown");                
    }
    if (this.dateTimeStarted==null){
        status=false;
        //log.warn("Invalid SR Document: Date Time started in Irradiation Event unknown");                
    }
    if (((this.doseRp!=null)||(this.entranceExposureAtRp!=null))&&this.referencePointDefinition==null){
        status=false;
        //log.warn("Invalid SR Document: Reference Point Definition is unknown");
    }
    if(this.irradiationEventUid==null){
        status=false;
        //log.warn("Invalid SR Document: Irradiation event UID in Irradiation Event unknown");
    }
    if((this.IrradiationEventType.equals("Projection X-ray"))&&this.doseAreaProduct==null){
        status=false;
        //log.warn("Invalid SR Document: Dose Area Product in Irradiation Event unknown");
    }
    if((this.IrradiationEventType.equals("Mammography"))&&this.averageGlandularDose==null){
        status=false;
        //log.warn("Invalid SR Document: Average Glandular Dose in Irradiation Event unknown");
    }
    if(this.IrradiationEventType.equals("Mammography")&&this.entranceExposureAtRp==null){
        status=false;
        //log.warn("Invalid SR Document: Entrance Exposure at RP unknow in mammography report");
    }
    if(this.columnAngulation!=null&&(this.positionerPrimaryAngle!=null||this.positionerSecondaryAngle!=null)){
        status=false;
        //log.warn("Invalid SR Document: Contains both column angulation as positioner angles");
    }
    if((this.positionerPrimaryEndAngle!=null||this.positionerSecondaryEndAngle!=null)&&!this.IrradiationEventType.equals("Rotational Acquisition")){
        status=false;
        //log.warn("Invalid SR Document: Contains Positioner End Angles while not a rotational acquisition");
    }
    if (this.IrradiationEventType.equals("Fluoroscopy")){
        if(this.fluoroMode.equals("Pulsed")&&(this.pulseRate==null)){
            status=false;
            //log.warn("Invalid SR Document: Pulse Fluoroscopy does not contain pulserate");
        }    
        if(this.fluoroMode.equals("Pulsed")&&!((this.xrayPulses.size()==1)||this.xrayPulses.size()==this.numberOfPulses)){
            status=false;
            //log.warn("Invalid SR Document: Pulsed Fluoroscopy does not contain right amount of pulse objects found: " + this.xrayPulses.size() + "expected 1 or " +this.numberOfPulses );
        }
    }
    
    if(this.patientOrientation!=null){
        if(this.patientOrientationModifier==null){
            status = false;
            //log.warn("Invalid Sr Document: patietOrientation does not contain patient Orientation Modifier");
        }
    }

    if(this.targetRegion==null){
        status=false;
        //log.warn("Invalid SR Document: Target region unknown in Irradiation Event");
    }
    
    Iterator i =this.xrayFilters.iterator();
    int k=1;
    while(i.hasNext()){
        if(!((XrayFilter)i.next()).checkForDb()){
        status=false;
        //log.warn("Not a valid RDSR: X-rayfilter with index "+k+" did not pass check");
        k++;
        }
    }
    Iterator j =this.xrayPulses.iterator();
    int l=1;
    while(i.hasNext()){
        if(!((XrayPulse)j.next()).checkForDb()){
        status=false;
        //log.warn("Not a valid RDSR: X-ray Pulse at index "+l+ " did not pass check");
        l++;
        }
    }
    return status;
}

public void insertIntoDb(Connection con, int seriesKey,  int eventKey) throws SQLException{
        
    PreparedStatement sqlInsStmnt = null;    
    try{
        //prepare the SQL statement 
        sqlInsStmnt = con.prepareStatement("INSERT INTO xrayIrradiationEvent (srSeriesIndex, "
                + "eventIndex, plane, dateTimeStarted, irradiationEventType, acquisitionProtocol, anatomicalStructure, "
                + "laterality, referencePointDefinition, irradiationEventUid, doseAreaProduct, averageGlandularDose, "
                + "doseRp, entranceExposureAtRp, positionerPrimaryAngle, positionerSecondaryAngle, "
                + "positionerPrimaryEndAngle, positionerSecondaryEndAngle, columnAngulation, collimatedFieldArea, "
                + "fluoroMode, pulseRate, numberOfPulses, exposureTime,"
                + "focalSpotSize, irradiationDuration, targetRegion, patientOrientation, patientOrientationModifier,"
                + "averageXrayTubeCurrent, averageKv)"
                + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

        sqlInsStmnt.setInt(1, seriesKey);
        sqlInsStmnt.setInt(2, eventKey);
        if (this.plane!=null){sqlInsStmnt.setString(3, this.plane);} else {sqlInsStmnt.setNull(3, 12);}
        if (this.dateTimeStarted != null) {sqlInsStmnt.setTimestamp(4, this.dateTimeStarted);} else {sqlInsStmnt.setNull(4,91);}
        if (this.IrradiationEventType!=null) {sqlInsStmnt.setString(5, this.IrradiationEventType);} else {sqlInsStmnt.setNull(5, 12);}
        if (this.acquisitionProtocol!=null) {sqlInsStmnt.setString(6, this.acquisitionProtocol);} else {sqlInsStmnt.setNull(6, 12);}
        if (this.anatomicalStructure!=null) {sqlInsStmnt.setString(7, this.anatomicalStructure);} else {sqlInsStmnt.setNull(7, 12);}
        if (this.laterality!=null) {sqlInsStmnt.setString(8, this.laterality);} else {sqlInsStmnt.setNull(8, 12);}
        if (this.referencePointDefinition!=null) {sqlInsStmnt.setString(9, this.referencePointDefinition);} else {sqlInsStmnt.setNull(9, 12);}
        if (this.irradiationEventUid!=null) {sqlInsStmnt.setString(10, this.irradiationEventUid);} else {sqlInsStmnt.setNull(10, 12);}
        if (this.doseAreaProduct!=null) {sqlInsStmnt.setFloat(11, this.doseAreaProduct);} else {sqlInsStmnt.setNull(11, 6);}
        if (this.averageGlandularDose!=null) {sqlInsStmnt.setFloat(12, this.averageGlandularDose);} else {sqlInsStmnt.setNull(12, 6);}
        if (this.doseRp!=null) {sqlInsStmnt.setFloat(13, this.doseRp);} else {sqlInsStmnt.setNull(13, 6);}
        if (this.entranceExposureAtRp!=null) {sqlInsStmnt.setFloat(14, this.entranceExposureAtRp);} else {sqlInsStmnt.setNull(14, 6);}
        if (this.positionerPrimaryAngle!=null) {sqlInsStmnt.setFloat(15, this.positionerPrimaryAngle);} else {sqlInsStmnt.setNull(15, 6);}
        if (this.positionerSecondaryAngle!=null) {sqlInsStmnt.setFloat(16, this.positionerSecondaryAngle);} else {sqlInsStmnt.setNull(16, 6);}
        if (this.positionerPrimaryEndAngle!=null) {sqlInsStmnt.setFloat(17, this.positionerPrimaryEndAngle);} else {sqlInsStmnt.setNull(17, 6);}
        if (this.positionerSecondaryEndAngle!=null) {sqlInsStmnt.setFloat(18, this.positionerSecondaryEndAngle);} else {sqlInsStmnt.setNull(18, 6);}
        if (this.columnAngulation!=null) {sqlInsStmnt.setFloat(19, this.columnAngulation);} else {sqlInsStmnt.setNull(19, 6);}
        if (this.collimatedFieldArea!=null) {sqlInsStmnt.setFloat(20, this.collimatedFieldArea);} else {sqlInsStmnt.setNull(20, 6);}
        if (this.fluoroMode!=null) {sqlInsStmnt.setString(21, this.fluoroMode);} else {sqlInsStmnt.setNull(21, 12);}
        if (this.pulseRate!=null) {sqlInsStmnt.setFloat(22, this.pulseRate);} else {sqlInsStmnt.setNull(22, 6);}
        if (this.numberOfPulses!=null) {sqlInsStmnt.setInt(23, this.numberOfPulses);} else {sqlInsStmnt.setNull(23, 4);}
        if (this.exposureTime!=null) {sqlInsStmnt.setFloat(24, this.exposureTime);} else {sqlInsStmnt.setNull(24, 6);}
        if (this.focalSpotSize!=null) {sqlInsStmnt.setFloat(25, this.focalSpotSize);} else {sqlInsStmnt.setNull(25, 6);}
        if (this.irradiationDuration!=null) {sqlInsStmnt.setFloat(26, this.irradiationDuration);} else {sqlInsStmnt.setNull(26, 6);}
        if (this.targetRegion!=null) {sqlInsStmnt.setString(27, this.targetRegion);} else {sqlInsStmnt.setNull(27, 6);}
        if (this.patientOrientation!=null){sqlInsStmnt.setString(28, this.patientOrientation);}else{sqlInsStmnt.setNull(28, 6);}
        if (this.patientOrientationModifier!=null){sqlInsStmnt.setString(29, this.patientOrientationModifier);} else{sqlInsStmnt.setNull(29, 6);}
        if (this.averageXrayTubeCurrent!=null) {sqlInsStmnt.setFloat(30, this.averageXrayTubeCurrent);} else {sqlInsStmnt.setNull(30, 6);}
        if (this.avarageKv!=null) {sqlInsStmnt.setFloat(31, this.avarageKv);} else {sqlInsStmnt.setNull(31, 6);}
        //execute statement
        sqlInsStmnt.executeUpdate();

        //insert all filter objects
        Iterator i = this.xrayFilters.iterator();
        int j =1;
        while (i.hasNext()){
            //log.debug("Inseting X-ray Filter " +j+ " Into Database");
            XrayFilter insertFilter = (XrayFilter)i.next();
            insertFilter.insertIntoDB(con, seriesKey, eventKey,j);
            j++;
        }
        
        //Insert all pulses into the DB
        Iterator k = this.xrayPulses.iterator();
        int l = 1;
        while(k.hasNext()){
            XrayPulse insertPulse = (XrayPulse)k.next();
            //log.debug("Inserting X-ray pulse "+l+" into Database");
            insertPulse.insertIntoDb(con, seriesKey, eventKey, l);
            l++;
        }
    }
    finally{
        if (sqlInsStmnt!= null){
            sqlInsStmnt.close(); 
        }
    }
}        
     
@Override
public String toString(){
    String eol = System.getProperty("line.separator");
    String returnString = "X-ray Irradiation Event:" +eol;
   
    returnString = returnString 
    + "Plane = " + (this.plane != null ? this.plane:"null") + eol
    + "Datetime started = " + (this.dateTimeStarted != null ? this.dateTimeStarted.toString():"null") + eol
    + "Irradiation Event Type = " + (this.IrradiationEventType != null ? this.IrradiationEventType:"null") +eol
    + "Acquisition Protocol = " + (this.acquisitionProtocol!= null ? this.acquisitionProtocol:"null") + eol
    + "Anatomical Structure  = " + (this.anatomicalStructure != null ? this.anatomicalStructure:"null") + eol
    + "Laterality = " + (this.laterality != null ?  this.laterality:"null") + eol
    + "Reference Point Definition = " + (this.referencePointDefinition != null ? this.referencePointDefinition:"null") + eol
    + "Irradiation Event UID = " + (this.irradiationEventUid != null ? this.irradiationEventUid:"null") + eol
    + "Dose Area Product = " + (this.doseAreaProduct != null ? Float.toString(this.doseAreaProduct):"null") +eol
    + "Average Glandular Dose = " + (this.averageGlandularDose != null ? Float.toString(this.averageGlandularDose):"null") +eol
    + "Dose Reference Point = " + (this.doseRp != null ? Float.toString(this.doseRp):"null") +eol
    + "Entrance Exposure at Reference point = " + (this.entranceExposureAtRp != null ? Float.toString(this.entranceExposureAtRp):"null") +eol        
    + "Positioner Primary Angle = " + (this.positionerPrimaryAngle != null ? Float.toString(this.positionerPrimaryAngle):"null") +eol
    + "Positioner Secondary Angle = " + (this.positionerSecondaryAngle != null ? Float.toString(this.positionerSecondaryAngle):"null") +eol
    + "Positioner Primary End Angle = " + (this.positionerPrimaryEndAngle != null ? Float.toString(this.positionerPrimaryEndAngle):"null") +eol
    + "Positioner Secondary End Angle = " + (this.positionerSecondaryEndAngle != null ? Float.toString(this.positionerSecondaryEndAngle):"null") +eol
    + "Column Angulation = " + (this.columnAngulation != null ? Float.toString(this.columnAngulation):"null") +eol
    + "Collimated Field Area = " + (this.collimatedFieldArea != null ? Float.toString(this.collimatedFieldArea):"null") +eol
    + "Fluoromode = " + (this.fluoroMode != null ? this.fluoroMode:"null") +eol        
    + "Pulse rate = " + (this.pulseRate != null ? Float.toString(this.pulseRate):"null") +eol
    + "Number of Pulses = " + (this.numberOfPulses != null ? Integer.toString(this.numberOfPulses):"null") +eol
    + "Exposure Time = " + (this.exposureTime != null ? Float.toString(this.exposureTime):"null") +eol
    + "Focal Spot Size = " + (this.focalSpotSize != null ? Float.toString(this.focalSpotSize):"null") +eol
    + "Irradiation Duration = " + (this.irradiationDuration != null ? Float.toString(this.irradiationDuration):"null") +eol
    + "Patient Orientation = " + (this.patientOrientation!=null? this.patientOrientation:"null")+eol
    + "Patient Orientation Modifier = " +(this.patientOrientationModifier!=null? this.patientOrientationModifier:"null")+eol
    + "Target Region = " + (this.targetRegion != null ? this.targetRegion:"null") +eol
    + "Avarage X-ray Tube Current = " + (this.averageXrayTubeCurrent!=null? this.averageXrayTubeCurrent:"null") +eol
    + "Avarage Kv = " + (this.avarageKv!=null? this.avarageKv:"null")+eol+eol;
    
    //convert filters to String
    Iterator i=this.xrayFilters.iterator();
     
    while (i.hasNext()){
        returnString = returnString + ((XrayFilter)i.next()).toString();
    }
    
    Iterator j=this.xrayPulses.iterator();
    
    while (j.hasNext()){
        returnString = returnString + ((XrayPulse)j.next()).toString();
    }
    return returnString;
}
}
