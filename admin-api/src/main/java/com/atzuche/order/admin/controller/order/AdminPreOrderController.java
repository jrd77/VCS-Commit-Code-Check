package com.atzuche.order.admin.controller.order;

import com.atzuche.order.admin.PreOrderReqVO;
import com.atzuche.order.admin.service.AdminOrderService;
import com.atzuche.order.admin.service.RemoteFeignService;
import com.atzuche.order.admin.vo.req.order.PreOrderAdminRequestVO;
import com.atzuche.order.admin.vo.resp.MemAvailableCouponVO;
import com.atzuche.order.admin.vo.resp.order.PreOrderAdminResponseVO;
import com.atzuche.order.car.CarProxyService;
import com.atzuche.order.coin.service.AutoCoinProxyService;
import com.atzuche.order.commons.BindingResultUtil;
import com.atzuche.order.commons.DateUtils;
import com.atzuche.order.commons.GlobalConstant;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.entity.dto.OwnerMemberDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberRightDTO;
import com.atzuche.order.commons.enums.HolidayTypeEnum;
import com.atzuche.order.commons.enums.MemberFlagEnum;
import com.atzuche.order.commons.enums.RightTypeEnum;
import com.atzuche.order.commons.exceptions.InputErrorException;
import com.atzuche.order.commons.vo.req.NormalOrderCostCalculateReqVO;
import com.atzuche.order.mem.MemProxyService;
import com.atzuche.order.wallet.WalletProxyService;
import com.autoyol.car.api.model.vo.CarPriceOfDayVO;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocGroup;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 下单前管理后台页面展示
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/11 1:24 下午
 **/
@RestController
public class AdminPreOrderController {
    private final static Logger logger = LoggerFactory.getLogger(AdminPreOrderController.class);
    

    @Autowired
    private MemProxyService memProxyService;

    @Autowired
    private CarProxyService carProxyService;

    @Autowired
    private AdminOrderService adminOrderService;

    @Autowired
    private WalletProxyService walletProxyService;

    @Autowired
    private AutoCoinProxyService coinProxyService;



