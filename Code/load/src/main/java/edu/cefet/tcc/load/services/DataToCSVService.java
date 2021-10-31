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

import edu.cefet.tcc.domain.shared.Data;

@Service
public class DataToCSVService {
    @Autowired
    @Qualifier("DataProcessorStreamBuilder")
    private StreamsBuilderFactoryBean streamBuilderFactoryBean;

    public void exportDataAsCSV(){
        Collection<Data> dataForExport = this.getDataFromKafkaStreams();
        String[] cabecalho = {"Id", "Texto"};
        
        Collection<String[]> linhas = new ArrayList<>();
        linhas.add(cabecalho);

        dataForExport.forEach(data -> {
            String[] linha = {data.getId(), data.getText()};
            linhas.add(linha);
        });

        try (CSVWriter writer = new CSVWriter(new FileWriter("EXPORT_DATA_" + LocalDate.now().toString() + ".csv"))) {
            writer.writeAll(linhas);
        } catch(Exception e){
            
        }
    }

    private Collection<Data> getDataFromKafkaStreams(){
        
        Collection<Data> data = new ArrayList<Data>();

        KafkaStreams streams = streamBuilderFactoryBean.getKafkaStreams();

        ReadOnlyKeyValueStore<String, String> keyValueStore = streams.store(
            StoreQueryParameters.fromNameAndType(
                "dataStore", 
                QueryableStoreTypes.keyValueStore()
            )
        );
        AtomicReference<Integer> identificador = new AtomicReference<>(1);
        keyValueStore.all().forEachRemaining(kv ->{
            String text = kv.value;
            Integer id = identificador.get();
            
            Data ed = new Data();
            ed.setId(id.toString());
            ed.setText(text);
            
            identificador.set(id + 1);
            data.add(ed);
        });

        return data;
    }
}
