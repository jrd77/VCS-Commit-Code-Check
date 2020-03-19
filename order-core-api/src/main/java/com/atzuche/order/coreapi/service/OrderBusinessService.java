package com.atzuche.order.coreapi.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.entity.dto.OwnerMemberDTO;
import com.atzuche.order.commons.entity.dto.OwnerPreIncomRespDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.commons.entity.dto.ReturnCarIncomeResultDTO;
import com.atzuche.order.commons.enums.CloseEnum;
import com.atzuche.order.commons.enums.NoticeSourceCodeEnum;
import com.atzuche.order.commons.exceptions.NoticeSourceNotFoundException;
import com.atzuche.order.commons.exceptions.OrderNotFoundException;
import com.atzuche.order.commons.vo.req.OwnerUpdateSeeVO;
import com.atzuche.order.commons.vo.req.RenterAndOwnerSeeOrderVO;
import com.atzuche.order.owner.mem.service.OwnerMemberService;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.ownercost.service.OwnerOrderService;
import com.atzuche.order.parentorder.entity.OrderNoticeEntity;
import com.atzuche.order.parentorder.service.OrderNoticeService;
import com.atzuche.order.rentermem.service.RenterMemberService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.atzuche.order.settle.service.OrderSettleService;
import com.atzuche.order.settle.vo.req.OwnerCosts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class OrderBusinessService {
    @Autowired
    private OrderNoticeService orderNoticeService;
    @Autowired
    private OwnerOrderService ownerOrderService;
    @Autowired
    private RenterOrderService renterOrderService;
    @Autowired
    private OwnerMemberService ownerMemberService;
    @Autowired
    private RenterMemberService renterMemberService;
    @Autowired
    private OrderSettleService orderSettleService;

    public void renterAndOwnerSeeOrder(RenterAndOwnerSeeOrderVO renterAndOwnerSeeOrderVO) {
        String orderNo = renterAndOwnerSeeOrderVO.getOrderNo();
        Integer sourceType = renterAndOwnerSeeOrderVO.getSourceType();
        String ownerMemeNo = renterAndOwnerSeeOrderVO.getOwnerMemeNo();
        NoticeSourceCodeEnum enumByCode = NoticeSourceCodeEnum.getEnumByCode(sourceType);
        if(enumByCode == null){
            log.error("找不到对应的来源类型sourceType={}",sourceType);
            throw new NoticeSourceNotFoundException();
        }
        List<OrderNoticeEntity> list = orderNoticeService.queryByOrderNo(orderNo);
        OrderNoticeEntity orderNoticeEntity = filterBysourceCode(list, enumByCode);
        if(orderNoticeEntity != null){
            return;
        }
        OwnerOrderEntity owner = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(orderNo);
        RenterOrderEntity renter = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
        OrderNoticeEntity insertEntity = new OrderNoticeEntity();
        insertEntity.setCloseFlag(CloseEnum.IS_CLOSE.getCode());
        insertEntity.setOrderNo(orderNo);
        insertEntity.setOwnerOrderNo(owner!=null?owner.getOwnerOrderNo():null);
        insertEntity.setRenterOrderNo(renter!=null?renter.getRenterOrderNo():null);
        insertEntity.setOwnerMem(ownerMemeNo!=null?ownerMemeNo:(owner!=null?owner.getMemNo():null));
        insertEntity.setSourceCode(enumByCode.getCode());
        int result = orderNoticeService.insert(insertEntity);
        log.info("标记订单是否查看result={},insertEntity={}",result, JSON.toJSONString(insertEntity));
    }



    private OrderNoticeEntity filterBysourceCode(List<OrderNoticeEntity> list, NoticeSourceCodeEnum noticeSourceCodeEnum){
        if(noticeSourceCodeEnum == null){
            return null;
        }
        Optional<OrderNoticeEntity> first = Optional.ofNullable(list).orElseGet(ArrayList::new).stream()
                .filter(x -> noticeSourceCodeEnum.getCode() == x.getSourceCode())
                .findFirst();
        if(first!=null && first.isPresent()){
            return first.get();
        }
        return null;
    }

    public void ownerUpdateSee(OwnerUpdateSeeVO ownerUpdateSeeVO) {
        ownerOrderService.updateByMemeNo(ownerUpdateSeeVO.getOwnerMemNo());
    }

    public OwnerMemberDTO queryOwnerMemDetail(String orderNo) {
        OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(orderNo);
        if(ownerOrderEntity == null){
            throw new OrderNotFoundException(orderNo);
        }
        OwnerMemberDTO ownerMemberDTO = ownerMemberService.selectownerMemberByOwnerOrderNo(ownerOrderEntity.getOwnerOrderNo(), false);
        return ownerMemberDTO;
    }

    public RenterMemberDTO queryRenterMemDetail(String orderNo) {
        RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
        if(renterOrderEntity == null){
            throw new OrderNotFoundException(orderNo);
        }
        RenterMemberDTO renterMemberDTO = renterMemberService.selectrenterMemberByRenterOrderNo(renterOrderEntity.getRenterOrderNo(), false);
        return renterMemberDTO;
    }

    public OwnerPreIncomRespDTO ownerPreIncom(String orderNo) {
        OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(orderNo);
        if(ownerOrderEntity == null){
            log.error("找不到有效的车主子订单 orderNo={}",orderNo);
            throw new OrderNotFoundException(orderNo);
        }
        OwnerCosts ownerCosts = orderSettleService.preOwnerSettleOrder(orderNo, ownerOrderEntity.getOwnerOrderNo());
        OwnerPreIncomRespDTO ownerPreIncomRespDTO = new OwnerPreIncomRespDTO();
        ownerPreIncomRespDTO.setOwnerCostAmtFinal(ownerCosts.getOwnerCostAmtFinal());
        return ownerPreIncomRespDTO;
    }

    public ReturnCarIncomeResultDTO queryOwnerIncome(String orderNo) {
        OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(orderNo);
        if(ownerOrderEntity == null){
            log.error("找不到有效的车主子订单 orderNo={}",orderNo);
            throw new OrderNotFoundException(orderNo);
        }
        ReturnCarIncomeResultDTO returnCarIncomeResultDTO = new ReturnCarIncomeResultDTO();
        OwnerCosts ownerCosts = orderSettleService.preOwnerSettleOrder(orderNo, ownerOrderEntity.getOwnerOrderNo());
        List<ReturnCarIncomeDTO> returnCarIncomeDTOS = new ArrayList<>();
        ReturnCarIncomeDTO returnCarIncomeDTO = new ReturnCarIncomeDTO();
        returnCarIncomeDTO.setSettleFlag("1");
        returnCarIncomeDTO.setSettleTitle("按照订单结束时间结算");
        returnCarIncomeDTO.setExpectIncome("预计收益：" + ownerCosts.getOwnerCostAmtFinal() + "元");
        returnCarIncomeDTOS.add(returnCarIncomeDTO);
        returnCarIncomeResultDTO.setNoticeText("为了避免纠纷，请主动与租客友好协商，有利于再次成单哦~");
        returnCarIncomeResultDTO.setReturnCarIncomeDTOList(returnCarIncomeDTOS);
        log.info("按照时间计算车主预计收益returnCarIncomeResultDTO={}",JSON.toJSONString(returnCarIncomeResultDTO));
        return returnCarIncomeResultDTO;
    }
}