    @AutoDocVersion(version = "下单前确定页面")
    @AutoDocGroup(group = "下单前确定页面")
    @AutoDocMethod(description = "下单前确定页面展示", value = "下单前确定页面展示",response = PreOrderAdminResponseVO.class)
    @PostMapping("console/order/adminPre")
    public ResponseData<PreOrderAdminResponseVO> preOrderAdmin(@RequestBody PreOrderAdminRequestVO request, BindingResult result){
        BindingResultUtil.checkBindingResult(result);

        PreOrderAdminResponseVO responseVO = new PreOrderAdminResponseVO();


        String memNo = request.getMemNo();
        if(StringUtils.trimToNull(memNo)==null){
            String mobile = request.getMobile();
            if(StringUtils.trimToNull(mobile)==null){
                throw new RuntimeException("memNo or mobile cannot be null at same time");
            }
            memNo = memProxyService.getMemNoByMoile(mobile).toString();
        }
        responseVO.setMemNo(memNo);
        RenterMemberDTO renterMember = memProxyService.getRenterMemberInfo(memNo);
        if(StringUtils.isNotBlank(StringUtils.trimToNull(request.getMobile()))) {
            responseVO.setMobile(request.getMobile());
        } else {
            responseVO.setMobile(renterMember.getPhone());
        }
        responseVO.setRenterName(renterMember.getRealName());
        responseVO.setCityCode(request.getCityCode());
        responseVO.setRentCity(request.getRentCity());

        CarProxyService.CarDetailReqVO carDetailReqVO = new CarProxyService.CarDetailReqVO();
        carDetailReqVO.setAddrIndex(0);
        carDetailReqVO.setCarNo(request.getCarNo());
        carDetailReqVO.setUseSpecialPrice(true);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime rentTime = LocalDateTime.parse(request.getRentTime(),dateTimeFormatter);
        LocalDateTime revertTime = LocalDateTime.parse(request.getRevertTime(),dateTimeFormatter);
        carDetailReqVO.setRentTime(rentTime);
        carDetailReqVO.setRevertTime(revertTime);


        CarProxyService.CarPriceDetail carPriceDetail = carProxyService.getCarPriceDetail(carDetailReqVO);
        if(carPriceDetail != null && carPriceDetail.getSeatNum()!= null && carPriceDetail.getSeatNum()==5 || carPriceDetail.getSeatNum()==7){
            responseVO.setIsDriverInsure(1);
        }

        responseVO.setCarPlatNo(carPriceDetail.getPlateNum());
        OwnerMemberDTO ownerMemberDTO = memProxyService.getOwnerMemberInfo(carPriceDetail.getOwnerNo().toString());
        responseVO.setOwnerMemNo(ownerMemberDTO.getMemNo());
        responseVO.setOwnerName(ownerMemberDTO.getRealName());
        responseVO.setOwnerMobile(ownerMemberDTO.getPhone());
        responseVO.setRentTime(DateUtils.formate(rentTime, DateUtils.DATE_DEFAUTE1));
        responseVO.setRevertTime(DateUtils.formate(revertTime, DateUtils.DATE_DEFAUTE1));
        // 设置是否禁用全面保障服务标记
        responseVO.setNoAbatementFlag(getNoAbatementFlagByCarNo(carPriceDetail.getInmsrp(), carPriceDetail.getGuidePrice(), carPriceDetail.getCarLevel()));
        List<CarPriceOfDayVO> renterGoodsPriceDetailDTOList = carPriceDetail.getCarPriceOfDayVOList();
        //是否可购买驾乘无忧险
        responseVO.setIsDriverInsure(isDriverInsure(carPriceDetail));

        List<PreOrderAdminResponseVO.CarDayPrice> carDayPrices = new ArrayList<>();

        for(CarPriceOfDayVO dto:renterGoodsPriceDetailDTOList){
            PreOrderAdminResponseVO.CarDayPrice carDayPrice = new PreOrderAdminResponseVO.CarDayPrice();
            carDayPrice.setDay(dto.getDateStr());
            carDayPrice.setPrice(String.valueOf(dto.getPrice()));

            String desc = "";
            if(StringUtils.isNotBlank(dto.getHolidayType())) {
                try {
                    desc = HolidayTypeEnum.from(dto.getHolidayType()).getTitle();
                } catch (Exception e) {
                    logger.info("无效节假日标识.holidayType:[{}]", dto.getHolidayType());
                }
            }
            if(StringUtils.isBlank(desc)) {
                if(isWorkDay(LocalDateTimeUtils.parseStringToLocalDate(dto.getDateStr(),
                        GlobalConstant.FORMAT_DATE_STR))) {
                    desc = "工作日";
                } else {
                    desc = "周末";
                }
            }
            carDayPrice.setDesc(desc);

            if(dto.getPrice().equals(dto.getHolidayPrice())||dto.getPrice().equals(dto.getDayPrice())) {
                logger.info("无特供价");
            }
            else{
                carDayPrices.add(carDayPrice);
            }
        }

        responseVO.setCarSpecialDayPrices(carDayPrices);
        int totalWallet = walletProxyService.getWalletByMemNo(memNo);
        if(totalWallet<=0){
            responseVO.setIsUseWallet(0);
        }else if(filterRight(renterMember.getRenterMemberRightDTOList(), RightTypeEnum.MEMBER_FLAG, Arrays.asList(MemberFlagEnum.QYYH,MemberFlagEnum.QYXYYH), "1") != null && totalWallet > 0){
            responseVO.setIsUseWallet(1);
        }else{
            responseVO.setIsUseWallet(-1);
        }
        responseVO.setTotalWallet(totalWallet);
        responseVO.setTotalAutoCoin(coinProxyService.getCrmCustPoint(memNo));

        NormalOrderCostCalculateReqVO normalOrderCostCalculateReqVO = new NormalOrderCostCalculateReqVO();
        BeanUtils.copyProperties(request,normalOrderCostCalculateReqVO);

        normalOrderCostCalculateReqVO.setRentTime(date2String(string2Date(request.getRentTime(),"yyyyMMddHHmmss"),"yyyy-MM-dd HH:mm:ss"));
        normalOrderCostCalculateReqVO.setRevertTime(date2String(string2Date(request.getRevertTime(),"yyyyMMddHHmmss"),"yyyy-MM-dd HH:mm:ss"));

        normalOrderCostCalculateReqVO.setDisCouponId(request.getDisCouponIds());

        normalOrderCostCalculateReqVO.setMemNo(memNo);
        normalOrderCostCalculateReqVO.setSceneCode("EX007");
        normalOrderCostCalculateReqVO.setSource("1");
        normalOrderCostCalculateReqVO.setPlatformParentType("7");
        normalOrderCostCalculateReqVO.setOrderCategory("1");
        normalOrderCostCalculateReqVO.setBusinessParentType("5");
        if(StringUtils.isNotBlank(request.getLongOwnerCouponNo())) {
            normalOrderCostCalculateReqVO.setOrderCategory("3");
            normalOrderCostCalculateReqVO.setBusinessParentType("6");
        }
        MemAvailableCouponVO memAvailableCouponVO = adminOrderService.getPreOrderCouponList(normalOrderCostCalculateReqVO);
        responseVO.setPlatCouponList(memAvailableCouponVO.getPlatCouponList());
        responseVO.setGetCarCouponList(memAvailableCouponVO.getGetCarCouponList());
        responseVO.setCarOwnerCouponDetailVOList(memAvailableCouponVO.getCarOwnerCouponDetailVOList());
        responseVO.setCountDays(memAvailableCouponVO.getCountDays());
        return ResponseData.success(responseVO);

    }
    /*
     * @Author ZhangBin
     * @Date 2020/4/17 10:56
     * @Description:
     *
     **/
    public static RenterMemberRightDTO filterRight(List<RenterMemberRightDTO> list, RightTypeEnum rightTypeEnum, List<MemberFlagEnum> memberFlagEnumList, String rightValue){
        if(rightValue == null||rightValue.trim().length()<=0)return null;
        if(memberFlagEnumList==null|| memberFlagEnumList.size()<=0)return null;
        List<String> rightCodeList = memberFlagEnumList.stream().map(x -> x.getRightCode()).collect(Collectors.toList());
        Optional<RenterMemberRightDTO> first = Optional.ofNullable(list)
                .orElseGet(ArrayList::new)
                .stream()
                .filter(x -> rightTypeEnum.getCode() == x.getRightType()
                        && rightCodeList.contains(x.getRightCode())
                        && rightValue.equals(x.getRightValue()))
                .findFirst();
        if(first.isPresent()){
            return first.get();
        }
        return null;
    }
    private int isDriverInsure(CarProxyService.CarPriceDetail carPriceDetail) {
        if(carPriceDetail == null || carPriceDetail.getSeatNum() == null){
            return 0;
        }
        Integer seatNum = carPriceDetail.getSeatNum();
        if(seatNum == 5 || seatNum == 7){
            return 1;
        }
        return 0;
    }

