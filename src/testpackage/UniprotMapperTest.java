package testpackage;

import Util.UniprotEnsemblMapper;

public class UniprotMapperTest {
    public static void main(String[] args){
        UniprotEnsemblMapper.ReadMappingData("data/uniprot_ensembl.csv", ";");
        System.out.println(UniprotEnsemblMapper.getUniprotID("ENSG00000149050"));
        
    }
}
