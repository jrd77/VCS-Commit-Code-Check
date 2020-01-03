package com.atzuche.order.settle.service.notservice;

import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostDetailEntity;
import com.atzuche.order.cashieraccount.service.CashierSettleService;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.MileageAmtDTO;
import com.atzuche.order.commons.entity.dto.OilAmtDTO;
import com.atzuche.order.commons.enums.RenterCashCodeEnum;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderIncrementDetailEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderPurchaseDetailEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderSubsidyDetailEntity;
import com.atzuche.order.ownercost.service.OwnerOrderCostCombineService;
import com.atzuche.order.ownercost.service.OwnerOrderIncrementDetailService;
import com.atzuche.order.ownercost.service.OwnerOrderPurchaseDetailService;
import com.atzuche.order.ownercost.service.OwnerOrderSubsidyDetailService;
import com.atzuche.order.rentercost.entity.*;
import com.atzuche.order.rentercost.service.*;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.settle.vo.req.OwnerCosts;
import com.atzuche.order.settle.vo.req.RentCosts;
import com.atzuche.order.settle.vo.req.SettleOrders;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 订单结算
 */
@Service
public class OrderSettleNoTService {

    @Autowired private CashierSettleService cashierSettleService;
    @Autowired private RenterOrderCostDetailService renterOrderCostDetailService;
    @Autowired private RenterOrderSubsidyDetailService renterOrderSubsidyDetailService;
    @Autowired private RenterOrderFineDeatailService renterOrderFineDeatailService;
    @Autowired private OrderConsoleSubsidyDetailService orderConsoleSubsidyDetailService;
    @Autowired private ConsoleRenterOrderFineDeatailService consoleRenterOrderFineDeatailService;
    @Autowired private OwnerOrderCostCombineService ownerOrderCostCombineService;
    @Autowired private OwnerOrderSubsidyDetailService ownerOrderSubsidyDetailService;
    @Autowired private OwnerOrderPurchaseDetailService ownerOrderPurchaseDetailService;
    @Autowired private OwnerOrderIncrementDetailService ownerOrderIncrementDetailService;
    @Autowired private RenterOrderCostCombineService renterOrderCostCombineService;

    /**
     * 车辆结算
     * @param orderNo
     * @return
     */
    public List<AccountRenterCostDetailEntity> getAccountRenterCostDetailsByOrderNo(String orderNo){
        return cashierSettleService.getAccountRenterCostDetailsByOrderNo(orderNo);
    }
    /**
     * 初始化结算对象
     * @param orderNo
     * @param renterOrderNo
     * @param ownerOrderNo
     * @param renterMemNo
     * @param ownerMemNo
     * @return
     */
    public SettleOrders initSettleOrders(String orderNo, String renterOrderNo, String ownerOrderNo, String renterMemNo, String ownerMemNo) {
        SettleOrders settleOrders = new SettleOrders();
        settleOrders.setOrderNo(orderNo);
        settleOrders.setRenterOrderNo(renterOrderNo);
        settleOrders.setOwnerOrderNo(ownerOrderNo);
        settleOrders.setRenterMemNo(renterMemNo);
        settleOrders.setOwnerMemNo(ownerMemNo);
        return settleOrders;
    }
    /**
     * 校验是否可以结算
     * @param renterOrder
     */
    public void check(RenterOrderEntity renterOrder) {
    }

    /**
     *  租客返回基本信息 TODO
     * @return
     */
    private CostBaseDTO getCostBaseRent(SettleOrders settleOrders,RenterOrderEntity renterOrder){
        CostBaseDTO costBaseDTO = new CostBaseDTO();
        costBaseDTO.setOrderNo(renterOrder.getOrderNo());
        costBaseDTO.setMemNo("");
        costBaseDTO.setRenterOrderNo(renterOrder.getRenterOrderNo());
        costBaseDTO.setStartTime(renterOrder.getExpRentTime());
        costBaseDTO.setEndTime(renterOrder.getExpRevertTime());
        return costBaseDTO;
    }
    /**
     * 租客交接车-油费 参数构建 TODO
     * @param settleOrders
     * @param renterOrder
     * @return
     */
    private OilAmtDTO getOilAmtDTO(SettleOrders settleOrders,RenterOrderEntity renterOrder) {
        OilAmtDTO oilAmtDTO = new OilAmtDTO();
        CostBaseDTO costBaseDTO = getCostBaseRent(settleOrders,renterOrder);
        oilAmtDTO.setCostBaseDTO(costBaseDTO);
        return oilAmtDTO;
    }