    public static Date string2Date(String dateStr,String pattern){
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        try {
            return dateFormat.parse(dateStr);
        }catch (Exception e){
            throw new InputErrorException(dateStr+"格式不对");
        }
    }

    public static String date2String(Date date,String pattern){
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(date);
    }


    @AutoDocVersion(version = "下单前确定页面")
    @AutoDocGroup(group = "下单前确定页面")
    @AutoDocMethod(description = "下单前确定页面的优惠券列表", value = "下单前确定页面的优惠券列表",response = MemAvailableCouponVO.class)
    @PostMapping("console/order/adminPreCoupons")
    public ResponseData<MemAvailableCouponVO> preOrderAdminCoupons(@RequestBody PreOrderReqVO request, BindingResult result){
        BindingResultUtil.checkBindingResult(result);
        NormalOrderCostCalculateReqVO normalOrderCostCalculateReqVO = new NormalOrderCostCalculateReqVO();
        BeanUtils.copyProperties(request,normalOrderCostCalculateReqVO);
        normalOrderCostCalculateReqVO.setMemNo(request.getMemNo());
        normalOrderCostCalculateReqVO.setOrderCategory("1");
        normalOrderCostCalculateReqVO.setSceneCode("EX007");
        normalOrderCostCalculateReqVO.setSource("1");
        normalOrderCostCalculateReqVO.setPlatformParentType("7");
        if(StringUtils.isNotBlank(request.getLongOwnerCouponNo())) {
            normalOrderCostCalculateReqVO.setOrderCategory("3");
            normalOrderCostCalculateReqVO.setBusinessParentType("6");
        }
        MemAvailableCouponVO memAvailableCouponVO = adminOrderService.getPreOrderCouponList(normalOrderCostCalculateReqVO);
        return ResponseData.success(memAvailableCouponVO);
    }



    public static boolean  isWorkDay(LocalDate localDate){
        DayOfWeek dayOfWeek= localDate.getDayOfWeek();
        if(dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY){
            return false;
        }else {
            return true;
        }
    }

    /**
	 * 获取不能使用不计免赔标记
	 * 【车辆等级】字段为：跑车 并且
	 * 【保费计算用购置价】字段值为：≥10万
	 * @return boolean
	 */
	public Integer getNoAbatementFlagByCarNo(Integer inmsrp, Integer guidePrice, Integer carLevel) {
		logger.info("getNoAbatementFlagByCarNo inmsrp=[{}],guidePrice=[{}],carLevel=[{}]", inmsrp,guidePrice,carLevel);
		if (inmsrp == null) {
			inmsrp = guidePrice;
		}
		inmsrp = inmsrp == null ? 0:inmsrp;
		if (carLevel != null && carLevel.equals(22) && inmsrp >= 100000) {
			return 1;
		}
		return 0;
	}
}
