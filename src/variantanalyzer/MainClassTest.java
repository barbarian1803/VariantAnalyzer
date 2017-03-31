package variantanalyzer;

/**
 *
 * @author bharata
 */
public class MainClassTest {
    public static void mian(String[] args){
        //read all data
        String gtfFileName="data/gtf_file.gtf";
        String vcfFileName="data/cancer.exome.vcf";
        String transcriptFASTAFileName="data/HG38_87.cdna.all.fa";
        int threadNumber = 8;
        
        GTFFile file = GTFParser.ReadGTF(gtfFileName);
        VariantFile vcf = VCFParser.ReadVCF(vcfFileName, threadNumber);
        TranscriptFASTAFile transcripts = TranscriptFASTAParser.ParseFASTAFile(transcriptFASTAFileName);
        //Assign variant location to corresponding annotation
        VariantFile vcfOut = VCFParser.AssignVariantLocation(vcf, file, threadNumber);
        
        
    }
}
