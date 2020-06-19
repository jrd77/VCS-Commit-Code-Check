package com.atzuche.order.coreapi.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeExamineEntity;
import com.atzuche.order.accountownerincome.entity.AddIncomeExamine;
import com.atzuche.order.accountownerincome.mapper.AddIncomeExamineMapper;
import com.atzuche.order.accountownerincome.service.notservice.AccountOwnerIncomeExamineNoTService;
import com.atzuche.order.accountownerincome.utils.AccountOwnerIncomeExamineUtil;
import com.atzuche.order.commons.entity.dto.*;
import com.atzuche.order.commons.entity.orderDetailDto.OwnerOrderSubsidyDetailDTO;
import com.atzuche.order.commons.entity.orderDetailDto.RenterDepositDetailDTO;
import com.atzuche.order.commons.enums.CloseEnum;
import com.atzuche.order.commons.enums.ExamineStatusEnum;
import com.atzuche.order.commons.enums.NoticeSourceCodeEnum;
import com.atzuche.order.commons.enums.account.SettleStatusEnum;
import com.atzuche.order.commons.enums.account.income.AccountOwnerIncomeExamineStatus;
import com.atzuche.order.commons.enums.account.income.AccountOwnerIncomeExamineType;
import com.atzuche.order.commons.exceptions.NoticeSourceNotFoundException;
import com.atzuche.order.commons.exceptions.OrderNotFoundException;
import com.atzuche.order.commons.exceptions.OrderStatusNotFoundException;
import com.atzuche.order.commons.exceptions.OwnerIncomeExamineNotFoundException;
import com.atzuche.order.commons.vo.req.OwnerUpdateSeeVO;
import com.atzuche.order.commons.vo.req.RenterAndOwnerSeeOrderVO;
import com.atzuche.order.owner.mem.service.OwnerMemberService;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderSubsidyDetailEntity;
import com.atzuche.order.ownercost.service.OwnerOrderService;
import com.atzuche.order.ownercost.service.OwnerOrderSubsidyDetailService;
import com.atzuche.order.parentorder.entity.OrderNoticeEntity;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderNoticeService;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.rentermem.service.RenterMemberService;
import com.atzuche.order.renterorder.entity.RenterDepositDetailEntity;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterDepositDetailService;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.atzuche.order.settle.service.OrderSettleService;
import com.atzuche.order.settle.vo.req.OwnerCosts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
    @Autowired
    private OrderStatusService orderStatusService;
    @Autowired
    private AccountOwnerIncomeExamineNoTService accountOwnerIncomeExamineNoTService;
    @Autowired
    private RenterDepositDetailService renterDepositDetailService;
    @Autowired
    private OwnerOrderSubsidyDetailService ownerOrderSubsidyDetailService;
    @Autowired
    private AddIncomeExamineMapper addIncomeExamineMapper;

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
        OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(orderNo);
        if(orderStatusEntity == null){
            log.error("订单状态查询失败orderNo={}",orderNo);
            throw new OrderStatusNotFoundException();
        }
        OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(orderNo);
        if(ownerOrderEntity == null){
            log.error("找不到有效的车主子订单 orderNo={}",orderNo);
            throw new OrderNotFoundException(orderNo);
        }
        OwnerPreIncomRespDTO ownerPreIncomRespDTO = new OwnerPreIncomRespDTO();
        int ownerIncomAmt = 0;
        if(SettleStatusEnum.SETTLED.getCode() == orderStatusEntity.getSettleStatus()){//已结算
            List<AccountOwnerIncomeExamineEntity> accountOwnerIncomeExamineEntityList = accountOwnerIncomeExamineNoTService.getAccountOwnerIncomeExamineByOrderNo(orderNo);
            List<AccountOwnerIncomeExamineEntity> auditPassList = AccountOwnerIncomeExamineUtil.filterByStatus(accountOwnerIncomeExamineEntityList, AccountOwnerIncomeExamineStatus.PASS_EXAMINE);
            if(auditPassList != null && auditPassList.size()>0){//优先选择审核通过的
                ownerIncomAmt = AccountOwnerIncomeExamineUtil.statisticsAmt(auditPassList);
            }else{//取结算收益
                AccountOwnerIncomeExamineEntity accountOwnerIncomeExamineEntity = AccountOwnerIncomeExamineUtil.filterByType(accountOwnerIncomeExamineEntityList, AccountOwnerIncomeExamineType.OWNER_INCOME);
                ownerIncomAmt =  accountOwnerIncomeExamineEntity==null?0:accountOwnerIncomeExamineEntity.getAmt()==null?0:accountOwnerIncomeExamineEntity.getAmt();
            }
            ownerPreIncomRespDTO.setSettleStatus(SettleStatusEnum.SETTLED.getCode());
        }else{
            OwnerCosts ownerCosts = orderSettleService.preOwnerSettleOrder(orderNo, ownerOrderEntity.getOwnerOrderNo());
            ownerIncomAmt = ownerCosts.getOwnerCostAmtFinal();
            ownerPreIncomRespDTO.setSettleStatus(orderStatusEntity.getSettleStatus());
        }

        //获取追加收益
        AddIncomeExamineDTO addIncomeExamineDTO = new AddIncomeExamineDTO();
        addIncomeExamineDTO.setOrderNo(orderNo);
        List<AddIncomeExamine> addIncomeExamineList = addIncomeExamineMapper.listAllAddIncomeExamine(addIncomeExamineDTO);
        int addIncomAmt = AccountOwnerIncomeExamineUtil.statisticsAddIncomAmt(addIncomeExamineList, ExamineStatusEnum.APPROVED);
        log.info("获取追加收益addIncomAmt={}",addIncomAmt);
        ownerPreIncomRespDTO.setOwnerCostAmtFinal(ownerIncomAmt + addIncomAmt);
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

    public OwnerPreAndSettleIncomRespDTO queryOwnerPreAndSettleIncom(String orderNo) {
        OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(orderNo);
        if(orderStatusEntity == null){
            log.error("订单状态查询失败orderNo={}",orderNo);
            throw new OrderStatusNotFoundException();
        }
        OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(orderNo);
        if(ownerOrderEntity == null){
            log.error("找不到有效的车主子订单 orderNo={}",orderNo);
            throw new OrderNotFoundException(orderNo);
        }
        OwnerPreAndSettleIncomRespDTO ownerPreAndSettleIncomRespDTO = new OwnerPreAndSettleIncomRespDTO();
        int ownerIncomAmt = 0;
        if(SettleStatusEnum.SETTLED.getCode() == orderStatusEntity.getSettleStatus()){//已结算

            List<AccountOwnerIncomeExamineEntity> accountOwnerIncomeExamineEntityList = accountOwnerIncomeExamineNoTService.getAccountOwnerIncomeExamineByOrderNo(orderNo);
            AccountOwnerIncomeExamineEntity accountOwnerIncomeExamineEntity = AccountOwnerIncomeExamineUtil.filterByType(accountOwnerIncomeExamineEntityList, AccountOwnerIncomeExamineType.OWNER_INCOME);
            if(accountOwnerIncomeExamineEntity == null){
                /*log.error("车主结算收益查询异常");
                throw new OwnerIncomeExamineNotFoundException();*/
                ownerIncomAmt = 0;
            }else{
                List<AccountOwnerIncomeExamineEntity> auditPassList = AccountOwnerIncomeExamineUtil.filterByStatus(accountOwnerIncomeExamineEntityList, null);
                ownerIncomAmt = AccountOwnerIncomeExamineUtil.statisticsAmt(auditPassList);
                ownerPreAndSettleIncomRespDTO.setAuditStatus(accountOwnerIncomeExamineEntity.getStatus());
            }
            ownerPreAndSettleIncomRespDTO.setSettleStatus(SettleStatusEnum.SETTLED.getCode());
        }else{
            OwnerCosts ownerCosts = orderSettleService.preOwnerSettleOrder(orderNo, ownerOrderEntity.getOwnerOrderNo());
            ownerIncomAmt = ownerCosts.getOwnerCostAmtFinal();
            ownerPreAndSettleIncomRespDTO.setSettleStatus(orderStatusEntity.getSettleStatus());
        }
        ownerPreAndSettleIncomRespDTO.setOwnerIncomAmt(ownerIncomAmt);
        return ownerPreAndSettleIncomRespDTO;
    }

    public RenterDepositDetailDTO queryrenterDepositDetail(String orderNo) {
        RenterDepositDetailEntity renterDepositDetailEntity = renterDepositDetailService.queryByOrderNo(orderNo);
        RenterDepositDetailDTO renterDepositDetailDTO = null;
        if(renterDepositDetailEntity != null){
            renterDepositDetailDTO = new RenterDepositDetailDTO();
            BeanUtils.copyProperties(renterDepositDetailEntity,renterDepositDetailDTO);
        }
        return renterDepositDetailDTO;
    }

    public List<OwnerOrderSubsidyDetailDTO> queryOwnerSubsidyByownerOrderNo(String orderNo,String ownerOrderNo) {
        List<OwnerOrderSubsidyDetailEntity> list = ownerOrderSubsidyDetailService.listOwnerOrderSubsidyDetail(orderNo, ownerOrderNo);
        List<OwnerOrderSubsidyDetailDTO> ownerOrderSubsidyDetailDTOS = new ArrayList<>();
        Optional.ofNullable(list).orElseGet(ArrayList::new).stream().forEach(x->{
            OwnerOrderSubsidyDetailDTO ownerOrderSubsidyDetailDTO = new OwnerOrderSubsidyDetailDTO();
            BeanUtils.copyProperties(x,ownerOrderSubsidyDetailDTO);
            ownerOrderSubsidyDetailDTOS.add(ownerOrderSubsidyDetailDTO);
        });
        return ownerOrderSubsidyDetailDTOS;
    }
}
