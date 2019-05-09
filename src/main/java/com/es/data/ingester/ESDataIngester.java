package com.es.data.ingester;

import java.io.File;
import java.util.Map;

import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.beans.factory.annotation.Value;

public class ESDataIngester {

    static ObjectMapper mapper = new ObjectMapper();

    @Value("${elasticsearch.domain.endpoint}")
    private String es_hostname;
	
	private void loadDataFromCsvToES(String fileName, String indexName, String esType) throws Exception {
		
    	RestHighLevelClient esClient = new RestHighLevelClient(RestClient.builder(HttpHost.create(es_hostname)));
		
		File input = new File(fileName);
		
        CsvSchema csvSchema = CsvSchema.builder().setUseHeader(true).build();
        CsvMapper csvMapper = new CsvMapper();
 
        // Read data from CSV file
        MappingIterator<String[]> mi = csvMapper.readerFor(Map.class).with(csvSchema).readValues(input);
        
        while (mi.hasNext()) {
        	        	
        	String document = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mi.next());
        	
            IndexRequest request = new IndexRequest(indexName, esType).source(document, XContentType.JSON);
            IndexResponse response = esClient.index(request, RequestOptions.DEFAULT);
            System.out.println(response.toString());
        }  
        
        esClient.close();        
	}	
		
    public static void main(String[] args) throws Exception {
    	    	
    	String indexName = "personal_capital_fin_plan";
    	String type = "fin_plan_v1";
    	
    	ESDataIngester esDataIngester = new ESDataIngester();
    	esDataIngester.loadDataFromCsvToES("src/main/resources/data.csv", indexName, type);
    	
    }
	
}
