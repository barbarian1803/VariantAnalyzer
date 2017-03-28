package variantanalyzer;


public class VCFParserTest {
    public static void main(String[] args) {
        VariantFile vcf = VCFParser.ReadVCF("data/cancer.exome.vcf",8);
        System.out.println(vcf.getVariantResult().get(9).getColValues("CHROM"));
    }
    
}
