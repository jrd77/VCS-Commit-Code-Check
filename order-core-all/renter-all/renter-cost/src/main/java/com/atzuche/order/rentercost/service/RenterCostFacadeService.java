package com.atzuche.order.rentercost.service;

import com.atzuche.order.commons.enums.SubsidySourceCodeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.vo.res.RenterCostDetailVO;
import com.atzuche.order.commons.vo.res.RenterDeliveryFeeDetailVO;
import com.atzuche.order.commons.vo.res.RenterFineVO;
import com.atzuche.order.commons.vo.res.RenterSubsidyDetail;
import com.atzuche.order.rentercost.entity.*;
import com.atzuche.order.rentercost.utils.FineDetailUtils;
import com.atzuche.order.rentercost.utils.OrderSubsidyDetailUtils;
import com.atzuche.order.rentercost.utils.RenterOrderCostDetailUtils;
import com.mysql.cj.x.protobuf.MysqlxCrud;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 提供租客费用的对外接口服务
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/2/7 10:22 上午
 **/
@Service
public class RenterCostFacadeService {
    @Autowired
    private RenterOrderCostDetailService orderCostDetailService;

    @Autowired
    private RenterOrderSubsidyDetailService subsidyDetailService;
    @Autowired
    private RenterOrderFineDeatailService fineDeatailService;
    @Autowired
    private OrderConsoleCostDetailService consoleCostDetailService;
    @Autowired
    private OrderConsoleSubsidyDetailService consoleSubsidyDetailService;
    @Autowired
    private ConsoleRenterOrderFineDeatailService consoleRenterOrderFineDeatailService;
    
    private final static Logger logger = LoggerFactory.getLogger(RenterCostFacadeService.class);
    

    /**
     * 返回租车的总费用
     * @param orderNo
     * @param renterOrderNo
     * @param memNo
     * @return
     */
    public int getTotalRenterCost(String orderNo,String renterOrderNo,String memNo){
        int totalBsicRentCostAmt = orderCostDetailService.getTotalOrderCostAmt(orderNo,renterOrderNo);
        int totalSubsidyAmt = subsidyDetailService.getTotalRenterOrderSubsidyAmt(orderNo,renterOrderNo);
        int totalRenterOrderFineAmt = fineDeatailService.getTotalRenterOrderFineAmt(orderNo,renterOrderNo);
        int totalConsoleFineAmt = consoleRenterOrderFineDeatailService.getTotalConsoleFineAmt(orderNo,memNo);
        int totalConsoleSubsidyAmt = consoleSubsidyDetailService.getTotalRenterOrderConsoleSubsidy(orderNo,memNo);
        int totalConsoleCostAmt = consoleCostDetailService.getTotalOrderConsoleCostAmt(orderNo);

        int totalCost = totalBsicRentCostAmt +totalSubsidyAmt+totalRenterOrderFineAmt+totalConsoleFineAmt+totalConsoleSubsidyAmt+totalConsoleCostAmt;
        logger.info("getTotalRenterCost[orderNo={},renterOrderNo={},memNo={}]==[totalBasicRentCostAmt={},totalSubsidyAmt={},totalRenterOrderFineAmt={},totalConsoleFineAmt={},totalConsoleSubsidyAmt={},totalConsoleCostAmt={}]",
                orderNo,renterOrderNo,memNo,totalBsicRentCostAmt,totalSubsidyAmt,totalRenterOrderFineAmt,totalConsoleFineAmt,totalConsoleSubsidyAmt,totalConsoleCostAmt);
        return totalCost;
    }

    /**
     * 获得租车的总费用（不包括罚金）
     * @param orderNo
     * @param renterOrderNo
     * @param memNo
     * @return
     */
    public int getTotalRenterCostWithoutFine(String orderNo,String renterOrderNo,String memNo){
        int totalBsicRentCostAmt = orderCostDetailService.getTotalOrderCostAmt(orderNo,renterOrderNo);
        int totalSubsidyAmt = subsidyDetailService.getTotalRenterOrderSubsidyAmt(orderNo,renterOrderNo);
        int totalConsoleSubsidyAmt = consoleSubsidyDetailService.getTotalRenterOrderConsoleSubsidy(orderNo,memNo);
        int totalConsoleCostAmt = consoleCostDetailService.getTotalOrderConsoleCostAmt(orderNo);

        int totalCost = totalBsicRentCostAmt +totalSubsidyAmt+totalConsoleSubsidyAmt+totalConsoleCostAmt;
        logger.info("getTotalRenterCost[orderNo={},renterOrderNo={},memNo={}]==[totalBasicRentCostAmt={},totalSubsidyAmt={},totalConsoleSubsidyAmt={},totalConsoleCostAmt={}]",
                orderNo,renterOrderNo,memNo,totalBsicRentCostAmt,totalSubsidyAmt,totalConsoleSubsidyAmt,totalConsoleCostAmt);
        return totalCost;
    }

