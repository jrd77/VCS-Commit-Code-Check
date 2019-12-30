package com.atzuche.order.renterorder.service;

import com.atzuche.order.commons.DateUtils;
import com.atzuche.order.commons.entity.dto.*;
import com.atzuche.order.commons.enums.RenterCashCodeEnum;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.entity.dto.OrderCouponDTO;
import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
import com.atzuche.order.rentercost.service.RenterOrderCostCombineService;
import com.atzuche.order.rentercost.service.RenterOrderSubsidyDetailService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.entity.dto.DeductAndSubsidyContextDTO;
import com.atzuche.order.renterorder.entity.dto.RenterOrderCostReqDTO;
import com.atzuche.order.renterorder.entity.dto.RenterOrderCostRespDTO;
import com.atzuche.order.renterorder.mapper.RenterOrderMapper;
import com.atzuche.order.renterorder.vo.RenterOrderReqVO;
import com.atzuche.order.renterorder.vo.owner.OwnerCouponGetAndValidReqVO;
import com.atzuche.order.renterorder.vo.platform.MemAvailCouponRequestVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
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

    @Resource
    private RenterOrderMapper renterOrderMapper;

    @Resource
    private RenterOrderCalCostService renterOrderCalCostService;

    @Resource
    private RenterOrderSubsidyDetailService renterOrderSubsidyDetailService;

    @Resource
    private RenterOrderCostCombineService renterOrderCostCombineService;

    @Resource
    private RenterOrderCostHandleService renterOrderCostHandleService;


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
     * @param id
     * @param effectiveFlag
     * @return Integer
     */
    public Integer updateRenterOrderEffective(Integer id, Integer effectiveFlag) {
        return renterOrderMapper.updateRenterOrderEffective(id, effectiveFlag);
    }

    /**
     * 保存租客子订单
     *
     * @param renterOrderEntity
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
    public void generateRenterOrderInfo(RenterOrderReqVO renterOrderReqVO) {
        //1. 租车费用计算
        RenterOrderCostReqDTO renterOrderCostReqDTO = buildRenterOrderCostReqDTO(renterOrderReqVO);
        RenterOrderCostRespDTO renterOrderCostRespDTO =
                renterOrderCalCostService.getOrderCostAndDeailList(renterOrderCostReqDTO);

        DeductAndSubsidyContextDTO context = initDeductAndSubsidyContextDTO(renterOrderCostRespDTO, renterOrderReqVO);
        //2. 送取服务券抵扣信息及补贴明细
        MemAvailCouponRequestVO getCarFeeCouponReqVO = buildMemAvailCouponRequestVO(renterOrderCostRespDTO,
                renterOrderReqVO);
        getCarFeeCouponReqVO.setDisCouponId(renterOrderReqVO.getGetCarFreeCouponId());
        renterOrderCostHandleService.handleGetCarFeeCoupon(context, getCarFeeCouponReqVO);

        //3. 车主券抵扣信息及补贴明细
        OwnerCouponGetAndValidReqVO ownerCouponGetAndValidReqVO = buildOwnerCouponGetAndValidReqVO(renterOrderReqVO,
                getCarFeeCouponReqVO.getRentAmt());
        renterOrderCostHandleService.handleOwnerCoupon(context, ownerCouponGetAndValidReqVO);

        //4. 限时红包补贴明细
        int reductiAmt = null == renterOrderReqVO.getReductiAmt() ? 0 : renterOrderReqVO.getReductiAmt();
        renterOrderCostHandleService.handleLimitRed(context, reductiAmt);

        //5. 平台优惠券抵扣及补贴明细
        MemAvailCouponRequestVO platformCouponReqVO = buildMemAvailCouponRequestVO(renterOrderCostRespDTO,
                renterOrderReqVO);
        platformCouponReqVO.setDisCouponId(renterOrderReqVO.getDisCouponIds());
        renterOrderCostHandleService.handlePlatformCoupon(context, platformCouponReqVO);

        //6. 凹凸币补贴明细
        renterOrderCostHandleService.handleAutoCoin(context, Integer.valueOf(renterOrderReqVO.getMemNo()),
                renterOrderReqVO.getUseAutoCoin());

        //7. 车辆押金
        renterOrderCostHandleService.handleCarDepositAmt(renterOrderReqVO);

        //8. 违章押金
        renterOrderCostHandleService.handleIllegalDepositAmt(renterOrderCostReqDTO.getCostBaseDTO(), renterOrderReqVO);



        //9. 落库操作



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
        costBaseDTO.setStartTime(renterOrderReqVO.getRevertTime());
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
        getReturnCarCostReqDto.setSource(renterOrderReqVO.getSource());
        getReturnCarCostReqDto.setSrvGetLat(renterOrderReqVO.getSrvGetLat());
        getReturnCarCostReqDto.setSrvGetLon(renterOrderReqVO.getSrvGetLon());
        getReturnCarCostReqDto.setSrvReturnLon(renterOrderReqVO.getSrvReturnLon());
        getReturnCarCostReqDto.setSrvReturnLat(renterOrderReqVO.getSrvReturnLat());
        getReturnCarCostReqDto.setIsPackageOrder(false);

        //超运能溢价计算相关信息
        GetReturnCarOverCostReqDto getReturnCarOverCostReqDto = new GetReturnCarOverCostReqDto();
        getReturnCarOverCostReqDto.setCostBaseDTO(costBaseDTO);
        getReturnCarOverCostReqDto.setCityCode(Integer.valueOf(renterOrderReqVO.getCityCode()));
        getReturnCarOverCostReqDto.setOrderType(1);

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
        memAvailCouponRequestVO.setCarNo(renterOrderReqVO.getCarNo());
        memAvailCouponRequestVO.setCityCode(Integer.valueOf(renterOrderReqVO.getCityCode()));
        memAvailCouponRequestVO.setIsNew(renterOrderReqVO.getIsNew());
        memAvailCouponRequestVO.setRentAmt(0);
        memAvailCouponRequestVO.setInsureTotalPrices(renterOrderCostRespDTO.getBasicEnsureAmount());
        memAvailCouponRequestVO.setAbatement(renterOrderCostRespDTO.getComprehensiveEnsureAmount());
        memAvailCouponRequestVO.setSrvGetCost(renterOrderCostRespDTO.getGetRealAmt() + renterOrderCostRespDTO.getGetOverAmt());
        memAvailCouponRequestVO.setSrvReturnCost(renterOrderCostRespDTO.getReturnRealAmt() + renterOrderCostRespDTO.getReturnOverAmt());


        Optional<RenterOrderCostDetailEntity> renterOrderCostDetailEntityOptional =
                renterOrderCostRespDTO.getRenterOrderCostDetailDTOList().stream().filter(d -> StringUtils.equals(d.getCostCode(), RenterCashCodeEnum.RENT_AMT.getCashNo())).findFirst();
        Integer holidayAverage = renterOrderCostDetailEntityOptional.isPresent() ?
                renterOrderCostDetailEntityOptional.get().getUnitPrice() : 0;
        memAvailCouponRequestVO.setHolidayAverage(holidayAverage);
        memAvailCouponRequestVO.setLabelIds(renterOrderReqVO.getLabelIds());
        memAvailCouponRequestVO.setRentTime(DateUtils.formateLong(renterOrderReqVO.getRentTime(), DateUtils.DATE_DEFAUTE));
        memAvailCouponRequestVO.setRevertTime(DateUtils.formateLong(renterOrderReqVO.getRevertTime(), DateUtils.DATE_DEFAUTE));

        memAvailCouponRequestVO.setCounterFee(renterOrderCostRespDTO.getCommissionAmount());
        memAvailCouponRequestVO.setOriginalRentAmt(0);

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
        ownerCouponGetAndValidReqVO.setCarNo(renterOrderReqVO.getCarNo());
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
        context.setOriginalRentAmt(renterOrderCostRespDTO.getRentAmount());
        context.setSurplusRentAmt(renterOrderCostRespDTO.getRentAmount());
        context.setOrderCouponList(couponList);
        context.setOrderSubsidyDetailList(subsidyList);
        return context;
    }
}
