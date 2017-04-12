package variantanalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VCFParser {

    public static VariantFile ReadVCF(String filename, int threadNumber) {

        VariantFile vcfFile = new VariantFile();
        List<String> tempData = new ArrayList();
        BufferedReader reader = null;
        FileReader fr = null;
        try {
            fr = new FileReader(filename);
            reader = new BufferedReader(fr);
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
            int splitSize = (int) Math.ceil((double)tempData.size() / threadNumber);
            VCFParserThread[] threads = new VCFParserThread[threadNumber];

            for (int i = 0; i < threadNumber; i++) {
                int startIdx = i * splitSize;
                int stopIdx = startIdx + splitSize;
                if (stopIdx > tempData.size()) {
                    stopIdx = tempData.size();
                }
                try{
                    threads[i] = new VCFParserThread(tempData.subList(startIdx, stopIdx), vcfFile.getColNames());
                    threads[i].start();
                }catch(Exception e){
                    
                }
            }

            for (int i = 0; i < threadNumber; i++) {
                try {
                    threads[i].join();
                } catch (InterruptedException | NullPointerException ex) {

                }
            }

            for (int i = 0; i < threadNumber; i++) {
                try{
                    vcfFile.getAllVariantResult().addAll(threads[i].getResult());
                } catch (NullPointerException ex) {

                }
            }   

        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }finally{
            try {
                fr.close();
                reader.close();
            } catch (IOException ex) {
            
            }
        }
        return vcfFile;
    }

    public static VariantFile AssignVariantLocation(VariantFile vcfFile, GTFFile gtfFile, int threadNumber) {
        List<VariantResult> variantResult = vcfFile.getAllVariantResult();
        int splitSize = (int) Math.ceil((double)variantResult.size() / threadNumber);
        AssignVariationThread[] threads = new AssignVariationThread[threadNumber];
        
        for (int i = 0; i < threadNumber; i++) {
            
            int startIdx = i * splitSize;
            int stopIdx = startIdx + splitSize;
            
            if (stopIdx > variantResult.size()) {
                stopIdx = variantResult.size();
            }
            try{
                threads[i] = new AssignVariationThread(variantResult.subList(startIdx, stopIdx), vcfFile.getColNames(), gtfFile);
                threads[i].start();
            }catch(Exception e){
                
            }
        }
        
        for (int i = 0; i < threadNumber; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException | NullPointerException ex) {

            }
        }

        vcfFile.setVariantResult(new ArrayList());

        for (int i = 0; i < threadNumber; i++) {
            try {
                vcfFile.getAllVariantResult().addAll(threads[i].getResult());
            } catch (NullPointerException ex) {

            }
        }

        return vcfFile;
    }
    
    public static Map<String, VariantResult> AssignVariantToExon(VariantFile vcfFile, GTFFile gtfFile, int threadNumber) {
        Map<String, VariantResult> output = new HashMap();
        List<VariantResult> variantResult = vcfFile.getAllVariantResult();
        int splitSize = (int) Math.ceil((double)variantResult.size() / threadNumber);
        AssignVariationToExonThread[] threads = new AssignVariationToExonThread[threadNumber];
        
        for (int i = 0; i < threadNumber; i++) {
            
            int startIdx = i * splitSize;
            int stopIdx = startIdx + splitSize;
            
            if (stopIdx > variantResult.size()) {
                stopIdx = variantResult.size();
            }
            try{
                threads[i] = new AssignVariationToExonThread(variantResult.subList(startIdx, stopIdx), vcfFile.getColNames(), gtfFile);
                threads[i].start();
            }catch(Exception e){
                
            }
        }
        
        for (int i = 0; i < threadNumber; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException | NullPointerException ex) {

            }
        }
        //merge result of each thread to output
        for (int i = 0; i < threadNumber; i++) {
            
        }
        
        return output;
    }

    public static void TranscriptConsensusSequence(VariantFile vcfWithCoordinate, TranscriptFASTAFile fasta, String sampleName, String fileOutput) throws FileNotFoundException {
        TranscriptFASTAFile fastaOutput = new TranscriptFASTAFile();
        
        for (int i = 0; i < vcfWithCoordinate.getVariantResultSize(); i++) {

            VariantResult variant = vcfWithCoordinate.getVariantResult(i);
            Set<String> sampleGenotype = variant.returnGenotypeFromSample(sampleName);

            for (int j = 0; j < variant.getCoordinates().size(); j++) {

                Coordinate coord = variant.getCoordinates().get(j);
                TranscriptFASTA transcript = fasta.getTranscript(coord.getTranscriptID());
                                
                for (String obj : sampleGenotype) {

                    int genotypeID = Integer.parseInt(obj);
                    int exonID = coord.getExonID();

                    try{
                        transcript.applyVariant(variant, genotypeID, exonID);
                    }catch(Exception ex){
                        
                    }
                }
            }
        }
        PrintWriter pw = new PrintWriter(new File(fileOutput));
        
        
        pw.close();
    }
    
    //NESTED CLASSES FOR THREAD
    
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
                    List<GTFEntry> gtfEntryInChromosome = gtfFile.getEntryInChromosome(result.getColValues("CHROM"));
                    for (GTFEntry gtfEntry : gtfEntryInChromosome) {

                        int pos = Integer.parseInt(result.getColValues("POS"));
                        if (pos >= gtfEntry.getStart() && pos <= gtfEntry.getEnd()) {
                            result.addCoordinate(gtfEntry.getTranscriptID(), gtfEntry.getExonID());
                        }
                    }
                    if (result.getCoordinates().size() > 0) {
                        variantResultOutput.add(result);
                    }
                } catch (NullPointerException ex) {
                    
                }
            }
        }

        public List<VariantResult> getResult() {
            return this.variantResultOutput;
        }
    }
    
    private static class AssignVariationToExonThread extends Thread {

        GTFFile gtfFile;
        List<VariantResult> variantResult;
        List colnames;
        Map<String,List<VariantResult>> exonListWithVariation;

        public AssignVariationToExonThread(List<VariantResult> variantResult, List colnames, GTFFile gtfFile) {
            this.gtfFile = gtfFile;
            this.colnames = colnames;
            this.variantResult = variantResult;
            this.exonListWithVariation = new HashMap<>();
        }

        @Override
        public void run() {
            for (VariantResult result : variantResult) {
                try {
                    List<GTFEntry> gtfEntryInChromosome = gtfFile.getEntryInChromosome(result.getColValues("CHROM"));
                    for (GTFEntry gtfEntry : gtfEntryInChromosome) {

                        int pos = Integer.parseInt(result.getColValues("POS"));
                        if (pos >= gtfEntry.getStart() && pos <= gtfEntry.getEnd()) {
                            String key = gtfEntry.getTranscriptID()+"_"+gtfEntry.getExonID();
                            if(!exonListWithVariation.containsKey(key)){
                                exonListWithVariation.put(key, new ArrayList());
                            }
                            exonListWithVariation.get(key).add(result);
                        }
                    }
                } catch (NullPointerException ex) {
                    
                }
            }
        }

        public Map<String,List<VariantResult>> getResult() {
            return this.exonListWithVariation;
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
