/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad_sr;
import com.pixelmed.dicom.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.Date;
import java.util.Calendar;
//import org.apache.log4j.*;


/**
 *
 * @author Bart
 */
public class PatientStudySeriesData {
    
    //private static final Logger log = Logger.getLogger(PatientStudySeriesData.class);
    public String patientID;
    public Date patientsBirthDate;
    public String patientsSex;
    public String studyInstanceUid;
    public Date studyDate;
    public String studyTime;
    public String referringPhysiciansName;
    public String studyId;
    public String accessionNumber;
    public String studyDescription;
    public String nameOfPhysicianReadingStudy;
    public String seriesInstanceUid;
    public String requestedProcedureDescription;
    public String srType;
    public Integer age;
    public DeviceObserver deviceObserver;
    public String filename;
    public String path;
    
    public PatientStudySeriesData(String typeString){
        this.patientID = null;
        this.patientsBirthDate = null;
        this.patientsSex = null;
        this.studyInstanceUid = null;
        this.studyDate = null;
        this.studyTime=null;
        this.referringPhysiciansName = null;
        this.studyId = null;
        this.accessionNumber = null;
        this.studyDescription = null;
        this.nameOfPhysicianReadingStudy = null;
        this.seriesInstanceUid = null;
        this.requestedProcedureDescription = null;
        this.srType = typeString;
        this.age=null;
        this.filename=null;
        this.path=null;
        this.deviceObserver=new DeviceObserver();
    } 
    
    public void parsePatientStudySeriesData(ContentItem root) throws DicomException, ParseException{
        AttributeList list = root.getAttributeList();
                
        this.patientID = Attribute.getSingleStringValueOrNull(list, new AttributeTag("(0x0010,0x0020)"));

        Attribute bDate= list.get(new AttributeTag("(0x0010,0x0030)"));
        if(bDate.getSingleStringValueOrNull()!=null){
            this.patientsBirthDate = DateTimeAttribute.getDateFromFormattedString(bDate.getSingleStringValueOrNull());
        }
        this.patientsSex = Attribute.getSingleStringValueOrNull(list, new AttributeTag("(0x0010,0x0040)"));
        this.studyInstanceUid = Attribute.getSingleStringValueOrNull(list, new DicomDictionary().getTagFromName("StudyInstanceUID"));

        Attribute sDate = list.get(new AttributeTag("(0x0008,0x0020)"));
        if(sDate.getSingleStringValueOrNull()!=null){
            this.studyDate = DateTimeAttribute.getDateFromFormattedString(sDate.getSingleStringValueOrNull());
        }
        Attribute sTime=list.get(new AttributeTag("(0x0008,0x0030)"));
        if(sTime.getSingleStringValueOrNull()!=null){
            String time = sTime.getSingleStringValueOrNull().replace(".","/0");
            if (time.length()<6){
                this.studyTime=null;
            } else {
                this.studyTime=time.substring(0, 6);
            }
        }

        this.referringPhysiciansName = Attribute.getSingleStringValueOrNull(list, new AttributeTag("(0x0008,0x0090)"));
        this.studyId=Attribute.getSingleStringValueOrNull(list,new AttributeTag("(0x0020,0x0010)"));
        this.accessionNumber = Attribute.getSingleStringValueOrNull(list, new AttributeTag("(0x0008,0x0050)"));
        this.studyDescription = Attribute.getSingleStringValueOrNull(list, new AttributeTag("(0x0008,0x1030)"));
        this.nameOfPhysicianReadingStudy = Attribute.getSingleStringValueOrNull(list, new AttributeTag("(0x0008,0x1060)"));
        this.seriesInstanceUid = Attribute.getSingleStringValueOrNull(list, new DicomDictionary().getTagFromName("SeriesInstanceUID"));
        this.requestedProcedureDescription = Attribute.getSingleStringValueOrNull(list, new AttributeTag("(0x0032,0x1060)"));
        //log.debug("Parsing Device Observer");

        //calculate the age of the patient at the moment of the study
        Calendar study = Calendar.getInstance();
        Calendar birth= Calendar.getInstance();
        if((this.studyDate!=null)&&(this.patientsBirthDate!=null)){
            study.setTime(this.studyDate);
            birth.setTime(this.patientsBirthDate);
            if(study.after(birth)){
                this.age=study.get(Calendar.YEAR)-birth.get(Calendar.YEAR);
                if(study.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR))  {
                    this.age-=1;
                }
            }
        }

