package variantanalyzer;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author bharata
 */
public class TranscriptFASTA {
    private String sequence;
    private String transcriptID;
    private int strand;
    private Map<Integer,GTFEntry> transcriptExons;
    
    
    public TranscriptFASTA(String metadata){
        this.transcriptID = this.parseTranscriptID(metadata);
        this.strand = this.parseStrand(metadata);
        this.sequence = "";
        transcriptExons = new HashMap();
    }
    
    private String parseTranscriptID(String metadata){
        String[] parsedMetadata = metadata.substring(1).split(" ");
        //first element: transcript id
        String[] parsedData = parsedMetadata[0].split("\\.");
        return parsedData[0];
    }
    
    private int parseStrand(String metadata){
        String[] parsedMetadata = metadata.substring(1).split(" ");
        //first element: transcript id
        String[] parsedData = parsedMetadata[2].split(":");
        return Integer.parseInt(parsedData[5]);
    }
    
    public void appendSequence(String sequence){
        this.sequence = this.sequence+sequence;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getTranscriptID() {
        return transcriptID;
    }

    public void setTranscriptID(String transcriptID) {
        this.transcriptID = transcriptID;
    } 
    
    public void addExon(int key, GTFEntry exon){
        this.transcriptExons.put(key,exon);
    }
    
    public void printData(){
        System.out.println(this.transcriptID);
        System.out.println("Strand: "+this.strand);
        System.out.println(this.sequence);
        Object[] exonsNumber = this.transcriptExons.keySet().toArray();
        for(Object i : exonsNumber){
            System.out.print(i+" ");
            this.transcriptExons.get((int)i).printEntry();
        }
    }
}
