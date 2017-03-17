package variantanalyzer;

import java.util.HashMap;
import java.util.Map;


public class VariantResult {
    private Map<String,String> item;

    public VariantResult() {
        this.item = new HashMap();
    }

    public void addItem(String key, String value){
        this.item.put(key, value);
    }
    
    public Map<String, String> getItem() {
        return item;
    }
    
    public String getColValues(String key) {
        return this.item.get(key);
    }
    
    public void setItem(Map<String, String> item) {
        this.item = item;
    }
    
    public void printVariant(){
        System.out.println(this.item.toString());
    }
    
}