    /**
     * 交接车-获取超里程费用 TODO
     */
    private MileageAmtDTO getMileageAmtDTO(SettleOrders settleOrders, RenterOrderEntity renterOrder) {
        MileageAmtDTO mileageAmtDTO = new MileageAmtDTO();
        CostBaseDTO costBaseDTO = getCostBaseRent(settleOrders,renterOrder);
        mileageAmtDTO.setCostBaseDTO(costBaseDTO);
        return mileageAmtDTO;
    }

    /**
     * 查询租客费用明细
     * @param settleOrders
     */
    public void getRenterCostSettleDetail(SettleOrders settleOrders,RenterOrderEntity renterOrder) {
        RentCosts rentCosts = new RentCosts();
        //1 查询租车费用
        List<RenterOrderCostDetailEntity> renterOrderCostDetails = renterOrderCostDetailService.listRenterOrderCostDetail(settleOrders.getOrderNo(),settleOrders.getRenterOrderNo());
        //2 交接车-油费
        OilAmtDTO oilAmtDTO =  getOilAmtDTO(settleOrders,renterOrder);
        RenterOrderCostDetailEntity oilAmt = renterOrderCostCombineService.getOilAmtEntity(oilAmtDTO);
        //3 交接车-获取超里程费用
        MileageAmtDTO mileageAmtDTO = getMileageAmtDTO(settleOrders,renterOrder);
        RenterOrderCostDetailEntity mileageAmt = renterOrderCostCombineService.getMileageAmtEntity(mileageAmtDTO);
        //4 补贴
        List<RenterOrderSubsidyDetailEntity> renterOrderSubsidyDetails = renterOrderSubsidyDetailService.listRenterOrderSubsidyDetail(settleOrders.getOrderNo(),settleOrders.getRenterOrderNo());
        //5 租客罚金
        List<RenterOrderFineDeatailEntity> renterOrderFineDeatails = renterOrderFineDeatailService.listRenterOrderFineDeatail(settleOrders.getOrderNo(),settleOrders.getRenterOrderNo());
        //6 管理后台补贴 （租客车主共用表 ，会员号区分车主/租客）
        List<OrderConsoleSubsidyDetailEntity> orderConsoleSubsidyDetails = orderConsoleSubsidyDetailService.listOrderConsoleSubsidyDetailByOrderNoAndMemNo(settleOrders.getOrderNo(),settleOrders.getRenterMemNo());
        //7 获取全局的租客订单罚金明细
        List<ConsoleRenterOrderFineDeatailEntity> consoleRenterOrderFineDeatails = consoleRenterOrderFineDeatailService.listConsoleRenterOrderFineDeatail(settleOrders.getOrderNo(),settleOrders.getRenterMemNo());

        //租车费用明细
        int renterOrderCost = 0;
        if(!CollectionUtils.isEmpty(renterOrderCostDetails)){
            renterOrderCost = renterOrderCostDetails.stream().filter(obj ->{
                return RenterCashCodeEnum.RENT_AMT.getCashNo().equals(obj.getCostCode());
            }).mapToInt(RenterOrderCostDetailEntity::getTotalAmount).sum();
        }
        rentCosts.setRenterOrderCostDetails(renterOrderCostDetails);
        rentCosts.setOilAmt(oilAmt);
        rentCosts.setMileageAmt(mileageAmt);
        rentCosts.setRenterOrderSubsidyDetails(renterOrderSubsidyDetails);
        rentCosts.setRenterOrderFineDeatails(renterOrderFineDeatails);
        rentCosts.setOrderConsoleSubsidyDetails(orderConsoleSubsidyDetails);
        rentCosts.setConsoleRenterOrderFineDeatails(consoleRenterOrderFineDeatails);

        settleOrders.setRenterOrderCost(renterOrderCost);
        settleOrders.setRentCosts(rentCosts);
    }

