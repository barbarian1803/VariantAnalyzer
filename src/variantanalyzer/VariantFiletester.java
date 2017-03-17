package variantanalyzer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;


public class VariantFiletester {

    
    
    public static void main(String[] args) {
        testReadMetaData();

    }
    
    public static void testReadMetaData(){
        VariantFile vcfFile = new VariantFile();
        String filename = "data/cancer.exome.vcf";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("##")) {
                    vcfFile.addMetadata(line.replaceAll("##", ""));
                }else if(line.startsWith("#")){
                    line = line.replaceAll("#", "");
                    vcfFile.setColNames(new ArrayList(Arrays.asList(line.split("\\t"))));
                }else{
                    System.out.println("Now : "+vcfFile.getVariantResult().size());
                    String[] variantEntry = line.split("\\t");
                    VariantResult entry = new VariantResult();
                    for(int i=0;i<variantEntry.length;i++){
                        entry.addItem(vcfFile.getColNames().get(i), variantEntry[i]);
                    }
                    vcfFile.addVariantResult(entry);
                }
            }
            //vcfFile.printMetada();
            //vcfFile.printColNames();
            vcfFile.getVariantResult().get(0).printVariant();
            reader.close();
        } catch (Exception e) {
            System.err.format("Exception occurred trying to read '%s'.", filename);
            e.printStackTrace();
        }
    }
    
}
