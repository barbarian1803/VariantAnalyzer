package variantanalyzer;

public class Coordinate {

    private String transcriptID;
    private int exonID;

    public Coordinate(String transcriptID, int exonID) {
        this.transcriptID = transcriptID;
        this.exonID = exonID;
    }

    public String getTranscriptID() {
        return transcriptID;
    }

    public void setTranscriptID(String transcriptID) {
        this.transcriptID = transcriptID;
    }

    public int getExonID() {
        return exonID;
    }

    public void setExonID(int exonID) {
        this.exonID = exonID;
    }

    @Override
    public String toString() {
        return "[" + transcriptID + "," + exonID + "]";
    }
}
