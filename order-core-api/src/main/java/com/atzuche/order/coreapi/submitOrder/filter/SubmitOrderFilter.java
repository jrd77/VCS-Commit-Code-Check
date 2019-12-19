/**
 * 
 */
package com.atzuche.order.coreapi.submitOrder.filter;

import com.atzuche.order.commons.entity.dto.OrderContextDto;
import com.atzuche.order.coreapi.entity.request.NormalOrderReqVO;
import com.atzuche.order.coreapi.submitOrder.exception.SubmitOrderException;

public interface SubmitOrderFilter {

	void doFilter(NormalOrderReqVO submitReqDto, OrderContextDto orderContextDto, FilterChain chain, int index) throws SubmitOrderException;

	boolean validator(NormalOrderReqVO submitReqDto, OrderContextDto orderContextDto) throws SubmitOrderException;
}
