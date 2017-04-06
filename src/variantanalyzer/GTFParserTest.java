package variantanalyzer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bharata
 */
public class GTFParserTest {

    public static void main(String[] args) {

        GTFFile file = GTFParser.ReadGTF("data/gtf_file.gtf");
        VariantFile vcf = VCFParser.ReadVCF("data/cancer.exome.vcf", 8);
        VariantFile vcfOut = VCFParser.AssignVariantLocation(vcf, file, 8);
        
        PrintWriter bw = null;
        File fw = null;
        try {
            fw = new File("data/vcf.with.coordinates.vcf");
            bw = new PrintWriter(fw);
            vcfOut.printVCFtoFile(bw);
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(GTFParserTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
