package com.atzuche.order.commons.vo.res.account.income;

import com.atzuche.order.commons.entity.orderDetailDto.AccountOwnerIncomeExamineDTO;
import lombok.Data;

import java.util.List;

@Data
public class OwnerIncomeExamineListResVO {

   List<AccountOwnerIncomeExamineDTO> accountOwnerIncomeExamineDTOS;
}
