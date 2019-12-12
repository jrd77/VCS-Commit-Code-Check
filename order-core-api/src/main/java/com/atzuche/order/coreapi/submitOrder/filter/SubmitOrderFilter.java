/**
 * 
 */
package com.atzuche.order.coreapi.submitOrder.filter;

import com.atzuche.order.coreapi.dto.OrderContextDto;
import com.atzuche.order.coreapi.dto.SubmitReqDto;
import com.atzuche.order.coreapi.exception.SubmitOrderException;

public interface SubmitOrderFilter {

	void doFilter(SubmitReqDto submitReqDto, OrderContextDto orderContextDto, FilterChain chain, int index) throws SubmitOrderException;

	boolean validator(SubmitReqDto submitReqDto, OrderContextDto orderContextDto) throws SubmitOrderException;
}
