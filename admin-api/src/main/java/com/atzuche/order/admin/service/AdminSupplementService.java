package com.atzuche.order.admin.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.entity.dto.OrderSupplementDetailDTO;
import com.atzuche.order.commons.exceptions.RemoteCallException;
import com.atzuche.order.commons.vo.res.rentcosts.OrderSupplementDetailEntity;
import com.atzuche.order.commons.vo.res.rentcosts.SupplementVO;
import com.atzuche.order.open.service.FeignSupplementService;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.platformcost.CommonUtils;
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
	public SupplementVO listSupplement(String orderNo) {
        ResponseData<SupplementVO> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "订单CoreAPI服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"AdminSupplementService.listSupplement");
            log.info("Feign 管理后台查询补付列表,param={}", JSON.toJSONString(orderNo));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(orderNo));
            responseObject = feignSupplementService.listSupplement(orderNo);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            SupplementVO supplementVO = responseObject.getData();
            if (supplementVO == null) {
            	return null;
            }
            List<OrderSupplementDetailEntity> list = supplementVO.getList();
            if (list != null && !list.isEmpty()) {
            	for (OrderSupplementDetailEntity sup:list) {
            		// 管理后台展示取反
            		sup.setAmt(sup.getAmt() != null ? -sup.getAmt():0);
            		if (sup.getCashType() != null && sup.getCashType() == 1) {
            			sup.setCashTypeTxt("补付费用");
            		} else if (sup.getCashType() != null && sup.getCashType() == 2) {
            			sup.setCashTypeTxt("订单欠款");
            		}
            		if (sup.getOpStatus() != null) {
            			if (sup.getOpStatus() == 0) {
            				sup.setOpStatusTxt("待提交");
            			} else if (sup.getOpStatus() == 1) {
            				sup.setOpStatusTxt("已生效 ");
            			} else if (sup.getOpStatus() == 2) {
            				sup.setOpStatusTxt("已失效 ");
            			} else if (sup.getOpStatus() == 3) {
            				sup.setOpStatusTxt("已撤回");
            			}
            		}
            		if (sup.getSupplementType() != null) {
            			if (sup.getSupplementType() == 1) {
            				sup.setSupplementTypeTxt("系统创建");
            			} else if (sup.getSupplementType() == 2) {
            				sup.setSupplementTypeTxt("手动创建");
            			}
            		}
            		if (sup.getOpType() != null) {
            			if (sup.getOpType() == 1) {
            				sup.setOpTypeTxt("修改订单");
            			} else if (sup.getOpType() == 2) {
            				sup.setOpTypeTxt("车管家录入");
            			} else if (sup.getOpType() == 3) {
            				sup.setOpTypeTxt("租车押金结算");
            			} else if (sup.getOpType() == 4) {
            				sup.setOpTypeTxt("违章押金结算");
            			} else if (sup.getOpType() == 5) {
            				sup.setOpTypeTxt("手动添加");
            			}
            		}
            		if (sup.getPayFlag() != null) {
            			if (sup.getPayFlag() == 0) {
            				sup.setPayFlagTxt("无需支付");
            			} else if (sup.getPayFlag() == 1) {
            				sup.setPayFlagTxt("未支付");
            			} else if (sup.getPayFlag() == 2) {
            				sup.setPayFlagTxt("已取消");
            			} else if (sup.getPayFlag() == 3) {
            				sup.setPayFlagTxt("已支付");
            			} else if (sup.getPayFlag() == 4) {
            				sup.setPayFlagTxt("支付中");
            			} else if (sup.getPayFlag() == 5) {
            				sup.setPayFlagTxt("支付失败");
            			} else if (sup.getPayFlag() == 10) {
            				sup.setPayFlagTxt("租车押金结算抵扣");
            			} else if (sup.getPayFlag() == 20) {
            				sup.setPayFlagTxt("违章押金结算抵扣");
            			}
            		}
            		if (sup.getCreateTime() != null) {
            			LocalDateTime createLocalDateTime = sup.getCreateTime().toInstant()
            			        .atZone(ZoneId.systemDefault() )
            			        .toLocalDateTime();
            			sup.setCreateTimeTxt(CommonUtils.formatTime(createLocalDateTime, CommonUtils.FORMAT_STR_DEFAULT));
            		}
            		sup.setPayTypeTxt("消费");
            	}
            }
            return supplementVO;
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
