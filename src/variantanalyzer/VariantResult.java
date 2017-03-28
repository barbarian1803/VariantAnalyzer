package variantanalyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    
    private class Coordinate{
        String transcriptID;
        int exonID;

        public Coordinate(String transcriptID, int exonID) {
            this.transcriptID = transcriptID;
            this.exonID = exonID;
        }

        public String getTranscriptID() {
            return transcriptID;
        }

        public void setTranscriptID(String transcriptID) {
            this.transcriptID = transcriptID;
        }

        public int getExonID() {
            return exonID;
        }

        public void setExonID(int exonID) {
            this.exonID = exonID;
        }
        
        @Override
        public String toString(){
            return "["+transcriptID+","+exonID+"]";
        }
    }
}
