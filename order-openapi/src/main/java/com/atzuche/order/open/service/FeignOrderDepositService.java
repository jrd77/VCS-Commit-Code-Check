package com.atzuche.order.open.service;

import com.atzuche.order.commons.entity.dto.WzCostLogDTO;
import com.atzuche.order.commons.entity.orderDetailDto.RenterOrderWzCostDetailDTO;
import com.atzuche.order.commons.vo.detain.CarDepositDetainReqVO;
import com.atzuche.order.commons.vo.detain.IllegalDepositDetainReqVO;
import com.atzuche.order.commons.vo.res.console.ConsoleOrderWzDetailQueryResVO;
import com.autoyol.commons.web.ResponseData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

/**
 * 订单押金操作集合
 *
 * @author pengcheng.fu
 * @date 2020/4/28 17:16
 */

@FeignClient(name = "order-center-api")
public interface FeignOrderDepositService {

    /**
     * 订单违章押金暂扣/撤销暂扣接口
     *
     * @param req 请求参数
     * @return ResponseData<?>
     */
    @PostMapping("/order/wz/deposit/detain/save")
    ResponseData<?> illegalDepositDetain(@Valid @RequestBody IllegalDepositDetainReqVO req);

    /**
     * 订单车辆押金暂扣/撤销暂扣接口
     *
     * @param req 请求参数
     * @return ResponseData<?>
     */
    @PostMapping("/order/car/deposit/detain/save")
    ResponseData<?> carDepositDetain(@Valid @RequestBody CarDepositDetainReqVO req);

    /**
     * 获取订单违章明细接口
     *
     * @param orderNo 订单类型
     * @return ResponseData<ConsoleOrderWzDetailQueryResVO>
     */
    @GetMapping("/order/wz/detail/get")
    ResponseData<ConsoleOrderWzDetailQueryResVO> queryWzDetailByOrderNo(@RequestParam("orderNo") String orderNo);


    /**
     * 获取订单违章费用变更日志接口
     *
     * @param orderNo 订单号
     * @return ResponseData<List<WzCostLogDTO>>
     */
    @GetMapping("/order/wz/cost/optlog/get")
    ResponseData<List<WzCostLogDTO>> queryWzCostOptLogByOrderNo(@RequestParam("orderNo") String orderNo);

    /**
     * 保存订单违章费用变更日志接口
     *
     * @param req 日志信息
     * @return ResponseData<?>
     */
    @PostMapping("/order/wz/cost/optlog/save")
    ResponseData<?> saveWzCostOptLog(@RequestBody WzCostLogDTO req);


    /**
     * 保存订单违章费用明细接口
     *
     * @param req 费用信息
     * @return ResponseData<?>
     */
    @PostMapping("/order/wz/cost/detail/save")
    ResponseData<?> saveWzCostDetail(@RequestBody RenterOrderWzCostDetailDTO req);
}
