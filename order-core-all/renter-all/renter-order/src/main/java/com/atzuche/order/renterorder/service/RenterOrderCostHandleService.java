package com.atzuche.order.renterorder.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.coin.service.AccountRenterCostCoinService;
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
import org.apache.commons.lang3.StringUtils;
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
    private AccountRenterCostCoinService accountRenterCostCoinService;

    /**
     * 车辆押金计算及处理(包含保存方法)
     *
     * @param renterOrderReqVO 租客订单请求信息
     * @return RenterOrderCarDepositResVO
     */
    public RenterOrderCarDepositResVO handleCarDepositAmt(RenterOrderReqVO renterOrderReqVO) {
        DepositAmtDTO depositAmtDTO = new DepositAmtDTO();
        depositAmtDTO.setSurplusPrice(renterOrderReqVO.getCarSurplusPrice()==null ? renterOrderReqVO.getGuidPrice():renterOrderReqVO.getCarSurplusPrice());
        depositAmtDTO.setCityCode(Integer.valueOf(renterOrderReqVO.getCityCode()));
        depositAmtDTO.setBrand(renterOrderReqVO.getBrandId());
        depositAmtDTO.setType(renterOrderReqVO.getTypeId());
        depositAmtDTO.setLicenseDay(renterOrderReqVO.getLicenseDay());
        LOGGER.info("车辆押金计算A.param is,depositAmtDTO:[{}]", JSON.toJSONString(depositAmtDTO));
        CarDepositAmtVO carDepositAmt = renterOrderCostCombineService.getCarDepositAmtVO(depositAmtDTO);
        LOGGER.info("车辆押金计算A.result is,carDepositAmt:[{}]", JSON.toJSONString(carDepositAmt));

        MemRightCarDepositAmtReqDTO memRightCarDepositAmtReqDTO = new MemRightCarDepositAmtReqDTO();
        memRightCarDepositAmtReqDTO.setGuidPrice(renterOrderReqVO.getGuidPrice());
        memRightCarDepositAmtReqDTO.setOriginalDepositAmt(null == carDepositAmt.getCarDepositAmt() ? 0 :
                Math.abs(carDepositAmt.getCarDepositAmt()));
        memRightCarDepositAmtReqDTO.setRenterMemberRightDTOList(renterOrderReqVO.getRenterMemberRightDTOList());
        memRightCarDepositAmtReqDTO.setOrderCategory(renterOrderReqVO.getOrderCategory());
        LOGGER.info("车辆押金减免计算A.param is,memRightCarDepositAmtReqDTO:[{}]",
                JSON.toJSONString(memRightCarDepositAmtReqDTO));
        MemRightCarDepositAmtRespDTO memRightCarDepositAmtRespDTO =
                renterMemberRightService.carDepositAmt(memRightCarDepositAmtReqDTO);
        LOGGER.info("车辆押金减免计算A.result is,memRightCarDepositAmtRespDTO:[{}]",
                JSON.toJSONString(memRightCarDepositAmtRespDTO));

        RenterOrderCarDepositResVO renterOrderCarDepositResVO = new RenterOrderCarDepositResVO();
        renterOrderCarDepositResVO.setMemNo(renterOrderReqVO.getMemNo());
        renterOrderCarDepositResVO.setOrderNo(renterOrderReqVO.getOrderNo());
        renterOrderCarDepositResVO.setReductionAmt(null == memRightCarDepositAmtRespDTO.getReductionDepositAmt() ? 0
                : Math.abs(memRightCarDepositAmtRespDTO.getReductionDepositAmt()));
        renterOrderCarDepositResVO.setYingfuDepositAmt(-(memRightCarDepositAmtRespDTO.getOriginalDepositAmt() - renterOrderCarDepositResVO.getReductionAmt()));
        renterOrderCarDepositResVO.setFreeDepositType(FreeDepositTypeEnum.getFreeDepositTypeEnumByCode(Integer.valueOf(renterOrderReqVO.getFreeDoubleTypeId())));
        renterOrderCarDepositResVO.setReductionRate(Double.valueOf(memRightCarDepositAmtRespDTO.getReductionRate() * 100).intValue());

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

        LOGGER.info("租客车辆押金A.result is,renterOrderCarDepositResVO:[{}]",
                JSON.toJSONString(renterOrderCarDepositResVO));
        return renterOrderCarDepositResVO;
    }


    /**
     * 车辆押金计算及处理(不保存)
     *
     * @param renterOrderReqVO 租客订单请求信息
     * @return RenterOrderCarDepositResVO
     */
    public RenterOrderCarDepositResVO handleCarDepositAmtNotSave(RenterOrderReqVO renterOrderReqVO) {
        DepositAmtDTO depositAmtDTO = new DepositAmtDTO();
        depositAmtDTO.setSurplusPrice(renterOrderReqVO.getCarSurplusPrice()==null ? renterOrderReqVO.getGuidPrice():renterOrderReqVO.getCarSurplusPrice());
        depositAmtDTO.setCityCode(Integer.valueOf(renterOrderReqVO.getCityCode()));
        depositAmtDTO.setBrand(renterOrderReqVO.getBrandId());
        depositAmtDTO.setType(renterOrderReqVO.getTypeId());
        depositAmtDTO.setLicenseDay(renterOrderReqVO.getLicenseDay());
        LOGGER.info("车辆押金计算B.param is,depositAmtDTO:[{}]", JSON.toJSONString(depositAmtDTO));
        CarDepositAmtVO carDepositAmt = renterOrderCostCombineService.getCarDepositAmtVO(depositAmtDTO);
        LOGGER.info("车辆押金计算B.result is,carDepositAmt:[{}]", JSON.toJSONString(carDepositAmt));

        MemRightCarDepositAmtReqDTO memRightCarDepositAmtReqDTO = new MemRightCarDepositAmtReqDTO();
        memRightCarDepositAmtReqDTO.setGuidPrice(renterOrderReqVO.getGuidPrice());
        memRightCarDepositAmtReqDTO.setOriginalDepositAmt(null == carDepositAmt.getCarDepositAmt() ? 0 :
                Math.abs(carDepositAmt.getCarDepositAmt()));
        memRightCarDepositAmtReqDTO.setRenterMemberRightDTOList(renterOrderReqVO.getRenterMemberRightDTOList());
        LOGGER.info("车辆押金减免计算B.param is,memRightCarDepositAmtReqDTO:[{}]",
                JSON.toJSONString(memRightCarDepositAmtReqDTO));
        MemRightCarDepositAmtRespDTO memRightCarDepositAmtRespDTO =
                renterMemberRightService.carDepositAmt(memRightCarDepositAmtReqDTO);
        LOGGER.info("车辆押金减免计算B.result is,memRightCarDepositAmtRespDTO:[{}]",
                JSON.toJSONString(memRightCarDepositAmtRespDTO));

        RenterOrderCarDepositResVO renterOrderCarDepositResVO = new RenterOrderCarDepositResVO();
        renterOrderCarDepositResVO.setMemNo(renterOrderReqVO.getMemNo());
        renterOrderCarDepositResVO.setOrderNo(renterOrderReqVO.getOrderNo());
        renterOrderCarDepositResVO.setReductionAmt(null == memRightCarDepositAmtRespDTO.getReductionDepositAmt() ? 0
                : Math.abs(memRightCarDepositAmtRespDTO.getReductionDepositAmt()));
        renterOrderCarDepositResVO.setYingfuDepositAmt(-(memRightCarDepositAmtRespDTO.getOriginalDepositAmt() - renterOrderCarDepositResVO.getReductionAmt()));
        renterOrderCarDepositResVO.setFreeDepositType(StringUtils.isNotBlank(renterOrderReqVO.getFreeDoubleTypeId()) ?
                FreeDepositTypeEnum.getFreeDepositTypeEnumByCode(Integer.valueOf(renterOrderReqVO.getFreeDoubleTypeId())) : null);
        renterOrderCarDepositResVO.setReductionRate(Double.valueOf(memRightCarDepositAmtRespDTO.getReductionRate() * 100).intValue());

        LOGGER.info("租客车辆押金B.result is,renterOrderCarDepositResVO:[{}]",
                JSON.toJSONString(renterOrderCarDepositResVO));
        return renterOrderCarDepositResVO;
    }


    /**
     * 违章押金处理
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
                        illegalDepositAmt,renterOrderReqVO.getOrderCategory());

        LOGGER.info("违章押金计算结果.result is, illegalDepositAmt:[{}],realIllegalDepositAmt:[{}]", illegalDepositAmt,
                realIllegalDepositAmt);
        RenterOrderIllegalResVO renterOrderIllegalResVO = new RenterOrderIllegalResVO();
        renterOrderIllegalResVO.setOrderNo(renterOrderReqVO.getOrderNo());
        renterOrderIllegalResVO.setFreeDepositType(StringUtils.isNotBlank(renterOrderReqVO.getFreeDoubleTypeId()) ?
                FreeDepositTypeEnum.getFreeDepositTypeEnumByCode(Integer.valueOf(renterOrderReqVO.getFreeDoubleTypeId())) : null);
        renterOrderIllegalResVO.setMemNo(renterOrderReqVO.getMemNo());
        renterOrderIllegalResVO.setYingfuDepositAmt(-Math.abs(realIllegalDepositAmt));
        LOGGER.info("违章押金处理.result is, renterOrderIllegalResVO:[{}]", renterOrderIllegalResVO);
        return renterOrderIllegalResVO;
    }

    /**
     * 送取服务券处理
     *
     * @param context              公共参数
     * @param getCarFeeCouponReqVO 送取服务券请求参数
     * @return boolean
     */
    public boolean handleGetCarFeeCoupon(DeductAndSubsidyContextDTO context,
                                         MemAvailCouponRequestVO getCarFeeCouponReqVO) {
        LOGGER.info("送取服务券处理.param is, context:[{}],getCarFeeCouponReqVO:[{}]",
                JSON.toJSONString(context), JSON.toJSONString(getCarFeeCouponReqVO));
        OrderCouponDTO getCarFeeCoupon =
                renterOrderCalCostService.calGetAndReturnSrvCouponDeductInfo(getCarFeeCouponReqVO);
        LOGGER.info("送取服务券处理-->优惠券信息.result is, getCarFeeCoupon:[{}]", JSON.toJSONString(getCarFeeCoupon));
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
            LOGGER.info("送取服务券处理-->优惠券补贴信息.result is, getCarFeeCouponSubsidyInfo:[{}]", JSON.toJSONString(getCarFeeCouponSubsidyInfo));
            return true;
        }

        return false;
    }

    /**
     * 车主券处理
     *
     * @param context                     公共参数
     * @param renterOrderResVO            租客订单返回信息
     * @param ownerCouponGetAndValidReqVO 车主券请求参数
     * @return boolean
     */
    public boolean handleOwnerCoupon(DeductAndSubsidyContextDTO context,
                                     OwnerCouponGetAndValidReqVO ownerCouponGetAndValidReqVO, RenterOrderResVO renterOrderResVO) {

        LOGGER.info("车主券处理.param is, context:[{}],ownerCouponGetAndValidReqVO:[{}],renterOrderResVO:[{}]",
                JSON.toJSONString(context), JSON.toJSONString(ownerCouponGetAndValidReqVO), JSON.toJSONString(renterOrderResVO));
        OrderCouponDTO ownerCoupon = renterOrderCalCostService.calOwnerCouponDeductInfo(ownerCouponGetAndValidReqVO);
        LOGGER.info("车主券处理-->优惠券信息.result is, ownerCoupon:[{}]", JSON.toJSONString(ownerCoupon));
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

            LOGGER.info("车主券处理-->优惠券补贴信息.result is, ownerCouponSubsidyInfo:[{}]",
                    JSON.toJSONString(ownerCouponSubsidyInfo));
            return true;
        }

        return false;
    }

    /**
     * 限时红包处理
     *
     * @param context    公共参数
     * @param reductiAmt 限时红包面额
     * @return boolean
     */
    public boolean handleLimitRed(DeductAndSubsidyContextDTO context, int reductiAmt) {
        LOGGER.info("限时红包处理.param is, context:[{}],reductiAmt:[{}]", JSON.toJSONString(context), reductiAmt);
        RenterOrderSubsidyDetailDTO limitRedSubsidyInfo =
                renterOrderSubsidyDetailService.calLimitRedSubsidyInfo(Integer.valueOf(context.getMemNo()),
                        context.getSurplusRentAmt(), reductiAmt);

        LOGGER.info("限时红包处理-->优惠券补贴信息.result is, limitRedSubsidyInfo:[{}]", JSON.toJSONString(limitRedSubsidyInfo));
        if (null != limitRedSubsidyInfo) {
            //重置剩余租金
            int realLimitReductiAmt = null == limitRedSubsidyInfo.getSubsidyAmount() ? 0 : limitRedSubsidyInfo.getSubsidyAmount();
            context.setSurplusRentAmt(context.getSurplusRentAmt() - realLimitReductiAmt);
            //补贴明细
            limitRedSubsidyInfo.setOrderNo(context.getOrderNo());
            limitRedSubsidyInfo.setRenterOrderNo(context.getRenterOrderNo());
            context.getOrderSubsidyDetailList().add(limitRedSubsidyInfo);

            return true;
        }

        return false;
    }

    /**
     * 平台优惠券处理
     *
     * @param context             公共参数
     * @param platformCouponReqVO 平台优惠券请求参数
     * @return boolean
     */
    public boolean handlePlatformCoupon(DeductAndSubsidyContextDTO context, MemAvailCouponRequestVO platformCouponReqVO) {
        LOGGER.info("平台优惠券处理.param is, context:[{}],platformCouponReqVO:[{}]", JSON.toJSONString(context), JSON.toJSONString(platformCouponReqVO));

        platformCouponReqVO.setRentAmt(context.getSurplusRentAmt());
        platformCouponReqVO.setSrvReturnCost(context.getSrvGetCost());
        platformCouponReqVO.setSrvGetCost(context.getSrvReturnCost());
        OrderCouponDTO platfromCoupon = renterOrderCalCostService.calPlatformCouponDeductInfo(platformCouponReqVO);
        LOGGER.info("平台优惠券处理-->优惠券信息.result is, platfromCoupon:[{}]", JSON.toJSONString(platfromCoupon));
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

            LOGGER.info("平台优惠券处理-->优惠券补贴信息.result is, platformCouponSubsidyInfo:[{}]", JSON.toJSONString(platformCouponSubsidyInfo));
            return true;
        }

        return false;
    }

    /**
     * 凹凸币处理
     *
     * @param context     公共参数
     * @param memNo       租客会员注册号
     * @param useAutoCoin 使用凹凸币标识
     * @return int
     */
    public int handleAutoCoin(DeductAndSubsidyContextDTO context, String memNo, Integer useAutoCoin) {
        LOGGER.info("凹凸币处理.param is, context:[{}],memNo:[{}],useAutoCoin:[{}]", JSON.toJSONString(context), memNo, useAutoCoin);
        int pointValue = accountRenterCostCoinService.getUserCoinTotalAmt(memNo);
        AutoCoinResponseVO crmCustPoint = new AutoCoinResponseVO();
        crmCustPoint.setMemNo(Integer.valueOf(memNo));
        crmCustPoint.setPointValue(pointValue);
        RenterOrderSubsidyDetailDTO autoCoinSubsidyInfo = renterOrderSubsidyDetailService.calAutoCoinSubsidyInfo(crmCustPoint,
                context.getOriginalRentAmt(), context.getSurplusRentAmt(), useAutoCoin);
        LOGGER.info("凹凸币处理-->凹凸币补贴信息.result is, autoCoinSubsidyInfo:[{}]", JSON.toJSONString(autoCoinSubsidyInfo));
        if (null != autoCoinSubsidyInfo) {
            autoCoinSubsidyInfo.setOrderNo(context.getOrderNo());
            autoCoinSubsidyInfo.setRenterOrderNo(context.getRenterOrderNo());
            context.getOrderSubsidyDetailList().add(autoCoinSubsidyInfo);
            return null == autoCoinSubsidyInfo.getSubsidyAmount() ? 0 : autoCoinSubsidyInfo.getSubsidyAmount();
        }

        return 0;
    }

}
