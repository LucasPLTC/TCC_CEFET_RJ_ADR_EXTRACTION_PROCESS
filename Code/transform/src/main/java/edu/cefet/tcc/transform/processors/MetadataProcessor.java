package edu.cefet.tcc.transform.processors;

import java.util.Properties;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Bean;

import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import org.springframework.stereotype.Component;

import edu.cefet.tcc.domain.shared.FilteredData;

@Component
public class MetadataProcessor {
    private static final String APPLICATION_ID = "app-metadata";
    
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Value("${edu.cefet.tcc.transform.app-metadata.input-topic}")
    private String FILTERED_DATA_INPUT;

    @Value("${edu.cefet.tcc.transform.app-metadata.output-topic}")
    private String METADATA_OUTPUT;

    @Value("${spring.kafka.bootstrap.servers}")
    private String HOST_ADDRESS;
    
    @Bean("MetadataProcessorStreamBuilder")
    public StreamsBuilderFactoryBean ProcessadorNovosTweetsStreamBuilderFactoryBean() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_ID);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, HOST_ADDRESS);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    
        StreamsBuilderFactoryBean sb = new StreamsBuilderFactoryBean(); 
        sb.setStreamsConfiguration(props);
        return sb;
    }
    
    @Bean("MetadataProcessorStreamTopology")
    public KStream<String, Long> startProcessing(
        @Qualifier("MetadataProcessorStreamBuilder") StreamsBuilder builder
    ) { 
        KStream<String, Long> stream = builder.stream(
            FILTERED_DATA_INPUT, 
            Consumed.with(
                Serdes.String(), 
                Serdes.serdeFrom(
                    new JsonSerializer<FilteredData>(), 
                    new JsonDeserializer<FilteredData>(FilteredData.class)
                )
            )
        )
        .groupBy((key, filteredData) -> {
            return filteredData.getDrugName();
        })
        .count(Materialized.as("CountStore"))
        .toStream();

        stream.to(
            METADATA_OUTPUT,
            Produced.with(
                Serdes.String(), 
                Serdes.Long()
            )
        );

        return stream;
    }
}
