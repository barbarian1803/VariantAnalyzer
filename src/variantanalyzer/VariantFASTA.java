package variantanalyzer;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author bharata
 */
public class VariantFASTA {
    private List<VariantFASTASequence> possibleSequences;
    private List<String> possiblePair;
    private int totalHeterozygous;
    
    public VariantFASTA(){
        possibleSequences = new ArrayList<>();
        possiblePair = new ArrayList<>();
        totalHeterozygous = 0;
    }
    
    public void addTotalHeterozygous(){
        totalHeterozygous++;
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
    
    private void calculatePair(){
        Set<Integer> done = new HashSet();
        for(int i=0;i<possibleSequences.size();i++){
            if(done.contains(i))
                continue;
            VariantFASTASequence current = possibleSequences.get(i);
            done.add(i);
            
            for(int j=0;j<possibleSequences.size();j++){
                if(done.contains(j))
                    continue;
                
                VariantFASTASequence candidate = possibleSequences.get(j);
                int testNumberHeterozygous =  current.getHeteroZygousNumber()+candidate.getHeteroZygousNumber();
                
                if(testNumberHeterozygous==totalHeterozygous && Collections.disjoint(candidate.getHeterozygousPos(), current.getHeterozygousPos())){
                    possiblePair.add(i+"-"+j);
                    done.add(j);
                    break;
                }
            }
        }
    }
    
    public void printfastaSequence(String oriMetadata,PrintWriter pw){
        //generate new metadata by appending heterozygous pair information
        //example : >ENST001.1 cdna chromosome:GRCh38:1:1000:1080:1 gene:ENSG001.1 pair:1 allele=1
        //example : >ENST001.1 cdna chromosome:GRCh38:1:1000:1080:1 gene:ENSG001.1 pair:1 allele=2
        //example : >ENST001.1 cdna chromosome:GRCh38:1:1000:1080:1 gene:ENSG001.1 pair:2 allele=1
        //etc
        calculatePair();
        if(possibleSequences.size()==1){
            pw.write(oriMetadata+" pair:1 allele:1");
            pw.write("\n");
            pw.write(possibleSequences.get(0).getSequence());
            pw.write("\n");
            return;
        }
        int noPair = 1;
        for(String s:possiblePair){
            String[] pairs = s.split("-");
            pw.write(oriMetadata+" pair:"+noPair+" allele:1");
            pw.write("\n");
            pw.write(possibleSequences.get(Integer.parseInt(pairs[0])).getSequence());
            pw.write("\n");
            pw.write(oriMetadata+" pair:"+noPair+" allele:2");
            pw.write("\n");
            pw.write(possibleSequences.get(Integer.parseInt(pairs[1])).getSequence());
            pw.write("\n");
            noPair++;
        }
    }
}
