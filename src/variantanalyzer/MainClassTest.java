package variantanalyzer;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author bharata
 */
public class MainClassTest {
    public static void main(String[] args) throws IOException{
        //read all data
        String gtfFileName="data/TEST.DATA.GTF";
        String vcfFileName="data/TEST.DATA.VCF";
        String transcriptFASTAFileName="data/TEST.DATA.FA";
        int threadNumber = 1;
        
        GTFFile gtf = GTFParser.ReadGTF(gtfFileName);
        
        VariantFile vcf = VCFParser.ReadVCF(vcfFileName, threadNumber);
        
        TranscriptFASTAFile transcripts = TranscriptFASTAParser.ParseFASTAFile(transcriptFASTAFileName);
        
        //Assign exon list to transcript sequence
        TranscriptFASTAFile transcriptWithExons = TranscriptFASTAParser.AssignExonsPerTranscript(transcripts,gtf,threadNumber);
       
//        Assign variant location to corresponding annotation
        VariantFile vcfOut = VCFParser.AssignVariantLocation(vcf, gtf, threadNumber);
        
        vcf.getVariantResult(0).printVariant();
        vcf.getVariantResult(1).printVariant();
        vcf.getVariantResult(2).printVariant();
        
//        try {
//            VCFParser.TranscriptConsensusSequence(vcfOut, transcriptWithExons, "33","data/consensus.fa");
//        } catch (FileNotFoundException ex) {
//            
//        }
    }
}
