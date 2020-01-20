package com.atzuche.order.admin.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.admin.common.AdminUserUtil;
import com.atzuche.order.admin.util.StringUtil;
import com.atzuche.order.admin.util.TimeUtil;
import com.atzuche.order.admin.vo.req.orderSubmit.AdminTransReqVO;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.ResponseCheckUtil;
import com.atzuche.order.commons.vo.req.AdminOrderReqVO;
import com.atzuche.order.commons.vo.res.OrderResVO;
import com.atzuche.order.open.service.FeignOrderAdminSubmitService;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
@Slf4j
public class OrderSubmitService {

    @Autowired
    private FeignOrderAdminSubmitService feignOrderAdminSubmitService;

    public ResponseData<OrderResVO> submit(AdminTransReqVO adminOrderReqVO, HttpServletRequest request) throws Exception {
        //1、组装参数
        AdminOrderReqVO adminOrderReqParam = this.transDto(adminOrderReqVO,request);

        ResponseData<OrderResVO> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "后台管理系统下单");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderAdminSubmitService.submitOrder");
            log.info("Feign 开始后台管理系统下单,adminOrderReqVO={}", adminOrderReqVO);
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(adminOrderReqVO));
            responseObject =  feignOrderAdminSubmitService.submitOrder(adminOrderReqParam);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 后台管理系统下单异常,responseObject={}", JSON.toJSONString(responseObject),e);
            Cat.logError("Feign 后台管理系统下单异常",e);
            throw e;
        }finally {
            t.complete();
        }

        //3、返回结果
        ResponseData responseData = new ResponseData();
        responseData.setData(responseObject.getData());
        responseData.setResCode(responseObject.getResCode());
        responseData.setResMsg(responseObject.getResMsg());
        return responseData;
    }

    private AdminOrderReqVO transDto(AdminTransReqVO reqVO, HttpServletRequest request){
        //2、组装新的下单参数
        AdminOrderReqVO param = new AdminOrderReqVO();

        BeanUtils.copyProperties(reqVO,param);

        //1、参数转化
        //1.2 rentTime和revertTime的转化
        String rentTime = reqVO.getRentTime();
        String revertTime = reqVO.getRevertTime();
        String newRentTime = TimeUtil.longStrToTimeStr(rentTime);
        String newRevertTime =  TimeUtil.longStrToTimeStr(revertTime);
        String abatement = reqVO.getAbatement()!=null?reqVO.getAbatement():"0";

        //1.4 ip和端口
        String srcIp = StringUtil.getReqIpAddr(request);
        int srcPort = request.getRemotePort();

        param.setUseSpecialPrice(reqVO.getUseSpecialPrice());
        param.setOperator(AdminUserUtil.getAdminUser().getAuthName());
        param.setSpecialConsole("0");
        param.setOfflineOrderStatus("0");

        param.setPlatformParentType("7");
        param.setCityName(reqVO.getRentCity());
        param.setOrderCategory("1");
        param.setSceneCode("EX007");
        param.setSource("1");

        param.setMemNo(reqVO.getMemNo());

        //不计免赔
        param.setAbatement(abatement);

        //券类
        param.setDisCouponIds(reqVO.getDisCouponIds());
        param.setDriverIds(reqVO.getDriverIds());
        param.setLimitReductionId(reqVO.getLimitReductionId());
        param.setFreeDoubleTypeId(reqVO.getFreeDoubleTypeId());
        param.setCarOwnerCouponNo(reqVO.getCarOwnerCouponNo());

        //布尔值的转化
        param.setSrvGetFlag(Integer.valueOf(reqVO.getSrvGetFlag()));
        param.setSrvReturnFlag(Integer.valueOf(reqVO.getSrvReturnFlag()));
        param.setUseAutoCoin(Integer.valueOf(reqVO.getUseAutoCoin()));
        param.setUseBal(Integer.valueOf(reqVO.getUseBal()));
        param.setUseAirportService(reqVO.getUseAirportService()!=null?Integer.valueOf(reqVO.getUseAirportService()):0);
        param.setIsLeaveCity(Integer.valueOf(reqVO.getIsLeaveCity()));

        //时间类的转化
        param.setRentTime(newRentTime);
        param.setRevertTime(newRevertTime);

        //经纬度的转化
        param.setSrvGetLon(StringUtil.convertLatOrLon(reqVO.getSrvGetLon()));
        param.setSrvGetLat(StringUtil.convertLatOrLon(reqVO.getSrvGetLat()));
        param.setSrvReturnLon(StringUtil.convertLatOrLon(reqVO.getSrvReturnLon()));
        param.setSrvReturnLat(StringUtil.convertLatOrLon(reqVO.getSrvReturnLat()));


        //其他
        param.setCarAddrIndex(reqVO.getCarAddrIndex());

        param.setMemNo(reqVO.getMemNo());
        param.setCarNo(reqVO.getCarNo());

        param.setCityCode(reqVO.getCityCode());


        param.setSrvGetAddr(reqVO.getSrvGetAddr());
        param.setSrvReturnAddr(reqVO.getSrvReturnAddr());
        param.setGetCarFreeCouponId(reqVO.getGetCarFreeCouponId());
        param.setFlightNo(reqVO.getFlightNo());
        param.setLimitRedStatus(reqVO.getLimitRedStatus());
        param.setRentCity(reqVO.getRentCity());
        param.setOilType(reqVO.getOilType());

        return param;
    }
}
