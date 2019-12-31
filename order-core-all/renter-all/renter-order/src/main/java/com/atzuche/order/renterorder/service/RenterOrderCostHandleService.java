package com.atzuche.order.renterorder.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.DepositAmtDTO;
import com.atzuche.order.commons.entity.dto.IllegalDepositAmtDTO;
import com.atzuche.order.commons.enums.account.FreeDepositTypeEnum;
import com.atzuche.order.rentercost.entity.dto.OrderCouponDTO;
import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
import com.atzuche.order.rentercost.service.RenterOrderCostCombineService;
import com.atzuche.order.rentercost.service.RenterOrderSubsidyDetailService;
import com.atzuche.order.rentermem.entity.dto.MemRightCarDepositAmtReqDTO;
import com.atzuche.order.rentermem.entity.dto.MemRightCarDepositAmtRespDTO;
import com.atzuche.order.rentermem.service.RenterMemberRightService;
import com.atzuche.order.renterorder.entity.RenterDepositDetailEntity;
import com.atzuche.order.renterorder.entity.dto.DeductAndSubsidyContextDTO;
import com.atzuche.order.renterorder.mapper.RenterDepositDetailMapper;
import com.atzuche.order.renterorder.vo.RenterOrderCarDepositResVO;
import com.atzuche.order.renterorder.vo.RenterOrderIllegalResVO;
import com.atzuche.order.renterorder.vo.RenterOrderReqVO;
import com.atzuche.order.renterorder.vo.RenterOrderResVO;
import com.atzuche.order.renterorder.vo.owner.OwnerCouponGetAndValidReqVO;
import com.atzuche.order.renterorder.vo.platform.MemAvailCouponRequestVO;
import com.autoyol.auto.coin.service.vo.res.AutoCoinResponseVO;
import com.autoyol.platformcost.model.CarDepositAmtVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 租客订单费用处理类
 *
 * @author pengcheng.fu
 * @date 2019/12/30 16:21
 */

