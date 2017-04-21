package GTF;

public class GTFEntry {
    private String chromosome;
    private String transcriptID;
    private int exonID;
    private String geneID;
    private String geneName;
    private int start;
    private int end;

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }
    
    public char getStrand() {
        return strand;
    }

    public void setStrand(char strand) {
        this.strand = strand;
    }
    private char strand;
    
    public String getGeneName() {
        return geneName;
    }

    public void setGeneName(String geneName) {
        this.geneName = geneName;
    }            

    public String getChromosome() {
        return chromosome;
    }

    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
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

    public String getGeneID() {
        return geneID;
    }

    public void setGeneID(String geneID) {
        this.geneID = geneID;
    }
    
    public void printEntry(){
        System.out.println(this.transcriptID+" "+this.geneID+" "+this.geneName+" "+this.exonID+" "+this.strand+" "+this.start+" "+this.end);
    }
    
}
