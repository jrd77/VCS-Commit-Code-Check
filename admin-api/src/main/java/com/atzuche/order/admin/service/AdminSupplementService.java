package com.atzuche.order.admin.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.entity.dto.OrderSupplementDetailDTO;
import com.atzuche.order.commons.exceptions.RemoteCallException;
import com.atzuche.order.commons.vo.res.rentcosts.OrderSupplementDetailEntity;
import com.atzuche.order.open.service.FeignSupplementService;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AdminSupplementService {

	@Autowired
	private FeignSupplementService feignSupplementService;
	
	/**
	 * 获取管理后台补付列表
	 * @param orderNo
	 * @return List<OrderSupplementDetailEntity>
	 */
	public List<OrderSupplementDetailEntity> listSupplement(String orderNo) {
        ResponseData<List<OrderSupplementDetailEntity>> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "订单CoreAPI服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"AdminSupplementService.listSupplement");
            log.info("Feign 管理后台查询补付列表,param={}", JSON.toJSONString(orderNo));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(orderNo));
            responseObject = feignSupplementService.listSupplement(orderNo);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            List<OrderSupplementDetailEntity> list = responseObject.getData();
            if (list != null && !list.isEmpty()) {
            	for (OrderSupplementDetailEntity sup:list) {
            		// 管理后台展示取反
            		sup.setAmt(sup.getAmt() != null ? -sup.getAmt():0);
            	}
            }
            return list;
        }catch (Exception e){
            log.error("Feign 管理后台查询补付列表,responseObject={},req={}",JSON.toJSONString(responseObject),JSON.toJSONString(orderNo),e);
            Cat.logError("Feign 管理后台查询补付列表",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }
	
	
	/**
	 * 管理后台新增补付
	 * @param req
	 */
	public void addSupplement(OrderSupplementDetailDTO req) {
        ResponseData<?> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "订单CoreAPI服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"AdminSupplementService.addSupplement");
            log.info("Feign 管理后台新增补付,param={}", JSON.toJSONString(req));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(req));
            req.setOpStatus(1);
            req.setPayFlag(1);
            // 入库取反
            req.setAmt(req.getAmt() != null ? -req.getAmt():0);
            responseObject = feignSupplementService.addSupplement(req);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 管理后台新增补付,responseObject={},modifyOrderReq={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
            Cat.logError("Feign 管理后台新增补付",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }
	
	
	/**
	 * 管理后台删除补付
	 * @param id
	 */
	public void delSupplement(Integer id) {
        ResponseData<?> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "订单CoreAPI服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"AdminSupplementService.delSupplement");
            log.info("Feign 管理后台删除补付,param={}", id);
            Cat.logEvent(CatConstants.FEIGN_PARAM,id+"");
            responseObject = feignSupplementService.delSupplement(id);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 管理后台删除补付,responseObject={},modifyOrderReq={}",JSON.toJSONString(responseObject),id+"",e);
            Cat.logError("Feign 管理后台删除补付",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }
	
	
	private void checkResponse(ResponseData responseObject){
        if(responseObject==null||!ErrorCode.SUCCESS.getCode().equalsIgnoreCase(responseObject.getResCode())){
            RemoteCallException remoteCallException = null;
            if(responseObject!=null){
                remoteCallException = new RemoteCallException(responseObject.getResCode(),responseObject.getResMsg(),responseObject.getData());
            }else{
                remoteCallException = new RemoteCallException(com.atzuche.order.commons.enums.ErrorCode.REMOTE_CALL_FAIL.getCode(),
                        com.atzuche.order.commons.enums.ErrorCode.REMOTE_CALL_FAIL.getText());
            }
            throw remoteCallException;
        }
    }
}
