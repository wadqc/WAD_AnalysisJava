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
// import org.apache.log4j.Logger;
        
/**
 *
 * @author Bart
 */
public class DeviceObserver {

public String deviceObserverUID;
public String deviceObserverManufacturer;
public String deviceObserverModelName;
public String deviceObserverSerialNumber;
public String deviceObserverPhysicalLocation;

public String manufacturer;
public String institutionName;
public String stationName;
public String institutionalDepartmentName;
public String manufacturersModelName;
public String deviceSerialNumber;
public String softwareVersions;

// private static final Logger log = Logger.getLogger(DeviceObserver.class);

public DeviceObserver(){
    this.deviceObserverUID=null;
    this.deviceObserverManufacturer=null;
    this.deviceObserverModelName=null;
    this.deviceObserverSerialNumber=null;
    this.deviceObserverPhysicalLocation=null;
    
    this.manufacturer=null;
    this.institutionName=null;
    this.stationName=null;
    this.institutionalDepartmentName=null;
    this.manufacturersModelName=null;
    this.deviceSerialNumber=null;
    this.softwareVersions=null;
}

public void parseDeviceObserver(ContentItem root)throws DicomException{
 
    this.deviceObserverUID = root.getSingleStringValueOrNullOfNamedChild("DCM", "121012");
    this.deviceObserverManufacturer = root.getSingleStringValueOrNullOfNamedChild("DCM", "121014");
    this.deviceObserverModelName = root.getSingleStringValueOrNullOfNamedChild("DCM", "121015");
    this.deviceObserverSerialNumber = root.getSingleStringValueOrNullOfNamedChild("DCM", "121016");
    this.deviceObserverPhysicalLocation = root.getSingleStringValueOrNullOfNamedChild("DCM", "121017"); 

    AttributeList list = root.getAttributeList();
    this.manufacturer = Attribute.getSingleStringValueOrNull(list, new AttributeTag("(0x0008,0x0070)"));
    this.institutionName = Attribute.getSingleStringValueOrNull(list, new AttributeTag("(0x0008,0x0080)"));
    this.stationName = Attribute.getSingleStringValueOrNull(list, new AttributeTag("(0x0008,0x1010)"));
    this.institutionalDepartmentName = Attribute.getSingleStringValueOrNull(list, new AttributeTag("(0x0008,0x1040)"));
    this.manufacturersModelName = Attribute.getSingleStringValueOrNull(list, new AttributeTag("(0x0008,0x1090)"));
    this.deviceSerialNumber = Attribute.getSingleStringValueOrNull(list, new AttributeTag("(0x0018,0x1000)"));
    this.softwareVersions=Attribute.getSingleStringValueOrNull(list, new AttributeTag("(0x0018,0x1020)"));   
}

public boolean checkForDb(){
    boolean status = true;
    
    if(this.deviceObserverUID == null){
        //log.warn("Invalid RDSR: Device UID is missing");
        status=false;
    }
    
    if(this.manufacturer==null){
        status=false;
        //log.warn("Invalid RDSR: Device Manufacturer is Missing");
    } else if (this.deviceObserverManufacturer!=null){
        if(!this.manufacturer.trim().equalsIgnoreCase(this.deviceObserverManufacturer.trim())){
            status=false;
            //log.warn("Device manufacturer and Device Observer manufacturer are not equal");
        }
    }
    
    if(this.manufacturersModelName==null){
        status=false;
        //log.warn("Invalid RDSR Manufacture model name is empty");
    } else if(this.deviceObserverModelName!=null){
        if(!this.manufacturersModelName.trim().equalsIgnoreCase(this.deviceObserverModelName.trim())){
            status=false;
            //log.warn("Device Model Name and device observer model name are not equal");                   
        }
    }
    
    if(this.deviceSerialNumber==null){
        status=false;
        //log.warn("Invalid RDSR: Device Serial Number is empty");
    } else if(this.deviceObserverSerialNumber!=null) {
        if(!this.deviceSerialNumber.trim().equalsIgnoreCase(this.deviceObserverSerialNumber.trim())){
            status=false;
            //log.warn("Device Serial Number and device Observer Serial number are not equal");
        }
    }
    
    if(this.softwareVersions==null){
        status=false;
        //log.warn("Invalid RDSR: Software Version is empty");
    }
    
    return status;
}

public int insertIntoDb(Connection con) throws SQLException{
    int deviceKey = -1;
    Statement queryStmnt;
    PreparedStatement sqlInsStmnt = null;
    ResultSet keyResult = null;
        
    try{
        queryStmnt = con.createStatement();

        String query = "SELECT deviceIndex FROM devices WHERE "
                + "deviceObserverUid "  + (this.deviceObserverUID==null? "IS NULL":"= '"+ this.deviceObserverUID+"'")
                + " AND deviceObserverManufacturer " + (this.deviceObserverManufacturer==null? "IS NULL":"= '"+this.deviceObserverManufacturer+"'")
                + " AND deviceObserverModelName " + (this.deviceObserverModelName==null? "IS NULL":"= '"+this.deviceObserverModelName+"'")
                + " AND deviceObserverSerialNumber " + (this.deviceObserverSerialNumber==null? "IS NUll":"= '"+this.deviceObserverSerialNumber+"'")
                + " AND deviceObserverPhysicalLocation " + (this.deviceObserverPhysicalLocation==null? "IS NULL":"= '"+this.deviceObserverPhysicalLocation+"'")
                + " AND manufacturer " + (this.manufacturer==null? "IS NULL":"= '"+this.manufacturer+"'")
                + " AND institutionName " + (this.institutionName==null? "IS NULL":"= '"+this.institutionName+"'")
                + " AND stationName " + (this.stationName==null? "IS NUll":"= '"+this.stationName+"'")
                + " AND institutionalDepartmentName " + (this.institutionalDepartmentName==null? "IS NULL":"= '"+this.institutionalDepartmentName+"'")
                + " AND manufacturersModelName " + (this.manufacturersModelName==null? "IS NULL":"= '"+this.manufacturersModelName+"'")
                + " AND deviceSerialNumber " + (this.deviceSerialNumber==null? "IS NULL":"= '"+this.deviceSerialNumber+"'")
                + " AND softwareVersions " + (this.softwareVersions==null? "IS NULL":"= '"+this.softwareVersions+"'")
                + ";";
       keyResult =queryStmnt.executeQuery(query);
       
       if (keyResult.next()){
           deviceKey = keyResult.getInt("deviceIndex");
           keyResult.close();
           //log.debug("Known device: Databse returned Device Index "+deviceKey);
           return deviceKey;
       } else {
           //log.debug("Unknown Device: Inserting new device into Device table");
           keyResult.close();
           //prepare the SQL statement 
           sqlInsStmnt = con.prepareStatement("INSERT INTO devices (deviceObserverUid, "
                   + "deviceObserverManufacturer, deviceObserverModelName, deviceObserverSerialNumber, "
                   + "deviceObserverPhysicalLocation, manufacturer, institutionName, stationName, "
                   + "institutionalDepartmentName, manufacturersModelName, deviceSerialNumber, "
                   + "softwareVersions) "
                   + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);

           if (this.deviceObserverUID!=null){sqlInsStmnt.setString(1, this.deviceObserverUID);} else {sqlInsStmnt.setNull(1, 12);}
           if (this.deviceObserverManufacturer!=null){sqlInsStmnt.setString(2, this.deviceObserverManufacturer);} else {sqlInsStmnt.setNull(2, 12);}
           if (this.deviceObserverModelName!=null){sqlInsStmnt.setString(3, this.deviceObserverModelName);} else {sqlInsStmnt.setNull(3, 12);}
           if (this.deviceObserverSerialNumber!=null){sqlInsStmnt.setString(4, this.deviceObserverSerialNumber);} else {sqlInsStmnt.setNull(4, 12);}
           if (this.deviceObserverPhysicalLocation!= null) {sqlInsStmnt.setString(5, this.deviceObserverPhysicalLocation);} else {sqlInsStmnt.setNull(5, 12);}
           if (this.manufacturer!=null){sqlInsStmnt.setString(6, this.manufacturer);} else {sqlInsStmnt.setNull(6, 12);}
           if (this.institutionName!=null){sqlInsStmnt.setString(7, this.institutionName);} else {sqlInsStmnt.setNull(7, 12);}
           if (this.stationName!=null){sqlInsStmnt.setString(8, this.stationName);} else {sqlInsStmnt.setNull(8, 12);}
           if (this.institutionalDepartmentName!=null){sqlInsStmnt.setString(9, this.institutionalDepartmentName);} else {sqlInsStmnt.setNull(9, 12);}
           if (this.manufacturersModelName!=null){sqlInsStmnt.setString(10, this.manufacturersModelName);} else {sqlInsStmnt.setNull(10, 12);}
           if (this.deviceSerialNumber!= null) {sqlInsStmnt.setString(11, this.deviceSerialNumber);} else {sqlInsStmnt.setNull(11, 12);}
           if (this.softwareVersions!=null){sqlInsStmnt.setString(12, this.softwareVersions);} else {sqlInsStmnt.setNull(12, 12);}
           
           //execute statement
           sqlInsStmnt.executeUpdate();
           ResultSet deviceKeyRes=sqlInsStmnt.getGeneratedKeys();

           if(deviceKeyRes.next()){
               deviceKey=deviceKeyRes.getInt(1);
               //log.debug("Device Inserted, Database returned deviceIndex: "+deviceKey);
           }
           return deviceKey;
        }
    }  
    finally{
        //close all statments and resultssets if exception occurs
        if(keyResult!=null){    
            keyResult.close();
        }
        if (sqlInsStmnt!= null){
            sqlInsStmnt.close(); 
        }
    }
}
        

@Override
public String toString(){
    String eol = System.getProperty("line.separator");
    String returnString = "Device Observer:"+eol;
    returnString = returnString 
    + "Device Observer UID = " + (this.deviceObserverUID != null ? this.deviceObserverUID:"null") + eol
    + "Device Observer Manufacturer = " + (this.deviceObserverManufacturer != null ? this.deviceObserverManufacturer:"null") + eol
    + "Device Observer Model Name = " + (this.deviceObserverModelName != null ? this.deviceObserverModelName:"null") +eol
    + "Device Observer Serial Number = " + (this.deviceObserverSerialNumber!= null ? this.deviceObserverSerialNumber:"null") + eol
    + "Device Observer Physical Location  = " + (this.deviceObserverPhysicalLocation != null ? this.deviceObserverPhysicalLocation:"null") + eol 
    + "Manufacturer = " +(this.manufacturer!=null? this.manufacturer:"null")+eol
    + "Institution Name = " +(this.institutionName!=null? this.institutionName:"null")+eol
    + "Station Name = " +(this.stationName!=null? this.stationName:"null")+eol
    + "Institutional Department Name = "+(this.institutionalDepartmentName!=null? this.institutionalDepartmentName:"null")+eol
    + "Manufacturer's Model Name = "+(this.manufacturersModelName!=null? this.manufacturersModelName:"null")+eol
    + "Device Serial Number = " + (this.deviceSerialNumber!=null? this.deviceSerialNumber:"null")+eol
    + "Software Version = " +(this.softwareVersions!=null? this.softwareVersions:"null")+eol;
    
    return returnString;
}

}

