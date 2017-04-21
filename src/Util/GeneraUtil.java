package Util;

import java.util.HashMap;
import java.util.Map;

public class GeneraUtil {
    public static String DNAToProtein(String dna, int frame){
        Map<String,String> codonTable = new HashMap();
        codonTable.put("GCT", "A");
        codonTable.put("GCC", "A");
        codonTable.put("GCA", "A");
        codonTable.put("GCG", "A");
        
        codonTable.put("CGT", "R");
        codonTable.put("CGC", "R");
        codonTable.put("CGA", "R");
        codonTable.put("CGG", "R");
        codonTable.put("AGA", "R");
        codonTable.put("AGG", "R");
        
        codonTable.put("AAT", "N");
        codonTable.put("AAC", "N");
        
        codonTable.put("GAT", "D");
        codonTable.put("GAC", "D");
        
        codonTable.put("TGT", "C");
        codonTable.put("TGC", "C");
        
        codonTable.put("CAA", "Q");
        codonTable.put("CAG", "Q");
        
        codonTable.put("GAA", "E");
        codonTable.put("GAG", "E");
        
        codonTable.put("GGT", "G");
        codonTable.put("GGC", "G");
        codonTable.put("GGA", "G");
        codonTable.put("GGG", "G");
        
        codonTable.put("CAT", "H");
        codonTable.put("CAC", "H");
        
        codonTable.put("ATC", "I");
        codonTable.put("ATA", "I");
        codonTable.put("ATT", "I");
        
        codonTable.put("ATG", "M");
        
        codonTable.put("TTA", "L");
        codonTable.put("TTG", "L");
        codonTable.put("CTT", "L");
        codonTable.put("CTC", "L");
        codonTable.put("CTA", "L");
        codonTable.put("CTG", "L");
        
        codonTable.put("AAA", "K");
        codonTable.put("AAG", "K");
        
        codonTable.put("TTT", "F");
        codonTable.put("TTC", "F");
        
        codonTable.put("CCT", "P");
        codonTable.put("CCC", "P");
        codonTable.put("CCA", "P");
        codonTable.put("CCG", "P");
        
        codonTable.put("TCT", "S");
        codonTable.put("TCC", "S");
        codonTable.put("TCA", "S");
        codonTable.put("TCG", "S");
        codonTable.put("AGT", "S");
        codonTable.put("AGC", "S");
        
        codonTable.put("ACT", "T");
        codonTable.put("ACC", "T");
        codonTable.put("ACA", "T");
        codonTable.put("ACG", "T");
        
        codonTable.put("TGG", "W");
        
        codonTable.put("TAT", "Y");
        codonTable.put("TAC", "Y");
        
        codonTable.put("GTT", "V");
        codonTable.put("GTC", "V");
        codonTable.put("GTA", "V");
        codonTable.put("GTG", "V");
        
        codonTable.put("TAA", "*");
        codonTable.put("TGA", "*");
        codonTable.put("TAG", "*");
        
        
        int start = frame-1;
        int pos1 = start;
        int pos2 = start+1;
        int pos3 = start+2;
        String protein = "";
        while(pos1<dna.length()&&pos2<dna.length()&&pos3<dna.length()){
            String codon = dna.substring(pos1, pos3+1);
            protein = protein+codonTable.get(codon);
            pos1+=3;
            pos2+=3;
            pos3+=3;
        }
        
        return protein;
    }
    
    public static String SplitFastaToLines(String fasta, int charPerLine){
        String output = "";
        int lineLength=0;
        for(int i=0;i<fasta.length();i++){
            output+=fasta.charAt(i);
            lineLength++;
            if(lineLength==charPerLine){
                output+="\n";
                lineLength=0;
            }
        }
        return output;
    }
    
}
