package com.atzuche.order.renterorder.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.DateUtils;
import com.atzuche.order.commons.entity.dto.*;
import com.atzuche.order.commons.enums.RenterCashCodeEnum;
import com.atzuche.order.commons.enums.RenterChildStatusEnum;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.entity.dto.OrderCouponDTO;
import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.entity.dto.DeductAndSubsidyContextDTO;
import com.atzuche.order.renterorder.entity.dto.RenterOrderCostReqDTO;
import com.atzuche.order.renterorder.entity.dto.RenterOrderCostRespDTO;
import com.atzuche.order.renterorder.mapper.RenterOrderMapper;
import com.atzuche.order.renterorder.vo.*;
import com.atzuche.order.renterorder.vo.owner.OwnerCouponGetAndValidReqVO;
import com.atzuche.order.renterorder.vo.platform.MemAvailCouponRequestVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * 租客订单子表
 *
 * @author ZhangBin
 * @date 2019-12-14 17:24:31
 */
@Service
public class RenterOrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RenterOrderService.class);

    @Resource
    private RenterOrderMapper renterOrderMapper;

    @Resource
    private OrderCouponService orderCouponService;

    @Resource
    private RenterOrderCalCostService renterOrderCalCostService;

    @Resource
    private RenterOrderCostHandleService renterOrderCostHandleService;

    @Resource
    private RenterAdditionalDriverService renterAdditionalDriverService;


    public List<RenterOrderEntity> listAgreeRenterOrderByOrderNo(String orderNo) {
        return renterOrderMapper.listAgreeRenterOrderByOrderNo(orderNo);
    }

    /**
     * 获取有效的租客子单
     *
     * @param orderNo 主订单号
     * @return RenterOrderEntity
     */
    public RenterOrderEntity getRenterOrderByOrderNoAndIsEffective(String orderNo) {
        return renterOrderMapper.getRenterOrderByOrderNoAndIsEffective(orderNo);
    }


    /**
     * 获取租客子单根据租客子单号
     *
     * @param renterOrderNo 租客子订单号
     * @return RenterOrderEntity
     */
    public RenterOrderEntity getRenterOrderByRenterOrderNo(String renterOrderNo) {
        return renterOrderMapper.getRenterOrderByRenterOrderNo(renterOrderNo);
    }

    /**
     * 修改租客子订单是否有效状态
     *
     * @param id 主键
     * @param effectiveFlag 否有效状态
     * @return Integer
     */
    public Integer updateRenterOrderEffective(Integer id, Integer effectiveFlag) {
        return renterOrderMapper.updateRenterOrderEffective(id, effectiveFlag);
    }

    /**
     * 保存租客子订单
     *
     * @param renterOrderEntity 租客订单信息
     * @return Integer
     */
    public Integer saveRenterOrder(RenterOrderEntity renterOrderEntity) {
        return renterOrderMapper.insertSelective(renterOrderEntity);
    }

    /**
     * 获取待支付的租客子订单
     *
     * @param orderNo 主订单号
     * @return RenterOrderEntity
     */
    public RenterOrderEntity getRenterOrderByOrderNoAndWaitPay(String orderNo) {
        return renterOrderMapper.getRenterOrderByOrderNoAndWaitPay(orderNo);
    }


    /**
     * 生成租客订单
     *
     * @param renterOrderReqVO 请求参数
     */
    public RenterOrderResVO generateRenterOrderInfo(RenterOrderReqVO renterOrderReqVO) {
        LOGGER.info("生成租客订单.param is,renterOrderReqVO:[{}]",JSON.toJSONString(renterOrderReqVO));
        RenterOrderResVO renterOrderResVO = new RenterOrderResVO();
        //1. 租车费用计算
        RenterOrderCostReqDTO renterOrderCostReqDTO = buildRenterOrderCostReqDTO(renterOrderReqVO);
        RenterOrderCostRespDTO renterOrderCostRespDTO =
                renterOrderCalCostService.getOrderCostAndDeailList(renterOrderCostReqDTO);
        renterOrderCostRespDTO.setMemNo(renterOrderReqVO.getMemNo());
        LOGGER.info("租客订单租车费用计算.result is, renterOrderCostRespDTO:[{}]", JSON.toJSONString(renterOrderCostRespDTO));
        DeductAndSubsidyContextDTO context = initDeductAndSubsidyContextDTO(renterOrderCostRespDTO, renterOrderReqVO);
        //2. 送取服务券抵扣信息及补贴明细
        MemAvailCouponRequestVO getCarFeeCouponReqVO = buildMemAvailCouponRequestVO(renterOrderCostRespDTO,
                renterOrderReqVO);
        getCarFeeCouponReqVO.setDisCouponId(renterOrderReqVO.getGetCarFreeCouponId());
        boolean isUseGetCarFeeCoupon = renterOrderCostHandleService.handleGetCarFeeCoupon(context,
                getCarFeeCouponReqVO);

        //3. 车主券抵扣信息及补贴明细
        OwnerCouponGetAndValidReqVO ownerCouponGetAndValidReqVO = buildOwnerCouponGetAndValidReqVO(renterOrderReqVO,
                getCarFeeCouponReqVO.getRentAmt());
        boolean isUseOwnerCoupon = renterOrderCostHandleService.handleOwnerCoupon(context, ownerCouponGetAndValidReqVO,
                renterOrderResVO);

        //4. 限时红包补贴明细
        int reductiAmt = null == renterOrderReqVO.getReductiAmt() ? 0 : renterOrderReqVO.getReductiAmt();
        boolean isUseLimitRed = renterOrderCostHandleService.handleLimitRed(context, reductiAmt);

        //5. 平台优惠券抵扣及补贴明细
        MemAvailCouponRequestVO platformCouponReqVO = buildMemAvailCouponRequestVO(renterOrderCostRespDTO,
                renterOrderReqVO);
        platformCouponReqVO.setDisCouponId(renterOrderReqVO.getDisCouponIds());
        boolean isUsePlatformCoupon = renterOrderCostHandleService.handlePlatformCoupon(context, platformCouponReqVO);

        //6. 凹凸币补贴明细
        int chargeAutoCoin = renterOrderCostHandleService.handleAutoCoin(context,
               renterOrderReqVO.getMemNo(),
                renterOrderReqVO.getUseAutoCoin());

        //7. 车辆押金
        RenterOrderCarDepositResVO renterOrderCarDepositResVO =
                renterOrderCostHandleService.handleCarDepositAmt(renterOrderReqVO);

        //8. 违章押金
        RenterOrderIllegalResVO renterOrderIllegalResVO =
                renterOrderCostHandleService.handleIllegalDepositAmt(renterOrderCostReqDTO.getCostBaseDTO(), renterOrderReqVO);

        //9. 落库操作
        //租客订单
        RenterOrderEntity record = new RenterOrderEntity();
        record.setOrderNo(renterOrderReqVO.getOrderNo());
        record.setRenterOrderNo(renterOrderReqVO.getRenterOrderNo());
        record.setExpRentTime(renterOrderReqVO.getRentTime());
        record.setExpRevertTime(renterOrderReqVO.getRevertTime());
        record.setGoodsCode(String.valueOf(renterOrderReqVO.getCarNo()));
        record.setGoodsType("1");
        record.setAgreeFlag(null == renterOrderReqVO.getReplyFlag() ? 0 : renterOrderReqVO.getReplyFlag());
        record.setReqAcceptTime(null == renterOrderReqVO.getReplyFlag() ? null : LocalDateTime.now());
        record.setIsUseCoin(renterOrderReqVO.getUseAutoCoin());
        record.setIsUseWallet(renterOrderReqVO.getUseBal());
        record.setAddDriver(CollectionUtils.isEmpty(renterOrderReqVO.getDriverIds()) ? 0 :
                renterOrderReqVO.getDriverIds().size());
        record.setIsUseCoupon(StringUtils.isNotBlank(renterOrderReqVO.getCarOwnerCouponNo())
                || StringUtils.isNotBlank(renterOrderReqVO.getDisCouponIds())
                || StringUtils.isNotBlank(renterOrderReqVO.getGetCarFreeCouponId()) ? 1 : 0);
        record.setIsGetCar(renterOrderReqVO.getSrvGetFlag());
        record.setIsReturnCar(renterOrderReqVO.getSrvReturnFlag());
        record.setIsAbatement(renterOrderReqVO.getAbatement()==null?0:Integer.valueOf(renterOrderReqVO.getAbatement()));
        record.setIsUseSpecialPrice(Integer.valueOf(renterOrderReqVO.getUseSpecialPrice()==null?"0":renterOrderReqVO.getUseSpecialPrice()));
        record.setChildStatus(RenterChildStatusEnum.PROCESS_ING.getCode());
        renterOrderMapper.insertSelective(record);
        //保存租客订单费用、费用明细、补贴明细等
        renterOrderCostRespDTO.setRenterOrderSubsidyDetailDTOList(context.getOrderSubsidyDetailList());
        renterOrderCostRespDTO.setOrderNo(renterOrderReqVO.getOrderNo());
        renterOrderCostRespDTO.setRenterOrderNo(renterOrderReqVO.getRenterOrderNo());
        renterOrderCalCostService.saveOrderCostAndDeailList(renterOrderCostRespDTO);
        //保存订单优惠券信息
        orderCouponService.insertBatch(context.getOrderCouponList());
        //保存附加驾驶人信息
        renterAdditionalDriverService.insertBatchAdditionalDriver(renterOrderReqVO.getOrderNo(),
                renterOrderReqVO.getRenterOrderNo(),renterOrderReqVO.getDriverIds(),renterOrderReqVO.getCommUseDriverList());

        //返回值处理
        renterOrderResVO.setRenterOrderCarDepositResVO(renterOrderCarDepositResVO);
        renterOrderResVO.setRenterOrderIllegalResVO(renterOrderIllegalResVO);
        Optional<RenterOrderCostDetailEntity> renterOrderCostDetailEntityOptional =
                renterOrderCostRespDTO.getRenterOrderCostDetailDTOList().stream().filter(costDetail -> StringUtils.equals(costDetail.getCostCode(),RenterCashCodeEnum.RENT_AMT.getCashNo())).findFirst();
        renterOrderResVO.setRentAmtEntity(renterOrderCostDetailEntityOptional.orElse(null));

        //凹凸币、优惠券使用情况返回
        CouponAndAutoCoinResVO couponAndAutoCoinResVO = new CouponAndAutoCoinResVO();
        couponAndAutoCoinResVO.setChargeAutoCoin(chargeAutoCoin);
        couponAndAutoCoinResVO.setIsUseGetCarFeeCoupon(isUseGetCarFeeCoupon);
        couponAndAutoCoinResVO.setIsUseOwnerCoupon(isUseOwnerCoupon);
        couponAndAutoCoinResVO.setIsUsePlatformCoupon(isUsePlatformCoupon);
        couponAndAutoCoinResVO.setRentAmt(renterOrderCostRespDTO.getRentAmount());

        renterOrderResVO.setCouponAndAutoCoinResVO(couponAndAutoCoinResVO);
        return renterOrderResVO;
    }


    /**
     * 租车费用计算相关参数封装
     *
     * @param renterOrderReqVO 生成租客订单请求参数
     * @return RenterOrderCostReqDTO
     */
    public RenterOrderCostReqDTO buildRenterOrderCostReqDTO(RenterOrderReqVO renterOrderReqVO) {
        //基础信息
        CostBaseDTO costBaseDTO = new CostBaseDTO();
        costBaseDTO.setStartTime(renterOrderReqVO.getRentTime());
        costBaseDTO.setEndTime(renterOrderReqVO.getRevertTime());
        costBaseDTO.setOrderNo(renterOrderReqVO.getOrderNo());
        costBaseDTO.setRenterOrderNo(renterOrderReqVO.getRenterOrderNo());
        costBaseDTO.setMemNo(renterOrderReqVO.getMemNo());

        //租金计算相关信息
        RentAmtDTO rentAmtDTO = new RentAmtDTO();
        rentAmtDTO.setCostBaseDTO(costBaseDTO);
        rentAmtDTO.setRenterGoodsPriceDetailDTOList(renterOrderReqVO.getRenterGoodsPriceDetailDTOList());

        //保险计算相关信息
        InsurAmtDTO insurAmtDTO = new InsurAmtDTO();
        insurAmtDTO.setCostBaseDTO(costBaseDTO);
        insurAmtDTO.setCarLabelIds(renterOrderReqVO.getLabelIds());
        insurAmtDTO.setCertificationTime(renterOrderReqVO.getCertificationTime());
        insurAmtDTO.setGetCarBeforeTime(renterOrderReqVO.getGetCarBeforeTime());
        insurAmtDTO.setReturnCarAfterTime(renterOrderReqVO.getReturnCarAfterTime());
        insurAmtDTO.setInmsrp(renterOrderReqVO.getInmsrp());
        insurAmtDTO.setGuidPrice(renterOrderReqVO.getGuidPrice());

        //补充全险计算相关信息
        AbatementAmtDTO abatementAmtDTO = new AbatementAmtDTO();
        abatementAmtDTO.setCostBaseDTO(costBaseDTO);
        abatementAmtDTO.setCarLabelIds(renterOrderReqVO.getLabelIds());
        abatementAmtDTO.setCertificationTime(renterOrderReqVO.getCertificationTime());
        abatementAmtDTO.setGetCarBeforeTime(renterOrderReqVO.getGetCarBeforeTime());
        abatementAmtDTO.setReturnCarAfterTime(renterOrderReqVO.getReturnCarAfterTime());
        abatementAmtDTO.setInmsrp(renterOrderReqVO.getInmsrp());
        abatementAmtDTO.setGuidPrice(renterOrderReqVO.getGuidPrice());
        abatementAmtDTO.setIsAbatement(null != renterOrderReqVO.getAbatement() && renterOrderReqVO.getAbatement() == 1 );

        //附加驾驶人险计算相关信息
        ExtraDriverDTO extraDriverDTO = new ExtraDriverDTO();
        extraDriverDTO.setCostBaseDTO(costBaseDTO);
        extraDriverDTO.setDriverIds(renterOrderReqVO.getDriverIds());

        //取还车费用计算相关信息
        GetReturnCarCostReqDto getReturnCarCostReqDto = new GetReturnCarCostReqDto();
        getReturnCarCostReqDto.setCostBaseDTO(costBaseDTO);
        getReturnCarCostReqDto.setCarLat(renterOrderReqVO.getCarLat());
        getReturnCarCostReqDto.setCarLon(renterOrderReqVO.getCarLon());
        getReturnCarCostReqDto.setCityCode(Integer.valueOf(renterOrderReqVO.getCityCode()));
        getReturnCarCostReqDto.setEntryCode(renterOrderReqVO.getEntryCode());
        getReturnCarCostReqDto.setSource(Integer.valueOf(renterOrderReqVO.getSource()));
        getReturnCarCostReqDto.setSrvGetLat(renterOrderReqVO.getSrvGetLat());
        getReturnCarCostReqDto.setSrvGetLon(renterOrderReqVO.getSrvGetLon());
        getReturnCarCostReqDto.setSrvReturnLon(renterOrderReqVO.getSrvReturnLon());
        getReturnCarCostReqDto.setSrvReturnLat(renterOrderReqVO.getSrvReturnLat());
        getReturnCarCostReqDto.setIsPackageOrder(StringUtils.equals("2",renterOrderReqVO.getOrderCategory()));
        getReturnCarCostReqDto.setIsGetCarCost(null != renterOrderReqVO.getSrvGetFlag() && StringUtils.equals("1",
                renterOrderReqVO.getSrvGetFlag().toString()));
        getReturnCarCostReqDto.setIsReturnCarCost(null != renterOrderReqVO.getSrvReturnFlag() && StringUtils.equals("1",
                renterOrderReqVO.getSrvReturnFlag().toString()));

        //超运能溢价计算相关信息
        GetReturnCarOverCostReqDto getReturnCarOverCostReqDto = new GetReturnCarOverCostReqDto();
        getReturnCarOverCostReqDto.setCostBaseDTO(costBaseDTO);
        getReturnCarOverCostReqDto.setCityCode(Integer.valueOf(renterOrderReqVO.getCityCode()));
        if (StringUtils.isNotBlank(renterOrderReqVO.getOrderCategory())) {
        	getReturnCarOverCostReqDto.setOrderType(Integer.valueOf(renterOrderReqVO.getOrderCategory()));
        }

        //租车费用计算相关参数
        RenterOrderCostReqDTO renterOrderCostReqDTO = new RenterOrderCostReqDTO();
        renterOrderCostReqDTO.setCostBaseDTO(costBaseDTO);
        renterOrderCostReqDTO.setAbatementAmtDTO(abatementAmtDTO);
        renterOrderCostReqDTO.setRentAmtDTO(rentAmtDTO);
        renterOrderCostReqDTO.setInsurAmtDTO(insurAmtDTO);
        renterOrderCostReqDTO.setExtraDriverDTO(extraDriverDTO);
        renterOrderCostReqDTO.setMileageAmtDTO(null);
        renterOrderCostReqDTO.setGetReturnCarCostReqDto(getReturnCarCostReqDto);
        renterOrderCostReqDTO.setGetReturnCarOverCostReqDto(getReturnCarOverCostReqDto);
        return renterOrderCostReqDTO;
    }


    /**
     * 封装请求平台券请求参数
     *
     * @param renterOrderCostRespDTO 租车费用相关信息
     * @param renterOrderReqVO       租客订单请求信息
     * @return MemAvailCouponRequestVO 优惠券请求信息
     */
    public MemAvailCouponRequestVO buildMemAvailCouponRequestVO(RenterOrderCostRespDTO renterOrderCostRespDTO,
                                                                 RenterOrderReqVO renterOrderReqVO) {

        MemAvailCouponRequestVO memAvailCouponRequestVO = new MemAvailCouponRequestVO();

        memAvailCouponRequestVO.setMemNo(Integer.valueOf(renterOrderReqVO.getMemNo()));
        memAvailCouponRequestVO.setCarNo(Integer.valueOf(renterOrderReqVO.getCarNo()));
        memAvailCouponRequestVO.setCityCode(Integer.valueOf(renterOrderReqVO.getCityCode()));
        memAvailCouponRequestVO.setIsNew(renterOrderReqVO.getIsNew());
        memAvailCouponRequestVO.setRentAmt(null == renterOrderCostRespDTO.getRentAmount() ? 1 : Math.abs(renterOrderCostRespDTO.getRentAmount()));
        memAvailCouponRequestVO.setInsureTotalPrices(null  == renterOrderCostRespDTO.getBasicEnsureAmount() ? 0 :
                Math.abs(renterOrderCostRespDTO.getBasicEnsureAmount()));
        memAvailCouponRequestVO.setAbatement(null == renterOrderCostRespDTO.getComprehensiveEnsureAmount() ? 0 : Math.abs(renterOrderCostRespDTO.getComprehensiveEnsureAmount()));
        int srvGetCost = null == renterOrderCostRespDTO.getGetRealAmt() ? 0 :
                Math.abs(renterOrderCostRespDTO.getGetRealAmt());
        int getOverCost = null == renterOrderCostRespDTO.getGetOverAmt() ? 0 :
                Math.abs(renterOrderCostRespDTO.getGetOverAmt());
        memAvailCouponRequestVO.setSrvGetCost(srvGetCost + getOverCost);

        int srvReturnCost = null == renterOrderCostRespDTO.getReturnRealAmt() ? 0 :
                Math.abs(renterOrderCostRespDTO.getReturnRealAmt());
        int returnOverCost = null == renterOrderCostRespDTO.getReturnOverAmt() ? 0 :
                Math.abs(renterOrderCostRespDTO.getReturnOverAmt());
        memAvailCouponRequestVO.setSrvReturnCost(srvReturnCost + returnOverCost);


        Optional<RenterOrderCostDetailEntity> renterOrderCostDetailEntityOptional =
                renterOrderCostRespDTO.getRenterOrderCostDetailDTOList().stream().filter(d -> StringUtils.equals(d.getCostCode(), RenterCashCodeEnum.RENT_AMT.getCashNo())).findFirst();
        int holidayAverage = renterOrderCostDetailEntityOptional.isPresent() ?
                renterOrderCostDetailEntityOptional.get().getUnitPrice() : 0;
        memAvailCouponRequestVO.setHolidayAverage(Math.abs(holidayAverage));
        memAvailCouponRequestVO.setLabelIds(renterOrderReqVO.getLabelIds());
        memAvailCouponRequestVO.setRentTime(DateUtils.formateLong(renterOrderReqVO.getRentTime(), DateUtils.DATE_DEFAUTE));
        memAvailCouponRequestVO.setRevertTime(DateUtils.formateLong(renterOrderReqVO.getRevertTime(), DateUtils.DATE_DEFAUTE));

        memAvailCouponRequestVO.setCounterFee(null == renterOrderCostRespDTO.getCommissionAmount() ? 20 : Math.abs(renterOrderCostRespDTO.getCommissionAmount()));
        memAvailCouponRequestVO.setOriginalRentAmt(null == renterOrderCostRespDTO.getRentAmount() ? 1 : Math.abs(renterOrderCostRespDTO.getRentAmount()));
        return memAvailCouponRequestVO;
    }

    /**
     * 车主券请求参数封装
     *
     * @param renterOrderReqVO 租客订单请求参数
     * @param rentAmt          原始租金(车主券优先级最高)
     * @return OwnerCouponGetAndValidReqVO 车主券请求参数
     */
    public OwnerCouponGetAndValidReqVO buildOwnerCouponGetAndValidReqVO(RenterOrderReqVO renterOrderReqVO,
                                                                         int rentAmt) {
        OwnerCouponGetAndValidReqVO ownerCouponGetAndValidReqVO = new OwnerCouponGetAndValidReqVO();
        ownerCouponGetAndValidReqVO.setCarNo(Integer.valueOf(renterOrderReqVO.getCarNo()));
        ownerCouponGetAndValidReqVO.setCouponNo(renterOrderReqVO.getCarOwnerCouponNo());
        ownerCouponGetAndValidReqVO.setRentAmt(rentAmt);
        ownerCouponGetAndValidReqVO.setMark(1);
        return ownerCouponGetAndValidReqVO;
    }

    /**
     * 初始化公共参数
     *
     * @param renterOrderCostRespDTO 计算租车费用返回信息
     * @param renterOrderReqVO       租客订单请求参数
     * @return DeductAndSubsidyContextDTO 公共参数
     */
    private DeductAndSubsidyContextDTO initDeductAndSubsidyContextDTO(RenterOrderCostRespDTO renterOrderCostRespDTO,
                                                                      RenterOrderReqVO renterOrderReqVO) {

        //租车费用补贴记录
        List<RenterOrderSubsidyDetailDTO> renterOrderSubsidyDetails =
                renterOrderCostRespDTO.getRenterOrderSubsidyDetailDTOList();

        List<OrderCouponDTO> couponList = new ArrayList<>();
        List<RenterOrderSubsidyDetailDTO> subsidyList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(renterOrderSubsidyDetails)) {
            subsidyList.addAll(renterOrderSubsidyDetails);
        }
        DeductAndSubsidyContextDTO context = new DeductAndSubsidyContextDTO();
        context.setOrderNo(renterOrderReqVO.getOrderNo());
        context.setRenterOrderNo(renterOrderReqVO.getRenterOrderNo());
        context.setMemNo(renterOrderReqVO.getMemNo());
        context.setOriginalRentAmt(null == renterOrderCostRespDTO.getRentAmount() ? 1 : Math.abs(renterOrderCostRespDTO.getRentAmount()));
        context.setSurplusRentAmt(null == renterOrderCostRespDTO.getRentAmount() ? 1 : Math.abs(renterOrderCostRespDTO.getRentAmount()));
        context.setOrderCouponList(couponList);
        context.setOrderSubsidyDetailList(subsidyList);
        return context;
    }
    
    /**
     * 修改租客子单子状态
     * @param id 主键
     * @param childStatus 子订单状态
     * @return Integer
     */
    public Integer updateRenterOrderChildStatus(Integer id, Integer childStatus) {
    	return renterOrderMapper.updateRenterOrderChildStatus(id, childStatus);
    }

}
