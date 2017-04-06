package variantanalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

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
                int stopIdx = startIdx + splitSize;
                if (stopIdx > tempData.size()) {
                    stopIdx = tempData.size();
                }
                threads[i] = new VCFParserThread(tempData.subList(startIdx, stopIdx), vcfFile.getColNames());
                threads[i].start();
            }

            for (int i = 0; i < threadNumber; i++) {
                threads[i].join();
            }

            for (int i = 0; i < threadNumber; i++) {
                vcfFile.getAllVariantResult().addAll(threads[i].getResult());
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
        List variantResult = vcfFile.getAllVariantResult();
        int splitSize = (int) Math.ceil(variantResult.size() / threadNumber);
        AssignVariationThread[] threads = new AssignVariationThread[threadNumber];
        
        for (int i = 0; i < threadNumber; i++) {
            
            int startIdx = i * splitSize;
            int stopIdx = startIdx + splitSize;
            
            if (stopIdx > variantResult.size()) {
                stopIdx = variantResult.size();
            }
            
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
            vcfFile.getAllVariantResult().addAll(threads[i].getResult());
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
                try {
                    List gtfEntryInChromosome = gtfFile.getEntryInChromosome(result.getColValues("CHROM"));
                    //search algorithm here
                    for (Object objGTFEntry : gtfEntryInChromosome) {

                        GTFEntry gtfEntry = (GTFEntry) objGTFEntry;
                        int pos = Integer.parseInt(result.getColValues("POS"));

                        if (pos >= gtfEntry.getStart() && pos <= gtfEntry.getEnd()) {
                            result.addCoordinate(gtfEntry.getTranscriptID(), gtfEntry.getExonID());
                        }
                    }
                    if (result.getCoordinates().size() > 0) {
                        variantResultOutput.add(result);
                    }

                } catch (NullPointerException ex) {
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

    public static void TranscriptConsensusSequence(VariantFile vcfWithCoordinate, TranscriptFASTAFile fasta, String sampleName, String fileOutput) throws FileNotFoundException {
        TranscriptFASTAFile fastaOutput = new TranscriptFASTAFile();
        PrintWriter pw = new PrintWriter(new File(fileOutput));
        for (int i = 0; i < vcfWithCoordinate.getVariantResultSize(); i++) {

            VariantResult variant = vcfWithCoordinate.getVariantResult(i);
            Set sampleGenotype = variant.returnGenotypeFromSample(sampleName);

            for (int j = 0; j < variant.getCoordinates().size(); j++) {

                Coordinate coord = variant.getCoordinates().get(j);
                TranscriptFASTA transcript = fasta.getTranscript(coord.getTranscriptID());
                
                if(sampleGenotype.size()>1){
                    System.out.println(coord.getTranscriptID()+" is heterozygous");
                }
                
                for (Object obj : sampleGenotype) {

                    int genotypeID = Integer.parseInt((String) obj);
                    int exonID = coord.getExonID();

                    String newMetadata = ">"+coord.getTranscriptID()+" genotype="+genotypeID;
                    pw.write(newMetadata+"\n");
                    try{
                        String newFasta = transcript.applyVariant(variant, genotypeID, exonID);
                        for(int xx=0;xx<=Math.ceil(newFasta.length() / 60);xx++){
                            int start = xx*60;
                            int end = start+60;
                            if(end>newFasta.length()){
                                end = newFasta.length();
                            }
                            pw.write(newFasta.substring(start,end)+"\n");
                        }
                    }catch(Exception ex){
                        System.out.println("Transcript "+coord.getTranscriptID()+" fasta sequence is not found");
                    }
                }
            }
        }
    }
}
