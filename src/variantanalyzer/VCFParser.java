/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package variantanalyzer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author barbarian
 */
public class VCFParser {
    public static void ReadVCF(String filename,int threadNumber){
        
        VariantFile vcfFile = new VariantFile();
        ArrayList<String> tempData = new ArrayList();

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
                    tempData.add(line);
                }
            }
            
            //multi threading process
            int splitSize = (int) Math.ceil(tempData.size()/threadNumber);
            VCFParserThread[] threads = new VCFParserThread[threadNumber];
           
            for(int i=0;i<threadNumber;i++){
                int startIdx = i*splitSize;
                int stopIdx = startIdx+splitSize-1;
                
                threads[i] = new VCFParserThread(tempData.subList(startIdx, stopIdx), vcfFile.getColNames());
                threads[i].start();
            }
            for(int i=0;i<threadNumber;i++){
                threads[i].join();
            }
            for(int i=0;i<threadNumber;i++){
                vcfFile.getVariantResult().addAll(threads[i].getResult());
            }
            System.out.println("Size "+vcfFile.getVariantResult().size());
            System.out.println(vcfFile.getVariantResult().get(0).getColValues("CHROM"));
            
            reader.close();
        } catch (Exception e) {
            System.err.format("Exception occurred trying to read '%s'.", filename);
            e.printStackTrace();
	}
    }
}
