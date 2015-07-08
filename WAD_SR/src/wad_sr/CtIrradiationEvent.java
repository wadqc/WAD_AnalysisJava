/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad_sr;
import com.pixelmed.dicom.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
// import org.apache.log4j.Logger;

/**
 *
 * @author Bart
 */
public class CtIrradiationEvent {
    // private static final Logger log = Logger.getLogger(CtIrradiationEvent.class);
    public String acquisitionProtocol;
    public String targetRegion;
    public String acquisitionType;
    public String irradiationEventUid;
    public Float exposureTime;
    public Float scanningLength;
    public Float lengthOfReconstructableVolume;
    public Float exposedRange;
    public Float nominalSingleCollimationWidth;
    public Float nominalTotalCollimationWidth;
    public Float pitchFactor;
    public Integer numberOfXraySources;
    public Float meanCtdiVol;
    public String ctdiwPhantomType;
    public Float dlp;
    public Float effectiveDose;
    public String xrayModulationType;
    public ArrayList<CtXraySourceParameters> sourceParameters;
    
    public CtIrradiationEvent(){
        this.acquisitionProtocol=null;
        this.targetRegion = null;
        this.acquisitionType = null;
        this.irradiationEventUid = null;
        this.exposureTime = null;
        this.scanningLength = null;
        this.lengthOfReconstructableVolume=null;
        this.exposedRange=null;
        this.nominalSingleCollimationWidth = null;
        this.nominalTotalCollimationWidth= null;
        this.pitchFactor=null;
        this.numberOfXraySources = null;
        this.meanCtdiVol = null;
        this.ctdiwPhantomType = null;
        this.dlp = null;
        this.effectiveDose=null;
        this.xrayModulationType=null;
        this.sourceParameters = new ArrayList();
    }
            
