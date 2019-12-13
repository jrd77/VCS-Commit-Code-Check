/**
 * 
 */
package com.atzuche.order.coreapi.submitOrder.filter;

import com.atzuche.order.coreapi.entity.dto.OrderContextDto;
import com.atzuche.order.coreapi.entity.request.SubmitReq;
import com.atzuche.order.coreapi.submitOrder.exception.SubmitOrderException;

public interface SubmitOrderFilter {

	void doFilter(SubmitReq submitReqDto, OrderContextDto orderContextDto, FilterChain chain, int index) throws SubmitOrderException;

	boolean validator(SubmitReq submitReqDto, OrderContextDto orderContextDto) throws SubmitOrderException;
}
