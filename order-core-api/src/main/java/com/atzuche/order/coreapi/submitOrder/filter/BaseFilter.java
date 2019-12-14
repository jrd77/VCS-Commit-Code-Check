/**
 * 
 */
package com.atzuche.order.coreapi.submitOrder.filter;

import com.atzuche.order.coreapi.entity.dto.OrderContextDto;
import com.atzuche.order.coreapi.entity.request.SubmitOrderReq;
import com.atzuche.order.coreapi.submitOrder.exception.SubmitOrderException;

/*
 * @Author ZhangBin
 * @Date 2019/12/12 15:30
 * @Description: 校验规则
 * 
 **/
public class BaseFilter implements SubmitOrderFilter{

    @Override
	public void doFilter(SubmitOrderReq submitReqDto, OrderContextDto orderContextDto, FilterChain chain, int index) throws SubmitOrderException {
		boolean flag = validator( submitReqDto,  orderContextDto);
        if(flag){
            //继续下一个验证
            chain.doFilter( submitReqDto,  orderContextDto, chain,index);
        }else{
            //验证失败
            return;
        }
	}

	@Override
	public boolean validator(SubmitOrderReq submitReqDto, OrderContextDto orderContextDto) throws SubmitOrderException {
		// TODO Auto-generated method stub
		return false;
	}
}
