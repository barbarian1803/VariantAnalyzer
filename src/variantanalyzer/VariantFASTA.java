package variantanalyzer;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bharata
 */
public class VariantFASTA {
    private List<VariantFASTASequence> possibleSequences;
    
    public VariantFASTA(){
        possibleSequences = new ArrayList<>();
    }
    
    public List<VariantFASTASequence> getSequences(){
        return this.possibleSequences;
    }
    
    public VariantFASTASequence getSequence(int i){
        return this.possibleSequences.get(i);
    }
    
    public void addSequences(VariantFASTASequence seq){
        this.possibleSequences.add(seq);
    }
    
    public VariantFASTASequence setSequence(int i,VariantFASTASequence seq){
        return this.possibleSequences.set(i, seq);
    }
    
}
