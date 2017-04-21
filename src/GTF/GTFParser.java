package GTF;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author bharata
 */
public class GTFParser {

    public static GTFFile ReadGTF(String filename) {
        GTFFile gtfFile = new GTFFile();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("#")) {
                    GTFEntry entry = GTFParser.parseEntryLine(line);
                    if(entry!=null){
                        gtfFile.addEntry(""+entry.getChromosome(), entry);
                    }
                }
            }
        } catch (Exception e) {
            System.err.format("Exception occurred trying to read '%s'.", filename);
            e.printStackTrace();
            return null;
        }

        return gtfFile;
    }

    private static GTFEntry parseEntryLine(String line) {
        GTFEntry entry = new GTFEntry();
        String[] splitted = line.split("\\t");

        if (!splitted[2].equalsIgnoreCase("exon")) {
            return null;
        }

        entry.setChromosome(splitted[0]);
        entry.setStrand(splitted[6].charAt(0));
        entry.setStart(Integer.parseInt(splitted[3]));
        entry.setEnd(Integer.parseInt(splitted[4]));

        String descriptions[] = splitted[8].split(";");

        Pattern p = Pattern.compile("\"([^\"]*)\"");

        for (String desc : descriptions) {
            Matcher m = p.matcher(desc);
            m.find();
            if(desc.contains("gene_id")){    
                entry.setGeneID(m.group(1));
            }
            if(desc.contains("transcript_id")){    
                entry.setTranscriptID(m.group(1));
            }
            if(desc.contains("exon_number")){    
                entry.setExonID(Integer.parseInt(m.group(1)));
            }
            if(desc.contains("gene_name")){    
                entry.setGeneName(m.group(1));
            }
            
        }
        
        return entry;
    }
}
