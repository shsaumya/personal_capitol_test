package com.es.controller;

import com.es.data.query.ESDataQuery;
import com.es.dto.FilterDto;
import com.es.dto.SearchResults;
import com.es.util.EsUtil;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class QueryController {

    private static final String INDEX_NAME = "personal_capital_fin_plan";
    private static final String TYPE = "fin_plan_v1";
    private ObjectMapper objectMapper = new ObjectMapper();

    @Value("${elasticsearch.domain.endpoint}")
    private String es_hostname;

    public QueryController() {

    }

    @RequestMapping(value={"/search/pageNum/{pages_index}/pageSize/{page_size}"}, method={RequestMethod.POST}, produces = {"application/json"})
    public ResponseEntity<SearchResults> searchRequest(@PathVariable("pages_index") int pages_index, @PathVariable("page_size") int page_size,
                                @RequestBody String filterObject) throws Exception {
        RestHighLevelClient esClient = new RestHighLevelClient(RestClient.builder(HttpHost.create(es_hostname)));
        ESDataQuery esDataQuery = new ESDataQuery();

        EsUtil.validatePageIndex(pages_index);

        FilterDto incomingQuery = EsUtil.deserializeFilterObject(filterObject, objectMapper);

        SearchResults result = esDataQuery.executeQueryUsingSearchFilters(esClient, INDEX_NAME, TYPE, incomingQuery, pages_index, page_size);

        esClient.close();

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @RequestMapping(value={"/health"}, method={RequestMethod.GET})
    public ResponseEntity<String> health( ) {

        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
