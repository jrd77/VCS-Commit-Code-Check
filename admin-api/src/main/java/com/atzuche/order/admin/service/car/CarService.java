package com.atzuche.order.admin.service.car;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.admin.vo.resp.car.CarBusinessResVO;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.DateUtils;
import com.atzuche.order.commons.JsonUtil;
import com.atzuche.order.commons.ResponseCheckUtil;
import com.atzuche.order.commons.entity.orderDetailDto.OrderDetailReqDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OrderDetailRespDTO;
import com.atzuche.order.commons.exceptions.RemoteCallException;
import com.autoyol.car.api.feign.api.CarDetailQueryFeignApi;
import com.autoyol.car.api.model.vo.CarBaseVO;
import com.autoyol.car.api.model.vo.ResponseObject;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@Service
public class CarService {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private static final String ERROR_CODE="999999";
    private static final String ERROR_TXT="系统异常";
    @Autowired
    private CarDetailQueryFeignApi carDetailQueryFeignApi;

    public CarBusinessResVO getCarBusiness(Integer carNo){
        CarBusinessResVO resVO = new CarBusinessResVO();
        ResponseObject<CarBaseVO> responseObject = carDetailQueryFeignApi.getCarDetailByCarNo(carNo);
        checkResponse(responseObject);
        CarBaseVO carBaseVO = responseObject.getData();
        resVO.setGetCarAddr(carBaseVO.getGetCarAddr());
        resVO.setLicenseOwer(carBaseVO.getLicenseOwer());
        resVO.setLicenseModel(carBaseVO.getLicenseModel());
        Date licenseExpire = carBaseVO.getLicenseExpireDate();
        if(licenseExpire!=null){
            resVO.setLicenseExpire(DateUtils.formate(licenseExpire, DateUtils.fmt_yyyyMMdd));
        }
        Date insuranceExpire = carBaseVO.getInsuranceExpire();
        if(insuranceExpire!=null){
            resVO.setInsuranceExpireDateStr(DateUtils.formate(insuranceExpire,DateUtils.fmt_yyyyMMdd));
        }
        resVO.setGpsNo(carBaseVO.getGpsNo());
        resVO.setSimNo(carBaseVO.getSimNo());
        resVO.setMemo(carBaseVO.getMemo());
        //carSelectMap car_select_upgrade
        resVO.setCarRemark(carBaseVO.getCarDesc());
        resVO.setGetRevertExplain(carBaseVO.getGetRevertExplain());
        resVO.setDayMileage(carBaseVO.getDayMileage());
        //
        return resVO;
    }
    
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
            logger.info("Feign 根据车牌获取车辆编号,plateNum={}", plateNum);
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
        	logger.error("Feign 管理后台根据车牌获取车辆编号,responseObject={},plateNum={}",JSON.toJSONString(responseObject),plateNum,e);
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
