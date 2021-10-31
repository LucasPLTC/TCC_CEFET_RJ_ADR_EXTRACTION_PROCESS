package edu.cefet.tcc.load.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.cefet.tcc.load.services.MetadataToCSVService;
import edu.cefet.tcc.load.services.DataToCSVService;

@RestController
@RequestMapping("/export")
public class DataExportController {
    @Autowired
    MetadataToCSVService metaService;

    @Autowired
    DataToCSVService dataService;

    @Scheduled(cron = "0 0 12 * * *")
    public void scheduledMetaExport(){
        this.exportMetadata();
    }

    @Scheduled(cron = "0 0 12 * * *")
    public void scheduledDataExport(){
        this.exportData();
    }

    @PostMapping("/meta/now")
    public void exportMetaNow(){
        exportMetadata();
    }

    @PostMapping("/data/now")
    public void exportDataNow(){
        this.exportData();
    }

    private void exportMetadata(){
        this.metaService.exportMetadataAsCSV();
    }

    private void exportData(){
        this.dataService.exportDataAsCSV();
    }
}