    public void parseCtIrradiationEvent(ContentItem root) throws DicomException{
        
        this.acquisitionProtocol = root.getSingleStringValueOrNullOfNamedChild("DCM", "125203");
        this.targetRegion = root.getSingleStringValueOrNullOfNamedChild("DCM", "123014");

        //parse acquisition type concept code
        this.acquisitionType=root.getSingleStringValueOrNullOfNamedChild("DCM", "113820");
        ContentItem acqIt = root.getNamedChild("DCM", "113820");
        if (acqIt!=null){
            AttributeList acqLs =acqIt.getAttributeList();  
            String acquisitionTypeCode=CodedSequenceItem.getSingleCodedSequenceItemOrNull(acqLs, TagFromName.ConceptCodeSequence).getCodingSchemeDesignator()+
            ":"+CodedSequenceItem.getSingleCodedSequenceItemOrNull(acqLs, TagFromName.ConceptCodeSequence).getCodeValue();
            this.acquisitionType= ConceptCodes.getConceptName(acquisitionTypeCode);
        }


        this.irradiationEventUid = root.getSingleStringValueOrNullOfNamedChild("DCM", "113769");

        //acquisition parameters
        ContentItem acquisitionParameters = root.getNamedChild("DCM", "113822");

        String exTime = acquisitionParameters.getSingleStringValueOrNullOfNamedChild("DCM", "113824");
        if(exTime!=null){
            this.exposureTime = Float.parseFloat(exTime);
        }

        String scanLength = acquisitionParameters.getSingleStringValueOrNullOfNamedChild("DCM", "113825");
        if(scanLength!=null){                
            this.scanningLength = Float.parseFloat(scanLength);
        }

         String reconLength = root.getSingleStringValueOrNullOfNamedChild("DCM", "113893");
        if(reconLength!=null){
           this.lengthOfReconstructableVolume=Float.parseFloat(reconLength);
        }

        String exLength = root.getSingleStringValueOrNullOfNamedChild("DCM", "113899");
        if(exLength!=null){
            this.exposedRange=Float.parseFloat(exLength);
        }

        String sColWidth = acquisitionParameters.getSingleStringValueOrNullOfNamedChild("DCM", "113826");
        if(sColWidth!=null){
            this.nominalSingleCollimationWidth = Float.parseFloat(sColWidth);
        }

        String nColWidth = acquisitionParameters.getSingleStringValueOrNullOfNamedChild("DCM", "113827");
        if (nColWidth!=null){
            this.nominalTotalCollimationWidth = Float.parseFloat(nColWidth);
        }


        String pFactor =acquisitionParameters.getSingleStringValueOrNullOfNamedChild("DCM", "113828");
        if(pFactor!=null){
            this.pitchFactor = Float.parseFloat(pFactor);
        }

        this.numberOfXraySources = Integer.parseInt(acquisitionParameters.getSingleStringValueOrNullOfNamedChild("DCM", "113823"));

        //source parameters

        //Get First Item
        ContentItem sourceParam = acquisitionParameters.getNamedChild("DCM", "113831");
        if (sourceParam!=null){
            int sourceIndex=acquisitionParameters.getIndex(sourceParam);
            int max =acquisitionParameters.getChildCount();

            //iterate through number of items
            for(int ind = sourceIndex;ind<max;ind++){
                ContentItem insertIt = (ContentItem)acquisitionParameters.getChildAt(ind);
                if (insertIt==null){
                    // log.error("Unexpected null for child at index "+ind+ "return while parsing source parameters in CT irradiation Event");
                    throw new DicomException("Unexpected null for child at index "+ind+ "return while parsing source parameters in CT irradiation Event");
                } else if (insertIt.getConceptNameCodeValue().equals("113831")){
                    //get next source parameters and parse
                    CtXraySourceParameters insertSource =  new CtXraySourceParameters();
                    // log.debug("Parsing CT X-ray Source Parameters");
                    insertSource.parseCtXraySourceParameters(insertIt);
                    this.sourceParameters.add(insertSource);
                } else{break;}
            }
        }    

        //dose parameters
        ContentItem ctDose = root.getNamedChild("DCM","113829");
        if (ctDose!= null){
            String mCtdi = ctDose.getSingleStringValueOrNullOfNamedChild("DCM", "113830");
            if (mCtdi!=null){
                this.meanCtdiVol = Float.parseFloat(mCtdi);
            }

            this.ctdiwPhantomType = ctDose.getSingleStringValueOrNullOfNamedChild("DCM", "113835");     

            String doseLp = ctDose.getSingleStringValueOrNullOfNamedChild("DCM", "113838");
            if (doseLp!=null){
                this.dlp = Float.parseFloat(doseLp);
            }

            String eDose=ctDose.getSingleStringValueOrNullOfNamedChild("DCM", "113839");
            if(eDose!=null){
                this.effectiveDose=Float.parseFloat(eDose);
            }
            this.xrayModulationType= root.getSingleStringValueOrNullOfNamedChild("DCM", "113842");
        }
    }
    
