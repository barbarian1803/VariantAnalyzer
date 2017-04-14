package variantanalyzer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class VariantFASTASequence {
    private Map<Integer,Integer> variantOffset; //map location and length of variation
    private int heterozygousNumber;
    private String sequence;
    private Set<Integer> heterozygousPos;
    
    public VariantFASTASequence(String seq,int number) {
        this.sequence = seq;
        this.heterozygousNumber = number;
        this.heterozygousPos = new HashSet<>();
        this.variantOffset = new HashMap<>();
    }

    public int getHeteroZygousNumber() {
        return heterozygousNumber;
    }

    public void setHeteroZygousNumber(int heterozygousNumber) {
        this.heterozygousNumber = heterozygousNumber;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }
    
    public void addHeterozygousPos(int pos){
        this.heterozygousPos.add(pos);
    }

    public Set<Integer> getHeterozygousPos() {
        return heterozygousPos;
    }
    
    public void addVariantOffset(int pos,int oriLength, int variantLength){
        int length = variantLength-oriLength;
        this.variantOffset.put(pos,length);
    }
    
    public Map<Integer,Integer> getVariantPos(){
        return this.variantOffset;
    }
}
