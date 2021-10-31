package edu.cefet.tcc.extractionhistorical.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.cefet.tcc.domain.extraction.stream.TwitterAPIStreamResponse;
import edu.cefet.tcc.extractionhistorical.services.ExtractionService;

@RestController()
@RequestMapping("/extraction")
public class ExtractionController {
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    
    @Autowired
    ExtractionService service;

    @PostMapping("/now")
    public void forcedExtraction(){
        LOG.info("Iniciando extração manual.");
        extract();
    }

    @PostMapping("/new-tweet")
    public void newTweetPost(@RequestBody TwitterAPIStreamResponse data){
        LOG.info("### Received data from the extraction stream service >> {}", data);
    }

    private void extract(){
        service.startExtraction();
    }
    
    @Scheduled(cron = "0 0,20,40 * * * *")
    public void scheduledExport(){
        LOG.info("Iniciando extração automatizada.");
        extract();
    }
}
