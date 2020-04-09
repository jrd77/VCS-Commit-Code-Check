package com.atzuche.order.coreapi.controller;

import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeExamineEntity;
import com.atzuche.order.accountownerincome.service.notservice.AccountOwnerIncomeExamineNoTService;
import com.atzuche.order.cashieraccount.service.CashierQueryService;
import com.atzuche.order.cashieraccount.service.CashierService;
import com.atzuche.order.commons.BindingResultUtil;
import com.atzuche.order.commons.vo.req.AdjustmentOwnerIncomeExamVO;
import com.atzuche.order.commons.vo.req.income.AccountOwnerIncomeExamineOpReqVO;
import com.atzuche.order.commons.vo.req.income.AccountOwnerIncomeExamineReqVO;
import com.atzuche.order.commons.vo.res.account.income.*;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


/**
 * 车主收益
 */
@RestController
@RequestMapping("owner/income")
@Slf4j
public class OwnerIncomeController {
    @Autowired
    private CashierService cashierService;
	@Autowired
	private CashierQueryService cashierQueryService;
    @Autowired
    private AccountOwnerIncomeExamineNoTService accountOwnerIncomeExamineNoTService;
    /**
     * 查询车主收益信息
     * @param orderNo
     * @return
     */
    @AutoDocMethod(value = "查询车主收益信息", description = "查询车主收益信息", response = AccountOwnerIncomeRealResVO.class)
    @GetMapping("/getOwnerIncomeByOrder")
    public ResponseData<AccountOwnerIncomeRealResVO> getOwnerIncomeByOrder(@RequestParam("orderNo") String orderNo,@RequestParam("memNo") String memNo) {
        log.info("OwnerIncomeController getOwnerIncomeByOrder start param [{}]", orderNo);
        Assert.notNull(orderNo,"主单号不能为空");
        Assert.notNull(memNo,"主单号不能为空");
        AccountOwnerIncomeRealResVO resVO = cashierQueryService.getOwnerRealIncomeByOrder(orderNo,memNo);
        log.info("OwnerIncomeController getOwnerIncomeByOrder end param [{}],result [{}]",orderNo,GsonUtils.toJson(resVO));
        return ResponseData.success(resVO);
    }
    @AutoDocMethod(value = "调账成功车主收益接口", description = "调账成功车主收益接口")
    @PostMapping("/adjustmentOwnerIncome")
    public ResponseData adjustmentOwnerIncome(@RequestBody AccountOwnerIncomeExamineReqVO vo){
        log.info("OwnerIncomeController adjustmentOwnerIncome start param [{}]", GsonUtils.toJson(vo));
        cashierService.insertOwnerIncomeExamine(vo);
        log.info("OwnerIncomeController adjustmentOwnerIncome end param [{}]",GsonUtils.toJson(vo));
        return ResponseData.success();
    }

    @AutoDocMethod(value = "车主收益审核", description = "车主收益审核", response = AdjustOwnerIncomeResVO.class)
    @PostMapping("/auditOwnerIncome")
    public ResponseData<AdjustOwnerIncomeResVO> auditOwnerIncome(@RequestBody AccountOwnerIncomeExamineOpReqVO vo){
        log.info("OwnerIncomeController auditOwnerIncome start param [{}]", GsonUtils.toJson(vo));
        AdjustOwnerIncomeResVO resVO = cashierService.examineOwnerIncomeExamine(vo);
        log.info("OwnerIncomeController auditOwnerIncome end param [{}]",GsonUtils.toJson(vo));
        return ResponseData.success(resVO);
    }

    @AutoDocMethod(value = "查询车主收益信息", description = "查询车主收益信息", response = OwnerIncomeExamineDetailResVO.class)
    @GetMapping("/getOwnerIncomeByOrderAndType")
    public ResponseData<OwnerIncomeExamineDetailResVO> getOwnerIncomeByOrderAndType(@RequestParam("orderNo") String orderNo,
                                         @RequestParam("memNo") String memNo,
                                         @RequestParam("type") Integer type
    ){
        log.info("OwnerIncomeController getOwnerIncomeByOrderAndType start param [{}] [{}] [{}]", orderNo,memNo,type);
        OwnerIncomeExamineDetailResVO vo = new OwnerIncomeExamineDetailResVO();
        List<OwnerIncomeExamineDetailVO> ownerIncomeExamineDetails = new ArrayList<>();
        vo.setMemNo(memNo);
        vo.setOrderNo(orderNo);
        vo.setType(type);
        List<AccountOwnerIncomeExamineEntity> resVO = cashierService.getOwnerIncomeByOrderAndType(orderNo,memNo,type);
        if(!CollectionUtils.isEmpty(resVO)){
            for(int i =0 ;i<resVO.size();i++){
                OwnerIncomeExamineDetailVO ownerIncomeExamine = new OwnerIncomeExamineDetailVO();
                AccountOwnerIncomeExamineEntity entity =  resVO.get(i);
                BeanUtils.copyProperties(entity,ownerIncomeExamine);
                ownerIncomeExamineDetails.add(ownerIncomeExamine);
            }
        }
        vo.setOwnerIncomeExamineDetailVO(ownerIncomeExamineDetails);
        log.info("OwnerIncomeController getOwnerIncomeByOrderAndType end param [{}] [{}]",orderNo,GsonUtils.toJson(vo));
        return ResponseData.success(vo);
    }

    @AutoDocMethod(value = "调账收益审核接口", description = "调账收益审核接口")
    @PostMapping("/adjustmentOwnerIncomeExam")
    public ResponseData adjustmentOwnerIncomeExam(@RequestBody @Valid AdjustmentOwnerIncomeExamVO adjustmentOwnerIncomeExamVO, BindingResult bindingResult){
        BindingResultUtil.checkBindingResult(bindingResult);
        log.info("OwnerIncomeController adjustmentOwnerIncome start param [{}]", GsonUtils.toJson(adjustmentOwnerIncomeExamVO));
        accountOwnerIncomeExamineNoTService.adjustmentOwnerIncomeExam(adjustmentOwnerIncomeExamVO);
        log.info("OwnerIncomeController adjustmentOwnerIncome end param [{}]",GsonUtils.toJson(adjustmentOwnerIncomeExamVO));
        return ResponseData.success();
    }
}