    public RenterCostDetailVO getRenterCostFullDetail(String orderNo, String renterOrderNo, String memNo){
        List<RenterOrderCostDetailEntity> renterOrderCostDetailEntityList = orderCostDetailService.getRenterOrderCostDetailList(orderNo,renterOrderNo);
        List<RenterOrderFineDeatailEntity> renterOrderFineDeatailEntityList=fineDeatailService.listRenterOrderFineDeatail(orderNo,renterOrderNo);
        List<ConsoleRenterOrderFineDeatailEntity> consoleRenterOrderFineDeatailEntityList = consoleRenterOrderFineDeatailService.listConsoleRenterOrderFineDeatail(orderNo,memNo);

        RenterCostDetailVO basicCostDetailVO = new RenterCostDetailVO();

        basicCostDetailVO.setMemNo(memNo);
        basicCostDetailVO.setOrderNo(orderNo);
        basicCostDetailVO.setRentAmt(-RenterOrderCostDetailUtils.getRentAmt(renterOrderCostDetailEntityList));
        basicCostDetailVO.setInsuranceAmt(-RenterOrderCostDetailUtils.getInsuranceAmt(renterOrderCostDetailEntityList));
        basicCostDetailVO.setFee(-RenterOrderCostDetailUtils.getFeeAmt(renterOrderCostDetailEntityList));
        basicCostDetailVO.setAbatementInsuranceAmt(-RenterOrderCostDetailUtils.getAbatementInsuranceAmt(renterOrderCostDetailEntityList));
        basicCostDetailVO.setExtraDriverInsuranceAmt(-RenterOrderCostDetailUtils.getExtraDriverInsureAmt(renterOrderCostDetailEntityList));

        RenterDeliveryFeeDetailVO deliveryFeeDetailVO = getRenterDeliveryFeeDetail(renterOrderCostDetailEntityList);
        basicCostDetailVO.setDeliveryFeeDetail(deliveryFeeDetailVO);

        RenterFineVO renterFineVO = getRenterFineDetail(orderNo,renterOrderNo,memNo);
        basicCostDetailVO.setFineDetail(renterFineVO);

        RenterSubsidyDetail subsidyDetail = getRenterSubsidyDetail(orderNo,renterOrderNo,memNo);
        basicCostDetailVO.setSubsidyDetail(subsidyDetail);

        //TODO:
        return basicCostDetailVO;
    }

