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
public class VCFParserThread extends Thread {

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
