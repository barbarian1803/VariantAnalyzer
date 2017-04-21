package Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

public class UniprotEnsemblMapper {
    public static BidiMap<String, String> Map = new DualHashBidiMap<>();
    
    public static void ReadMappingData(String filePath,String sep){
        FileReader fr = null;
        BufferedReader br = null;
        
        try {
            fr = new FileReader(new File(filePath));
            br = new BufferedReader(fr);
            
            String line;
            while((line=br.readLine())!=null){
                String[] result = line.split(sep);
                Map.put(result[0], result[2]);
            }
            
        } catch (FileNotFoundException ex) {
            
        } catch (IOException ex) {
            
        }finally{
            try {
                br.close();
                fr.close();
            } catch (IOException ex) {
                
            }
        }    
    }
    
    public static String getEnsemblID(String uniprotID){
        return Map.get(uniprotID);
    }
    
    public static String getUniprotID(String ensemblID){
        return Map.inverseBidiMap().get(ensemblID);
    }
}
