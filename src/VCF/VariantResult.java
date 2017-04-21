package VCF;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class VariantResult {
    //map colnames to its value
    private Map<String,String> item;
    private List<Coordinate> coordinates;

    public VariantResult() {
        this.item = new HashMap();
        this.coordinates = new ArrayList();
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

    public List<Coordinate> getCoordinates() {
        return coordinates;
    }
    
    public String getCoordinatesToString() {
        String[] toString = new String[this.coordinates.size()];
        int i = 0;
        for(Object obj: this.coordinates){
            Coordinate coord = (Coordinate) obj;
            toString[i] = coord.toString();
            i++;
        }
        return String.join(",", toString);
    }
    
    public void printVariant(){
        System.out.print(this.item.toString());
        System.out.println(" Size:"+this.coordinates.size()+" Coordinate : "+this.coordinates.toString());
    }
    
    public void addCoordinate(String transcript, int exon){
        this.coordinates.add(new Coordinate(transcript, exon));
    }
    
    public String[] returnAlleleList(){
        //index 0 is reference while 1,2,3,etc are the alternatives
        String[] alt = this.getColValues("ALT").split(",");
        String[] retval = new String[alt.length+1];
        retval[0]=this.getColValues("REF");
        for(int i=0;i<alt.length;i++){
            retval[i+1]=alt[i];
        }
        return retval;
    }
        
    public String[] returnGenotypeFromSample(String sample){
        String[] GTPL  = this.getColValues(sample).split(":");
        
        String alleleCol = GTPL[0];
        String PL = GTPL[1];
                
        String[] alleleID = alleleCol.split("/|\\|");
        Set<String> retVal = new HashSet();
        for(String i:alleleID){
            if(i.equalsIgnoreCase(".")){
                retVal.add("0");
            }else{
                retVal.add(i);
            }
        }
        return retVal.toArray(new String[retVal.size()]);
    }
    
    public String[] returnPLFromSample(String sample){
        String[] GTPL  = this.getColValues(sample).split(":");
        String PL = GTPL[1];

        return PL.split(",");
    }
}
