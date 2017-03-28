/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package variantanalyzer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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
        
        BufferedWriter bw = null;
        FileWriter fw = null;
        try {
            fw = new FileWriter("data/vcf.with.coordinates.vcf");
            bw = new BufferedWriter(fw);
            vcfOut.printVCFtoFile(bw);
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(GTFParserTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
