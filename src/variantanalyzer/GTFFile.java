package variantanalyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GTFFile {
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
}
