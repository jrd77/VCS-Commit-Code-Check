package com.atzuche.order.admin.controller;

import com.atzuche.order.admin.vo.req.DeliveryCarRepVO;
import com.atzuche.order.admin.vo.req.cost.OwnerInComeReqVO;
import com.atzuche.order.admin.vo.req.cost.RentalCostReqVO;
import com.atzuche.order.admin.vo.resp.cost.*;
import com.atzuche.order.admin.vo.resp.delivery.DeliveryCarVO;
import com.atzuche.order.admin.vo.resp.income.OwnerInComeRepVO;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocGroup;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 胡春林
 * 租车费用相关接口
 */
@RestController
@RequestMapping("/api/cost")
public class OrderRentalCostController {

    /**
     * 查询租车费用
     * @param rentalCostReqVO
     * @return
     */
    @AutoDocVersion(version = "管理后台租车费用信息")
    @AutoDocGroup(group = "管理后台租车费用信息")
    @AutoDocMethod(description = "租车费用", value = "租车费用",response = RentalCostRepVO.class)
    @PostMapping("/list")
    public ResponseData<?> findRentalCostListByOrderNo(@RequestBody RentalCostReqVO rentalCostReqVO) {
        if (null == rentalCostReqVO || StringUtils.isBlank(rentalCostReqVO.getRenterOrderNo())) {
            return ResponseData.createErrorCodeResponse(ErrorCode.ORDER_NO_PARAM_ERROR.getCode(), "租客子订单编号为空");
        }
        return ResponseData.success();
    }

    /**
     * 基础费用
     * @param
     * @return
     */
    @AutoDocVersion(version = "管理后台基础费用修改")
    @AutoDocGroup(group = "管理后台基础费用修改")
    @AutoDocMethod(description = "基础费用修改", value = "基础费用修改",response = ResponseData.class)
    @PostMapping("/cost/baseCost/update")
    public ResponseData<?> updateBaseCostByRenterOrderNo(@RequestBody BaseCostVO baseCostVO) {
        if (null == baseCostVO || StringUtils.isBlank(baseCostVO.getRenterOrderNo())) {
            return ResponseData.createErrorCodeResponse(ErrorCode.ORDER_NO_PARAM_ERROR.getCode(), "租客子订单编号为空");
        }
        return ResponseData.success();
    }

    /**
     * 平台补贴
     * @param
     * @return
     */
    @AutoDocVersion(version = "管理后台平台补贴修改")
    @AutoDocGroup(group = "管理后台平台补贴修改")
    @AutoDocMethod(description = "平台补贴修改", value = "平台补贴修改",response = ResponseData.class)
    @PostMapping("/cost/platformSubsidy/update")
    public ResponseData<?> updatePlatformSubsidyByRenterOrderNo(@RequestBody PlatformSubsidyVO platformSubsidyVO) {
        if (null == platformSubsidyVO || StringUtils.isBlank(platformSubsidyVO.getRenterOrderNo())) {
            return ResponseData.createErrorCodeResponse(ErrorCode.ORDER_NO_PARAM_ERROR.getCode(), "租客子订单编号为空");
        }
        return ResponseData.success();
    }

    /**
     * 优惠抵扣
     * @param
     * @return
     */
    @AutoDocVersion(version = "管理后台优惠抵扣修改")
    @AutoDocGroup(group = "管理后台优惠抵扣修改")
    @AutoDocMethod(description = "优惠抵扣修改", value = "优惠抵扣修改",response = ResponseData.class)
    @PostMapping("/cost/couponDeduction/update")
    public ResponseData<?> updateCouponDeductionByRenterOrderNo(@RequestBody CouponDeductionVO couponDeductionVO) {
        if (null == couponDeductionVO || StringUtils.isBlank(couponDeductionVO.getRenterOrderNo())) {
            return ResponseData.createErrorCodeResponse(ErrorCode.ORDER_NO_PARAM_ERROR.getCode(), "租客子订单编号为空");
        }
        return ResponseData.success();
    }

    /**
     * 车辆押金
     * @param
     * @return
     */
    @AutoDocVersion(version = "管理后台车辆押金修改")
    @AutoDocGroup(group = "管理后台车辆押金修改")
    @AutoDocMethod(description = "车辆押金修改", value = "车辆押金修改",response = ResponseData.class)
    @PostMapping("/cost/carDeposit/update")
    public ResponseData<?> updateCarDepositByRenterOrderNo(@RequestBody CarDepositVO carDepositVO) {
        if (null == carDepositVO || StringUtils.isBlank(carDepositVO.getRenterOrderNo())) {
            return ResponseData.createErrorCodeResponse(ErrorCode.ORDER_NO_PARAM_ERROR.getCode(), "租客子订单编号为空");
        }
        return ResponseData.success();
    }

    /**
     * 违章押金
     * @param
     * @return
     */
    @AutoDocVersion(version = "管理后台违章押金修改")
    @AutoDocGroup(group = "管理后台违章押金修改")
    @AutoDocMethod(description = "违章押金修改", value = "车辆押金修改",response = ResponseData.class)
    @PostMapping("/cost/vehicleDeposit/update")
    public ResponseData<?> updateVehicleDepositByRenterOrderNo(@RequestBody VehicleDepositVO vehicleDepositVO) {
        if (null == vehicleDepositVO || StringUtils.isBlank(vehicleDepositVO.getRenterOrderNo())) {
            return ResponseData.createErrorCodeResponse(ErrorCode.ORDER_NO_PARAM_ERROR.getCode(), "租客子订单编号为空");
        }
        return ResponseData.success();
    }


    /**
     * 查询租车收益
     * @param ownerInComeReqVO
     * @return
     */
    @AutoDocVersion(version = "管理后台租车收益")
    @AutoDocGroup(group = "管理后台租车收益")
    @AutoDocMethod(description = "租车收益", value = "租车收益",response = OwnerInComeRepVO.class)
    @PostMapping("/inCome/list")
    public ResponseData<?> findRentalIncomeListByOrderNo(@RequestBody OwnerInComeReqVO ownerInComeReqVO) {
        if (null == ownerInComeReqVO || StringUtils.isBlank(ownerInComeReqVO.getOwnerOrderNo())) {
            return ResponseData.createErrorCodeResponse(ErrorCode.ORDER_NO_PARAM_ERROR.getCode(), "租客子订单编号为空");
        }
        return ResponseData.success();
    }







}
