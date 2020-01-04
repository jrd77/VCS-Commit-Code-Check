package com.atzuche.order.coreapi.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.GlobalConstant;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.OwnerGoodsPriceDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsPriceDetailDTO;
import com.atzuche.order.coreapi.entity.vo.req.CarRentTimeRangeReqVO;
import com.atzuche.order.coreapi.entity.vo.res.CarRentTimeRangeResVO;
import com.atzuche.order.coreapi.submitOrder.exception.CarDetailByFeignException;
import com.atzuche.order.coreapi.submitOrder.exception.RenterCarDetailErrException;
import com.atzuche.order.coreapi.submitOrder.exception.RenterCarDetailFailException;
import com.atzuche.order.coreapi.submitOrder.exception.RenterMemberFailException;
import com.autoyol.car.api.CarRentalTimeApi;
import com.autoyol.car.api.feign.api.CarDetailQueryFeignApi;
import com.autoyol.car.api.model.dto.CarAddressDTO;
import com.autoyol.car.api.model.dto.LocationDTO;
import com.autoyol.car.api.model.dto.OrderCarInfoParamDTO;
import com.autoyol.car.api.model.vo.*;
import com.autoyol.commons.web.ErrorCode;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 封装对远程车辆详情服务的调用
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/19 4:55 下午
 **/
@Service
public class GoodsService {
    
    private final static Logger log = LoggerFactory.getLogger(GoodsService.class);
    
    @Autowired
    private CarDetailQueryFeignApi carDetailQueryFeignApi;

    @Autowired
    private CarRentalTimeApi carRentalTimeApi;

    @Data
    public static class CarDetailReqVO {
        private String carNo;
        private int addrIndex;
        private boolean useSpecialPrice;
        private LocalDateTime rentTime;
        private LocalDateTime revertTime;
    }

