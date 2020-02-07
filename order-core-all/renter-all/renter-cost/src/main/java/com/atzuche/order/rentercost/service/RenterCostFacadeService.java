package com.atzuche.order.rentercost.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
