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
    
    public String applyVariant(VariantResult variant,int genotype,int exonID){
        String alleleRef = variant.returnAlleleList()[0];
        String variation = variant.returnAlleleList()[genotype];
        
        int pos = Integer.parseInt(variant.getColValues("POS"));
        GTFEntry exon = transcriptExons.get(exonID);
        
        int offset = -1;
        
        if(exon.getStrand()=='+'){
            offset = pos-exon.getStart()+1;
        }else{
            offset = pos-exon.getEnd()+1;
        }
        
        int totalOffset = 0;
        
        for(int i:transcriptExons.keySet()){
            if(i<exonID){
                totalOffset = totalOffset+1+Math.abs(transcriptExons.get(i).getEnd()-transcriptExons.get(i).getStart());
            }
        }
        
        offset=offset+totalOffset;
        
        String preVariation = this.sequence.substring(0, offset);
        String afterVariation = this.sequence.substring(offset+alleleRef.length());
        String newSeq = preVariation+variation+afterVariation;
        return newSeq;
    }
}
