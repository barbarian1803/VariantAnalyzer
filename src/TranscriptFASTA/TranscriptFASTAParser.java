package TranscriptFASTA;

import GTF.GTFEntry;
import GTF.GTFFile;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

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
                    transcriptFile.getTranscriptFASTA(currentTranscript).appendSequence(line);
                }
            }
            reader.close();
        } catch (FileNotFoundException ex) {
            
        } catch (IOException ex) {
            
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
                                transcriptFile.getTranscriptFASTA(transcriptID).addExon(gtfEntry.getExonID(), gtfEntry);
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