    public boolean checkForDb(){
        boolean status = true;
        
        if(this.targetRegion == null){
            status = false;
            // log.warn("Invalid SR Document targetRegion in CT irradiation Event is empty");
        } else if (this.acquisitionType == null){
            status = false;
            //log.warn("Invalid SR Document acquisition type in CT irradiation Event is empty");
        } else if (this.irradiationEventUid == null){
            status = false;
            //log.warn("Invalid SR Document irradiation event UID in CT irradiation Event is empty");
        } else if (this.exposureTime == null){
            status = false;
            //log.warn("Invalid SR Document exposure Time in CT irradiation Event is empty");
        } else if (this.scanningLength == null){
            status = false;
            //log.warn("Invalid SR Document Scanning Length in CT irradiation Event is empty");
        } else if (this.nominalSingleCollimationWidth == null){
            status = false;
            //log.warn("Invalid SR Document nominal single collimation width in CT irradiation Event is empty");
        } else if (this.nominalTotalCollimationWidth== null){
            status = false;
            //log.warn("Invalid SR Document nominal total collimation width in CT irradiation Event is empty");
        } else if (this.pitchFactor== null && (this.acquisitionType.equals("Spiral Acquisition")||this.acquisitionType.equals("Sequenced Acquisition") )){
            status=false;
            //log.warn("Invalid SR Document pitch factor in CT irradiation Event is empty");
        } else if (this.numberOfXraySources== null){
            status = false;
            //log.warn("Invalid SR Document numberofXray Sources in CT irradiation Event is empty");
        } else if (this.meanCtdiVol == null ^ this.acquisitionType.equals("Constant Angle Acquisition"))  {
            status = false;
            //log.warn("Invalid SR Document mean CTDI vol in CT irradiation Event is empty");
        } else if (this.ctdiwPhantomType== null ^ this.acquisitionType.equals("Constant Angle Acquisition")){
           status = false;
           //log.warn("Invalid SR Document ctdiw phatnom type in CT irradiation Event is empty");
        } else if (this.dlp== null ^ this.acquisitionType.equals("Constant Angle Acquisition")){
           status = false;
           //log.warn("Invalid SR Document dlp in CT irradiation Event is empty"); 
        }         
        
        Iterator i=this.sourceParameters.iterator();
        int j=1;
        while(i.hasNext()){
            CtXraySourceParameters testParam = (CtXraySourceParameters)i.next();
            if(!testParam.checkForDb()){
                status=false;
                //log.warn("CT Sourceparameters with index "+j+ "failed check");
            }
            j++;
        }
        
        return status;
    }
    
