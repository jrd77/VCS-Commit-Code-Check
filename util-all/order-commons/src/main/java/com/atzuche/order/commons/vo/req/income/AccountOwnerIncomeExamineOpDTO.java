package com.atzuche.order.commons.vo.req.income;

import com.autoyol.commons.web.ErrorCode;
import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.util.Assert;

import javax.validation.constraints.Size;
import java.util.List;

@Data
public class AccountOwnerIncomeExamineOpDTO {
    /**
     * 车主审核收益ExamineId集合（支持批量审核用）
     */
    @Size(min = 1,max = 100,message = "必须选择一条记录且不超过100条记录")
    private List<AccountOwnerIncomExamineVO> accountOwnerIncomExamineVOS;

    /**
     * 审核车主状态类型
     */
    @AutoDocProperty(value = "收益审核状态,1:未审核，2:审核通过,3:审核不通过,4:审核中，待核查（异常）,5:审核中，待核查（测试）,99:其他")
    @NotBlank(message = "收益审核状态不能为空")
    private Integer status;

    /**
     * 收益审核描述
     */
    private String detail;

    /**
     * 审核人
     */
    private String opName;

    /**
     * 更新人
     */
    private String updateOp;
}
