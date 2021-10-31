package edu.cefet.tcc.load.services;

import java.io.FileWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

import com.opencsv.CSVWriter;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Service;

import edu.cefet.tcc.domain.load.ExportMetadata;

@Service
public class MetadataToCSVService {
    @Autowired
    @Qualifier("MetadataProcessorStreamBuilder")
    private StreamsBuilderFactoryBean streamBuilderFactoryBean;

    public void exportMetadataAsCSV(){
        Collection<ExportMetadata> dataForExport = this.getMetadaDataFromKafkaStreams();
        String[] cabecalho = {"Id", "Medicamento", "Tweets Encontrados"};
        
        Collection<String[]> linhas = new ArrayList<>();
        linhas.add(cabecalho);

        AtomicReference<Integer> identificador = new AtomicReference<>(1);
        dataForExport.forEach(exportData -> {
            Integer id = identificador.get();
            String[] linha = {id.toString(), exportData.getDrugName(), exportData.getTotalResults()};
            identificador.set(id+1);

            linhas.add(linha);
        });

        try (CSVWriter writer = new CSVWriter(new FileWriter("EXPORT_METADATA_" + LocalDate.now().toString() + ".csv"))) {
            writer.writeAll(linhas);
        } catch(Exception e){
            
        }
    }

    private Collection<ExportMetadata> getMetadaDataFromKafkaStreams(){
        KafkaStreams streams = streamBuilderFactoryBean.getKafkaStreams();

        ReadOnlyKeyValueStore<String, Long> keyValueStore = streams.store(
            StoreQueryParameters.fromNameAndType(
                "metadataStore", 
                QueryableStoreTypes.keyValueStore()
            )
        );

        Collection<ExportMetadata> data = new ArrayList<ExportMetadata>();

        keyValueStore.all().forEachRemaining(kv ->{
            String medicamento = kv.key;
            Long count = kv.value;

            ExportMetadata ed = new ExportMetadata();
            ed.setDrugName(medicamento);
            ed.setTotalResults(count.toString());
            
            data.add(ed);
        });

        return data;
    }
}