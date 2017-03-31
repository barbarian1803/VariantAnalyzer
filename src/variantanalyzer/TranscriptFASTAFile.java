package variantanalyzer;

import java.util.HashMap;
import java.util.Map;

public class TranscriptFASTAFile {
    private Map<String,TranscriptFASTA> transripts;

    public TranscriptFASTAFile() {
        this.transripts = new HashMap();
    }
    
    public void addTranscript(String key, TranscriptFASTA value){
        this.transripts.put(key, value);
    }
    
    public TranscriptFASTA getTranscript(String transcript){
        return this.transripts.get(transcript);
    }
}