@Service
public class RenterOrderCostHandleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RenterOrderCostHandleService.class);

    @Resource
    private RenterDepositDetailMapper renterDepositDetailMapper;

    @Resource
    private RenterMemberRightService renterMemberRightService;

    @Resource
    private RenterOrderCostCombineService renterOrderCostCombineService;

    @Resource
    private RenterOrderCalCostService renterOrderCalCostService;

    @Resource
    private RenterOrderSubsidyDetailService renterOrderSubsidyDetailService;

    @Resource
    private AutoCoinService autoCoinService;

    /**
     * 车辆押金计算已处理
     *
     * @param renterOrderReqVO 租客订单请求信息
     */
    public RenterOrderCarDepositResVO handleCarDepositAmt(RenterOrderReqVO renterOrderReqVO) {
        DepositAmtDTO depositAmtDTO = new DepositAmtDTO();
        depositAmtDTO.setSurplusPrice(renterOrderReqVO.getCarSurplusPrice());
        depositAmtDTO.setCityCode(Integer.valueOf(renterOrderReqVO.getCityCode()));
        depositAmtDTO.setBrand(renterOrderReqVO.getBrandId());
        depositAmtDTO.setType(renterOrderReqVO.getTypeId());
        depositAmtDTO.setLicenseDay(renterOrderReqVO.getLicenseDay());
        LOGGER.info("车辆押金计算.param is,depositAmtDTO:[{}]", JSON.toJSONString(depositAmtDTO));
        CarDepositAmtVO carDepositAmt = renterOrderCostCombineService.getCarDepositAmtVO(depositAmtDTO);
        LOGGER.info("车辆押金计算.result is,carDepositAmt:[{}]", JSON.toJSONString(carDepositAmt));

        MemRightCarDepositAmtReqDTO memRightCarDepositAmtReqDTO = new MemRightCarDepositAmtReqDTO();
        memRightCarDepositAmtReqDTO.setGuidPrice(renterOrderReqVO.getGuidPrice());
        memRightCarDepositAmtReqDTO.setOriginalDepositAmt(carDepositAmt.getCarDepositAmt());
        memRightCarDepositAmtReqDTO.setRenterMemberRightDTOList(renterOrderReqVO.getRenterMemberRightDTOList());
        LOGGER.info("车辆押金减免计算.param is,memRightCarDepositAmtReqDTO:[{}]", JSON.toJSONString(memRightCarDepositAmtReqDTO));
        MemRightCarDepositAmtRespDTO memRightCarDepositAmtRespDTO =
                renterMemberRightService.carDepositAmt(memRightCarDepositAmtReqDTO);
        LOGGER.info("车辆押金减免计算.result is,memRightCarDepositAmtRespDTO:[{}]", JSON.toJSONString(memRightCarDepositAmtRespDTO));

        RenterOrderCarDepositResVO renterOrderCarDepositResVO = new RenterOrderCarDepositResVO();
        renterOrderCarDepositResVO.setYingfuDepositAmt(carDepositAmt.getCarDepositAmt());
        renterOrderCarDepositResVO.setMemNo(renterOrderReqVO.getMemNo());
        renterOrderCarDepositResVO.setOrderNo(renterOrderReqVO.getOrderNo());
        renterOrderCarDepositResVO.setReductionAmt(memRightCarDepositAmtRespDTO.getReductionDepositAmt());
        renterOrderCarDepositResVO.setFreeDepositType(FreeDepositTypeEnum.getFreeDepositTypeEnumByCode(Integer.valueOf(renterOrderReqVO.getFreeDoubleTypeId())));
        //cashierService.insertRenterDeposit(createOrderRenterDepositReqVO);

        //车辆押金明细入库
        RenterDepositDetailEntity record = new RenterDepositDetailEntity();
        record.setOrderNo(renterOrderReqVO.getOrderNo());
        record.setSuggestTotal(carDepositAmt.getSuggestTotal());
        record.setCarSpecialCoefficient(carDepositAmt.getCarSpecialCoefficient());
        record.setNewCarCoefficient(carDepositAmt.getNewCarCoefficient());
        record.setOriginalDepositAmt(carDepositAmt.getCarDepositAmt());
        record.setReductionDepositAmt(memRightCarDepositAmtRespDTO.getReductionDepositAmt());
        record.setReductionRate(memRightCarDepositAmtRespDTO.getReductionRate());
        renterDepositDetailMapper.insertSelective(record);

        LOGGER.info("租客车辆押金.result is,renterOrderCarDepositResVO:[{}]",
                JSON.toJSONString(renterOrderCarDepositResVO));
        return renterOrderCarDepositResVO;
    }


    /**
     * 违章押金计算已处理
     *
     * @param costBaseDTO      基础数据
     * @param renterOrderReqVO 租客订单请求信息
     */
    public RenterOrderIllegalResVO handleIllegalDepositAmt(CostBaseDTO costBaseDTO, RenterOrderReqVO renterOrderReqVO) {
        IllegalDepositAmtDTO illDTO = new IllegalDepositAmtDTO();
        illDTO.setCostBaseDTO(costBaseDTO);
        illDTO.setCarPlateNum(renterOrderReqVO.getPlateNum());
        illDTO.setCityCode(Integer.valueOf(renterOrderReqVO.getCityCode()));
        Integer illegalDepositAmt = renterOrderCostCombineService.getIllegalDepositAmt(illDTO);
        int realIllegalDepositAmt =
                renterMemberRightService.wzDepositAmt(renterOrderReqVO.getRenterMemberRightDTOList(),
                        illegalDepositAmt);

        RenterOrderIllegalResVO renterOrderIllegalResVO = new RenterOrderIllegalResVO();
        renterOrderIllegalResVO.setOrderNo(renterOrderReqVO.getOrderNo());
        renterOrderIllegalResVO.setFreeDepositType(FreeDepositTypeEnum.getFreeDepositTypeEnumByCode(Integer.valueOf(renterOrderReqVO.getFreeDoubleTypeId())));
        renterOrderIllegalResVO.setMemNo(renterOrderReqVO.getMemNo());
        renterOrderIllegalResVO.setYingfuDepositAmt(realIllegalDepositAmt);
        //cashierService.insertRenterWZDeposit(createOrderRenterIllegalDepositReqVO);
        return renterOrderIllegalResVO;
    }

    /**
     * 送取服务券处理
     *
     * @param context              公共参数
     * @param getCarFeeCouponReqVO 送取服务券请求参数
     */
    public void handleGetCarFeeCoupon(DeductAndSubsidyContextDTO context,
                                      MemAvailCouponRequestVO getCarFeeCouponReqVO) {
        OrderCouponDTO getCarFeeCoupon =
                renterOrderCalCostService.calGetAndReturnSrvCouponDeductInfo(getCarFeeCouponReqVO);
        context.setSrvGetCost(getCarFeeCouponReqVO.getSrvGetCost());
        context.setSrvReturnCost(getCarFeeCouponReqVO.getSrvReturnCost());
        if (null != getCarFeeCoupon) {
            context.setSrvGetCost(0);
            context.setSrvReturnCost(0);
            //记录送取服务券
            getCarFeeCoupon.setOrderNo(context.getOrderNo());
            getCarFeeCoupon.setRenterOrderNo(context.getRenterOrderNo());
            context.getOrderCouponList().add(getCarFeeCoupon);
            //补贴明细
            RenterOrderSubsidyDetailDTO getCarFeeCouponSubsidyInfo =
                    renterOrderSubsidyDetailService.calGetCarFeeCouponSubsidyInfo(Integer.valueOf(context.getMemNo())
                            , getCarFeeCoupon);
            getCarFeeCouponSubsidyInfo.setOrderNo(context.getOrderNo());
            getCarFeeCouponSubsidyInfo.setRenterOrderNo(context.getRenterOrderNo());
            context.getOrderSubsidyDetailList().add(getCarFeeCouponSubsidyInfo);
        }

    }

    /**
     * 车主券处理
     *
     * @param context                     公共参数
     * @param renterOrderResVO            租客订单返回信息
     * @param ownerCouponGetAndValidReqVO 车主券请求参数
     */
    public void handleOwnerCoupon(DeductAndSubsidyContextDTO context,
                                  OwnerCouponGetAndValidReqVO ownerCouponGetAndValidReqVO, RenterOrderResVO renterOrderResVO) {

        OrderCouponDTO ownerCoupon = renterOrderCalCostService.calOwnerCouponDeductInfo(ownerCouponGetAndValidReqVO);
        if (null != ownerCoupon) {
            renterOrderResVO.setOwnerCoupon(ownerCoupon);
            //重置剩余租金
            int disAmt = null == ownerCoupon.getAmount() ? 0 : ownerCoupon.getAmount();
            context.setSurplusRentAmt(context.getSurplusRentAmt() - disAmt);
            //记录车主券
            ownerCoupon.setOrderNo(context.getOrderNo());
            ownerCoupon.setRenterOrderNo(context.getRenterOrderNo());
            context.getOrderCouponList().add(ownerCoupon);
            //补贴明细
            RenterOrderSubsidyDetailDTO ownerCouponSubsidyInfo =
                    renterOrderSubsidyDetailService.calOwnerCouponSubsidyInfo(Integer.valueOf(context.getMemNo()),
                            ownerCoupon);
            ownerCouponSubsidyInfo.setOrderNo(context.getOrderNo());
            ownerCouponSubsidyInfo.setRenterOrderNo(context.getRenterOrderNo());
            context.getOrderSubsidyDetailList().add(ownerCouponSubsidyInfo);
        }

    }

    /**
     * 限时红包处理
     *
     * @param context    公共参数
     * @param reductiAmt 限时红包面额
     */
    public void handleLimitRed(DeductAndSubsidyContextDTO context, int reductiAmt) {
        RenterOrderSubsidyDetailDTO limitRedSubsidyInfo =
                renterOrderSubsidyDetailService.calLimitRedSubsidyInfo(Integer.valueOf(context.getMemNo()),
                        context.getSurplusRentAmt(), reductiAmt);
        if (null != limitRedSubsidyInfo) {
            //重置剩余租金
            int realLimitReductiAmt = null == limitRedSubsidyInfo.getSubsidyAmount() ? 0 : limitRedSubsidyInfo.getSubsidyAmount();
            context.setSurplusRentAmt(context.getSurplusRentAmt() - realLimitReductiAmt);
            //补贴明细
            limitRedSubsidyInfo.setOrderNo(context.getOrderNo());
            limitRedSubsidyInfo.setRenterOrderNo(context.getRenterOrderNo());
            context.getOrderSubsidyDetailList().add(limitRedSubsidyInfo);
        }
    }

    /**
     * 平台优惠券处理
     *
     * @param context             公共参数
     * @param platformCouponReqVO 平台优惠券请求参数
     */
    public void handlePlatformCoupon(DeductAndSubsidyContextDTO context, MemAvailCouponRequestVO platformCouponReqVO) {
        platformCouponReqVO.setRentAmt(context.getSurplusRentAmt());
        platformCouponReqVO.setSrvReturnCost(context.getSrvGetCost());
        platformCouponReqVO.setSrvGetCost(context.getSrvReturnCost());
        OrderCouponDTO platfromCoupon = renterOrderCalCostService.calPlatformCouponDeductInfo(platformCouponReqVO);
        if (null != platfromCoupon) {
            //重置剩余租金
            int disAmt = null == platfromCoupon.getAmount() ? 0 : platfromCoupon.getAmount();
            context.setSurplusRentAmt(context.getSurplusRentAmt() - disAmt);
            //记录平台券
            platfromCoupon.setOrderNo(context.getOrderNo());
            platfromCoupon.setRenterOrderNo(context.getRenterOrderNo());
            context.getOrderCouponList().add(platfromCoupon);
            //补贴明细
            RenterOrderSubsidyDetailDTO platformCouponSubsidyInfo =
                    renterOrderSubsidyDetailService.calPlatformCouponSubsidyInfo(Integer.valueOf(context.getMemNo()), platfromCoupon);
            platformCouponSubsidyInfo.setOrderNo(context.getOrderNo());
            platformCouponSubsidyInfo.setRenterOrderNo(context.getRenterOrderNo());
            context.getOrderSubsidyDetailList().add(platformCouponSubsidyInfo);
        }

    }

    /**
     * 凹凸币处理
     *
     * @param context     公共参数
     * @param memNo       租客会员注册号
     * @param useAutoCoin 使用凹凸币标识
     */
    public void handleAutoCoin(DeductAndSubsidyContextDTO context, Integer memNo, Integer useAutoCoin) {
        AutoCoinResponseVO crmCustPoint = autoCoinService.getCrmCustPoint(memNo);
        RenterOrderSubsidyDetailDTO autoCoinSubsidyInfo = renterOrderSubsidyDetailService.calAutoCoinSubsidyInfo(crmCustPoint,
                context.getOriginalRentAmt(), context.getSurplusRentAmt(), useAutoCoin);
        if (null != autoCoinSubsidyInfo) {
            autoCoinSubsidyInfo.setOrderNo(context.getOrderNo());
            autoCoinSubsidyInfo.setRenterOrderNo(context.getRenterOrderNo());
            context.getOrderSubsidyDetailList().add(autoCoinSubsidyInfo);
        }

    }

}
