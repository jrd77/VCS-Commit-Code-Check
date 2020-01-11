package com.atzuche.order.admin.controller.order;

import com.atzuche.order.admin.vo.req.order.PreOrderAdminRequestVO;
import com.atzuche.order.admin.vo.resp.order.PreOrderAdminResponseVO;
import com.atzuche.order.car.CarProxyService;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.atzuche.order.mem.MemProxyService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 下单前管理后台页面展示
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/11 1:24 下午
 **/
@RestController
public class PreOrderController {

    @Autowired
    private MemProxyService memProxyService;

    @Autowired
    private CarProxyService carProxyService;


    @PostMapping("console/order/adminPre")
    public ResponseData<PreOrderAdminResponseVO> preOrderAdmin(@RequestBody PreOrderAdminRequestVO request, BindingResult result){
        if (result.hasErrors()) {
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }

        String memNo = request.getMemNo();
        if(StringUtils.trimToNull(memNo)==null){
            String mobile = request.getMobile();
            if(StringUtils.trimToNull(mobile)==null){
                throw new RuntimeException("memNo or mobile cannot be null at same time");
            }

            memNo = memProxyService.getMemNoByMoile(mobile).toString();
        }

        CarProxyService.CarDetailReqVO carDetailReqVO = new CarProxyService.CarDetailReqVO();
        carDetailReqVO.setAddrIndex(0);
        carDetailReqVO.setCarNo(request.getCarNo());
        carDetailReqVO.setUseSpecialPrice(true);





        //FIXME:temp
        return null;

    }
}