    //获取租客商品信息
    public RenterGoodsDetailDTO getRenterGoodsDetail(CarDetailReqVO reqVO){
        OrderCarInfoParamDTO orderCarInfoParamDTO = new OrderCarInfoParamDTO();
        orderCarInfoParamDTO.setCarNo(Integer.parseInt(reqVO.getCarNo()));
        orderCarInfoParamDTO.setCarAddressIndex(Integer.valueOf(reqVO.getAddrIndex()));
        orderCarInfoParamDTO.setRentTime(LocalDateTimeUtils.localDateTimeToLong(reqVO.getRentTime()));
        orderCarInfoParamDTO.setRevertTime(LocalDateTimeUtils.localDateTimeToLong(reqVO.getRevertTime()));
        orderCarInfoParamDTO.setUseSpecialPrice(reqVO.useSpecialPrice);
        ResponseObject<CarDetailVO> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "租客商品信息");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"CarDetailQueryFeignApi.getCarDetailOfTransByCarNo");
            log.info("Feign 开始获取车辆信息,orderCarInfoParamDTO={}", JSON.toJSONString(orderCarInfoParamDTO));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(orderCarInfoParamDTO));
            responseObject = carDetailQueryFeignApi.getCarDetailOfTransByCarNo(orderCarInfoParamDTO);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            if(responseObject == null || !ErrorCode.SUCCESS.getCode().equals(responseObject.getResCode())){
                log.error("Feign 获取车辆信息失败,responseObject={},orderCarInfoParamDTO={}",JSON.toJSONString(responseObject),JSON.toJSONString(orderCarInfoParamDTO));
                RenterCarDetailFailException failException = new RenterCarDetailFailException();
                Cat.logError("Feign 获取车辆信息失败",failException);
                throw failException;
            }
            t.setStatus(Transaction.SUCCESS);
        }catch (RenterCarDetailFailException e){
            Cat.logError("Feign 获取车辆信息失败",e);
            t.setStatus(e);
            throw e;
        }catch (Exception e){
            log.error("Feign 获取车辆信息异常,responseObject={},orderCarInfoParamDTO={}",JSON.toJSONString(responseObject),JSON.toJSONString(orderCarInfoParamDTO),e);
            RenterCarDetailErrException carDetailByFeignException = new RenterCarDetailErrException();
            Cat.logError("Feign 获取车辆信息异常",carDetailByFeignException);
            throw carDetailByFeignException;
        }finally {
            t.complete();
        }

        CarDetailVO data = responseObject.getData();
        CarBaseVO carBaseVO = data.getCarBaseVO();
        CarDetectVO carDetect = data.getCarDetect();
        CarStewardVO carSteward = data.getCarSteward();
        CarDetailImageVO detailImageVO = data.getDetailImageVO();
        CarAddressOfTransVO carAddressOfTransVO = data.getCarAddressOfTransVO();
        CarTagListVO carTagVO = data.getCarTagVO();
        List<CarGpsVO> carGpsVOS = data.getCarGpsVOS();
        TransReplyVO transReplyVO = carBaseVO.getTransReplyVO();
        RenterGoodsDetailDTO renterGoodsDetailDto = new RenterGoodsDetailDTO();
        renterGoodsDetailDto.setRentTime(reqVO.getRentTime());
        renterGoodsDetailDto.setRevertTime(reqVO.getRevertTime());
        renterGoodsDetailDto.setReplyFlag(transReplyVO ==null || transReplyVO.getReplyFlag() == null ? 0: transReplyVO.getReplyFlag());
        renterGoodsDetailDto.setCarAddrIndex(Integer.valueOf(reqVO.getAddrIndex()));
        renterGoodsDetailDto.setCarNo(carBaseVO.getCarNo());
        renterGoodsDetailDto.setCarPlateNum(carBaseVO.getPlateNum());
        renterGoodsDetailDto.setCarBrand(carBaseVO.getBrand());
        renterGoodsDetailDto.setCarBrandTxt(carBaseVO.getBrandTxt());
        renterGoodsDetailDto.setCarRating(carBaseVO.getRating());
        renterGoodsDetailDto.setCarType(Integer.valueOf(carBaseVO.getType()));
        renterGoodsDetailDto.setCarTypeTxt(carBaseVO.getTypeTxt());
        renterGoodsDetailDto.setCarCylinderCapacity(carBaseVO.getCc()==null ? 0D:Double.valueOf(carBaseVO.getCc()));
        renterGoodsDetailDto.setCarCcUnit(carBaseVO.getCcUnit());
        renterGoodsDetailDto.setCarGearboxType(carBaseVO.getGbType());
        renterGoodsDetailDto.setCarDayMileage(carBaseVO.getDayMileage());
        renterGoodsDetailDto.setCarIntrod(carBaseVO.getCarDesc());
        renterGoodsDetailDto.setCarSurplusPrice(carBaseVO.getSurplusPrice());
        renterGoodsDetailDto.setCarGuidePrice(carBaseVO.getGuidePrice());
        renterGoodsDetailDto.setCarStatus(carBaseVO.getStatus());
        renterGoodsDetailDto.setCarImageUrl(getCoverPic(detailImageVO));
        renterGoodsDetailDto.setCarOwnerType(carBaseVO.getMajorType());
        renterGoodsDetailDto.setCarUseType(carBaseVO.getUseType());
        renterGoodsDetailDto.setCarOilVolume(carBaseVO.getOilVolume());
        renterGoodsDetailDto.setCarEngineType(carBaseVO.getEngineType());
        renterGoodsDetailDto.setCarDesc(carBaseVO.getCarDesc());
        renterGoodsDetailDto.setCarStewardPhone(carSteward==null||carSteward.getStewardPhone()==null ? "" : String.valueOf(carSteward.getStewardPhone()));
        renterGoodsDetailDto.setCarCheckStatus(carDetect == null ? null : carDetect.getDetectStatus());
        renterGoodsDetailDto.setCarShowAddr(carAddressOfTransVO==null ? "" : carAddressOfTransVO.getCarVirtualAddress());
        renterGoodsDetailDto.setCarShowLon(carAddressOfTransVO==null || carAddressOfTransVO.getVirtualAddressLon()==null ? "" : String.valueOf(carAddressOfTransVO.getVirtualAddressLon()));
        renterGoodsDetailDto.setCarShowLat(carAddressOfTransVO==null || carAddressOfTransVO.getVirtualAddressLat()==null ? "" : String.valueOf(carAddressOfTransVO.getVirtualAddressLat()));
        renterGoodsDetailDto.setCarRealAddr(carAddressOfTransVO==null ? "" : carAddressOfTransVO.getCarRealAddress());
        renterGoodsDetailDto.setCarRealLon(carAddressOfTransVO==null || carAddressOfTransVO.getRealAddressLon()==null ? "" : String.valueOf(carAddressOfTransVO.getRealAddressLon()));
        renterGoodsDetailDto.setCarRealLat(carAddressOfTransVO==null || carAddressOfTransVO.getRealAddressLat()==null ? "" : String.valueOf(carAddressOfTransVO.getRealAddressLat()));
        renterGoodsDetailDto.setOwnerMemNo(carAddressOfTransVO==null ? "" : String.valueOf(carBaseVO.getOwnerNo()));
        renterGoodsDetailDto.setLabelIds(carTagVO == null ? new ArrayList<>():carTagVO.getLabelIds());
        renterGoodsDetailDto.setEngineSource(carBaseVO.getEngineSource());
        renterGoodsDetailDto.setFrameNo(carBaseVO.getFrameNo());
        renterGoodsDetailDto.setEngineNum(carBaseVO.getEngineNum());
        renterGoodsDetailDto.setCarTag(carTagVO == null ? "" : String.join(",",carTagVO.getLabelIds()));
        renterGoodsDetailDto.setType(carBaseVO.getType());
        renterGoodsDetailDto.setBrand(carBaseVO.getBrand()==null ? null:String.valueOf(carBaseVO.getBrand()));
        renterGoodsDetailDto.setLicenseDay(LocalDateTimeUtils.parseStringToLocalDate(carBaseVO.getLicenseDay()));
        renterGoodsDetailDto.setCarInmsrp(data.getCarModelParam().getInmsrp());
        renterGoodsDetailDto.setStopCostRate(data.getStopCostRate()==null ? 0D:Double.valueOf(data.getStopCostRate()));
        renterGoodsDetailDto.setServiceRate(data.getServerRate()==null ? 0D:Double.valueOf(data.getServerRate()));
        renterGoodsDetailDto.setCarGuideDayPrice(carBaseVO.getGuideDayPrice());
        renterGoodsDetailDto.setOilTotalCalibration(carBaseVO.getOilTotalCalibration());
        String serialNumbers = Optional.ofNullable(carGpsVOS)
                .orElseGet(ArrayList::new)
                .stream()
                .map(x -> x.getSerialNumber())
                .collect(Collectors.joining(","));
        renterGoodsDetailDto.setGpsSerialNumber(serialNumbers);

        List<RenterGoodsPriceDetailDTO> list = new ArrayList<>();
        List<CarPriceOfDayVO> daysPrice = data.getDaysPrice();
        if(daysPrice == null){
            renterGoodsDetailDto.setRenterGoodsPriceDetailDTOList(list);
            return renterGoodsDetailDto;
        }
        daysPrice.stream().forEach(x->{
            RenterGoodsPriceDetailDTO dto = new RenterGoodsPriceDetailDTO();
            dto.setCarDay(LocalDateTimeUtils.parseStringToLocalDate(x.getDateStr(), GlobalConstant.FORMAT_DATE_STR));
            dto.setCarUnitPrice(x.getPrice());
            list.add(dto);
        });
        renterGoodsDetailDto.setRenterGoodsPriceDetailDTOList(list);
        return renterGoodsDetailDto;
    }
    //获取车主商品信息
    public OwnerGoodsDetailDTO getOwnerGoodsDetail(RenterGoodsDetailDTO renterGoodsDetailDto) throws CarDetailByFeignException, RenterMemberFailException {
        OwnerGoodsDetailDTO ownerGoodsDetailDto = new OwnerGoodsDetailDTO();
        BeanUtils.copyProperties(renterGoodsDetailDto, ownerGoodsDetailDto);
        List<OwnerGoodsPriceDetailDTO> ownerGoodsPriceList = renterGoodsDetailDto.getRenterGoodsPriceDetailDTOList()
                .stream()
                .map(x -> {
                    OwnerGoodsPriceDetailDTO ownerGoodsPriceDetailDTO = new OwnerGoodsPriceDetailDTO();
                    BeanUtils.copyProperties(x, ownerGoodsPriceDetailDTO);
                    return ownerGoodsPriceDetailDTO;
                })
                .collect(Collectors.toList());
        ownerGoodsDetailDto.setOwnerGoodsPriceDetailDTOList(ownerGoodsPriceList);
        return ownerGoodsDetailDto;
    }
    //获取车辆封面图片路径
    private String getCoverPic(CarDetailImageVO detailImageVO){
        String coverPic = "";
        if(detailImageVO == null || detailImageVO.getCarImages() == null || detailImageVO.getCarImages().size()<=0){
            return coverPic;
        }
        List<ImageVO> collect = detailImageVO.getCarImages()
                .stream()
                .filter(x -> "1".equals(x.getCover()))
                .limit(1)
                .collect(Collectors.toList());
        coverPic = collect.size()<=0 ?  "" : collect.get(0).getPicPath();
        return coverPic;
    }


    /**
     * 提前延后时间计算
     *
     * @param reqVO 请求参数
     * @return CarRentTimeRangeResVO 返回结果
     */
    public CarRentTimeRangeResVO getCarRentTimeRange(CarRentTimeRangeReqVO reqVO) {
        log.info("提前延后时间计算. param is,reqVO:[{}]", JSON.toJSONString(reqVO));

        CarAddressDTO carAddressDTO = new CarAddressDTO();
        carAddressDTO.setCarNo(Integer.valueOf(reqVO.getCarNo()));
        carAddressDTO.setCityCode(Integer.valueOf(reqVO.getCityCode()));
        carAddressDTO.setStartDate(LocalDateTimeUtils.localDateTimeToDate(reqVO.getRentTime()));
        carAddressDTO.setEndDate(LocalDateTimeUtils.localDateTimeToDate(reqVO.getRevertTime()));

        LocationDTO getCarAddress = new LocationDTO();
        getCarAddress.setFlag(reqVO.getSrvGetFlag());
        getCarAddress.setCarAddress(reqVO.getSrvGetAddr());
        getCarAddress.setLat(Double.valueOf(reqVO.getSrvGetLat()));
        getCarAddress.setLon(Double.valueOf(reqVO.getSrvGetLon()));
        LocationDTO returnCarAddress = new LocationDTO();
        returnCarAddress.setFlag(reqVO.getSrvReturnFlag());
        returnCarAddress.setCarAddress(reqVO.getSrvReturnAddr());
        returnCarAddress.setLat(Double.valueOf(reqVO.getSrvReturnLat()));
        returnCarAddress.setLon(Double.valueOf(reqVO.getSrvReturnLon()));

        carAddressDTO.setGetCarAddress(getCarAddress);
        carAddressDTO.setReturnCarAddress(returnCarAddress);
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "车辆服务");
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD, "carRentalTimeApi.getCarRentTimeRange");
            Cat.logEvent(CatConstants.FEIGN_PARAM, "reqVO=" + JSON.toJSONString(reqVO));
            ResponseObject<CarAddressDTO> responseObject = carRentalTimeApi.getCarRentTimeRange(carAddressDTO);
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(responseObject));
            log.info("提前延后时间计算. result is,responseObject:[{}]", JSON.toJSONString(responseObject));

            if (null == responseObject || null == responseObject.getData() || !StringUtils.equals(responseObject.getResCode(), ErrorCode.SUCCESS.getCode())) {
                t.setStatus(new RenterCarDetailFailException(responseObject.getResCode(),responseObject.getResMsg()));
            } else {
                CarAddressDTO carAddress = responseObject.getData();
                CarRentTimeRangeResVO carRentTimeRangeResVO = new CarRentTimeRangeResVO();
                carRentTimeRangeResVO.setGetMinutes(carAddress.getGetMinutes());
                carRentTimeRangeResVO.setReturnMinutes(carAddress.getReturnMinutes());
                carRentTimeRangeResVO.setAdvanceStartDate(LocalDateTimeUtils.dateToLocalDateTime(carAddress.getAdvanceStartDate()));
                carRentTimeRangeResVO.setDelayEndDate(LocalDateTimeUtils.dateToLocalDateTime(carAddress.getDelayEndDate()));
                t.setStatus(Transaction.SUCCESS);
                return carRentTimeRangeResVO;
            }
        } catch (Exception e) {
            t.setStatus(e);
            log.info("提前延后时间计算异常.", e);
            Cat.logError("提前延后时间计算异常.", e);
        } finally {
            t.complete();
        }
        return null;
    }
}