    public void insertIntoDb(Connection con, int primaryKey, int eventIndex) throws SQLException{
        
    PreparedStatement sqlInsStmnt = null;  
    try{
        //prepare the SQL statement 
        sqlInsStmnt = con.prepareStatement("INSERT INTO ctIrradiationEvent (srSeriesIndex, eventIndex,"
                + " irradiationEventUid, acquisitionProtocol, targetRegion, ctAcquisitionType, "
                + " exposureTime, scanningLength,lengthOfReconstructableVolume, exposedRange,"
                + " nominalSingleCollimationWidth, nominalTotalCollimationWidth,"
                + " pitchFactor, numberOfXraySources,"
                + " meanCtdiVol, ctdiwPhantomType, dlp, effectiveDose, xrayModulationType)"
                + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

        sqlInsStmnt.setInt(1, primaryKey);
        sqlInsStmnt.setInt(2, eventIndex);
        if (this.irradiationEventUid!=null){sqlInsStmnt.setString(3, this.irradiationEventUid);} else {sqlInsStmnt.setNull(3, 12);}
        if (this.acquisitionProtocol!=null){sqlInsStmnt.setString(4, this.acquisitionProtocol);} else {sqlInsStmnt.setNull(4, 12);}
        if (this.targetRegion!=null) {sqlInsStmnt.setString(5, this.targetRegion);} else {sqlInsStmnt.setNull(5, 12);}
        if (this.acquisitionType!=null) {sqlInsStmnt.setString(6, this.acquisitionType);} else {sqlInsStmnt.setNull(6, 12);}
        if (this.exposureTime!=null) {sqlInsStmnt.setFloat(7, this.exposureTime);} else {sqlInsStmnt.setNull(7, 6);}
        if (this.scanningLength!=null){sqlInsStmnt.setFloat(8, this.scanningLength);} else {sqlInsStmnt.setNull(8, 6);}
        if (this.lengthOfReconstructableVolume!=null){sqlInsStmnt.setFloat(9, this.lengthOfReconstructableVolume);} else {sqlInsStmnt.setNull(9, 6);}
        if (this.exposedRange!=null){sqlInsStmnt.setFloat(10, this.exposedRange);} else {sqlInsStmnt.setNull(10, 6);}
        if (this.nominalSingleCollimationWidth!=null){sqlInsStmnt.setFloat(11, this.nominalSingleCollimationWidth);} else {sqlInsStmnt.setNull(11, 6);}
        if (this.nominalTotalCollimationWidth!=null){sqlInsStmnt.setFloat(12, this.nominalTotalCollimationWidth);} else {sqlInsStmnt.setNull(12, 6);}
        if (this.pitchFactor != null){sqlInsStmnt.setFloat(13, this.pitchFactor);} else {sqlInsStmnt.setNull(13, 6);}
        if (this.numberOfXraySources!=null){sqlInsStmnt.setInt(14, this.numberOfXraySources);}else {sqlInsStmnt.setNull(14, 6);}
        if (this.meanCtdiVol!=null){sqlInsStmnt.setFloat(15, this.meanCtdiVol);} else {sqlInsStmnt.setNull(15, 6);}
        if (this.ctdiwPhantomType!=null){sqlInsStmnt.setString(16, this.ctdiwPhantomType);} else {sqlInsStmnt.setNull(16, 12);}
        if (this.dlp!= null) {sqlInsStmnt.setFloat(17, this.dlp);} else {sqlInsStmnt.setNull(17,6);}
        if (this.effectiveDose!=null){sqlInsStmnt.setFloat(18, this.effectiveDose);} else{sqlInsStmnt.setNull(18, 6);}
        if (this.xrayModulationType!=null){sqlInsStmnt.setString(19, this.xrayModulationType);} else {sqlInsStmnt.setNull(19, 12);}

        //execute statement
        sqlInsStmnt.executeUpdate();         

        Iterator i =this.sourceParameters.iterator();
        int j=1;
        while(i.hasNext()){
            //log.debug("Inserting SourceParameters "+j+" into Database");
            CtXraySourceParameters ctSourceIns = (CtXraySourceParameters)i.next();
            ctSourceIns.insertIntoDb(con, primaryKey, eventIndex, j);
            j++;
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
        String returnString = "CT Irradiation Event:"+eol;
        returnString = returnString 
        + "Acquisition Protocol = " + (this.acquisitionProtocol != null ? this.acquisitionProtocol:"null") + eol
        + "Target Region = " + (this.targetRegion != null ? this.targetRegion:"null") + eol
        + "Acquisition Type = " + (this.acquisitionType != null ? this.acquisitionType:"null") +eol
        + "Irradiation Event UID = " + (this.irradiationEventUid!= null ? this.irradiationEventUid:"null") + eol
        + "Exposure Time = " + (this.exposureTime != null ? Float.toString(this.exposureTime):"null") + eol
        + "Scanning Length = " + (this.scanningLength != null ?  Float.toString(this.scanningLength):"null") + eol
        + "Length of Reconstructable Volume = " +(this.lengthOfReconstructableVolume!=null? this.lengthOfReconstructableVolume.toString():"null")+eol
        + "Exposed Range = "+(this.exposedRange!=null? this.exposedRange.toString():"null")+eol
        + "Nominal Single Collimation Width = " + (this.nominalSingleCollimationWidth != null ?  Float.toString(this.nominalSingleCollimationWidth):"null") + eol
        + "Nominal Total Colimation Width = " + (this.nominalTotalCollimationWidth != null ?  Float.toString(this.nominalTotalCollimationWidth):"null") + eol 
        + "Pitch Factor = " + (this.pitchFactor != null ?  Float.toString(this.pitchFactor):"null") + eol
        + "Number of X-ray Sources = " + (this.numberOfXraySources != null ?  Float.toString(this.numberOfXraySources):"null") +eol
        + "Mean CTDIvol = " + (this.meanCtdiVol != null ?  Float.toString(this.meanCtdiVol):"null") +eol
        + "CTDIw Phantom Type = " + (this.ctdiwPhantomType != null ?  this.ctdiwPhantomType:"null") +eol
        + "Dose Length Product = " + (this.dlp != null ?  Float.toString(this.dlp):"null")+eol
        + "Effective Dose = " +(this.effectiveDose!=null? this.effectiveDose.toString():"null")+eol
        + "X-Ray Modulation Type = " + (this.xrayModulationType!=null? this.xrayModulationType:"null")+eol
        +eol;
        
        Iterator i = this.sourceParameters.iterator();
        while(i.hasNext()){
            returnString = returnString+((CtXraySourceParameters)i.next()).toString();
        }
        
        return returnString;
    }
 }
    