        this.deviceObserver.parseDeviceObserver(root);
    }
    
    public boolean checkForDb(){
        boolean status = true;
        
        if(this.patientID == null){
            //log.warn("Incorrect SR Document: Patient ID missing");
            status = false; return status;
        } else if (this.patientsBirthDate == null){
            //log.warn("Incorrect SR Document: Patient's Bith dat is missing");
            status = false; return status;
        } else if (this.studyInstanceUid == null){
            //log.warn("Incorrect SR Document: Study Instance UID is missing");
            status = false; return status;
        } else if (this.studyDate == null){
            //log.warn("Incorrect SR Doument: Study Date is missing");
            status = false; return status;
        } else if (this.seriesInstanceUid == null){
            //log.warn("Incorrect SR Document: Series Instance UID is missing");
            status = false; return status;
        }  else if (this.srType == null){
            //log.warn("Incorrect SR Document SR type is missing");
            status = false; return status;
        }  else if (!this.deviceObserver.checkForDb()){
            status=false;return status;
        }          
        return status;
    }
    
    
    public int insertIntoDb(Connection con) throws SQLException{
    
    //checksum
    int returnKey = -1;
    int deviceKey = -1;
    
    //create instances for DB Query
    ResultSet keyResult =null;
    PreparedStatement sqlInsStmnt = null;
   
    try{
        //prepare the SQL statement
        
        //log.debug("Inserting Device Observer into DB");
        deviceKey=this.deviceObserver.insertIntoDb(con);
        
        
        sqlInsStmnt = con.prepareStatement("INSERT INTO srSeries (seriesInstanceUID, accessionNumber, "
                + "srType, patientID, patientsBirthdate, patientsSex, studyInstanceUid, studyDate, studyDescription, "
                + "requestedProcedureDescription, referringPhysiciansname, nameOfPhysicianReadingStudy,studyId,deviceIndex, "
                + "age, studyTime, fileName,path) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
        if (this.seriesInstanceUid != null){sqlInsStmnt.setString(1, this.seriesInstanceUid);} else {sqlInsStmnt.setNull(1, 12);};
        if (this.accessionNumber != null){sqlInsStmnt.setString(2, this.accessionNumber);} else {sqlInsStmnt.setNull(2, 12);};
        if (this.srType != null) { sqlInsStmnt.setString(3, this.srType);} else {sqlInsStmnt.setNull(3, 12);};
        if (this.patientID != null) {sqlInsStmnt.setString(4, this.patientID);} else {sqlInsStmnt.setNull(4, 12);};
        if (this.patientsBirthDate != null) {sqlInsStmnt.setDate(5, new java.sql.Date(this.patientsBirthDate.getTime()));} else {sqlInsStmnt.setNull(5,91);}
        if (this.patientsSex!=null){sqlInsStmnt.setString(6, this.patientsSex);} else {sqlInsStmnt.setNull(6, 12);}
        if (this.studyInstanceUid!=null){sqlInsStmnt.setString(7, this.studyInstanceUid);} else {sqlInsStmnt.setNull(7, 12);}
        if (this.studyDate!=null){sqlInsStmnt.setDate(8, new java.sql.Date(this.studyDate.getTime()));} else {sqlInsStmnt.setNull(8, 91);}
        if (this.studyDescription!=null){sqlInsStmnt.setString(9, this.studyDescription);} else {sqlInsStmnt.setNull(9, 12);}
        if (this.requestedProcedureDescription!=null){sqlInsStmnt.setString(10, this.requestedProcedureDescription);} else {sqlInsStmnt.setNull(10, 12);}
        if (this.referringPhysiciansName!=null){sqlInsStmnt.setString(11, this.referringPhysiciansName);} else {sqlInsStmnt.setNull(11, 12);}
        if (this.nameOfPhysicianReadingStudy!=null){sqlInsStmnt.setString(12, this.nameOfPhysicianReadingStudy);} else {sqlInsStmnt.setNull(12, 12);}
        if (this.studyId!=null){sqlInsStmnt.setString(13, this.studyId);}else{sqlInsStmnt.setNull(13,12);}
        sqlInsStmnt.setInt(14, deviceKey);
        if (this.age!=null){sqlInsStmnt.setInt(15, this.age);}else{sqlInsStmnt.setNull(15, 4);}
        if (this.studyTime!=null){sqlInsStmnt.setString(16, this.studyTime);}else{sqlInsStmnt.setNull(16, 12);}
        if (this.filename!=null){sqlInsStmnt.setString(17, this.filename);}else{sqlInsStmnt.setNull(17, 12);}
        if (this.path!=null){sqlInsStmnt.setString(18, this.path);}else{sqlInsStmnt.setNull(18,12);}       
        
        
        //execute statement
        sqlInsStmnt.executeUpdate();
        //get the new primary key
        keyResult = sqlInsStmnt.getGeneratedKeys();
        //ResultSet sqlResult = null;

        if(keyResult.next()){
            returnKey=keyResult.getInt(1);
            //log.debug("Database returened Series Index: "+returnKey);
        }
        return returnKey;
    }  

    finally{
        if(keyResult!=null){    
            keyResult.close();
        }
        if (sqlInsStmnt!= null){
            sqlInsStmnt.close(); 
        }
        return returnKey;
    }   
}
   
    @Override
    public String toString(){
        String eol = System.getProperty("line.separator");
        String returnString = "Study Information:" +eol;
        
        returnString = returnString 
        + "Patient ID = " + (this.patientID != null ? this.patientID:"null") + eol
        + "Patient's Birth Date = " + (this.patientsBirthDate != null ? this.patientsBirthDate:"null") + eol
        + "Patient's Sex = " + (this.patientsSex != null ? this.patientsSex:"null") +eol
        + "Study Instance UID = " + (this.studyInstanceUid!= null ? this.studyInstanceUid:"null") + eol
        + "Study Date = " + (this.studyDate != null ? this.studyDate.toString():"null") + eol
        + "Referring Physician's Name = " + (this.referringPhysiciansName != null ?  this.referringPhysiciansName:"null") + eol
        + "Study ID = " + (this.studyId!=null? this.studyId:"null") +eol        
        + "Accession Number = " + (this.accessionNumber != null ? this.accessionNumber:"null") + eol
        + "Study Description = " + (this.studyDescription != null ?  this.studyDescription:"null") + eol 
        + "Name of Physician Reading Study = " + (this.nameOfPhysicianReadingStudy != null ?  this.nameOfPhysicianReadingStudy:"null") + eol
        + "Series Instance UID = " + (this.seriesInstanceUid != null ?  this.seriesInstanceUid:"null") +eol
        + "Requested Procedure Description = " + (this.requestedProcedureDescription != null ?  this.requestedProcedureDescription:"null") + eol
        + "SR RDSR Type = " + (this.srType != null ?  this.srType:"null")+eol
        + "Age at Study date = " + (this.age!=null? this.age:"null") +eol 
        + "Filename = " +(this.filename!=null? this.filename:"null") +eol
        + "Path = " + (this.path!=null? this.path:"null")+eol+eol
        + this.deviceObserver.toString();
         
        
        return returnString;
    }
}
