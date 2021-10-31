package edu.cefet.tcc.extractionhistorical.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import edu.cefet.tcc.domain.shared.FilteredData;

@Service
public class KafkaService {
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    
    public KafkaService(){
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Value("${edu.cefet.tcc.extraction-historical.app-tweets.output-topic}")
    private String DATA_OUTPUT;
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    ObjectMapper objectMapper = new ObjectMapper();

    public void sendToDataTopic(FilteredData data){
        LOG.info(">>> ENVIANDO DADO {} PARA O TOPICO: {} ", data.getId(),  DATA_OUTPUT);
        try {
            String jsonTweet = objectMapper.writeValueAsString(data);

            this.kafkaTemplate.send(
                DATA_OUTPUT,
                data.getId().toString(),
                jsonTweet
            );
        } catch (JsonProcessingException e) {
            LOG.error("OCORREU UM ERRO AO ENVIAR O TWEET!");
            e.printStackTrace();
        }
    }
}