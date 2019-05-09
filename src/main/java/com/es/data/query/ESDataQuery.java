package com.es.data.query;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.es.dto.FilterDto;
import com.es.filter.*;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import com.es.dto.SearchResults;
import org.springframework.beans.factory.annotation.Value;


public class ESDataQuery {

	@Value("${elasticsearch.domain.endpoint}")
	private String es_hostname;

	/**
	 * Returns user query results.
	 */
	public SearchResults executeQueryUsingSearchFilters(RestHighLevelClient esClient, String indexName, String type, FilterDto inoutQuery, int pageNum, int pageSize) throws Exception {

		BoolQueryBuilder queryBuilder = createQueryBuilder(inoutQuery);

		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(queryBuilder);
		sourceBuilder.from(pageNum*pageSize);
		sourceBuilder.size(pageSize);

		SearchRequest sr = new SearchRequest(indexName).types(type).source(sourceBuilder);

		SearchResponse response = esClient.search(sr, RequestOptions.DEFAULT);
		return getSearchResultsFromEsResponse(response);
	}

	/**
	 * Retuns hit results, searched records
	 * @param response ES response
	 * @return returns search results.
	 */
	private SearchResults getSearchResultsFromEsResponse(SearchResponse response) {

		SearchResults results = new SearchResults();
		if (response == null)
			return results;

		results.setTotalRecords(response.getHits().getTotalHits());
		results.setList(new ArrayList<>(response.getHits().getHits().length));
		for (SearchHit hit : response.getHits().getHits()) {
			Map<String, Object> record = new LinkedHashMap<>();
			if (hit != null) {
				record = hit.getSourceAsMap();
			}
			results.getList().add(record);
		}
		return results;
	}

	/**
	 * Validates input query and creates ES query. If no operator is given. everything will be returned.
	 * @param inputQuery user query.
	 * @return generated ES query builder.
	 */
	private BoolQueryBuilder createQueryBuilder(FilterDto inputQuery){

		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

		if (inputQuery != null) {
			if(inputQuery.getOperator() == null) {
				throw new IllegalArgumentException("Opertaor missing from query!");
			}
			buildFilterForStringDataType(queryBuilder, inputQuery);
		} else {
			System.out.println("search all");
		}
		return queryBuilder;
	}

	/**
	 * Query Builder for string operations. Current supported operation are EQUAL and LIKE.
	 * @param boolQueryBuilder boolQueryBuilder as input for additional string operations
	 * @param inputQuery user query.
	 */
	private void buildFilterForStringDataType(BoolQueryBuilder boolQueryBuilder, FilterDto inputQuery) {
		StringOperator operator = (StringOperator)inputQuery.getOperator();

		Set<String> values = inputQuery.getValues();
		switch (operator) {
		case EQ:
			boolQueryBuilder.must(QueryBuilders.termsQuery(inputQuery.getFilterName(), values));
			break;
		case EXISTS:
				boolQueryBuilder.must(QueryBuilders.existsQuery(inputQuery.getFilterName()));
				break;
		case LIKE:
			if (values.size() == 1) {
				boolQueryBuilder.must(QueryBuilders.wildcardQuery(inputQuery.getFilterName(),
						"*" + values.iterator().next() + "*"));
			} else {
				String value = values.stream().map((val) -> ".*" + val + ".*").collect(Collectors.joining("|"));
				boolQueryBuilder.must(QueryBuilders.regexpQuery(inputQuery.getFilterName(), value));
			}
			break;
		}
	}

	/**
	 * Please Ignore this code
	 *
	public static void main(String[] args) throws Exception {

		RestHighLevelClient esClient = new RestHighLevelClient(RestClient.builder(HttpHost.create("es_hostname")));

		String indexName = "personal_capital_fin_plan";
		String type = "fin_plan_v1";


		int pageNum = 1;
		int pageSize = 10;

		JsonObject jsonFilterObject = new JsonObject();

		JsonArray filterArray = new JsonArray();
		JsonObject filterObject = new JsonObject();

		filterObject.addProperty("filterName", "PLAN_NAME");
		filterObject.addProperty("operator", "LIKE");

		JsonArray filterValuesArray = new JsonArray();
		filterValuesArray.add("INSURANCE");
		filterObject.add("values", filterValuesArray);

		filterArray.add(filterObject);

		jsonFilterObject.add("filterBy", filterArray);

		QueryObjectDefinition objDef = new QueryObjectDefinition("filters.json"); ;
		FilterCriteria filterCriteria = FilterCriteria.mapJsonToFilterCriteria(jsonFilterObject.toString(), objectMapper, objDef);

		ESDataQuery esDataQuery = new ESDataQuery();

		// Elastic search paging starts from 0
		pageNum = pageNum - 1;

		SearchResults result = esDataQuery.executeQueryUsingSearchFilters(esClient, indexName, type, filterCriteria, pageNum, pageSize);
		System.out.println("Total: "+result.getTotalRecords());

		esClient.close();
	}
	**/

}
