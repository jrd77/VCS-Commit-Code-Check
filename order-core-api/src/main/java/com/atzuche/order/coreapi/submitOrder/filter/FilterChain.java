/**
 * 
 */
package com.atzuche.order.coreapi.submitOrder.filter;

import com.atzuche.order.coreapi.dto.OrderContextDto;
import com.atzuche.order.coreapi.dto.SubmitReqDto;
import com.atzuche.order.coreapi.exception.SubmitOrderException;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * @Author ZhangBin
 * @Date 2019/12/12 15:29
 * @Description: 线程安全的过滤链
 * 
 **/
@Service
public class FilterChain {

	ThreadLocal<List<SubmitOrderFilter>> filters = new ThreadLocal<List<SubmitOrderFilter>>();

	public FilterChain() {
		super();
	}

	public FilterChain addFilterAll(List<SubmitOrderFilter> lst) {
		this.filters.set(lst);
		return this;
	}

	public void doFilter(SubmitReqDto submitReqDto, OrderContextDto orderContextDto, FilterChain chain, int index) throws SubmitOrderException {
		if (index == filters.get().size()) {
			return;
		}
		SubmitOrderFilter submitOrderFilter = filters.get().get(index);
		index++;
        submitOrderFilter.doFilter(submitReqDto, orderContextDto, chain, index);
	}

}
