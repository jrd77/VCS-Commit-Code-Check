package com.atzuche.order.admin.service.car;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.exceptions.RemoteCallException;
import com.autoyol.car.api.feign.api.CarDetailQueryFeignApi;
import com.autoyol.car.api.model.vo.ResponseObject;
import com.autoyol.commons.web.ErrorCode;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CarService {

    @Autowired
    private CarDetailQueryFeignApi carDetailQueryFeignApi;


    /**
     * 根据车牌获取车辆编号
     * @param plateNum
     * @return String
     */
    public String getCarNoByPlateNum(String plateNum){
        ResponseObject<Integer> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "车辆服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderDetailService.getOrderDetail");
            log.info("Feign 根据车牌获取车辆编号,plateNum={}", plateNum);
            Cat.logEvent(CatConstants.FEIGN_PARAM,plateNum);
            responseObject = carDetailQueryFeignApi.getCarNoByPlateNum(plateNum);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            String carNo = responseObject.getData() == null ? null:String.valueOf(responseObject.getData());
            if (StringUtils.isBlank(carNo)) {
            	throw new RemoteCallException(com.atzuche.order.commons.enums.ErrorCode.REMOTE_CALL_FAIL.getCode(), "根据车牌获取车辆注册号为空");
            }
            return carNo;
        }catch (Exception e){
        	log.error("Feign 管理后台根据车牌获取车辆编号,responseObject={},plateNum={}",JSON.toJSONString(responseObject),plateNum,e);
            Cat.logError("Feign 管理后台根据车牌获取车辆编号",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }

    public static void  checkResponse(ResponseObject responseObject){
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
