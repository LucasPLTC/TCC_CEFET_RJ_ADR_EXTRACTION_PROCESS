package edu.cefet.tcc.extractionhistorical.services;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import edu.cefet.tcc.domain.extraction.SearchItem;

@Service
public class DrugsFileService {
    @Value("${edu.cefet.tcc.drugs-files.full-list-file-name}")
    private String ALL_DRUGS_FILE_NAME;

    @Value("${edu.cefet.tcc.drugs-files.checkpoint-file-name}")
    private String REMAINING_DRUGS_FILE_NAME;

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    public boolean SaveRemaingData(Collection<SearchItem> remainingItems){
        Collection<String[]> linhas = new ArrayList<>();

        remainingItems.forEach(item -> {
            String[] linha = {item.getDrug(), item.getExpirationDate().toString()};
            linhas.add(linha);
        });

        try (CSVWriter writer = new CSVWriter(new FileWriter(REMAINING_DRUGS_FILE_NAME, Charset.forName("ISO-8859-15")))) {
            writer.writeAll(linhas);
            return true;
        } catch(Exception e){
            return false;
        }
    }
    
    //Em [0] tem o nome do medicamento, em [1] tem a data de vencimento do registro do medicamento
    public Collection<SearchItem> getSearchItems(){
        try {
            CSVReader reader;
            
            try {
                reader = new CSVReader(new FileReader(REMAINING_DRUGS_FILE_NAME, Charset.forName("ISO-8859-15")));
                LOG.info("Arquivo de medicamentos remanescentes encontrado! Resumindo extração.");
            } catch(IOException noRemainingDrugsFile){
                LOG.info("Arquivo de medicamentos remanescentes não encontrado. Pegando arquivo completo.");
                try {
                    LOG.info("Iniciando nova extração com todos os medicamentos.");
                    reader = new CSVReader(new FileReader(ALL_DRUGS_FILE_NAME, Charset.forName("ISO-8859-15")));
                } catch(IOException e){
                    LOG.info("Erro ao tentar localizar os arquivos.");
                    throw new Exception("Erro ao localizar arquivos");
                }
            }
            
            List<String[]> drugsAsString = reader.readAll();
            
            ArrayList<SearchItem> searchItems = new ArrayList<>();
            
            drugsAsString.stream().forEach(drug -> {
                searchItems.add(new SearchItem(drug));
            });
            
            reader.close();
            return searchItems.stream().filter(drug -> drug.isActive()).collect(Collectors.toList());
        }catch(Exception e){
            LOG.error("ERRO NO PARSE DO CSV DE MEDICAMENTOS! ");
            e.printStackTrace();
            return new ArrayList<SearchItem>();
        }
    }

    public void deleteRemainingDrugsFile() {
        try{
            File myObj = new File(REMAINING_DRUGS_FILE_NAME); 
            if (myObj.delete()) { 
                LOG.info("Arquivo de pesquisa de remanescentes deletado.");
            } else {
                LOG.error("Erro ao deletar arquivo de remanescentes");
            } 
        } catch(Exception e){
            LOG.error("Erro ao deletar arquivo.");
        }
    }
}
