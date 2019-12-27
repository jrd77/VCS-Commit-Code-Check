package com.atzuche.order.coreapi.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsPriceDetailDTO;
import com.atzuche.order.coreapi.enums.SubmitOrderErrorEnum;
import com.atzuche.order.coreapi.submitOrder.exception.CarDetailByFeignException;
import com.atzuche.order.coreapi.submitOrder.exception.RenterMemberByFeignException;
import com.autoyol.car.api.feign.api.CarDetailQueryFeignApi;
import com.autoyol.car.api.model.dto.OrderCarInfoParamDTO;
import com.autoyol.car.api.model.vo.*;
import com.autoyol.commons.web.ErrorCode;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 封装对远程车辆详情服务的调用
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/19 4:55 下午
 **/
@Service
public class CarService {
    
    private final static Logger log = LoggerFactory.getLogger(CarService.class);
    
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
            if(responseObject == null || !ErrorCode.SUCCESS.getCode().equals(responseObject.getResCode())){
                log.error("Feign 获取车辆信息失败,responseObject={},orderCarInfoParamDTO={}",JSON.toJSONString(responseObject),JSON.toJSONString(orderCarInfoParamDTO));
                RenterMemberByFeignException renterMemberByFeignException = new RenterMemberByFeignException(SubmitOrderErrorEnum.FEIGN_GET_CAR_DETAIL_FAIL.getCode(), SubmitOrderErrorEnum.FEIGN_GET_CAR_DETAIL_FAIL.getText());
                Cat.logError("Feign 获取车辆信息失败",renterMemberByFeignException);
                throw renterMemberByFeignException;
            }
            t.setStatus(Transaction.SUCCESS);
        }catch (RenterMemberByFeignException e){
            Cat.logError("Feign 获取车辆信息失败",e);
            t.setStatus(e);
            throw e;
        }catch (Exception e){
            log.error("Feign 获取车辆信息异常,responseObject={},orderCarInfoParamDTO={}",JSON.toJSONString(responseObject),JSON.toJSONString(orderCarInfoParamDTO),e);
            CarDetailByFeignException carDetailByFeignException = new CarDetailByFeignException(SubmitOrderErrorEnum.FEIGN_GET_CAR_DETAIL_ERROR.getCode(), SubmitOrderErrorEnum.FEIGN_GET_CAR_DETAIL_ERROR.getText());
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

        RenterGoodsDetailDTO renterGoodsDetailDto = new RenterGoodsDetailDTO();
        renterGoodsDetailDto.setReplyFlag(carBaseVO.getTransReplyVO().getReplyFlag());
        renterGoodsDetailDto.setCarAddrIndex(Integer.valueOf(reqVO.getAddrIndex()));
        renterGoodsDetailDto.setCarNo(carBaseVO.getCarNo());
        renterGoodsDetailDto.setCarPlateNum(carBaseVO.getPlateNum());
        renterGoodsDetailDto.setCarBrand(carBaseVO.getBrand());
        renterGoodsDetailDto.setCarBrandTxt(carBaseVO.getBrandTxt());
        renterGoodsDetailDto.setCarRating(carBaseVO.getRating());
        renterGoodsDetailDto.setCarType(Integer.valueOf(carBaseVO.getType()));
        renterGoodsDetailDto.setCarTypeTxt(carBaseVO.getTypeTxt());
        renterGoodsDetailDto.setCarCylinderCapacity(carBaseVO.getCc()==null?0D:Double.valueOf(carBaseVO.getCc()));
        renterGoodsDetailDto.setCarCcUnit(carBaseVO.getCcUnit());
        renterGoodsDetailDto.setCarGearboxType(carBaseVO.getGbType());
        renterGoodsDetailDto.setCarDayMileage(carBaseVO.getDayMileage());
        renterGoodsDetailDto.setCarIntrod(carBaseVO.getCarDesc());
        renterGoodsDetailDto.setCarSurplusPrice(carBaseVO.getSurplusPrice());
        renterGoodsDetailDto.setCarUseSpecialPrice(reqVO.useSpecialPrice);
        renterGoodsDetailDto.setCarGuidePrice(carBaseVO.getGuidePrice());
        renterGoodsDetailDto.setCarStatus(carBaseVO.getStatus());
        renterGoodsDetailDto.setCarImageUrl(getCoverPic(detailImageVO));
        renterGoodsDetailDto.setCarOwnerType(carBaseVO.getMajorType());
        renterGoodsDetailDto.setCarUseType(carBaseVO.getUseType());
        renterGoodsDetailDto.setCarOilVolume(carBaseVO.getOilVolume());
        renterGoodsDetailDto.setCarEngineType(carBaseVO.getEngineType());
        renterGoodsDetailDto.setCarDesc(carBaseVO.getCarDesc());
        renterGoodsDetailDto.setCarStewardPhone(carSteward.getStewardPhone()==null?"":String.valueOf(carSteward.getStewardPhone()));
        renterGoodsDetailDto.setCarCheckStatus(carDetect.getDetectStatus());
        renterGoodsDetailDto.setCarShowAddr(carAddressOfTransVO.getCarVirtualAddress());
        renterGoodsDetailDto.setCarShowLon(carAddressOfTransVO.getVirtualAddressLon()==null?"":String.valueOf(carAddressOfTransVO.getVirtualAddressLon()));
        renterGoodsDetailDto.setCarShowLat(carAddressOfTransVO.getVirtualAddressLat()==null?"":String.valueOf(carAddressOfTransVO.getVirtualAddressLat()));
        renterGoodsDetailDto.setCarRealAddr(carAddressOfTransVO.getCarRealAddress());
        renterGoodsDetailDto.setCarRealLon(carAddressOfTransVO.getRealAddressLon()==null?"":String.valueOf(carAddressOfTransVO.getRealAddressLon()));
        renterGoodsDetailDto.setCarRealLat(carAddressOfTransVO.getRealAddressLat()==null?"":String.valueOf(carAddressOfTransVO.getRealAddressLat()));
        renterGoodsDetailDto.setOwnerMemNo(String.valueOf(carBaseVO.getOwnerNo()));
        renterGoodsDetailDto.setLabelIds(carTagVO == null?new ArrayList<>():carTagVO.getLabelIds());
        List<RenterGoodsPriceDetailDTO> list = new ArrayList<>();
        List<CarPriceOfDayVO> daysPrice = data.getDaysPrice();
        if(daysPrice == null){
            renterGoodsDetailDto.setRenterGoodsPriceDetailDTOList(list);
            return renterGoodsDetailDto;
        }
        daysPrice.stream().forEach(x->{
            RenterGoodsPriceDetailDTO dto = new RenterGoodsPriceDetailDTO();
            dto.setCarDay(LocalDateTimeUtils.parseStringToLocalDate(x.getDateStr()));
            dto.setCarUnitPrice(dto.getCarUnitPrice());
            list.add(dto);
        });
        renterGoodsDetailDto.setRenterGoodsPriceDetailDTOList(list);
        return renterGoodsDetailDto;
    }
    //获取车主商品信息
    public OwnerGoodsDetailDTO getOwnerGoodsDetail(RenterGoodsDetailDTO renterGoodsDetailDto) throws CarDetailByFeignException, RenterMemberByFeignException {
        OwnerGoodsDetailDTO ownerGoodsDetailDto = new OwnerGoodsDetailDTO();
        BeanUtils.copyProperties(renterGoodsDetailDto, ownerGoodsDetailDto);
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
                .filter(x -> "0".equals(x.getCover()))
                .limit(1)
                .collect(Collectors.toList());
        coverPic = collect.size()<=0 ?  "" : collect.get(0).getPicPath();
        return coverPic;
    }
}
