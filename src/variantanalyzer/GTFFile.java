package variantanalyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GTFFile {
    //map chromosome to its list of exons
    private Map<String,List<GTFEntry>> gtfEntries;
    
    public GTFFile(){
        this.gtfEntries = new HashMap();
        this.gtfEntries.put("X", new ArrayList());
        this.gtfEntries.put("Y", new ArrayList());
        this.gtfEntries.put("MT", new ArrayList());
        for(int i=1;i<=22;i++){
            this.gtfEntries.put(""+i, new ArrayList());
        }
    }
    public void addEntry(String key, GTFEntry entry){
        if(this.gtfEntries.containsKey(key)){
            this.gtfEntries.get(key).add(entry);
        }
    }
    
    public List getEntryInChromosome(String chrom){
        return this.gtfEntries.get(chrom);
    }
    
    public void printData(String key){
        if(this.gtfEntries.containsKey(key)){
            List data = this.gtfEntries.get(key);
            for(Object item:data){
                GTFEntry itemEntry = (GTFEntry)item;
                System.out.println(itemEntry.getChromosome()+" "+itemEntry.getStart()+"-"+itemEntry.getEnd()+" : "+itemEntry.getTranscriptID()+"-"+itemEntry.getExonID());
            }
        }
    }
}