    /**
     * 查询车主费用明细
     * @param settleOrders
     */
    public void getOwnerCostSettleDetail(SettleOrders settleOrders, OwnerOrderEntity ownerOrder) {
        OwnerCosts ownerCosts = new OwnerCosts();
        //1 车主端代管车服务费
        CostBaseDTO costBaseDTO= getCostBaseOwner(ownerOrder);
        Integer rentAmt=settleOrders.getRenterOrderCost();
        //代管车服务费比例 商品
        Integer proxyProportion=0;
        OwnerOrderPurchaseDetailEntity proxyExpense = ownerOrderCostCombineService.getProxyExpense(costBaseDTO,rentAmt,proxyProportion);
        //2 车主端平台服务费
        //服务费比例 商品
        Integer serviceProportion = 0;
        OwnerOrderPurchaseDetailEntity serviceExpense = ownerOrderCostCombineService.getServiceExpense(costBaseDTO,rentAmt,serviceProportion);
        //3 获取车主补贴明细列表
        List<OwnerOrderSubsidyDetailEntity> ownerOrderSubsidyDetail = ownerOrderSubsidyDetailService.listOwnerOrderSubsidyDetail(settleOrders.getOrderNo(),settleOrders.getOwnerOrderNo());
        //4 获取车主费用列表
        List<OwnerOrderPurchaseDetailEntity> ownerOrderPurchaseDetail = ownerOrderPurchaseDetailService.listOwnerOrderPurchaseDetail(settleOrders.getOrderNo(),settleOrders.getOwnerOrderNo());
        //5 获取车主增值服务费用列表
        List<OwnerOrderIncrementDetailEntity> ownerOrderIncrementDetail = ownerOrderIncrementDetailService.listOwnerOrderIncrementDetail(settleOrders.getOrderNo(),settleOrders.getOwnerOrderNo());
        // 6 获取gps服务费
        //车辆安装gps序列号列表 商品系统 TODO
        List<Integer> lsGpsSerialNumber = null;
        List<OwnerOrderPurchaseDetailEntity> gpsCost =  ownerOrderCostCombineService.getGpsServiceAmtEntity(costBaseDTO,lsGpsSerialNumber);
        //7 获取车主油费
        OilAmtDTO oilAmtDTO = getOilAmtOwner(ownerOrder);
        OwnerOrderPurchaseDetailEntity renterOrderCostDetail = ownerOrderCostCombineService.getOilAmtEntity(oilAmtDTO);

        ownerCosts.setProxyExpense(proxyExpense);
        ownerCosts.setServiceExpense(serviceExpense);
        ownerCosts.setOwnerOrderSubsidyDetail(ownerOrderSubsidyDetail);
        ownerCosts.setOwnerOrderPurchaseDetail(ownerOrderPurchaseDetail);
        ownerCosts.setOwnerOrderIncrementDetail(ownerOrderIncrementDetail);
        ownerCosts.setGpsCost(gpsCost);
        ownerCosts.setRenterOrderCostDetail(renterOrderCostDetail);
        settleOrders.setOwnerCosts(ownerCosts);
    }

    /**
     * 车主交接车-油费 参数构建 TODO
     * @param ownerOrder
     * @return
     */
    private OilAmtDTO getOilAmtOwner(OwnerOrderEntity ownerOrder) {
        OilAmtDTO oilAmtDTO = new OilAmtDTO();
        CostBaseDTO costBaseDTO = getCostBaseOwner(ownerOrder);
        oilAmtDTO.setCostBaseDTO(costBaseDTO);
        return oilAmtDTO;
    }
    /**
     *  租客返回基本信息 TODO
     * @return
     */
    private CostBaseDTO getCostBaseOwner(OwnerOrderEntity ownerOrder){
        CostBaseDTO costBaseDTO = new CostBaseDTO();
        costBaseDTO.setOrderNo(ownerOrder.getOrderNo());
        costBaseDTO.setMemNo(ownerOrder.getMemNo());
        costBaseDTO.setOwnerOrderNo(ownerOrder.getOwnerOrderNo());
        costBaseDTO.setStartTime(ownerOrder.getExpRentTime());
        costBaseDTO.setEndTime(ownerOrder.getExpRevertTime());
        return costBaseDTO;
    }

}
