package com.atzuche.order.admin.controller;

import com.atzuche.order.admin.vo.req.cost.OwnerInComeReqVO;
import com.atzuche.order.admin.vo.req.cost.RentalCostReqVO;
import com.atzuche.order.admin.vo.resp.cost.*;
import com.atzuche.order.admin.vo.resp.income.OwnerInComeRepVO;
import com.atzuche.order.admin.vo.resp.income.OwnerToPlatFormVO;
import com.atzuche.order.admin.vo.resp.income.PlatFormToOwnerVO;
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
    @PostMapping("/baseCost/update")
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
    @PostMapping("/platformSubsidy/update")
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
    @PostMapping("/couponDeduction/update")
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
    @PostMapping("/carDeposit/update")
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
    @PostMapping("/vehicleDeposit/update")
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

    /**
     * 平台给车主的补贴
     * @param ownerInComeReqVO
     * @return
     */
    @AutoDocVersion(version = "管理后台平台给车主的补贴")
    @AutoDocGroup(group = "管理后台平台给车主的补贴")
    @AutoDocMethod(description = "平台给车主的补贴", value = "平台给车主的补贴",response = PlatFormToOwnerVO.class)
    @PostMapping("/platFormToOwner/list")
    public ResponseData<?> findPlatFormToOwnerListByOrderNo(@RequestBody OwnerInComeReqVO ownerInComeReqVO) {
        if (null == ownerInComeReqVO || StringUtils.isBlank(ownerInComeReqVO.getOwnerOrderNo())) {
            return ResponseData.createErrorCodeResponse(ErrorCode.ORDER_NO_PARAM_ERROR.getCode(), "租客子订单编号为空");
        }
        return ResponseData.success();
    }

    /**
     * 车主需支付给平台的费用
     * @param ownerInComeReqVO
     * @return
     */
    @AutoDocVersion(version = "管理后台车主需支付给平台的费用")
    @AutoDocGroup(group = "管理后台平台车主需支付给平台的费用")
    @AutoDocMethod(description = "车主需支付给平台的费用", value = "车主需支付给平台的费用",response = OwnerToPlatFormVO.class)
    @PostMapping("/ownerToPlatForm/list")
    public ResponseData<?> findOwnerToPlatFormListByOrderNo(@RequestBody OwnerInComeReqVO ownerInComeReqVO) {
        if (null == ownerInComeReqVO || StringUtils.isBlank(ownerInComeReqVO.getOwnerOrderNo())) {
            return ResponseData.createErrorCodeResponse(ErrorCode.ORDER_NO_PARAM_ERROR.getCode(), "租客子订单编号为空");
        }
        return ResponseData.success();
    }

    /**
     * 服务费
     * @param ownerInComeReqVO
     * @return
     */
    @AutoDocVersion(version = "管理后台服务费")
    @AutoDocGroup(group = "管理后台平台服务费")
    @AutoDocMethod(description = "服务费", value = "服务费",response = ResponseData.class)
    @PostMapping("/serviceCharge/list")
    public ResponseData<?> findServiceChargeListByOrderNo(@RequestBody OwnerInComeReqVO ownerInComeReqVO) {
        if (null == ownerInComeReqVO || StringUtils.isBlank(ownerInComeReqVO.getOwnerOrderNo())) {
            return ResponseData.createErrorCodeResponse(ErrorCode.ORDER_NO_PARAM_ERROR.getCode(), "租客子订单编号为空");
        }
        return ResponseData.success();
    }

    /**
     * 租客车主互相调价
     * @param ownerInComeReqVO
     * @return
     */
    @AutoDocVersion(version = "管理后台租客车主互相调价")
    @AutoDocGroup(group = "管理后台平台租客车主互相调价")
    @AutoDocMethod(description = "租客车主互相调价", value = "租客车主互相调价",response = ResponseData.class)
    @PostMapping("/priceAdjustment/list")
    public ResponseData<?> findPriceAdjustmentListByOrderNo(@RequestBody OwnerInComeReqVO ownerInComeReqVO) {
        if (null == ownerInComeReqVO || StringUtils.isBlank(ownerInComeReqVO.getOwnerOrderNo())) {
            return ResponseData.createErrorCodeResponse(ErrorCode.ORDER_NO_PARAM_ERROR.getCode(), "租客子订单编号为空");
        }
        return ResponseData.success();
    }

    /**
     * 违约罚金
     * @param ownerInComeReqVO
     * @return
     */
    @AutoDocVersion(version = "管理后台违约罚金")
    @AutoDocGroup(group = "管理后台平台违约罚金")
    @AutoDocMethod(description = "违约罚金", value = "违约罚金",response = ResponseData.class)
    @PostMapping("/falsifyAmt/list")
    public ResponseData<?> findfalsifyAmtListByOrderNo(@RequestBody OwnerInComeReqVO ownerInComeReqVO) {
        if (null == ownerInComeReqVO || StringUtils.isBlank(ownerInComeReqVO.getOwnerOrderNo())) {
            return ResponseData.createErrorCodeResponse(ErrorCode.ORDER_NO_PARAM_ERROR.getCode(), "租客子订单编号为空");
        }
        return ResponseData.success();
    }

    /**
     * 车主租金
     * @param ownerInComeReqVO
     * @return
     */
    @AutoDocVersion(version = "管理后台车主租金")
    @AutoDocGroup(group = "管理后台平台车主租金")
    @AutoDocMethod(description = "车主租金", value = "车主租金",response = ResponseData.class)
    @PostMapping("/ownerAmt/list")
    public ResponseData<?> findOwnerAmtListByOrderNo(@RequestBody OwnerInComeReqVO ownerInComeReqVO) {
        if (null == ownerInComeReqVO || StringUtils.isBlank(ownerInComeReqVO.getOwnerOrderNo())) {
            return ResponseData.createErrorCodeResponse(ErrorCode.ORDER_NO_PARAM_ERROR.getCode(), "租客子订单编号为空");
        }
        return ResponseData.success();
    }

    /**
     *减免明细
     * @param rentalCostReqVO
     * @return
     */
    @AutoDocVersion(version = "管理后台减免明细")
    @AutoDocGroup(group = "管理后台减免明细")
    @AutoDocMethod(description = "减免明细", value = "减免明细",response = ResponseData.class)
    @PostMapping("/waiverDetails/list")
    public ResponseData<?> findWaiverDetailsListByOrderNo(@RequestBody RentalCostReqVO rentalCostReqVO) {
        if (null == rentalCostReqVO || StringUtils.isBlank(rentalCostReqVO.getRenterOrderNo())) {
            return ResponseData.createErrorCodeResponse(ErrorCode.ORDER_NO_PARAM_ERROR.getCode(), "租客子订单编号为空");
        }
        return ResponseData.success();
    }

    /**
     *平台给租客的补贴
     * @param rentalCostReqVO
     * @return
     */
    @AutoDocVersion(version = "管理后台平台给租客的补贴")
    @AutoDocGroup(group = "管理后台平台给租客的补贴")
    @AutoDocMethod(description = "平台给租客的补贴", value = "平台给租客的补贴",response = ResponseData.class)
    @PostMapping("/platFormToRenter/list")
    public ResponseData<?> findPlatFormToRenterListByOrderNo(@RequestBody RentalCostReqVO rentalCostReqVO) {
        if (null == rentalCostReqVO || StringUtils.isBlank(rentalCostReqVO.getRenterOrderNo())) {
            return ResponseData.createErrorCodeResponse(ErrorCode.ORDER_NO_PARAM_ERROR.getCode(), "租客子订单编号为空");
        }
        return ResponseData.success();
    }

    /**
     *配送费用
     * @param rentalCostReqVO
     * @return
     */
    @AutoDocVersion(version = "管理后台平台给租客的补贴")
    @AutoDocGroup(group = "管理后台平台给租客的补贴")
    @AutoDocMethod(description = "平台给租客的补贴", value = "平台给租客的补贴",response = DistributionCostVO.class)
    @PostMapping("/distributionCost/list")
    public ResponseData<?> findDistributionCostListByOrderNo(@RequestBody RentalCostReqVO rentalCostReqVO) {
        if (null == rentalCostReqVO || StringUtils.isBlank(rentalCostReqVO.getRenterOrderNo())) {
            return ResponseData.createErrorCodeResponse(ErrorCode.ORDER_NO_PARAM_ERROR.getCode(), "租客子订单编号为空");
        }
        return ResponseData.success();
    }


    /**
     *租客租金明细
     * @param rentalCostReqVO
     * @return
     */
    @AutoDocVersion(version = "管理后台租客租金")
    @AutoDocGroup(group = "管理后台租客租金")
    @AutoDocMethod(description = "租客租金", value = "租客租金",response = ResponseData.class)
    @PostMapping("/tenantRent/list")
    public ResponseData<?> findTenantRentListByOrderNo(@RequestBody RentalCostReqVO rentalCostReqVO) {
        if (null == rentalCostReqVO || StringUtils.isBlank(rentalCostReqVO.getRenterOrderNo())) {
            return ResponseData.createErrorCodeResponse(ErrorCode.ORDER_NO_PARAM_ERROR.getCode(), "租客子订单编号为空");
        }
        return ResponseData.success();
    }


    /**
     *附加驾驶员险
     * @param rentalCostReqVO
     * @return
     */
    @AutoDocVersion(version = "管理后台附加驾驶员险")
    @AutoDocGroup(group = "管理后台附加驾驶员险")
    @AutoDocMethod(description = "附加驾驶员险", value = "附加驾驶员险",response = AdditionalDriverInsuranceVO.class)
    @PostMapping("/additionalDriverInsurance/list")
    public ResponseData<?> findAdditionalDriverInsuranceByOrderNo(@RequestBody RentalCostReqVO rentalCostReqVO) {
        if (null == rentalCostReqVO || StringUtils.isBlank(rentalCostReqVO.getRenterOrderNo())) {
            return ResponseData.createErrorCodeResponse(ErrorCode.ORDER_NO_PARAM_ERROR.getCode(), "租客子订单编号为空");
        }
        return ResponseData.success();
    }

    /**
     *附加驾驶员险
     * @param additionalDriverInsuranceVO
     * @return
     */
    @AutoDocVersion(version = "管理后台附加驾驶员险")
    @AutoDocGroup(group = "管理后台附加驾驶员险")
    @AutoDocMethod(description = "新增附加驾驶员险", value = "新增附加驾驶员险",response = ResponseData.class)
    @PostMapping("/additionalDriverInsurance/add")
    public ResponseData<?> insertAdditionalDriverInsuranceByOrderNo(@RequestBody AdditionalDriverInsuranceVO additionalDriverInsuranceVO) {
        if (null == additionalDriverInsuranceVO || StringUtils.isBlank(additionalDriverInsuranceVO.getRenterOrderNo())) {
            return ResponseData.createErrorCodeResponse(ErrorCode.ORDER_NO_PARAM_ERROR.getCode(), "租客子订单编号为空");
        }
        return ResponseData.success();
    }

    /**
     *违约罚金
     * @param rentalCostReqVO
     * @return
     */
    @AutoDocVersion(version = "管理后台违约罚金")
    @AutoDocGroup(group = "管理后台违约罚金")
    @AutoDocMethod(description = "违约罚金", value = "违约罚金",response = PenaltyContractVO.class)
    @PostMapping("/penaltyContract/list")
    public ResponseData<?> findPenaltyContractByOrderNo(@RequestBody RentalCostReqVO rentalCostReqVO) {
        if (null == rentalCostReqVO || StringUtils.isBlank(rentalCostReqVO.getRenterOrderNo())) {
            return ResponseData.createErrorCodeResponse(ErrorCode.ORDER_NO_PARAM_ERROR.getCode(), "租客子订单编号为空");
        }
        return ResponseData.success();
    }

    /**
     *违约罚金
     * @param penaltyContractVO
     * @return
     */
    @AutoDocVersion(version = "管理后台违约罚金")
    @AutoDocGroup(group = "管理后台违约罚金")
    @AutoDocMethod(description = "违约罚金", value = "违约罚金",response = ResponseData.class)
    @PostMapping("/penaltyContract/update")
    public ResponseData<?> updatePenaltyContractByOrderNo(@RequestBody PenaltyContractVO penaltyContractVO) {
        if (null == penaltyContractVO || StringUtils.isBlank(penaltyContractVO.getRenterOrderNo())) {
            return ResponseData.createErrorCodeResponse(ErrorCode.ORDER_NO_PARAM_ERROR.getCode(), "租客子订单编号为空");
        }
        return ResponseData.success();
    }

    /**
     *租客车主互相调价
     * @param priceAdjustmentVO
     * @return
     */
    @AutoDocVersion(version = "管理后台租客车主互相调价")
    @AutoDocGroup(group = "管理后台租客车主互相调价")
    @AutoDocMethod(description = "租客车主互相调价", value = "租客车主互相调价",response = ResponseData.class)
    @PostMapping("/priceAdjustment/update")
    public ResponseData<?> updatePriceAdjustmentByOrderNo(@RequestBody PriceAdjustmentVO priceAdjustmentVO) {
        if (null == priceAdjustmentVO || StringUtils.isBlank(priceAdjustmentVO.getRenterOrderNo())) {
            return ResponseData.createErrorCodeResponse(ErrorCode.ORDER_NO_PARAM_ERROR.getCode(), "租客子订单编号为空");
        }
        return ResponseData.success();
    }

    /**
     *租客车主互相调价
     * @param rentalCostReqVO
     * @return
     */
    @AutoDocVersion(version = "管理后台租客车主互相调价")
    @AutoDocGroup(group = "管理后台租客车主互相调价")
    @AutoDocMethod(description = "租客车主互相调价", value = "租客车主互相调价",response = PriceAdjustmentVO.class)
    @PostMapping("/renterPriceAdjustment/list")
    public ResponseData<?> findPriceAdjustmentByOrderNo(@RequestBody RentalCostReqVO rentalCostReqVO) {
        if (null == rentalCostReqVO || StringUtils.isBlank(rentalCostReqVO.getRenterOrderNo())) {
            return ResponseData.createErrorCodeResponse(ErrorCode.ORDER_NO_PARAM_ERROR.getCode(), "租客子订单编号为空");
        }
        return ResponseData.success();
    }

    /**
     * 租客需支付给平台的费用
     * @param rentalCostReqVO
     * @return
     */
    @AutoDocVersion(version = "管理后台租客需支付给平台的费用")
    @AutoDocGroup(group = "管理后台平台租客需支付给平台的费用")
    @AutoDocMethod(description = "租客需支付给平台的费用", value = "租客需支付给平台的费用",response = OwnerToPlatFormVO.class)
    @PostMapping("/renterToPlatForm/list")
    public ResponseData<?> findRenterToPlatFormListByOrderNo(@RequestBody RentalCostReqVO rentalCostReqVO) {
        if (null == rentalCostReqVO || StringUtils.isBlank(rentalCostReqVO.getRenterOrderNo())) {
            return ResponseData.createErrorCodeResponse(ErrorCode.ORDER_NO_PARAM_ERROR.getCode(), "租客子订单编号为空");
        }
        return ResponseData.success();
    }

    /**
     * 租客需支付给平台的费用
     * @param ownerToPlatFormVO
     * @return
     */
    @AutoDocVersion(version = "管理后台租客需支付给平台的费用")
    @AutoDocGroup(group = "管理后台平台租客需支付给平台的费用")
    @AutoDocMethod(description = "租客需支付给平台的费用", value = "租客需支付给平台的费用",response = OwnerToPlatFormVO.class)
    @PostMapping("/ownerToPlatForm/update")
    public ResponseData<?> updateRenterToPlatFormListByOrderNo(@RequestBody OwnerToPlatFormVO ownerToPlatFormVO) {
        if (null == ownerToPlatFormVO || StringUtils.isBlank(ownerToPlatFormVO.getRenterOrderNo())) {
            return ResponseData.createErrorCodeResponse(ErrorCode.ORDER_NO_PARAM_ERROR.getCode(), "租客子订单编号为空");
        }
        return ResponseData.success();
    }







}
