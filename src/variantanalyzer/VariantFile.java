package variantanalyzer;

import java.util.ArrayList;
import java.util.List;

public class VariantFile {
    private List<String> metadata;
    private List<String> colNames;
    private List<VariantResult> variantResult;

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
    
    public List<String> getMetadata() {
        return metadata;
    }

    public void setMetadata(ArrayList<String> metadata) {
        this.metadata = metadata;
    }

    public List<String> getColNames() {
        return colNames;
    }

    public void setColNames(ArrayList<String> colNames) {
        this.colNames = colNames;
    }

    public List<VariantResult> getVariantResult() {
        return variantResult;
    }

    public void setVariantResult(ArrayList<VariantResult> variantResult) {
        this.variantResult = variantResult;
    }
    
    
}
