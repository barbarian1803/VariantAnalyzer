/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package variantanalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bharata
 */
public class CDNAFastaParser {
    
    public static void main(String[] args){
        CDNAFastaParser obj = new CDNAFastaParser();
        obj.FastaSlicer();
    }
    
    public void FastaSlicer() {
        String filename = "data/HG38_87.cdna.all.fa";
        String output = "data/HG38_87.cdna.all.chr1.fa";
        List<FASTAData> fasta = new ArrayList();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(">")) {
                    fasta.add(new FASTAData(line, ""));
                } else {
                    String sequence = fasta.get(fasta.size() - 1).getSequence() +"\n"+ line;
                    fasta.get(fasta.size() - 1).setSequence(sequence);
                }
            }
            reader.close();
            
            
            PrintWriter writer = new PrintWriter(new File(output));
            for (Object obj : fasta) {
                FASTAData data = (FASTAData) obj;
                if (data.getMetadata().contains("GRCh38:1:")) {
                    writer.write(data.getMetadata());
                    writer.write(data.getSequence()+"\n");
                }
            }
            writer.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(CDNAFastaParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CDNAFastaParser.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private class FASTAData {

        private String metadata;
        private String sequence;

        public FASTAData(String metadata, String sequence) {
            this.metadata = metadata;
            this.sequence = sequence;
        }

        public String getMetadata() {
            return metadata;
        }

        public void setMetadata(String metadata) {
            this.metadata = metadata;
        }

        public String getSequence() {
            return sequence;
        }

        public void setSequence(String sequence) {
            this.sequence = sequence;
        }
    }

}
