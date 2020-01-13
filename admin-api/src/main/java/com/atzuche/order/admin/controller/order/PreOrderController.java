package com.atzuche.order.admin.controller.order;

import com.atzuche.order.admin.vo.req.order.PreOrderAdminRequestVO;
import com.atzuche.order.admin.vo.resp.order.PreOrderAdminResponseVO;
import com.atzuche.order.car.CarProxyService;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsPriceDetailDTO;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.atzuche.order.mem.MemProxyService;
import com.autoyol.doc.annotation.AutoDocGroup;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 下单前管理后台页面展示
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/11 1:24 下午
 **/
@RestController
public class PreOrderController {
    private final static Logger logger = LoggerFactory.getLogger(PreOrderController.class);
    

    @Autowired
    private MemProxyService memProxyService;

    @Autowired
    private CarProxyService carProxyService;

    @AutoDocVersion(version = "下单前确定页面")
    @AutoDocGroup(group = "下单前确定页面")
    @AutoDocMethod(description = "下单前确定页面展示", value = "下单前确定页面展示",response = PreOrderAdminResponseVO.class)
    @PostMapping("console/order/adminPre")
    public ResponseData<PreOrderAdminResponseVO> preOrderAdmin(@RequestBody PreOrderAdminRequestVO request, BindingResult result){
        if (result.hasErrors()) {
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }

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


        CarProxyService.CarDetailReqVO carDetailReqVO = new CarProxyService.CarDetailReqVO();
        carDetailReqVO.setAddrIndex(0);
        carDetailReqVO.setCarNo(request.getCarNo());
        carDetailReqVO.setUseSpecialPrice(true);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime rentTime = LocalDateTime.parse(request.getRentTime(),dateTimeFormatter);

        LocalDateTime revertTime = LocalDateTime.parse(request.getRevertTime(),dateTimeFormatter);
        carDetailReqVO.setRentTime(rentTime);
        carDetailReqVO.setRevertTime(revertTime);

        RenterGoodsDetailDTO renterGoodsDetailDTO = carProxyService.getRenterGoodsDetail(carDetailReqVO);

        responseVO.setCarPlatNo(renterGoodsDetailDTO.getCarPlateNum());
        List<RenterGoodsPriceDetailDTO> renterGoodsPriceDetailDTOList = renterGoodsDetailDTO.getRenterGoodsPriceDetailDTOList();

        List<PreOrderAdminResponseVO.CarDayPrice> carDayPrices = new ArrayList<>();

        for(RenterGoodsPriceDetailDTO dto:renterGoodsPriceDetailDTOList){
            logger.info("dto is {}",dto);
            PreOrderAdminResponseVO.CarDayPrice carDayPrice = new PreOrderAdminResponseVO.CarDayPrice();
            carDayPrice.setDay(dto.getCarDay().toString());
            carDayPrice.setPrice(dto.getCarUnitPrice().toString());
            carDayPrice.setDesc("工作日");
            carDayPrices.add(carDayPrice);
        }

        responseVO.setCarSpecialDayPrices(carDayPrices);

        //TODO:
        responseVO.setTotalWallet(0);
        responseVO.setTotalAutoCoin(0);

        return ResponseData.success(responseVO);

    }
}
