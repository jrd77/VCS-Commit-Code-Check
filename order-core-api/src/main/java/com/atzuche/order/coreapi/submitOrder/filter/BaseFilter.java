/**
 * 
 */
package com.atzuche.order.coreapi.submitOrder.filter;

import com.atzuche.order.commons.entity.dto.OrderContextDTO;
import com.atzuche.order.coreapi.submitOrder.exception.SubmitOrderException;
import com.atzuche.order.request.NormalOrderReqVO;

/*
 * @Author ZhangBin
 * @Date 2019/12/12 15:30
 * @Description: 校验规则
 * 
 **/
public class BaseFilter implements SubmitOrderFilter{

    @Override
	public void doFilter(NormalOrderReqVO submitReqDto, OrderContextDTO orderContextDto, FilterChain chain, int index) throws SubmitOrderException {
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
	public boolean validator(NormalOrderReqVO submitReqDto, OrderContextDTO orderContextDto) throws SubmitOrderException {
		// TODO Auto-generated method stub
		return false;
	}
}
