/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad_sr;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Bart
 */
public class XrayPulse {
    public Float kvp;
    public Float xrayTubeCurrent;
    public Float pulseWidth;
    public Float exposure;
    
    public XrayPulse(){
        this.kvp= null;
        this.xrayTubeCurrent= null;
        this.pulseWidth= null;
        this.exposure= null;
    }
    
    public XrayPulse(Float kvp, Float xrayTubeCurrent, Float pulsewidth, Float exposure){
        this.kvp=kvp;
        this.xrayTubeCurrent=xrayTubeCurrent;
        this.pulseWidth=pulsewidth;
        this.exposure=exposure;                
    }
    
    public boolean checkForDb(){
        return true;
    }
    
    public void insertIntoDb(Connection con, int seriesKey, int eventKey, int pulseIndex) throws SQLException{
                
        PreparedStatement sqlInsStmnt = null;
        try{           
            //prepare the SQL statement 
            sqlInsStmnt = con.prepareStatement("INSERT INTO xrayPulse (srSeriesIndex, "
                    + "eventIndex, xrayPulseIndex, kvp, pulseWidth, xrayTubeCurrent, "
                    + " exposure )"
                    + " VALUES(?,?,?,?,?,?,?)");

            sqlInsStmnt.setInt(1, seriesKey);
            sqlInsStmnt.setInt(2, eventKey);
            sqlInsStmnt.setInt(3, pulseIndex);
            if (this.kvp!=null){sqlInsStmnt.setFloat(4, this.kvp);} else {sqlInsStmnt.setNull(4, 6);}
            if (this.pulseWidth!=null){sqlInsStmnt.setFloat(5, this.pulseWidth);} else {sqlInsStmnt.setNull(5, 6);}
            if (this.xrayTubeCurrent!=null) {sqlInsStmnt.setFloat(6, this.xrayTubeCurrent);} else {sqlInsStmnt.setNull(6, 6);}
            if (this.exposure!=null){sqlInsStmnt.setFloat(7, this.exposure);} else {sqlInsStmnt.setNull(7, 6);}

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
        String returnString="Pulse Parameters:"+eol;
        returnString=returnString+
        "kVp = " + (this.kvp!=null? this.kvp.toString():"null") + eol +
        "X-Ray Tube Current = " +(this.xrayTubeCurrent!=null? this.xrayTubeCurrent.toString():"null")+ eol +
        "Pulse Width = " + (this.pulseWidth!=null? this.pulseWidth.toString():"null") + eol+
        "Exposure = " + (this.exposure!=null? this.exposure.toString():"null") +eol +eol;       
 
        return returnString;
    }
        
}
