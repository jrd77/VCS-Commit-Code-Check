/**
 * 
 */
package com.atzuche.order.coreapi.submitOrder.filter;

import com.atzuche.order.commons.entity.dto.OrderContextDTO;
import com.atzuche.order.coreapi.submitOrder.exception.SubmitOrderException;
import com.atzuche.order.request.NormalOrderReqVO;

public interface SubmitOrderFilter {

	void doFilter(NormalOrderReqVO submitReqDto, OrderContextDTO orderContextDto, FilterChain chain, int index) throws SubmitOrderException;

	boolean validator(NormalOrderReqVO submitReqDto, OrderContextDTO orderContextDto) throws SubmitOrderException;
}
