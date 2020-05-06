package com.atzuche.order.commons.vo.res.account.income;

import com.atzuche.order.commons.entity.orderDetailDto.AccountOwnerIncomeExamineDTO;
import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

import java.util.List;


@Data
public class OwnerIncomeExamineListResVO {
    /**
     * 订单号
     */
    @AutoDocProperty("收益明细")
    private List<AccountOwnerIncomeExamineDTO> accountOwnerIncomeExamineDTOS;

}
