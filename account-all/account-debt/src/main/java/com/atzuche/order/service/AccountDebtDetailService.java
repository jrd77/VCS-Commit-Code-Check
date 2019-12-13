package com.atzuche.order.service;

import com.atzuche.order.dto.AccountDeductDebtDTO;
import com.atzuche.order.exception.AccountDebtException;
import com.atzuche.order.service.notservice.AccountDebtDetailNoTService;
import com.atzuche.order.service.notservice.AccountDebtNoTService;
import com.atzuche.order.service.notservice.AccountDebtReceivableaDetailNoTService;
import com.atzuche.order.vo.req.AccountDeductDebtReqVO;
import com.atzuche.order.vo.req.AccountInsertDebtReqVO;
import com.atzuche.order.vo.res.AccountDebtResVO;
import com.autoyol.commons.web.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.AccountDebtDetailMapper;
import com.atzuche.order.entity.AccountDebtDetailEntity;
import org.springframework.util.Assert;

import java.util.List;


/**
 * 个人历史欠款明细
 *
 * @author ZhangBin
 * @date 2019-12-11 17:34:34
 */
@Service
public class AccountDebtDetailService{
    @Autowired
    private AccountDebtNoTService accountDebtNoTService;
    @Autowired
    private AccountDebtDetailNoTService accountDebtDetailNoTService;
    @Autowired
    private AccountDebtReceivableaDetailNoTService accountDebtReceivableaDetailNoTService;



}
