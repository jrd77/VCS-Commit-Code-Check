package com.atzuche.order.coreapi.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.ResponseCheckUtil;
import com.atzuche.order.commons.entity.dto.LianHeMaiTongOrderDTO;
import com.atzuche.order.commons.enums.CategoryEnum;
import com.atzuche.order.commons.enums.DeliveryOrderTypeEnum;
import com.atzuche.order.commons.enums.ErrorCode;
import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.atzuche.order.commons.exceptions.LianHeMaiTongMemberException;
import com.atzuche.order.commons.vo.LianHeMaiTongMemberReqVO;
import com.atzuche.order.commons.vo.LianHeMaiTongMemberVO;
import com.atzuche.order.commons.vo.LianHeMaiTongOrderVO;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.service.RenterOrderDeliveryService;
import com.atzuche.order.owner.commodity.entity.OwnerGoodsEntity;
import com.atzuche.order.owner.commodity.service.OwnerGoodsService;
import com.atzuche.order.owner.mem.entity.OwnerMemberEntity;
import com.atzuche.order.owner.mem.service.OwnerMemberService;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.parentorder.service.OrderSourceStatService;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.rentercommodity.service.RenterGoodsService;
import com.atzuche.order.rentermem.entity.RenterMemberEntity;
import com.atzuche.order.rentermem.service.RenterMemberService;
import com.autoyol.commons.utils.Page;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.riskCheckService.api.RiskCheckServiceFeignService;
import com.autoyol.riskCheckService.api.VO.MemberAuditResultReqVO;
import com.autoyol.riskCheckService.api.VO.MemberAuditResultResVO;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
    private OwnerGoodsService ownerGoodsService;
    @Autowired
    private OwnerMemberService ownerMemberService;
    @Autowired
    private RenterMemberService renterMemberService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private RenterOrderDeliveryService renterOrderDeliveryService;
    @Autowired
    private RiskCheckServiceFeignService riskCheckServiceFeignService;

    public LianHeMaiTongMemberVO getMemberInfo(LianHeMaiTongMemberReqVO lianHeMaiTongMemberReqVO) {
        String memNo = lianHeMaiTongMemberReqVO.getMemNo();
        String phone = lianHeMaiTongMemberReqVO.getPhone();
        String platNum = lianHeMaiTongMemberReqVO.getPlatNum();
        LianHeMaiTongMemberVO lhmtVO = new LianHeMaiTongMemberVO();

        if(memNo!=null && memNo.trim().length()>0){
           /* OwnerMemberEntity ownerMemberEntity = ownerMemberService.getOwnerNoByMemberNo(memNo);
            RenterMemberEntity renterMemberEntity = null;
            if(ownerMemberEntity != null){
                lhmtVO.setMemNo(memNo);
                lhmtVO.setName(ownerMemberEntity.getRealName());
                lhmtVO.setPhone(ownerMemberEntity.getPhone());
                memberTag =2;
            }else*/
            RenterMemberEntity renterMemberEntity = renterMemberService.getRenterMemberByMemberNo(memNo);
            if(renterMemberEntity !=null){
                lhmtVO.setMemNo(renterMemberEntity.getMemNo());
                lhmtVO.setName(renterMemberEntity.getRealName());
                lhmtVO.setPhone(renterMemberEntity.getPhone());
            }else{
                LianHeMaiTongMemberException e = new LianHeMaiTongMemberException(ErrorCode.LHMT_QUERY_ERROR.getCode(),"会员号信息查询为空");
                log.error("联合麦通查询异常-会员号查询为空memNo={}",memNo,e);
                throw e;
            }
        }else if(phone!=null && phone.trim().length()>0){
            /*OwnerMemberEntity ownerMemberEntity = ownerMemberService.queryOwnerMemberByPhone(phone);

            if(ownerMemberEntity != null){
                lhmtVO.setMemNo(memNo);
                lhmtVO.setName(ownerMemberEntity.getRealName());
                lhmtVO.setPhone(ownerMemberEntity.getPhone());
                memberTag =2;
            }else*/
            RenterMemberEntity renterMemberEntity = renterMemberService.getRenterMemberByMemberNo(memNo);
            if(renterMemberEntity != null){
                lhmtVO.setMemNo(renterMemberEntity.getMemNo());
                lhmtVO.setName(renterMemberEntity.getRealName());
                lhmtVO.setPhone(renterMemberEntity.getPhone());
            }else{
                LianHeMaiTongMemberException e = new LianHeMaiTongMemberException(ErrorCode.LHMT_QUERY_ERROR.getCode(),"手机号信息查询为空");
                log.error("联合麦通查询异常-手机号查询为空phone={}",phone,e);
                throw e;
            }
        }else if(platNum!=null && platNum.trim().length()>0){
            OwnerGoodsEntity ownerGoodsEntity = ownerGoodsService.getOwnerGoodsByPlatNum(platNum);
            if(ownerGoodsEntity != null){
                String orderNo = ownerGoodsEntity.getOrderNo();
                String ownerOrderNo = ownerGoodsEntity.getOwnerOrderNo();
                OwnerMemberEntity ownerMemberEntity = ownerMemberService.queryOwnerInfoByOrderNoAndOwnerNo(orderNo, ownerOrderNo);
                if(ownerMemberEntity != null){
                    lhmtVO.setMemNo(memNo);
                    lhmtVO.setName(ownerMemberEntity.getRealName());
                    lhmtVO.setPhone(ownerMemberEntity.getPhone());
                }else{
                    LianHeMaiTongMemberException e = new LianHeMaiTongMemberException(ErrorCode.LHMT_QUERY_ERROR.getCode(),"车牌号找不到对应的用户信息");
                    log.error("联合麦通查询异常-车牌号获取车主会员信息为空platNum={}",platNum,e);
                    throw e;
                }
            }else{
                LianHeMaiTongMemberException e = new LianHeMaiTongMemberException(ErrorCode.LHMT_QUERY_ERROR.getCode(),"获取不到车辆信息");
                log.error("联合麦通查询异常-车牌号获取车获取不到车辆信息platNum={}",platNum,e);
                throw e;
            }
        }else{
            LianHeMaiTongMemberException e = new LianHeMaiTongMemberException(ErrorCode.LHMT_QUERY_ERROR.getCode(),"至少输入一个参数条件");
            log.error("联合麦通查询异常",e);
            throw e;
        }
        Page<LianHeMaiTongOrderVO> lianHeMaiTongOrderVO = getLianHeMaiTongOrderVO(lianHeMaiTongMemberReqVO);
        lhmtVO.setPage(lianHeMaiTongOrderVO);
        return lhmtVO;
    }

    public Page<LianHeMaiTongOrderVO> getLianHeMaiTongOrderVO(LianHeMaiTongMemberReqVO lianHeMaiTongMemberReqVO){
        String memNo = lianHeMaiTongMemberReqVO.getMemNo();
        String platNum = lianHeMaiTongMemberReqVO.getPlatNum();
        Integer pageNum = lianHeMaiTongMemberReqVO.getPageNum();
        Integer pageSize = lianHeMaiTongMemberReqVO.getPageSize();
        Page pageRes = new Page();
        List<LianHeMaiTongOrderVO> list = new ArrayList<>();
        PageHelper.startPage(pageNum,pageSize);
        List<LianHeMaiTongOrderDTO> lianHeMaiTongOrderDTOS = orderService.getByMemNoAndPlatNum(memNo,platNum);
        PageInfo<LianHeMaiTongOrderDTO> pageInfo = new PageInfo<>(lianHeMaiTongOrderDTOS);
        List<LianHeMaiTongOrderDTO> pageList = pageInfo.getList();
        pageList.stream().forEach(x->{
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
            vo.setIsGetReturnCar(isGetCar);
            vo.setIsGetReturnCar(isReturnCar);
            list.add(vo);
        });
        pageRes.setList(pageList);
        pageRes.setTotal(pageInfo.getTotal());
        return pageRes;
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
