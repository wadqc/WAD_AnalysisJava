/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad_sr;
import com.pixelmed.dicom.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
//import org.apache.log4j.Logger;
/**
 *
 * @author Bart
 */
public class CtXraySourceParameters {
    //private static final Logger log = Logger.getLogger(CtXraySourceParameters.class);
    public String xraySourceIdentification;
    public Float kvp;
    public Float maximumXrayTubeCurrent;
    public Float xrayTubeCurrent;
    public Float exposureTimePerRotation;
    public Float xrayFilterAluminiumEquivalent;

public CtXraySourceParameters(){
        this.xraySourceIdentification = null;
        this.kvp = null;
        this.maximumXrayTubeCurrent = null;
        this.xrayTubeCurrent = null;
        this.exposureTimePerRotation = null;
        this.xrayFilterAluminiumEquivalent = null;
}
    
public void parseCtXraySourceParameters(ContentItem root) {

    //Parse source parameters
    this.xraySourceIdentification=root.getSingleStringValueOrNullOfNamedChild("DCM", "113832");

    String kVolts = root.getSingleStringValueOrNullOfNamedChild("DCM", "113733");
    if(kVolts!=null){
        this.kvp = Float.parseFloat(kVolts);
    }

    String maxCurr = root.getSingleStringValueOrNullOfNamedChild("DCM", "113833");
    if (maxCurr!=null){
        this.maximumXrayTubeCurrent = Float.parseFloat(maxCurr);
    }

    String xCurr = root.getSingleStringValueOrNullOfNamedChild("DCM", "113734");
    if (xCurr!=null){
        this.xrayTubeCurrent = Float.parseFloat(xCurr);
    }

    String exTimeRot = root.getSingleStringValueOrNullOfNamedChild("DCM", "113834");
    if(exTimeRot!=null){
        this.exposureTimePerRotation= Float.parseFloat(exTimeRot);
    }

    String filterAl = root.getSingleStringValueOrNullOfNamedChild("DCM", "113821");
    if (filterAl!=null){
        this.xrayFilterAluminiumEquivalent=Float.parseFloat(filterAl);
    }
}

public boolean checkForDb(){//<editor-fold>
    boolean status = true;
    if (this.xraySourceIdentification==null){
       // log.warn("RDSR document is not valid: x-ray source identification in X-ray Source Parameters is empty");
        status=false;
    }
    if(this.kvp==null){
        //log.warn("RDSR document is not valid: kVp in X-ray Source Parameters is empty");
        status=false;
    }
    if(this.maximumXrayTubeCurrent==null){
        //log.warn("RDSR document is not valid: maximum X-ray Tube current in X-ray Source Parameters is empty");
        status=false;
    }
    if(this.xrayTubeCurrent==null){
        //log.warn("RDSR document is not valid: x-ray tube current in X-ray Source Parameters is empty");
        status=false;
    }
    return status;
}//</editor-fold>

public void insertIntoDb(Connection con, int seriesKey,int eventKey,int sourceKey) throws SQLException{
    
    PreparedStatement sqlInsStmnt = null;
      
    try{
        //prepare the SQL statement 
        sqlInsStmnt = con.prepareStatement("INSERT INTO ctXraySourceParameters (srSeriesIndex,"
                + " eventIndex, ctSourceIndex,"
                + " xraySourceIdentification, kvp, maximumXrayTubeCurrent, "
                + " xrayTubeCurrent, exposureTimePerRotation, xrayFilterAluminiumEquivalent)"
                + " VALUES(?,?,?,?,?,?,?,?,?)");

        sqlInsStmnt.setInt(1, seriesKey);
        sqlInsStmnt.setInt(2, eventKey);
        sqlInsStmnt.setInt(3, sourceKey);
        if (this.xraySourceIdentification!=null){sqlInsStmnt.setString(4, this.xraySourceIdentification);} else {sqlInsStmnt.setNull(4, 12);}
        if (this.kvp!=null){sqlInsStmnt.setFloat(5, this.kvp);} else {sqlInsStmnt.setNull(5, 6);}
        if (this.maximumXrayTubeCurrent!=null) {sqlInsStmnt.setFloat(6, this.maximumXrayTubeCurrent);} else {sqlInsStmnt.setNull(6, 6);}
        if (this.xrayTubeCurrent!=null){sqlInsStmnt.setFloat(7, this.xrayTubeCurrent);} else {sqlInsStmnt.setNull(7, 6);}
        if (this.exposureTimePerRotation!=null){sqlInsStmnt.setFloat(8, this.exposureTimePerRotation);} else {sqlInsStmnt.setNull(8, 6);}
        if (this.xrayFilterAluminiumEquivalent!=null){sqlInsStmnt.setFloat(9, this.xrayFilterAluminiumEquivalent);} else {sqlInsStmnt.setNull(9, 6);}

        //execute statement
        sqlInsStmnt.executeUpdate();

           
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
    String returnString = "CT X-Ray Source Parameters:"+eol;
    returnString=returnString+
            "X-ray Source Identification = " + (this.xraySourceIdentification!=null? this.xraySourceIdentification:"null")+eol+
            "kVp = " + (this.kvp!=null? this.kvp.toString():"null")+eol+
            "Maximum X-Ray Tube Current = " + (this.maximumXrayTubeCurrent!=null? this.maximumXrayTubeCurrent.toString():"null") +eol+
            "X-Ray Tube Current = " + (this.xrayTubeCurrent!=null? this.xrayTubeCurrent.toString():"null")+eol+
            "Exposure Time per Rotation = " + (this.exposureTimePerRotation!=null? this.exposureTimePerRotation.toString():"null")+eol+
            "X-Ray Filter Aluminium Equivalent = " +(this.xrayFilterAluminiumEquivalent!=null? this.xrayFilterAluminiumEquivalent.toString():"null")+eol+eol;
   
   return returnString;            
}
}
