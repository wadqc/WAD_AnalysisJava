/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wad_sr;
import com.pixelmed.dicom.DicomException;
import java.util.HashMap;
/**
 *
 * @author Bart
 */
public final class ConceptCodes {
    
    private ConceptCodes(){
        
    }
    
    public static String getConceptName(String schemeCode) throws DicomException{
        HashMap dictionary = new HashMap(100);
        //CID 19
        dictionary.put("SRT:F-10440", "erect");
        dictionary.put("SRT:F-10450", "recumbent");
        dictionary.put("SRT:F-10460", "semi-erect");
        
        //CID 20
        dictionary.put("SRT:F-10310", "prone");
        dictionary.put("SRT:F-10316", "semi-prone");
        dictionary.put("SRT:F-10318", "lateral decubitus");
        dictionary.put("SRT:F-10320", "standing");
        dictionary.put("SRT:F-10326", "anatomical");
        dictionary.put("SRT:F-10330", "kneeling");
        dictionary.put("SRT:F-10336", "knee-chest");
        dictionary.put("SRT:F-10340", "supine");
        dictionary.put("SRT:F-10346", "lithotomy");
        dictionary.put("SRT:F-10348", "Trendelenburg");
        dictionary.put("SRT:F-10349", "inverse Trendelenburg");
        dictionary.put("SRT:F-10380", "frog");
        dictionary.put("SRT:F-10390", "stooped-over");
        dictionary.put("SRT:F-103A0", "sitting");
        dictionary.put("SRT:F-10410", "curled-up");
        dictionary.put("SRT:F-10317", "right lateral decubitus");
        dictionary.put("SRT:F-10319", "left lateral decubitus");
        dictionary.put("SRT:R-40799", "lordotic");
        
        //CID 244
        dictionary.put("SRT:G-A100", "Right");
        dictionary.put("SRT:G-A101", "Left");
        dictionary.put("SRT:G-A102", "Right and left");
        dictionary.put("SRT:G-A103", "Unilateral");
        
        //CID 10002
        dictionary.put("SRT:P5-06000","Fluoroscopy");
        dictionary.put("DCM:113611", "Stationary Acquisition");
        dictionary.put("DCM:113612", "Stepping Acquisition");
        dictionary.put("DCM:113613", "Rotational Acquisition");
        
        //CID 10003
        dictionary.put("DCM:113620", "Plane A");
        dictionary.put("DCM:113621", "Plane B");
        dictionary.put("DCM:113622", "Single Plane");
        dictionary.put("DCM:113890", "All Planes");
        
        //CID 10004
        dictionary.put("DCM:113630", "Continuous");
        dictionary.put("DCM:113631", "Pulsed");
        
        //CID 10006
        dictionary.put("SRT:C-150F9", "Molybdenum or Molybdenum compound");
        dictionary.put("SRT:C-120F9", "Aluminum or Aluminum compound");
        dictionary.put("SRT:C-127F9", "Copper or Copper compound");
        dictionary.put("SRT:C-167F9", "Rhodium or Rhodium compound");
        dictionary.put("SRT:C-1190E", "Niobium or Niobium compound");
        dictionary.put("SRT:C-1190F", "Europium or Europium compound");
        dictionary.put("SRT:C-132F9", "Lead or Lead compound");
        dictionary.put("SRT:C-156F9", "Tantalum or Tantalum compound");
        dictionary.put("SRT:C-137F9", "Silver or Silver compound");
        
        //CID 10007
        dictionary.put("DCM:113650", "Strip filter");
        dictionary.put("DCM:113651", "Wedge filter");
        dictionary.put("DCM:113652", "Butterfly filter");
        dictionary.put("DCM:111609", "No Filter");
        
        //CID 10008
        dictionary.put("DCM:113748", "Distance Source to Isocenter");
        dictionary.put("DCM:113737", "Distance Source to Reference Point");
        dictionary.put("DCM:113750", "Distance Source to Detector");
        dictionary.put("DCM:113751", "Table Longitudinal Position");
        dictionary.put("DCM:113752", "Table Lateral Position");
        dictionary.put("DCM:113753", "Table Height Position");
        dictionary.put("DCM:113792", "Distance Source to Table Plane");
        
        //CID 100013
        dictionary.put("DCM:113804", "Sequenced Acquisition");
        dictionary.put("SRT:P5-08001", "Spiral Acquisition");
        dictionary.put("DCM:113805", "Constant Angle Acquisition");
        dictionary.put("DCM:113806", "Stationary Acquisition");
        dictionary.put("DCM:113807", "Free Acquisition");
        
        //CID 10025
        dictionary.put("DCM:113860", "15cm from Isocenter toward Source");
        dictionary.put("DCM:113861", "30cm in Front of Image Input Surface");
        dictionary.put("DCM:113862", "1cm above Tabletop");
        dictionary.put("DCM 113863", "30cm above Tabletop");
        dictionary.put("DCM:113864", "15cm from Table Centerline");
        dictionary.put("DCM:113865", "Entrance exposure to a 4.2 cm breast thickness");
        
        //partial imports
        
        //controlled terminology
        dictionary.put("DCM:113704", "Projection X-ray");
        //CID 6060
        dictionary.put("SRT:P5-40010", "Mammography");
        
        String returnString = (String)dictionary.get(schemeCode);
        if (returnString==null){
            throw new DicomException("concept Code not found in dictionary");
        } else{
            return returnString;
        }
    }
   
    
}
