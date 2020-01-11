package com.atzuche.order.admin.controller.car;

import com.atzuche.order.admin.exception.OrderAdminException;
import com.atzuche.order.admin.service.car.CarService;
import com.atzuche.order.admin.vo.req.car.CarBaseReqVO;
import com.atzuche.order.admin.vo.resp.car.CarBusinessResVO;
import com.autoyol.car.api.feign.api.CarDetailQueryFeignApi;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import com.dianping.cat.Cat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@RestController
@AutoDocVersion(version = "车辆信息管理")
public class CarController {

    private  Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CarDetailQueryFeignApi carDetailQueryFeignApi;

    @Autowired
    private CarService carService;

    /**
     * 老后台参考:
     * com.autoyolConsole.controller.TransController.detail(String, String, HttpServletRequest)
     * /autoyolConsole/src/main/webapp/WEB-INF/view/trans/detail.jsp
     */
    @AutoDocMethod(description = "【liujun】订单详细信息-查看车辆信息-车辆运营信息", value = "【liujun】订单详细信息-查看车辆信息-车辆运营信息", response = CarBusinessResVO.class)
    @PostMapping(value = "/car/bussiness")
    public ResponseData <?> getCarBusiness(@Valid @RequestBody CarBaseReqVO reqVo, BindingResult bindingResult) {
        try {
            CarBusinessResVO carBusiness = carService.getCarBusiness(reqVo.getCarNo());
            return ResponseData.success(carBusiness);
        } catch (OrderAdminException e) {
            logger.error("获取车辆运营信息异常[{}]", reqVo, e);
            Cat.logError("获取车辆运营信息异常[{" + reqVo + "}]", e);
            return new ResponseData <>(e.getErrorCode(), e.getErrorMsg());
        } catch (Exception e) {
            logger.error("获取车辆运营信息异常[{}]", reqVo, e);
            Cat.logError("获取车辆运营信息异常[{" + reqVo + "}]", e);
            return ResponseData.error();
        }
    }


}
