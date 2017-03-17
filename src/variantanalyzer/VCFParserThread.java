/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package variantanalyzer;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bharata
 */
public class VCFParserThread extends Thread{
    List<String> lines;
    ArrayList<VariantResult> result;
    ArrayList<String> colnames;
    
    public VCFParserThread(List lines,ArrayList colnames){
        this.lines = lines;
        result = new ArrayList();
        this.colnames = colnames;
    }
    
    @Override
    public void run() {
        for(String line:lines){
            String[] variantEntry = line.split("\\t");
            VariantResult entry = new VariantResult();
            for(int i=0;i<variantEntry.length;i++){
              entry.addItem(this.colnames.get(i), variantEntry[i]);
            }
            result.add(entry);
        }
    }

    public ArrayList<VariantResult> getResult() {
        return result;
    }

    public void setResult(ArrayList<VariantResult> result) {
        this.result = result;
    }
    
}
