package variantanalyzer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TranscriptFASTAParser {
    public static TranscriptFASTAFile ParseFASTAFile(String name){
        String filename = name;
        TranscriptFASTAFile transcriptFile = new TranscriptFASTAFile();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            String currentTranscript = "";
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(">")) {
                    TranscriptFASTA transcript = new TranscriptFASTA(line);
                    transcriptFile.addTranscript(transcript.getTranscriptID(), transcript);
                    currentTranscript = transcript.getTranscriptID();
                } else {
                    transcriptFile.getTranscript(currentTranscript).appendSequence(line);
                }
            }
            reader.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CDNAFastaParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CDNAFastaParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return transcriptFile;
    }
    
    public static TranscriptFASTAFile AssignExonsPerTranscript(TranscriptFASTAFile transcriptFile, GTFFile gtfFile, int threadNumber){
        String[] chromosomeList = gtfFile.gethromosomeName();
        for(String chromosome:chromosomeList){
            List gtfEntriesInChromosome = gtfFile.getEntryInChromosome(chromosome);
            
            int splitSize = (int) Math.ceil(gtfEntriesInChromosome.size()/threadNumber);
            Thread[] t = new Thread[threadNumber];
            
            for(int i=0;i<threadNumber;i++){
                int startIdx = i * splitSize;
                int stopIdx = startIdx + splitSize;

                if (stopIdx > gtfEntriesInChromosome.size()) {
                    stopIdx = gtfEntriesInChromosome.size();
                }
                List gtfEntriesSubList = gtfEntriesInChromosome.subList(startIdx, stopIdx);
                t[i] =new Thread(new Runnable() {

                    @Override
                    public void run() {
                        for(Object obj:gtfEntriesSubList){
                            GTFEntry gtfEntry = (GTFEntry) obj;
                            String transcriptID = gtfEntry.getTranscriptID();
                            try{
                                transcriptFile.getTranscript(transcriptID).addExon(gtfEntry.getExonID(), gtfEntry);
                            }catch(Exception ex){

                            }
                        }
                    }
                });
                t[i].start();
            }
            for(int i=0;i<threadNumber;i++){
                try {
                    t[i].join();
                } catch (InterruptedException ex) {
                    
                }
            }
        }
        return transcriptFile;
    }
    
}
