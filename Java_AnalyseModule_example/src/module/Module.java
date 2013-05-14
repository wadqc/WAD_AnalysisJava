/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package module;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import module.analyse.Analyse;
import module.wad.xml.AnalyseModuleConfigFile;
import module.wad.xml.AnalyseModuleInputFile;
import module.wad.xml.AnalyseModuleResultFile;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;


public class Module {
    
    private static Log log = LogFactory.getLog(Module.class);
    private static Properties p = new Properties();
    
    /* constructor */
    private Module(){    
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
   
        String inputfile_path="";

        // gebruik config/log4j.properties indien aanwezig
        // wanneer niet gevonden wordt de log4j.properties gebruikt uit de src folder (komt in jar-file terecht)
        try {
            p.load(new FileInputStream("config/log4j.properties"));
            PropertyConfigurator.configure(p);
        } catch (IOException ex) {
            log.error(ex);
        }
        
        // eerste argument is een link naar de inputfile
        if (args.length==1) {
            inputfile_path = args[0];
        } else {
            log.error("Input file niet gevonden!");
            System.exit(-1);
        }
        
        log.info("inputfile = " + inputfile_path);
            
        // lees input.xml in (stop patient/study/series/instance info in een ArrayList en verkrijg de lokatie van de configfile)
        AnalyseModuleInputFile inputfile = new AnalyseModuleInputFile(inputfile_path);
          
        // lees configfile in
        AnalyseModuleConfigFile configfile = new AnalyseModuleConfigFile(inputfile.getAnalyseModuleCfg());
        // AnalyseModuleConfigFile.readSettingsElement("naam_parameter");
       
        // initialiseer de file result.xml
        AnalyseModuleResultFile resultfile = new AnalyseModuleResultFile(inputfile.getAnalyseModuleOutput());    
        
        // voer analyse uit en vul de resultfile
        Analyse analyzer = new Analyse(inputfile, configfile, resultfile);
            
        // schrijf de resultfile weg
        resultfile.write();
    }
}
