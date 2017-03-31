package variantanalyzer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
    
    
    
}
