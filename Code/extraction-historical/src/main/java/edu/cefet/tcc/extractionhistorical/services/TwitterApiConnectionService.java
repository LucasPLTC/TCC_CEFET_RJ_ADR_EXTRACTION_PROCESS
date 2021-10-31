package edu.cefet.tcc.extractionhistorical.services;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import edu.cefet.tcc.domain.extraction.SearchItem;
import edu.cefet.tcc.domain.extraction.historical.TwitterAPISearchResponse;
import edu.cefet.tcc.domain.shared.Data;
import edu.cefet.tcc.domain.shared.SearchResults;

@Service
public class TwitterApiConnectionService {
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Value("${edu.cefet.twitter.default-url}")
    private String DEFAULT_URL;

    @Value("${edu.cefet.twitter.auth-token}")
    private String BEARER_TOKEN;

    @Value("${edu.cefet.twitter.request-limit}")
    private Integer REQUEST_LIMIT;

    private ObjectMapper mapper = new ObjectMapper();

    public SearchResults getTweetsPerDrug(SearchItem searchItem, Integer startRequestCount){
        LOG.info(">> INICIANDO EXTRAÇÃO PARA O MEDICAMENTO: {}; TOTAL DE REQUESTS: {}.", searchItem.getDrug(), startRequestCount);

        List<Data> extractedData = new ArrayList<>();

        Integer currentPageCount = 0;
        Integer requestCount = 0;

        TwitterAPISearchResponse convertedResponse = new TwitterAPISearchResponse();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        
        try{
            do{
                String url;
                if(currentPageCount == 0){
                    LOG.info("Pagina atual: {}", currentPageCount);
                    url = configQuery(searchItem.getDrug(), null);
                } else {
                    LOG.info("Pagina atual: {}, token de próxima página: {}", currentPageCount, convertedResponse.getMeta().getNext_token());
                    url = configQuery(searchItem.getDrug(), convertedResponse.getMeta().getNext_token());
                }

                
                HttpGet request = new HttpGet(url);

                request.setHeader(HttpHeaders.CONTENT_TYPE,"application/json");
                request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + BEARER_TOKEN);

                HttpResponse response = httpClient.execute(request);
                requestCount++;

                String responseBody = EntityUtils.toString(response.getEntity());

                convertedResponse = mapper.readValue(responseBody, TwitterAPISearchResponse.class);
                
                if(convertedResponse.getData() != null){
                    convertedResponse.getData().forEach(data -> {
                        extractedData.add(data);
                    });
                    currentPageCount++;
                } else {
                    LOG.debug("Sem resultados para: {}", searchItem.getDrug());
                    break;
                }
            } while((convertedResponse.getMeta() != null && convertedResponse.hasNextPage()) && (requestCount + startRequestCount < REQUEST_LIMIT));
            LOG.info("Páginas: {}, Requests feitos: {}, resultados obtidos: {}", currentPageCount, requestCount, extractedData.size());
            httpClient.close();
        } catch(Exception e){
            LOG.error("Deu pau em algo:");
            e.printStackTrace();
        }

        SearchResults searchResults = new SearchResults();
        searchResults.setDrugName(searchItem.getDrug());
        searchResults.setRequestsExecuted(requestCount);
        searchResults.setExtractedData(extractedData);
        return searchResults;
    }

    private String configQuery(String drugName, String nextPageToken){
        String queryParameter = "?query=";
        String query = "tomei " + drugName;
        String restrictions = " lang:pt -is:retweet";
        String nextToken = "&next_token=";
        String maxResults = "&max_results=100";
        String fullUrl = queryParameter.concat(query).concat(restrictions).concat(maxResults);

        if(nextPageToken != null){
            fullUrl = fullUrl.concat(nextToken).concat(nextPageToken);
        }

        fullUrl = fullUrl.replace(" ", "%20");
        fullUrl = fullUrl.replace(":", "%3A");

        fullUrl = DEFAULT_URL.concat(fullUrl);

        LOG.info("Query para o medicamento {}: {}", drugName, fullUrl);

        return fullUrl;
    }
}
