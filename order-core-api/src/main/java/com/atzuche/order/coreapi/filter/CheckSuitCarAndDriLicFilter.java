package com.atzuche.order.coreapi.filter;

import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.commons.exceptions.CarSettingCheckException;
import com.atzuche.order.commons.filter.OrderFilter;
import com.atzuche.order.commons.filter.OrderFilterException;
import com.autoyol.commons.web.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/*
 * @Author ZhangBin
 * @Date 2020/2/25 10:58
 * @Description: 准驾车型校验
 *
 **/
@Slf4j
@Service("checkSuitCarAndDriLicFilter")
public class CheckSuitCarAndDriLicFilter implements OrderFilter {

    @Override
    public void validate(OrderReqContext context) throws OrderFilterException {
        RenterGoodsDetailDTO renterGoodsDetailDto = context.getRenterGoodsDetailDto();
        RenterMemberDTO renterMemberDto = context.getRenterMemberDto();
        Integer carGearboxType = renterGoodsDetailDto.getCarGearboxType();
        Integer seatNum = renterGoodsDetailDto.getSeatNum();
        String driLicAllowCar = renterMemberDto.getDriLicAllowCar();
        log.info("Filter checkSuitCarAndDriLicFilter carGearboxType={},seatNum={},driLicAllowCar={}",carGearboxType,seatNum,driLicAllowCar);
        checkSuitCarAndDriLic(carGearboxType, driLicAllowCar);
        // C1驾照开放下单9座蓝牌车辆
        checkSuitCarSeatAndDriLic(seatNum, driLicAllowCar);
    }

    public Boolean checkSuitCarAndDriLic(Integer gearboxType, String driLicAllowCar) throws CarSettingCheckException {
        boolean driLicFlag = false;
        if (driLicAllowCar != null) {
            if (driLicAllowCar.indexOf("C2") > -1) { // 未找到的值为 -1
                driLicFlag = true;
            } else if (driLicAllowCar.indexOf("c2") > -1) { // 未找到的值为 -1
                driLicFlag = true;
            }
        } else { // 否则 driLicAllowCar为null，跳过验证。
            return true;
        }

        if (driLicFlag == false) { // 直接跳过。 //"C2".equals(driLicAllowCar) ||
            return true;
        }
        if (gearboxType!=null && gearboxType == 1 && driLicFlag) {
            throw new CarSettingCheckException(ErrorCode.CAR_GEARBOXTYPE_DRILIC_NOT_ALLOW.getCode(),ErrorCode.CAR_GEARBOXTYPE_DRILIC_NOT_ALLOW.getText());
        }
        return true;
    }

    public Boolean checkSuitCarSeatAndDriLic(Integer seatNum, String driLicAllowCar) throws CarSettingCheckException {
        boolean driLicFlag = false;
        if (driLicAllowCar != null) {
            if (driLicAllowCar.indexOf("A1") > -1 || driLicAllowCar.indexOf("A2") > -1
                    || driLicAllowCar.indexOf("A3") > -1 || driLicAllowCar.indexOf("B1") > -1
                    || driLicAllowCar.indexOf("C1") > -1) { /*AUT-2032需求*/ // 未找到的值为
                driLicFlag = true;
            } else if (driLicAllowCar.indexOf("a1") > -1 || driLicAllowCar.indexOf("a2") > -1
                    || driLicAllowCar.indexOf("a3") > -1 || driLicAllowCar.indexOf("b1") > -1
                    || driLicAllowCar.indexOf("c1") > -1) { /*AUT-2032需求*/ // 未找到的值为
                driLicFlag = true;
            }
        } else { // 否则 driLicAllowCar为null，跳过验证。
            return true;
        }

        if (driLicFlag) { // 直接跳过。 驾照级别高，车都可以开，不验证座位数。
            return true;
        } else {
            if (seatNum!=null && seatNum == 8) {
                throw new CarSettingCheckException(ErrorCode.CAR_SEATNUM_DRILIC_NOT_ALLOW.getCode(),ErrorCode.CAR_SEATNUM_DRILIC_NOT_ALLOW.getText());
            }
        }
        return true;
    }
}
