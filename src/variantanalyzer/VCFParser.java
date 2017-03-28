/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package variantanalyzer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author barbarian
 */
public class VCFParser {
//    public static void sliceVCF() {
//        try {
//            BufferedReader reader = new BufferedReader(new FileReader("data/cancer.exome.vcf"));
//            String line;
//            PrintWriter writer = new PrintWriter("data/cancer.exome.chr1.vcf", "UTF-8");
//            while ((line = reader.readLine()) != null) {
//                if(line.startsWith("10")){
//                    break;
//                }
//                writer.println(line);
//            }
//            writer.close();
//        }catch(Exception ex){
//            
//        }
//    }
    
    public static VariantFile ReadVCF(String filename, int threadNumber) {

        VariantFile vcfFile = new VariantFile();
        List<String> tempData = new ArrayList();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("##")) {
                    vcfFile.addMetadata(line.replaceAll("##", ""));
                } else if (line.startsWith("#")) {
                    line = line.replaceAll("#", "");
                    vcfFile.setColNames(new ArrayList(Arrays.asList(line.split("\\t"))));
                } else {
                    tempData.add(line);
                }
            }

            //multi threading process
            int splitSize = (int) Math.ceil(tempData.size() / threadNumber);
            VCFParserThread[] threads = new VCFParserThread[threadNumber];

            for (int i = 0; i < threadNumber; i++) {
                int startIdx = i * splitSize;
                int stopIdx = startIdx + splitSize - 1;

                threads[i] = new VCFParserThread(tempData.subList(startIdx, stopIdx), vcfFile.getColNames());
                threads[i].start();
            }

            for (int i = 0; i < threadNumber; i++) {
                threads[i].join();
            }

            for (int i = 0; i < threadNumber; i++) {
                vcfFile.getVariantResult().addAll(threads[i].getResult());
            }

            reader.close();

            return vcfFile;

        } catch (Exception e) {
            System.err.format("Exception occurred trying to read '%s'.", filename);
            e.printStackTrace();
            return null;
        }
    }

    public static VariantFile AssignVariantLocation(VariantFile vcfFile, GTFFile gtfFile, int threadNumber) {
        List variantResult = vcfFile.getVariantResult();
        int splitSize = (int) Math.ceil(variantResult.size() / threadNumber);
        AssignVariationThread[] threads = new AssignVariationThread[threadNumber];
        for (int i = 0; i < threadNumber; i++) {
            int startIdx = i * splitSize;
            int stopIdx = startIdx + splitSize - 1;

            threads[i] = new AssignVariationThread(variantResult.subList(startIdx, stopIdx), vcfFile.getColNames(), gtfFile);
            threads[i].start();
        }
        for (int i = 0; i < threadNumber; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException ex) {
                //Logger.getLogger(VCFParser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        vcfFile.setVariantResult(new ArrayList());
        
        for (int i = 0; i < threadNumber; i++) {
            vcfFile.getVariantResult().addAll(threads[i].getResult());
        }
        
        return vcfFile;
    }

    private static class AssignVariationThread extends Thread {

        GTFFile gtfFile;
        List variantResult;
        List colnames;
        List<VariantResult> variantResultOutput;

        public AssignVariationThread(List variantResult, List colnames, GTFFile gtfFile) {
            this.gtfFile = gtfFile;
            this.colnames = colnames;
            this.variantResult = variantResult;
            this.variantResultOutput = new ArrayList();
        }

        @Override
        public void run() {
            for (Object obj : variantResult) {
                VariantResult result = (VariantResult) obj;
                try{
                    List gtfEntryInChromosome = gtfFile.getEntryInChromosome(result.getColValues("CHROM"));
                    //search algorithm here
                    for (Object objGTFEntry : gtfEntryInChromosome) {
                        
                        GTFEntry gtfEntry = (GTFEntry) objGTFEntry;
                        int pos = Integer.parseInt(result.getColValues("POS"));
                        
                        if (pos >= gtfEntry.getStart() && pos <= gtfEntry.getEnd()) {
                            result.addCoordinate(gtfEntry.getTranscriptID(), gtfEntry.getExonID());
                        }
                    }
                    if(result.getCoordinates().size()>0){
                        variantResultOutput.add(result);
                    }
                    
                }catch(NullPointerException ex){
                    //System.out.println(result.getColValues("CHROM"));
                }
            }
        }

        public List<VariantResult> getResult() {
            return this.variantResultOutput;
        }
    }

    private static class VCFParserThread extends Thread {

        List<String> lines;
        List<VariantResult> result;
        List<String> colnames;

        public VCFParserThread(List lines, List colnames) {
            this.lines = lines;
            result = new ArrayList();
            this.colnames = colnames;
        }

        private VariantResult processVCFLine(String line) {
            String[] variantEntry = line.split("\\t");
            VariantResult entry = new VariantResult();
            for (int i = 0; i < variantEntry.length; i++) {
                entry.addItem(this.colnames.get(i), variantEntry[i]);
            }
            return entry;
        }

        @Override
        public void run() {
            for (String line : lines) {
                this.result.add(this.processVCFLine(line));
            }
        }

        public List<VariantResult> getResult() {
            return result;
        }

    }
}
