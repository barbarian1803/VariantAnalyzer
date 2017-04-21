package VCF;

import TranscriptFASTA.TranscriptFASTAFile;
import TranscriptFASTA.TranscriptFASTA;
import GTF.GTFEntry;
import GTF.GTFFile;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import TranscriptFASTA.VariantFASTA;
import TranscriptFASTA.VariantFASTASequence;

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
            int splitSize = (int) Math.ceil((double) tempData.size() / threadNumber);
            VCFParserThread[] threads = new VCFParserThread[threadNumber];

            for (int i = 0; i < threadNumber; i++) {
                int startIdx = i * splitSize;
                int stopIdx = startIdx + splitSize;
                if (stopIdx > tempData.size()) {
                    stopIdx = tempData.size();
                }
                try {
                    threads[i] = new VCFParserThread(tempData.subList(startIdx, stopIdx), vcfFile.getColNames());
                    threads[i].start();
                } catch (Exception e) {

                }
            }

            for (int i = 0; i < threadNumber; i++) {
                try {
                    threads[i].join();
                } catch (InterruptedException | NullPointerException ex) {

                }
            }

            for (int i = 0; i < threadNumber; i++) {
                try {
                    vcfFile.getAllVariantResult().addAll(threads[i].getResult());
                } catch (NullPointerException ex) {

                }
            }

        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        } finally {
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
        int splitSize = (int) Math.ceil((double) variantResult.size() / threadNumber);
        AssignVariationThread[] threads = new AssignVariationThread[threadNumber];

        for (int i = 0; i < threadNumber; i++) {

            int startIdx = i * splitSize;
            int stopIdx = startIdx + splitSize;

            if (stopIdx > variantResult.size()) {
                stopIdx = variantResult.size();
            }
            try {
                threads[i] = new AssignVariationThread(variantResult.subList(startIdx, stopIdx), vcfFile.getColNames(), gtfFile);
                threads[i].start();
            } catch (Exception e) {

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

    public static Map<String, List<VariantResult>> AssignVariantToExon(VariantFile vcfFile, GTFFile gtfFile, int threadNumber) {
        // key is transcript id+"_"+exon_number where variation is occured
        Map<String, List<VariantResult>> output = new HashMap();
        List<VariantResult> variantResult = vcfFile.getAllVariantResult();
        int splitSize = (int) Math.ceil((double) variantResult.size() / threadNumber);
        AssignVariationToExonThread[] threads = new AssignVariationToExonThread[threadNumber];

        for (int i = 0; i < threadNumber; i++) {

            int startIdx = i * splitSize;
            int stopIdx = startIdx + splitSize;

            if (stopIdx > variantResult.size()) {
                stopIdx = variantResult.size();
            }
            try {
                threads[i] = new AssignVariationToExonThread(variantResult.subList(startIdx, stopIdx), vcfFile.getColNames(), gtfFile);
                threads[i].start();
            } catch (Exception e) {

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
            for (String key : threads[i].getResult().keySet()) {
                if (!output.containsKey(key)) {
                    output.put(key, new ArrayList<>());
                }
                List listFromKey = threads[i].getResult().get(key);
                output.get(key).addAll(listFromKey);
            }
        }

        return output;
    }

    public static void TranscriptConsensusSequence(Map<String, List<VariantResult>> variantPerExon, TranscriptFASTAFile fasta, String sampleName, String fileOutput) {
        PrintWriter pw;
        try {
            pw = new PrintWriter(new File(fileOutput));
            for (String transcriptID : fasta.getTranscriptList()) {
                TranscriptFASTA fastaTranscript = fasta.getTranscriptFASTA(transcriptID);

                VariantFASTA variantFasta = new VariantFASTA();
                variantFasta.addSequences(new VariantFASTASequence(fastaTranscript.getSequence(), 0));

                for (int exonNumber : fastaTranscript.getExonNumber()) {
                    try {
                        for (VariantResult variant : variantPerExon.get(transcriptID + "_" + exonNumber)) {
                            String[] genotypes = variant.returnGenotypeFromSample(sampleName);
                            int pos = Integer.parseInt(variant.getColValues("POS"));
                            String alleleRef = variant.returnAlleleList()[0];
                            if (genotypes.length > 1) {
                                //heterozygous
                                variantFasta.addTotalHeterozygous();
                                List<VariantFASTASequence> temp = new ArrayList<>();
                                for (int i = 0; i < variantFasta.getSequences().size(); i++) {
                                    String seq = variantFasta.getSequence(i).getSequence();
                                    int offset = TranscriptFASTA.ChromPosToTranscriptOffset(fastaTranscript, pos, exonNumber, alleleRef, variantFasta.getSequence(i));
                                    int offsetWithIndel = TranscriptFASTA.IndelAdjustedTranscriptOffset(offset, variantFasta.getSequence(i));
                                    String alleleAlt1 = variant.returnAlleleList()[Integer.parseInt(genotypes[0])];
                                    String alleleAlt2 = variant.returnAlleleList()[Integer.parseInt(genotypes[1])];

                                    try {
                                        String seqWithVariation1 = fastaTranscript.applyVariant(seq, alleleRef, alleleAlt1, exonNumber, offsetWithIndel);
                                        variantFasta.getSequence(i).setSequence(seqWithVariation1);
                                        variantFasta.getSequence(i).addVariantOffset(offset, alleleRef.length(), alleleAlt1.length());
                                    } catch (IndexOutOfBoundsException ex) {
                                        System.out.println(transcriptID);
                                    }

                                    try {
                                        String seqWithVariation2 = fastaTranscript.applyVariant(seq, alleleRef, alleleAlt2, exonNumber, offsetWithIndel);
                                        VariantFASTASequence newlyAdded = new VariantFASTASequence(seqWithVariation2, variantFasta.getSequences().get(i).getHeteroZygousNumber() + 1);

                                        newlyAdded.getHeterozygousPos().addAll(variantFasta.getSequences().get(i).getHeterozygousPos());
                                        newlyAdded.addHeterozygousPos(Integer.parseInt(variant.getColValues("POS")));
                                        newlyAdded.addVariantOffset(offset, alleleRef.length(), alleleAlt2.length());
                                        temp.add(newlyAdded);
                                    } catch (IndexOutOfBoundsException ex) {
                                        System.out.println(transcriptID);
                                    }
                                }
                                variantFasta.getSequences().addAll(temp);
                            } else {
                                //homozygous
                                for (int i = 0; i < variantFasta.getSequences().size(); i++) {
                                    int offset = TranscriptFASTA.ChromPosToTranscriptOffset(fastaTranscript, pos, exonNumber, alleleRef, variantFasta.getSequence(i));
                                    int offsetWithIndel = TranscriptFASTA.IndelAdjustedTranscriptOffset(offset, variantFasta.getSequence(i));
                                    String alleleAlt1 = variant.returnAlleleList()[Integer.parseInt(genotypes[0])];
                                    String seq = variantFasta.getSequences().get(i).getSequence();
                                    try {
                                        String seqWithVariation = fastaTranscript.applyVariant(seq, alleleRef, alleleAlt1, exonNumber, offsetWithIndel);
                                        variantFasta.getSequences().get(i).setSequence(seqWithVariation);
                                        variantFasta.getSequence(i).addVariantOffset(offset, alleleRef.length(), alleleAlt1.length());
                                    } catch (IndexOutOfBoundsException ex) {
                                        System.out.println(transcriptID);
                                    }
                                }
                            }
                        }
                    } catch (NullPointerException ex) {

                    }
                }

                variantFasta.printfastaSequence(fastaTranscript.getMetadataOriginal(), pw);
            }
            pw.close();
        } catch (FileNotFoundException ex) {

        }
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
        Map<String, List<VariantResult>> exonListWithVariation;

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
                            String key = gtfEntry.getTranscriptID() + "_" + gtfEntry.getExonID();
                            if (!exonListWithVariation.containsKey(key)) {
                                exonListWithVariation.put(key, new ArrayList());
                            }
                            exonListWithVariation.get(key).add(result);
                        }
                    }
                } catch (NullPointerException ex) {

                }
            }
        }

        public Map<String, List<VariantResult>> getResult() {
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
