package com.atzuche.order.admin.controller;

import com.atzuche.order.admin.vo.resp.insurance.OrderInsuranceResponseVO;
import com.atzuche.order.admin.vo.resp.order.OrderShortDetailListVO;
import com.atzuche.order.admin.vo.resp.order.OrderShortDetailVO;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 订单详情接口
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/6 7:03 下午
 **/
@RestController
public class OrderDetailController {

      @AutoDocMethod(description = "订单历史信息", value = "订单历史信息",response = OrderShortDetailListVO.class)
      @RequestMapping(value = "console/order/history/list", method = RequestMethod.GET)
      public ResponseData<OrderShortDetailListVO> listOrderHistory(@RequestParam("orderNo") String orderNo,
                                                                   HttpServletRequest request,
                                                                   HttpServletResponse response)throws Exception{
          //TODO:
          return null;

      }
}
