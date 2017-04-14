package variantanalyzer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
    
    public Set<Integer> getExonNumber(){
        return this.transcriptExons.keySet();
    }
    
    public GTFEntry getExon(int i){
        return this.transcriptExons.get(i);
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
    
    public String applyVariant(String sequence, String alleleRef,String variation,int exonID, int offset){
        String preVariation = sequence.substring(0, offset);
        String afterVariation = sequence.substring(offset+alleleRef.length());
        
        if(transcriptExons.get(exonID).getStrand()=='-'){
            variation = TranscriptFASTA.ReverseComplement(variation);
        }

        String newSeq = preVariation+variation+afterVariation;
        return newSeq;
    }
    
    public static int ChromPosToTranscriptOffset(TranscriptFASTA transcript,int pos, int exonID, String alleleRef, VariantFASTASequence variantFASTA){
        int offset = -1;
        GTFEntry exon = transcript.getExon(exonID);
        if(exon.getStrand()=='+'){
            offset = pos-exon.getStart()+1;
        }else{
            int startIdx = (pos+alleleRef.length())-1;
            offset = 1+(exon.getEnd()-startIdx);
        }
        int totalOffset = 0;
        
        for(int i:transcript.getExonNumber()){
            if(i<exonID){
                totalOffset = totalOffset+1+Math.abs(transcript.getExon(i).getEnd()-transcript.getExon(i).getStart());
            }
        }
        offset=offset+totalOffset;
        return offset-1;
    }
    
    public static int IndelAdjustedTranscriptOffset(int offset, VariantFASTASequence variantFASTA){
        Map<Integer,Integer> recordedIndel = variantFASTA.getVariantPos();
        for(int preOffset:recordedIndel.keySet()){
            if(preOffset<=offset){
                offset+=recordedIndel.get(preOffset);
            }
        }
        return offset;
    }
    
    public static String ReverseComplement(String s){
        String retval="";
        
        for (int i = 0; i < s.length(); i++){
            char c = s.charAt(i);        
            if(c=='A'){
                retval = 'T'+retval;
            }
            if(c=='T'){
                retval = 'A'+retval;
            }
            if(c=='C'){
                retval = 'G'+retval;
            }
            if(c=='G'){
                retval = 'C'+retval;
            }
        }
        
        return retval;
    }
}
