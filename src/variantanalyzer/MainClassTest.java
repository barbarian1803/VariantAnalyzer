package variantanalyzer;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
        
        
        Map<String, List<VariantResult>> variantPerExon = VCFParser.AssignVariantToExon(vcf, gtf, threadNumber);
        VCFParser.TranscriptConsensusSequence(variantPerExon, transcriptWithExons, "19","data/consensus.fa");
    }
}
