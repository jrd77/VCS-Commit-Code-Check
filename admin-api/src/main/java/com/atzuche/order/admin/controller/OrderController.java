package com.atzuche.order.admin.controller;

import com.atzuche.order.admin.vo.req.order.CancelOrderByPlatVO;
import com.atzuche.order.admin.vo.req.order.CancelOrderVO;
import com.atzuche.order.admin.vo.req.order.OrderModifyTimeVO;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocGroup;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 订单操作接口
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/7 11:08 上午
 **/
@RestController
public class OrderController {

    @AutoDocVersion(version = "订单修改")
    @AutoDocGroup(group = "订单修改")
    @AutoDocMethod(description = "修改订单", value = "修改订单",response = ResponseData.class)
    @RequestMapping(value="console/order/modifyTime",method = RequestMethod.POST)
    public ResponseData modifyOrderTime(@RequestBody OrderModifyTimeVO orderModifyTimeVO,
                                        HttpServletRequest request, HttpServletResponse response)throws Exception{
        //TODO:
        return null;
    }

    @AutoDocVersion(version = "订单修改")
    @AutoDocGroup(group = "订单修改")
    @AutoDocMethod(description = "平台取消", value = "平台取消",response = ResponseData.class)
    @RequestMapping(value="console/order/cancel/plat",method = RequestMethod.POST)
    public ResponseData cancelOrderByPlat(@RequestBody CancelOrderByPlatVO cancelOrderByPlatVO, HttpServletRequest request, HttpServletResponse response)throws Exception{
        //TODO:
        return null;
    }

    @AutoDocVersion(version = "订单修改")
    @AutoDocGroup(group = "订单修改")
    @AutoDocMethod(description = "车主或者租客取消", value = "车主或者租客取消",response = ResponseData.class)
    @RequestMapping(value="console/order/cancel",method = RequestMethod.POST)
    public ResponseData cancelOrder(@RequestBody CancelOrderVO cancelOrderVO, HttpServletRequest request, HttpServletResponse response)throws Exception{
        //TODO:
        return null;
    }
}
