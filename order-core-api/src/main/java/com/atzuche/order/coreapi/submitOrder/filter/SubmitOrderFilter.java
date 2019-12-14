/**
 * 
 */
package com.atzuche.order.coreapi.submitOrder.filter;

import com.atzuche.order.commons.entity.dto.OrderContextDto;
import com.atzuche.order.coreapi.entity.request.SubmitOrderReq;
import com.atzuche.order.coreapi.submitOrder.exception.SubmitOrderException;

public interface SubmitOrderFilter {

	void doFilter(SubmitOrderReq submitReqDto, OrderContextDto orderContextDto, FilterChain chain, int index) throws SubmitOrderException;

	boolean validator(SubmitOrderReq submitReqDto, OrderContextDto orderContextDto) throws SubmitOrderException;
}
