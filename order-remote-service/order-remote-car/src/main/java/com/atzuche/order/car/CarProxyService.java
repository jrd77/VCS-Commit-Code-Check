package com.atzuche.order.car;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.GlobalConstant;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.OwnerGoodsPriceDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsPriceDetailDTO;
import com.atzuche.order.commons.enums.CarOwnerTypeEnum;
import com.atzuche.order.commons.exceptions.RemoteCallException;
import com.autoyol.car.api.feign.api.CarDetailQueryFeignApi;
import com.autoyol.car.api.model.dto.OrderCarInfoParamDTO;
import com.autoyol.car.api.model.vo.*;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/11 3:03 下午
 **/
@Service
public class CarProxyService {
    private final static Logger log = LoggerFactory.getLogger(CarProxyService.class);

    @Autowired
    private CarDetailQueryFeignApi carDetailQueryFeignApi;

    @Data
    public static class CarDetailReqVO {
        private String carNo;
        private int addrIndex;
        private boolean useSpecialPrice;
        private LocalDateTime rentTime;
        private LocalDateTime revertTime;
    }

    @Data
    public static class CarPriceDetail{
        private List<CarPriceOfDayVO> carPriceOfDayVOList;
        private String plateNum;
    }

    /**
     * 获得车辆的价格参数
     * @param reqVO
     * @return
     */
    public CarPriceDetail getCarPriceDetail(CarDetailReqVO reqVO){
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
            log.info("reponse is [{}]",JSON.toJSONString(responseObject));
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            checkResponse(responseObject);
            CarPriceDetail carPriceDetail = new CarPriceDetail();
            carPriceDetail.setCarPriceOfDayVOList(responseObject.getData().getDaysPrice());
            carPriceDetail.setPlateNum(responseObject.getData().getCarBaseVO().getPlateNum());
            t.setStatus(Transaction.SUCCESS);

            return carPriceDetail;
        }catch (Exception e){
            log.error("Feign 获取车辆信息异常,responseObject={},orderCarInfoParamDTO={}",JSON.toJSONString(responseObject),JSON.toJSONString(orderCarInfoParamDTO),e);
            Cat.logError("Feign 获取车辆信息异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }

    }

    public CarDetailDTO getCarDetail(String carNo){
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "商品信息");
        ResponseObject<CarBaseVO> responseObject = null;
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"CarDetailQueryFeignApi.getCarDetail");
            log.info("Feign 开始获取车辆信息,carNo={}", JSON.toJSONString(carNo));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(carNo));
            responseObject = carDetailQueryFeignApi.getCarDetailByCarNo(Integer.parseInt(carNo));
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            checkResponse(responseObject);
            CarDetailDTO dto = new CarDetailDTO();
            CarBaseVO baseVO = responseObject.getData();
            log.info("baseVo is {}",baseVO);
            BeanUtils.copyProperties(baseVO,dto);
            log.info("dto is {}",dto);
            t.setStatus(Transaction.SUCCESS);
            return dto;
        }catch (Exception e){
            log.error("Feign 获取车辆信息异常,responseObject={},orderCarInfoParamDTO={}",JSON.toJSONString(responseObject),JSON.toJSONString(carNo),e);
            Cat.logError("Feign 获取车辆信息异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }

    /**
     * 获取租客商品信息
     */
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
            log.info("reponse is [{}]",JSON.toJSONString(responseObject));
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 获取车辆信息异常,responseObject={},orderCarInfoParamDTO={}",JSON.toJSONString(responseObject),JSON.toJSONString(orderCarInfoParamDTO),e);
            Cat.logError("Feign 获取车辆信息异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }

        CarDetailVO data = responseObject.getData();
        List<CarInspectVO> carInspectS = data.getCarInspectS();
        CarChargeLevelVO carChargeLevelVO = data.getCarChargeLevelVO();
        CarBaseVO carBaseVO = data.getCarBaseVO();
        CarDetectVO carDetect = data.getCarDetect();
        CarStewardVO carSteward = data.getCarSteward();
        CarDetailImageVO detailImageVO = data.getDetailImageVO();
        CarAddressOfTransVO carAddressOfTransVO = data.getCarAddressOfTransVO();
        CarTagListVO carTagVO = data.getCarTagVO();
        List<CarGpsVO> carGpsVOS = data.getCarGpsVOS();
        TransReplyVO transReplyVO = carBaseVO.getTransReplyVO();
        RenterGoodsDetailDTO renterGoodsDetailDto = new RenterGoodsDetailDTO();
        renterGoodsDetailDto.setCarAge(carBaseVO.getCarAge());
        renterGoodsDetailDto.setSucessRate(carChargeLevelVO!=null?carChargeLevelVO.getSucessRate():null);
        renterGoodsDetailDto.setIsLocal(carBaseVO.getIsLocal());
        renterGoodsDetailDto.setModelTxt(carBaseVO.getModelTxt());
        renterGoodsDetailDto.setChoiceCar(data.isChoiceCar());
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
        renterGoodsDetailDto.setCarOwnerType(carBaseVO.getOwnerType());
        renterGoodsDetailDto.setCarUseType(carBaseVO.getUseType());
        renterGoodsDetailDto.setCarOilVolume(carBaseVO.getOilVolume());
        renterGoodsDetailDto.setCarEngineType(carBaseVO.getEngineType());
        renterGoodsDetailDto.setCarDesc(carBaseVO.getCarDesc());
        renterGoodsDetailDto.setWeekendPrice(null == carBaseVO.getWeekendPrice() ? 0 : carBaseVO.getWeekendPrice());
        renterGoodsDetailDto.setCarStewardPhone(carSteward==null||carSteward.getStewardPhone()==null ? "" : String.valueOf(carSteward.getStewardPhone()));
        renterGoodsDetailDto.setCarCheckStatus(carDetect == null ? null : carDetect.getDetectStatus());
        renterGoodsDetailDto.setCarShowAddr(carAddressOfTransVO==null ? "" : carAddressOfTransVO.getCarVirtualAddress());
        renterGoodsDetailDto.setCarShowLon(carAddressOfTransVO==null || carAddressOfTransVO.getVirtualAddressLon()==null ? "" : String.valueOf(carAddressOfTransVO.getVirtualAddressLon()));
        renterGoodsDetailDto.setCarShowLat(carAddressOfTransVO==null || carAddressOfTransVO.getVirtualAddressLat()==null ? "" : String.valueOf(carAddressOfTransVO.getVirtualAddressLat()));
        renterGoodsDetailDto.setCarRealAddr(carAddressOfTransVO==null ? "" : carAddressOfTransVO.getCarRealAddress());
        renterGoodsDetailDto.setCarRealLon(carAddressOfTransVO==null || carAddressOfTransVO.getRealAddressLon()==null ? "" : String.valueOf(carAddressOfTransVO.getRealAddressLon()));
        renterGoodsDetailDto.setCarRealLat(carAddressOfTransVO==null || carAddressOfTransVO.getRealAddressLat()==null ? "" : String.valueOf(carAddressOfTransVO.getRealAddressLat()));
        renterGoodsDetailDto.setOwnerMemNo(carAddressOfTransVO==null || carBaseVO.getOwnerNo()==null? "" : String.valueOf(carBaseVO.getOwnerNo()));
        renterGoodsDetailDto.setLabelIds(carTagVO == null ? new ArrayList<>():carTagVO.getLabelIds());
        renterGoodsDetailDto.setEngineSource(carBaseVO.getEngineSource());
        renterGoodsDetailDto.setFrameNo(carBaseVO.getFrameNo());
        renterGoodsDetailDto.setEngineNum(carBaseVO.getEngineNum());
        renterGoodsDetailDto.setCarTag(carTagVO == null ? "" : String.join(",",carTagVO.getLabelIds()));
        renterGoodsDetailDto.setType(carBaseVO.getType());
        renterGoodsDetailDto.setYear(carBaseVO.getYear() == null ? "" : String.valueOf(carBaseVO.getYear()));
        renterGoodsDetailDto.setBrand(carBaseVO.getBrand()==null ? null:String.valueOf(carBaseVO.getBrand()));
        renterGoodsDetailDto.setLicenseDay(LocalDateTimeUtils.parseStringToLocalDate(carBaseVO.getLicenseDay()));
        renterGoodsDetailDto.setMoreLicenseFlag(carBaseVO.getMoreLicenseFlag());
        renterGoodsDetailDto.setLicenseExpire(carBaseVO.getLicenseExpireDate()==null?null:LocalDateTimeUtils.dateToLocalDateTime(carBaseVO.getLicenseExpireDate()));
        renterGoodsDetailDto.setIsPlatformShow(carBaseVO.getIsPlatformShow());
        renterGoodsDetailDto.setSeatNum(carBaseVO.getSeatNum());
        CarInspectVO carInspectVO = carInspectS != null && carInspectS.size() > 0 ? carInspectS.get(0) : null;
        String inspectExpire = carInspectVO != null ? carInspectVO.getInspectExpire() : null;
        renterGoodsDetailDto.setInspectExpire(inspectExpire!=null?LocalDateTimeUtils.parseStringToLocalDate(inspectExpire):null);
        renterGoodsDetailDto.setLastMileage(StringUtils.isNotBlank(carBaseVO.getLastMileage()) ? Integer.valueOf(carBaseVO.getLastMileage()) : 0);
        if (data.getCarModelParam() != null) {
            renterGoodsDetailDto.setCarInmsrp(data.getCarModelParam().getInmsrp());
        }
        renterGoodsDetailDto.setStopCostRate(data.getStopCostRate()==null ? 0D:Double.valueOf(data.getStopCostRate()));

        renterGoodsDetailDto.setServiceRate(data.getServiceProportion() == null ? 0D : Double.valueOf(data.getServiceProportion()));
        renterGoodsDetailDto.setServiceProxyRate(0D);
        if(null != carBaseVO.getOwnerType()) {
            if(CarOwnerTypeEnum.G.getCode() == carBaseVO.getOwnerType() || CarOwnerTypeEnum.H.getCode() == carBaseVO.getOwnerType()) {
                //代管车
                renterGoodsDetailDto.setServiceProxyRate(null == data.getServiceProxyProportion() ? 0D : Double.valueOf(data.getServiceProxyProportion()));
                renterGoodsDetailDto.setServiceRate(0D);
            }
        }

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
    public OwnerGoodsDetailDTO getOwnerGoodsDetail(RenterGoodsDetailDTO renterGoodsDetailDto) {
        OwnerGoodsDetailDTO ownerGoodsDetailDto = new OwnerGoodsDetailDTO();
        BeanUtils.copyProperties(renterGoodsDetailDto, ownerGoodsDetailDto);
        ownerGoodsDetailDto.setMemNo(renterGoodsDetailDto.getOwnerMemNo());
        List<OwnerGoodsPriceDetailDTO> ownerGoodsPriceList = renterGoodsDetailDto.getRenterGoodsPriceDetailDTOList()
                .stream()
                .map(x -> {
                    OwnerGoodsPriceDetailDTO ownerGoodsPriceDetailDTO = new OwnerGoodsPriceDetailDTO();
                    BeanUtils.copyProperties(x, ownerGoodsPriceDetailDTO);
                    return ownerGoodsPriceDetailDTO;
                })
                .collect(Collectors.toList());
        ownerGoodsDetailDto.setOwnerGoodsPriceDetailDTOList(ownerGoodsPriceList);
        ownerGoodsDetailDto.setModelTxt(renterGoodsDetailDto.getModelTxt());
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

    public static void main(String[] args) {
        LocalDate localDate = LocalDateTimeUtils.parseStringToLocalDate("2012-06-21");
        System.out.println(localDate);
    }

    public static void  checkResponse(ResponseObject responseObject){
        if(responseObject==null||!ErrorCode.SUCCESS.getCode().equalsIgnoreCase(responseObject.getResCode())){
            RemoteCallException remoteCallException = null;
            if(responseObject!=null){
                remoteCallException = new RemoteCallException(responseObject.getResCode(),responseObject.getResMsg(),responseObject.getData());
            }else{
                remoteCallException = new RemoteCallException(com.atzuche.order.commons.enums.ErrorCode.REMOTE_CALL_FAIL.getCode(),
                        com.atzuche.order.commons.enums.ErrorCode.REMOTE_CALL_FAIL.getText());
            }
            throw remoteCallException;
        }
    }
}