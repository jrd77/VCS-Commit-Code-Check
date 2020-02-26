/**
 * 
 */
package com.atzuche.order.coreapi.submitOrder.filter;

import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.filter.OrderFilter;
import com.atzuche.order.commons.filter.OrderFilterException;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单过滤链
 */
public class FilterChain implements OrderFilter{

	private List<OrderFilter> filterList = new ArrayList<>();

	public FilterChain() {
		super();
	}

	public FilterChain addFilterAll(List<OrderFilter> lst) {
		this.filterList.addAll(lst);
		return this;
	}

	@Override
	public void validate(OrderReqContext context) throws OrderFilterException {
		for(OrderFilter orderFilter: filterList){
			orderFilter.validate(context);
		}
	}
}
