package com.es.util;

import com.es.dto.FilterDto;
import com.es.dto.FilterQueryDto;
import com.fasterxml.jackson.databind.ObjectMapper;


public class EsUtil {

	/**
	 * ES index starts from 0 hence PageIndex should not be less than 0.
	 * @param pageIndex Search start index
	 * @return returns valid page start index
	 */
	public static int validatePageIndex(int pageIndex) {
		if (pageIndex == 0)
			pageIndex = 1;

		if (pageIndex < 1) {
			pageIndex = 1;

		}

		// Elastic search paging starts from 0
		pageIndex = pageIndex - 1;

		return pageIndex;
	}

	public static FilterDto deserializeFilterObject(String filterObjectJson, ObjectMapper mapper) throws Exception {
		if (filterObjectJson == null )
			return null;

		FilterQueryDto filterQueryDto = mapper.readValue(filterObjectJson, FilterQueryDto.class);

		FilterDto incomingQuery= filterQueryDto.getFilterBy();

		return incomingQuery;
	}


}