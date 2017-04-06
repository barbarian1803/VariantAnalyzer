package variantanalyzer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bharata
 */
public class MainClassTest {
    public static void main(String[] args){
        //read all data
        String gtfFileName="data/gtf_file.chr1.gtf";
        String vcfFileName="data/cancer.exome.chr1.vcf";
        String transcriptFASTAFileName="data/HG38_87.cdna.all.chr1.fa";
        int threadNumber = 8;
        
        GTFFile gtf = GTFParser.ReadGTF(gtfFileName);
        VariantFile vcf = VCFParser.ReadVCF(vcfFileName, threadNumber);
        TranscriptFASTAFile transcripts = TranscriptFASTAParser.ParseFASTAFile(transcriptFASTAFileName);
        
        //Assign exon list to transcript sequence
        TranscriptFASTAFile transcriptWithExons = TranscriptFASTAParser.AssignExonsPerTranscript(transcripts, gtf,threadNumber);
        
        //transcriptWithExons.getTranscript("ENST00000335137").printData();
        
        //Assign variant location to corresponding annotation
        VariantFile vcfOut = VCFParser.AssignVariantLocation(vcf, gtf, threadNumber);
        
        
        try {
            VCFParser.TranscriptConsensusSequence(vcfOut, transcriptWithExons, "33","data/consensus.fa");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainClassTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
