package variantanalyzer;

import java.util.ArrayList;

public class VariantFile {
    private ArrayList<String> metadata;
    private ArrayList<String> colNames;
    private ArrayList<VariantResult> variantResult;

    public VariantFile() {
        this.metadata = new ArrayList();
        this.colNames = new ArrayList();
        this.variantResult = new ArrayList();
    }

    public void addMetadata(String data){
        this.metadata.add(data);
    }
    
    public void printMetada(){
        for(String metadata: this.metadata){
            System.out.println(metadata);
        }
    }
    
    public void addColNames(String data){
        this.colNames.add(data);
    }
    
    public void printColNames(){
        System.out.println(this.colNames.toString());
    }
    
    public void addVariantResult(VariantResult entry){
        this.variantResult.add(entry);
    }
    
    public ArrayList<String> getMetadata() {
        return metadata;
    }

    public void setMetadata(ArrayList<String> metadata) {
        this.metadata = metadata;
    }

    public ArrayList<String> getColNames() {
        return colNames;
    }

    public void setColNames(ArrayList<String> colNames) {
        this.colNames = colNames;
    }

    public ArrayList<VariantResult> getVariantResult() {
        return variantResult;
    }

    public void setVariantResult(ArrayList<VariantResult> variantResult) {
        this.variantResult = variantResult;
    }
    
    
}
