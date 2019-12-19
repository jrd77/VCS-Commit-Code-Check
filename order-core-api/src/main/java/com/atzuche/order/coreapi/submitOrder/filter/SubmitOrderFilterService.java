/**
 * 
 */
package com.atzuche.order.coreapi.submitOrder.filter;

import com.atzuche.order.commons.entity.dto.OrderContextDto;
import com.atzuche.order.coreapi.entity.request.NormalOrderReqVO;
import com.atzuche.order.coreapi.submitOrder.exception.SubmitOrderException;
import com.atzuche.order.coreapi.submitOrder.rules.CancleOrderCountFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/*
 * @Author ZhangBin
 * @Date 2019/12/12 15:30
 * @Description: 添加校验规则
 *
 **/
@Slf4j
@Service
public class SubmitOrderFilterService {
	@Autowired
	private FilterChain filterChain;
    @Autowired
	private CancleOrderCountFilter cancleOrderCountFilter;


    //添加校验规则
	public void addCheckRules(){
		List<SubmitOrderFilter> list = new ArrayList<>();
        list.add(cancleOrderCountFilter);
		filterChain.addFilterAll(list);
	}
	
	public void checkRules(NormalOrderReqVO submitReqDto, OrderContextDto orderContextDto) throws SubmitOrderException {
        //添加校验规则
        this.addCheckRules();
		
		int index = 0;
		//过滤规则
		filterChain.doFilter(submitReqDto,  orderContextDto, filterChain,index);
	}

}
