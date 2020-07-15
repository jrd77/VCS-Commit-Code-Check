package com.atzuche.order.coreapi.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.ResponseCheckUtil;
import com.atzuche.order.commons.entity.dto.LianHeMaiTongOrderDTO;
import com.atzuche.order.commons.enums.DeliveryOrderTypeEnum;
import com.atzuche.order.commons.vo.LianHeMaiTongOrderVO;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.service.RenterOrderDeliveryService;
import com.atzuche.order.parentorder.service.OrderService;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.riskCheckService.api.RiskCheckServiceFeignService;
import com.autoyol.riskCheckService.api.VO.MemberAuditResultReqVO;
import com.autoyol.riskCheckService.api.VO.MemberAuditResultResVO;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class LianHeMaiTongService {

    @Autowired
    private OrderService orderService;
    @Autowired
    private RenterOrderDeliveryService renterOrderDeliveryService;
    @Autowired
    private RiskCheckServiceFeignService riskCheckServiceFeignService;



    public List<LianHeMaiTongOrderVO> getOrderListByMemberNo(String memNo) {
        try{
            List<LianHeMaiTongOrderVO> lianHeMaiTongOrderVO = getLianHeMaiTongOrderVO(memNo);
            return lianHeMaiTongOrderVO;
        }catch (Exception e){
            log.error("联合麦通查询异常-会员号查询为空memNo={}",memNo,e);
            throw e;
        }
    }
    public List<LianHeMaiTongOrderVO> getLianHeMaiTongOrderVO(String memNo){
        List<LianHeMaiTongOrderVO> list = new ArrayList<>();
        List<LianHeMaiTongOrderDTO> lianHeMaiTongOrderDTOS = orderService.getOrderByMemNo(memNo);
        lianHeMaiTongOrderDTOS.stream().forEach(x->{
            LianHeMaiTongOrderVO vo = new LianHeMaiTongOrderVO();
            boolean isGetCar = false;
            boolean isReturnCar = false;
            if(x != null){
                List<RenterOrderDeliveryEntity> renterOrderDeliveryList = renterOrderDeliveryService.selectByRenterOrderNo(x.getRenterOrderNo());
                isGetCar = isGetReturnCarService(renterOrderDeliveryList, DeliveryOrderTypeEnum.GET_CAR);
                isReturnCar = isGetReturnCarService(renterOrderDeliveryList, DeliveryOrderTypeEnum.RETURN_CAR);
            }
            //获取风控审核结果
            MemberAuditResultReqVO memberAuditResultReqVO = new MemberAuditResultReqVO();
            memberAuditResultReqVO.setMemNo(x.getRenterMemNo());
            memberAuditResultReqVO.setOrderNo(x.getOrderNo());
            try{
                MemberAuditResultResVO memberAuditResultResVO = getRenterMemberFromRemote(memberAuditResultReqVO);
                if(memberAuditResultResVO != null){
                    vo.setFirstAuditStatus(memberAuditResultResVO.getFirstAuditStatus());
                    vo.setSecondAuditStatus(memberAuditResultResVO.getSecondAuditStatus());
                    vo.setGpsAuditStatus(memberAuditResultResVO.getGpsRiskStatus());
                }
            }catch (Exception e){
                log.error("获取风控审核数据异常",e);
            }
            BeanUtils.copyProperties(x,vo);
            vo.setIsGetCar(isGetCar);
            vo.setIsReturnCar(isReturnCar);
            vo.setRentAmt(x.getRentAmt()==null?"0":String.valueOf(Math.abs(x.getRentAmt())));
            list.add(vo);
        });
        return list;
    }

    /*
     * @Author ZhangBin
     * @Date 2020/7/9 14:08
     * @Description: 判断是否使用取还车服务
     *
     **/
    public boolean isGetReturnCarService(List<RenterOrderDeliveryEntity> list, DeliveryOrderTypeEnum deliveryOrderTypeEnum){
        Optional<RenterOrderDeliveryEntity> first = Optional.ofNullable(list).orElseGet(ArrayList::new).stream()
                .filter(x -> deliveryOrderTypeEnum.getCode() == x.getType() && 1 == x.getIsNotifyRenyun())
                .findFirst();
        if(first.isPresent()){
            return true;
        }
        return false;
    }




    /*
     * @Author ZhangBin
     * @Date 2020/4/30 10:44
     * @Description: 订单号获取订单审核信息
     *
     **/
    public MemberAuditResultResVO getRenterMemberFromRemote(MemberAuditResultReqVO memberAuditResultReqVO){
        ResponseData<MemberAuditResultResVO> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "订单号获取订单审核信息");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"riskCheckServiceFeignService.auditResult");
            log.info("Feign 开始获取订单审核信息,memberAuditResultReqVO={}", JSON.toJSONString(memberAuditResultReqVO));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(memberAuditResultReqVO));
            responseObject =  riskCheckServiceFeignService.auditResult(memberAuditResultReqVO);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            return responseObject.getData();
        }catch (Exception e){
            log.error("Feign 获取订单审核信息异常,responseObject={},memberAuditResultReqVO={}", JSON.toJSONString(responseObject),JSON.toJSONString(memberAuditResultReqVO),e);
            Cat.logError("Feign 获取订单审核信息异常",e);
            throw e;
        }finally {
            t.complete();
        }
    }

}
