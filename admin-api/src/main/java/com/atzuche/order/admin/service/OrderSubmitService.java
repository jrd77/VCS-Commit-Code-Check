package com.atzuche.order.admin.service;

import com.atzuche.order.admin.util.StringUtil;
import com.atzuche.order.admin.util.TimeUtil;
import com.atzuche.order.admin.vo.req.orderSubmit.AdminTransReqVO;
import com.atzuche.order.commons.vo.req.AdminOrderReqVO;
import com.atzuche.order.commons.vo.res.OrderResVO;
import com.atzuche.order.open.service.FeignOrderAdminSubmitService;
import com.autoyol.commons.web.ResponseData;
import lombok.extern.slf4j.Slf4j;
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

        //2、http发送
        ResponseData<OrderResVO> orderDetail = feignOrderAdminSubmitService.getOrderDetail(adminOrderReqParam);
        //HttpResult orderDetail = HttpUtil.doPostNotGzip("http://10.0.3.235:1412/order/admin/req", JSON.toJSONString(adminOrderReqParam));
        //HttpResult orderDetail = HttpUtil.doPostNotGzip("http://localhost:7777/order/admin/req", JSON.toJSONString(adminOrderReqParam));
        //3、返回结果
        ResponseData responseData = new ResponseData();
        responseData.setData(orderDetail.getData());
        responseData.setResCode(orderDetail.getResCode());
        responseData.setResMsg(orderDetail.getResMsg());
        return responseData;
    }

    private AdminOrderReqVO transDto(AdminTransReqVO reqVO, HttpServletRequest request){
        String sceneCode = reqVO.getSceneCode();
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

        //2、组装新的下单参数
        AdminOrderReqVO param = new AdminOrderReqVO();

        param.setUseSpecialPrice(reqVO.getUseSpecialPrice());
        param.setOperator(reqVO.getOperator());
        param.setSpecialConsole(reqVO.getSpecialConsole());
        param.setOfflineOrderStatus(reqVO.getOfflineOrderStatus());

        param.setOrderCategory("1");
        param.setBusinessParentType("5");
        param.setBusinessChildType("");
        param.setPlatformParentType("7");
        param.setPlatformChildType("");
        param.setModuleName("order");
        param.setFunctionName("order/admin/req");
        param.setReqSource(1);
        param.setReqVersion("10");
        param.setReqOs("ReqOs");
        param.setActivityId("ActivityId");
        param.setRentReason("租个车还填啥原因啊！");
        param.setCityName("上海");

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
        param.setPublicLongitude(StringUtil.convertLatOrLon(reqVO.getPublicLongitude()));
        param.setPublicLatitude(StringUtil.convertLatOrLon(reqVO.getPublicLatitude()));
        param.setSrvGetLon(StringUtil.convertLatOrLon(reqVO.getSrvGetLon()));
        param.setSrvGetLat(StringUtil.convertLatOrLon(reqVO.getSrvGetLat()));
        param.setSrvReturnLon(StringUtil.convertLatOrLon(reqVO.getSrvReturnLon()));
        param.setSrvReturnLat(StringUtil.convertLatOrLon(reqVO.getSrvReturnLat()));

        //ip、端口
        param.setSrcIp(srcIp);
        param.setSrcPort(srcPort);

        //设备版本
        param.setOS(reqVO.getOS());
        param.setOsVersion(reqVO.getOsVersion());
        param.setAppVersion(reqVO.getAppVersion());
        param.setIMEI(reqVO.getIMEI());
        param.setOAID(reqVO.getOAID());
        param.setDeviceName(reqVO.getDeviceName());
        param.setMac(reqVO.getMac());

        //其他
        param.setCarAddrIndex(reqVO.getCarAddrIndex());
        param.setPublicCityCode(reqVO.getPublicCityCode());
        param.setAppName(reqVO.getAppName());
        param.setPublicToken(reqVO.getPublicToken());
        param.setAppChannelId(reqVO.getAppChannelId());
        param.setMemNo(reqVO.getMemNo()==null?reqVO.getMem_no():null);
        param.setCarNo(reqVO.getCarNo());
        param.setSchema(reqVO.getSchema());
        param.setCityCode(reqVO.getCityCode());
        param.setSceneCode(sceneCode);
        param.setSource("3");
        param.setSubSource(reqVO.getSubSource());
        param.setSrvGetAddr(reqVO.getSrvGetAddr());
        param.setSrvReturnAddr(reqVO.getSrvReturnAddr());
        param.setGetCarFreeCouponId(reqVO.getGetCarFreeCouponId());
        param.setFlightNo(reqVO.getFlightNo());
        param.setLimitRedStatus(reqVO.getLimitRedStatus());
        param.setRentCity(reqVO.getRentCity());
        param.setQueryId(reqVO.getQueryId());
        param.setOilType(reqVO.getOilType());
        param.setConPhone(reqVO.getConPhone());
        param.setUtmSource(reqVO.getUtmSource());
        param.setUtmMedium(reqVO.getUtmMedium());
        param.setUtmTerm(reqVO.getUtmTerm());
        param.setUtmCampaign(reqVO.getUtmCampaign());
        return param;
    }
}