    public RenterSubsidyDetail getRenterSubsidyDetail(String orderNo,String renterOrderNo,String memNo){
        List<RenterOrderSubsidyDetailEntity> renterOrderSubsidyDetailEntityList = subsidyDetailService.listRenterOrderSubsidyDetail(orderNo,renterOrderNo);
        List<OrderConsoleSubsidyDetailEntity> consoleSubsidyDetailEntityList = consoleSubsidyDetailService.listOrderConsoleSubsidyDetailByOrderNoAndMemNo(orderNo,memNo);

        RenterSubsidyDetail detail = new RenterSubsidyDetail();

        detail.setPlatformCouponSubsidyAmt(-OrderSubsidyDetailUtils.getRenterPlatCouponSubsidyAmt(renterOrderSubsidyDetailEntityList));
        detail.setOwnerCouponSubsidyAmt(-OrderSubsidyDetailUtils.getRenterOwnerCouponSubsidyAmt(renterOrderSubsidyDetailEntityList));
        detail.setAutoCoinSubsidyAmt(-OrderSubsidyDetailUtils.getRenterAutoCoinSubsidyAmt(renterOrderSubsidyDetailEntityList));
        detail.setGetCarCouponSubsidyAmt(-OrderSubsidyDetailUtils.getRenterGetCarFeeSubsidyAmt(renterOrderSubsidyDetailEntityList));
        detail.setLimitTimeSubsidyAmt(-OrderSubsidyDetailUtils.getRenterRealLimitDeductSubsidyAmt(renterOrderSubsidyDetailEntityList));
        detail.setUpdateSubsidyAmt(-OrderSubsidyDetailUtils.getRenterUpateSubsidyAmt(renterOrderSubsidyDetailEntityList));

        detail.setRenter2OwnerSubsidyAmt(-(OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.SUBSIDY_RENTERTOOWNER_ADJUST)+
                OrderSubsidyDetailUtils.getConsoleSubsidyAmt(consoleSubsidyDetailEntityList, SubsidySourceCodeEnum.RENTER,RenterCashCodeEnum.SUBSIDY_RENTERTOOWNER_ADJUST)));
        detail.setOwner2RenterSubsidyAmt(-(OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.SUBSIDY_OWNERTORENTER_ADJUST)+
                OrderSubsidyDetailUtils.getConsoleSubsidyAmt(consoleSubsidyDetailEntityList, SubsidySourceCodeEnum.RENTER,RenterCashCodeEnum.SUBSIDY_OWNERTORENTER_ADJUST)));
        detail.setOwner2RenterRentSubsidyAmt(-(OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.SUBSIDY_OWNER_TORENTER_RENTAMT)+
                OrderSubsidyDetailUtils.getConsoleSubsidyAmt(consoleSubsidyDetailEntityList, SubsidySourceCodeEnum.RENTER,RenterCashCodeEnum.SUBSIDY_OWNER_TORENTER_RENTAMT)));
        detail.setAbatementSubsidyAmt(-(OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.SUBSIDY_ABATEMENT)+
                OrderSubsidyDetailUtils.getConsoleSubsidyAmt(consoleSubsidyDetailEntityList, SubsidySourceCodeEnum.RENTER,RenterCashCodeEnum.SUBSIDY_ABATEMENT)));
        detail.setCleanCarSubsidyAmt(-(OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.SUBSIDY_CLEANCAR)+
                OrderSubsidyDetailUtils.getConsoleSubsidyAmt(consoleSubsidyDetailEntityList, SubsidySourceCodeEnum.RENTER,RenterCashCodeEnum.SUBSIDY_CLEANCAR)));
        detail.setDelaySubsidyAmt(-(OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.SUBSIDY_DELAY)+
                OrderSubsidyDetailUtils.getConsoleSubsidyAmt(consoleSubsidyDetailEntityList, SubsidySourceCodeEnum.RENTER,RenterCashCodeEnum.SUBSIDY_DELAY)));
        detail.setFeeSubsidyAmt(-(OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.SUBSIDY_FEE)+
                OrderSubsidyDetailUtils.getConsoleSubsidyAmt(consoleSubsidyDetailEntityList, SubsidySourceCodeEnum.RENTER,RenterCashCodeEnum.SUBSIDY_FEE)));
        detail.setInsureSubsidyAmt(-(OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.SUBSIDY_INSURE)+
                OrderSubsidyDetailUtils.getConsoleSubsidyAmt(consoleSubsidyDetailEntityList, SubsidySourceCodeEnum.RENTER,RenterCashCodeEnum.SUBSIDY_INSURE)));
        detail.setOilSubsidyAmt(-(OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.SUBSIDY_OIL)+
                OrderSubsidyDetailUtils.getConsoleSubsidyAmt(consoleSubsidyDetailEntityList, SubsidySourceCodeEnum.RENTER,RenterCashCodeEnum.SUBSIDY_OIL)));
        detail.setOtherSubsidyAmt(-(OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.SUBSIDY_OTHER)+
                OrderSubsidyDetailUtils.getConsoleSubsidyAmt(consoleSubsidyDetailEntityList, SubsidySourceCodeEnum.RENTER,RenterCashCodeEnum.SUBSIDY_OTHER)));

        detail.setRentSubsidyAmt(-(OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.SUBSIDY_RENTAMT)+
                OrderSubsidyDetailUtils.getConsoleSubsidyAmt(consoleSubsidyDetailEntityList, SubsidySourceCodeEnum.RENTER,RenterCashCodeEnum.SUBSIDY_RENTAMT)));
        detail.setGetReturnDelaySubsidyAmt(-(OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.SUBSIDY_GETRETURNDELAY)+
                OrderSubsidyDetailUtils.getConsoleSubsidyAmt(consoleSubsidyDetailEntityList, SubsidySourceCodeEnum.RENTER,RenterCashCodeEnum.SUBSIDY_GETRETURNDELAY)));
        detail.setTrafficSubsidyAmt(-(OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.SUBSIDY_TRAFFIC)+
                OrderSubsidyDetailUtils.getConsoleSubsidyAmt(consoleSubsidyDetailEntityList, SubsidySourceCodeEnum.RENTER,RenterCashCodeEnum.SUBSIDY_TRAFFIC)));



        return detail;
    }

    public RenterFineVO getRenterFineDetail(String orderNo, String renterOrderNo, String memNo){
        List<RenterOrderFineDeatailEntity> renterOrderFineDeatailEntityList=fineDeatailService.listRenterOrderFineDeatail(orderNo,renterOrderNo);
        List<ConsoleRenterOrderFineDeatailEntity> consoleRenterOrderFineDeatailEntityList = consoleRenterOrderFineDeatailService.listConsoleRenterOrderFineDeatail(orderNo,memNo);

        RenterFineVO renterFineVO = new RenterFineVO();
        renterFineVO.setCancelFine(-(FineDetailUtils.getRenterOrderCancelFineAmt(renterOrderFineDeatailEntityList)+FineDetailUtils.getRenterConsoleCancelFineAmt(consoleRenterOrderFineDeatailEntityList)));
        renterFineVO.setModifyGetFine(-(FineDetailUtils.getRenterOrderModifyGetFineAmt(renterOrderFineDeatailEntityList)+FineDetailUtils.getRenterConsoleModifyGetFineAmt(consoleRenterOrderFineDeatailEntityList)));
        renterFineVO.setModifyReturnFine(-(FineDetailUtils.getRenterOrderModifyReturnFineAmt(renterOrderFineDeatailEntityList)+FineDetailUtils.getRenterConsoleModifyReturnFineAmt(consoleRenterOrderFineDeatailEntityList)));
        renterFineVO.setModifyAdvanceFine(-(FineDetailUtils.getRenterOrderModifyAdvanceFineAmt(renterOrderFineDeatailEntityList)+FineDetailUtils.getRenterConsoleModifyAdvanceFineAmt(consoleRenterOrderFineDeatailEntityList)));
        renterFineVO.setDelayFine(-(FineDetailUtils.getRenterOrderDelayFineAmt(renterOrderFineDeatailEntityList)+FineDetailUtils.getRenterConsoleDelayFineAmt(consoleRenterOrderFineDeatailEntityList)));
        renterFineVO.setGetReturnCarFine(-(FineDetailUtils.getRenterOrderGetReturnCarFineAmt(renterOrderFineDeatailEntityList)+FineDetailUtils.getRenterConsoleGetReturnCarFineAmt(consoleRenterOrderFineDeatailEntityList)));
        renterFineVO.setRenterAdvanceReturnFine(-(FineDetailUtils.getRenterOrderAdvanceReturnFineAmt(renterOrderFineDeatailEntityList)+FineDetailUtils.getRenterConsoleAdvanceReturnFineAmt(consoleRenterOrderFineDeatailEntityList)));
        renterFineVO.setRenterDelayReturnFine(-(FineDetailUtils.getRenterOrderDelayReturnFineAmt(renterOrderFineDeatailEntityList)+FineDetailUtils.getRenterConsoleDelayFineAmt(consoleRenterOrderFineDeatailEntityList)));

        return renterFineVO;
    }

    private RenterDeliveryFeeDetailVO getRenterDeliveryFeeDetail(List<RenterOrderCostDetailEntity> renterOrderCostDetailEntityList) {
        RenterDeliveryFeeDetailVO deliveryFeeDetailVO = new RenterDeliveryFeeDetailVO();
        deliveryFeeDetailVO.setSrvGetCostAmt(-RenterOrderCostDetailUtils.getSrvGetCostAmt(renterOrderCostDetailEntityList));
        deliveryFeeDetailVO.setSrvReturnCostAmt(-RenterOrderCostDetailUtils.getSrvReturnCostAmt(renterOrderCostDetailEntityList));
        deliveryFeeDetailVO.setGetBlockedRaiseAmt(-RenterOrderCostDetailUtils.getGetBlockedRaiseAmt(renterOrderCostDetailEntityList));
        deliveryFeeDetailVO.setReturnBlockedRaiseAmt(-RenterOrderCostDetailUtils.getReturnBlockedRaiseAmt(renterOrderCostDetailEntityList));
        return deliveryFeeDetailVO;
    }


    /**
     * 获取订单罚金总额
     * @param orderNo
     * @param renterOrderNo
     * @param memNo
     * @return
     */
    public int getTotalFine(String orderNo,String renterOrderNo,String memNo){
        int totalRenterOrderFineAmt = fineDeatailService.getTotalRenterOrderFineAmt(orderNo,renterOrderNo);
        int totalConsoleFineAmt = consoleRenterOrderFineDeatailService.getTotalConsoleFineAmt(orderNo,memNo);
        return totalRenterOrderFineAmt+totalConsoleFineAmt;
    }


}
