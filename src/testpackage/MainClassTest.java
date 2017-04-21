package testpackage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import GTF.GTFFile;
import GTF.GTFParser;
import TranscriptFASTA.TranscriptFASTA;
import TranscriptFASTA.TranscriptFASTAFile;
import TranscriptFASTA.TranscriptFASTAParser;
import VCF.VCFParser;
import VCF.VariantFile;
import VCF.VariantResult;

/**
 *
 * @author bharata
 */
public class MainClassTest {
    public static void main(String[] args) throws IOException{
        //read all data
        String gtfFileName="data/gtf_file.gtf";
        String vcfFileName="data/cancer.exome.vcf";
        String transcriptFASTAFileName="data/HG38_87.cdna.all.fa";
        int threadNumber = 8;
        
        GTFFile gtf = GTFParser.ReadGTF(gtfFileName);
        
        VariantFile vcf = VCFParser.ReadVCF(vcfFileName, threadNumber);
        VariantFile newvcf = VCFParser.AssignVariantLocation(vcf, gtf, threadNumber);
        TranscriptFASTAFile transcripts = TranscriptFASTAParser.ParseFASTAFile(transcriptFASTAFileName);
        
        
        //Assign exon list to transcript sequence
        TranscriptFASTAFile transcriptWithExons = TranscriptFASTAParser.AssignExonsPerTranscript(transcripts,gtf,threadNumber);
        
        Map<String, List<VariantResult>> variantPerExon = VCFParser.AssignVariantToExon(newvcf, gtf, threadNumber);
        
        Set<String> bHLHGenes = new HashSet();
        BufferedReader br = new BufferedReader(new FileReader("data/bhlh_with_ensembl.csv"));
        String line;
        while((line=br.readLine())!=null){
            String[] cols = line.split(";");
            bHLHGenes.add(cols[1]);
        }
        
        TranscriptFASTAFile newFile = new TranscriptFASTAFile();
        
        int i=1;
        for(String s:variantPerExon.keySet()){            
            String id = s.split("_")[0];
            try{
                TranscriptFASTA fasta = transcriptWithExons.getTranscriptFASTA(id); 
                if(bHLHGenes.contains(fasta.getGeneID())){
                    newFile.addTranscript(id, fasta);
                    System.out.println(fasta.getGeneID());
                    for(VariantResult v: variantPerExon.get(s)){
                        v.printVariant();
                    }
                }

            }catch(NullPointerException ex){
                //System.out.println(id);
            }
        }
        
        
        VCFParser.TranscriptConsensusSequence(variantPerExon, newFile, "19","data/consensus.19.chr1.fa");
        VCFParser.TranscriptConsensusSequence(variantPerExon, newFile, "21","data/consensus.21.chr1.fa");
        VCFParser.TranscriptConsensusSequence(variantPerExon, newFile, "23","data/consensus.23.chr1.fa");
        VCFParser.TranscriptConsensusSequence(variantPerExon, newFile, "25","data/consensus.25.chr1.fa");
        VCFParser.TranscriptConsensusSequence(variantPerExon, newFile, "27","data/consensus.27.chr1.fa");
        VCFParser.TranscriptConsensusSequence(variantPerExon, newFile, "29","data/consensus.29.chr1.fa");
        VCFParser.TranscriptConsensusSequence(variantPerExon, newFile, "31","data/consensus.31.chr1.fa");
        VCFParser.TranscriptConsensusSequence(variantPerExon, newFile, "33","data/consensus.33.chr1.fa");
        
    }
}
