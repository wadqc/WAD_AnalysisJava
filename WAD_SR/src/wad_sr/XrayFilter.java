/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad_sr;
import com.pixelmed.dicom.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Bart
 */
public class XrayFilter {
public String filterType;
public String filterMaterial;
public Float filterThicknessMinimum;
public Float filterThicknessMaximum;

public XrayFilter(){
    this.filterMaterial=null;
    this.filterThicknessMaximum=null;
    this.filterThicknessMinimum=null;
    this.filterType = null;
    }

public void parseXrayFilter(ContentItem root) throws DicomException{
    if(!root.getConceptNameCodeValue().equals("113771")){
        this.filterMaterial = null;
        this.filterThicknessMaximum = null;
        this.filterThicknessMinimum = null;
        this.filterType = null;
        throw new DicomException("Wrong ContentItem, ConceptNameCodeValue = "+root.getConceptNameCodeValue()+", 113771 expected");
    }
    ContentItem fTypeIt = root.getNamedChild("DCM", "113772");
    if (fTypeIt!=null){
    AttributeList fTypeLs =fTypeIt.getAttributeList();  
    String filterTypeCode=CodedSequenceItem.getSingleCodedSequenceItemOrNull(fTypeLs, TagFromName.ConceptCodeSequence).getCodingSchemeDesignator()+
            ":"+CodedSequenceItem.getSingleCodedSequenceItemOrNull(fTypeLs, TagFromName.ConceptCodeSequence).getCodeValue();
    this.filterType=ConceptCodes.getConceptName(filterTypeCode);
    }


    ContentItem fMatIt = root.getNamedChild("DCM","113757");
    if (fMatIt!=null){
    AttributeList fMatLs = fMatIt.getAttributeList();    
    String filterMaterialCode = CodedSequenceItem.getSingleCodedSequenceItemOrNull(fMatLs, TagFromName.ConceptCodeSequence).getCodingSchemeDesignator()+
            ":"+CodedSequenceItem.getSingleCodedSequenceItemOrNull(fMatLs, TagFromName.ConceptCodeSequence).getCodeValue();
    this.filterMaterial=ConceptCodes.getConceptName(filterMaterialCode);
    }

    String fThickMin = root.getSingleStringValueOrNullOfNamedChild("DCM", "113758");
    if (fThickMin != null){
        this.filterThicknessMinimum = Float.parseFloat(fThickMin);
    } else{
        this.filterThicknessMinimum = null;
    }

    String fThickMax = root.getSingleStringValueOrNullOfNamedChild("DCM", "113773");
    if (fThickMax != null){
        this.filterThicknessMaximum = Float.parseFloat(fThickMax);
    } else{
        this.filterThicknessMaximum = null;
    }
}

public boolean checkForDb(){
    return true;
}

public void insertIntoDB(Connection con, int seriesKey, int eventKey, int filterKey) throws SQLException{
    
    PreparedStatement sqlInsStmnt = null;
        try{
            //prepare the SQL statement 
            sqlInsStmnt = con.prepareStatement("INSERT INTO xrayFilter (srSeriesIndex, "
                    + "eventIndex, xrayFilterIndex, xrayFilterType, xrayFilterMaterial, "
                    + "xrayFilterMinimumThickness, xrayFilterMaximumThickness) "
                    + "VALUES(?,?,?,?,?,?,?)");

            sqlInsStmnt.setInt(1, seriesKey);
            sqlInsStmnt.setInt(2, eventKey);
            sqlInsStmnt.setInt(3, filterKey);
            if (this.filterType!=null){sqlInsStmnt.setString(4, this.filterType);} else {sqlInsStmnt.setNull(4, 12);}
            if (this.filterMaterial!=null){sqlInsStmnt.setString(5, this.filterMaterial);} else {sqlInsStmnt.setNull(5, 12);}
            if (this.filterThicknessMinimum!=null) {sqlInsStmnt.setFloat(6, this.filterThicknessMinimum);} else {sqlInsStmnt.setNull(6, 6);}
            if (this.filterThicknessMaximum!=null){sqlInsStmnt.setFloat(7, this.filterThicknessMaximum);} else {sqlInsStmnt.setNull(7, 6);}

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
    String returnString = "X-ray Filter:" +eol;
    returnString = returnString 
    + "Filter Type = " + (this.filterType != null ? this.filterType:"null") + eol
    + "Filter Material = " + (this.filterMaterial != null ? this.filterMaterial:"null") + eol
    + "Filter Minimum Thickness = " + (this.filterThicknessMinimum != null ?  Float.toString(this.filterThicknessMinimum):"null") + eol
    + "Filter Maximum Thickness = " + (this.filterThicknessMaximum != null ? Float.toString(this.filterThicknessMaximum):"null") + eol +eol;
    
    return returnString;
}
}




