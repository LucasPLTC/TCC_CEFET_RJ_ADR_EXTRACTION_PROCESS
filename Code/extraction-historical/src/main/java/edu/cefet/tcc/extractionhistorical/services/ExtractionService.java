package edu.cefet.tcc.extractionhistorical.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import edu.cefet.tcc.domain.shared.FilteredData;
import edu.cefet.tcc.domain.extraction.SearchItem;
import edu.cefet.tcc.domain.shared.Data;
import edu.cefet.tcc.domain.shared.SearchResults;

@Service
public class ExtractionService {
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    
    @Autowired
    DrugsFileService drugsFileService;

    @Autowired
    TwitterApiConnectionService twitterService;

    @Autowired
    KafkaService kafkaService;

    @Value("${edu.cefet.twitter.request-limit}")
    private Integer REQUEST_LIMIT;

    public void startExtraction(){
        Integer currentRequestCount = 0;
        Collection<SearchItem> searchItems = drugsFileService.getSearchItems();
        Collection<SearchItem> remainingItems = new ArrayList<SearchItem>(searchItems);

        for(SearchItem searchItem: searchItems){
            SearchResults sr = twitterService.getTweetsPerDrug(searchItem, currentRequestCount);
            currentRequestCount += sr.getRequestsExecuted();
            
            for(Data data: sr.getExtractedData()){

                FilteredData d = new FilteredData();
                d.setDrugName(sr.getDrugName());
                d.setExtractedAt(LocalDate.now());
                d.setId(data.getId());
                d.setText(data.getText());

                kafkaService.sendToDataTopic(d);
            }

            remainingItems.remove(searchItem);

            if(currentRequestCount > REQUEST_LIMIT){
                LOG.info("Parando devido a limite de request.");
                if(remainingItems.isEmpty()){
                    drugsFileService.deleteRemainingDrugsFile();
                } else {
                    drugsFileService.SaveRemaingData(remainingItems);
                }
                break;
            }
        }

        if(remainingItems.isEmpty()){
            drugsFileService.deleteRemainingDrugsFile();
        }
    }
}
