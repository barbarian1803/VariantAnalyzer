package testpackage;

import VCF.VariantFile;
import VCF.VCFParser;


public class VCFParserTest {
    public static void main(String[] args) {
        VariantFile vcf = VCFParser.ReadVCF("data/cancer.exome.vcf",8);
        System.out.println(vcf.getVariantResult(1).getColValues("CHROM"));
    }
    
}
