package com.atzuche.order.open.service;

import com.atzuche.order.commons.vo.req.AdjustmentOwnerIncomeExamVO;
import com.atzuche.order.commons.vo.req.income.AccountOwnerIncomeExamineOpReqVO;
import com.atzuche.order.commons.vo.req.income.AccountOwnerIncomeExamineReqVO;
import com.atzuche.order.commons.vo.res.account.income.AccountOwnerIncomeRealResVO;
import com.atzuche.order.commons.vo.res.account.income.AdjustOwnerIncomeResVO;
import com.atzuche.order.commons.vo.res.account.income.OwnerIncomeExamineDetailResVO;
import com.autoyol.commons.web.ResponseData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 查询车主收益信息
 **/
@FeignClient(name="order-center-api")
public interface FeignOwnerIncomeService {

    /**
     * 查询车主收益信息
     * @param orderNo
     * @return
     */
    @GetMapping("/owner/income/getOwnerIncomeByOrder")
    public ResponseData<AccountOwnerIncomeRealResVO> getOwnerIncomeByOrder(@RequestParam("orderNo") String orderNo);

    /**
     * 调账成功车主收益接口
     * @param vo
     * @return
     */
    @PostMapping("/owner/income/adjustmentOwnerIncome")
    public ResponseData adjustmentOwnerIncome(@RequestBody AccountOwnerIncomeExamineReqVO vo);

    /**
     * 车主收益审核
     */
    @PostMapping("/owner/income/auditOwnerIncome")
    public ResponseData<AdjustOwnerIncomeResVO> auditOwnerIncome(@RequestBody AccountOwnerIncomeExamineOpReqVO vo);

    @GetMapping("/owner/income/getOwnerIncomeByOrderAndType")
    public ResponseData<OwnerIncomeExamineDetailResVO> getOwnerIncomeByOrderAndType(@RequestParam("orderNo") String orderNo, @RequestParam("memNo") String memNo, @RequestParam("type") Integer type
    );
    @PostMapping("/owner/income/adjustmentOwnerIncomeExam")
    public ResponseData adjustmentOwnerIncomeExam(@RequestBody AdjustmentOwnerIncomeExamVO adjustmentOwnerIncomeExamVO);
}
