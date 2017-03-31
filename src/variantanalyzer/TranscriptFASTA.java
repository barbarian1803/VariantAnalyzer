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
    private Map<Integer,GTFEntry> transcriptExons;
    
    public TranscriptFASTA(String metadata){
        this.transcriptID = this.parseMetadata(metadata);
        this.sequence = "";
        transcriptExons = new HashMap();
    }
    
    private String parseMetadata(String metadata){
        String[] parsedMetadata = metadata.substring(1).split(" ");
        //first element: transcript id
        String[] parsedData = parsedMetadata[0].split("\\.");
        return parsedData[0];
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
}
