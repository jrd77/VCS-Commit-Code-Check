package com.atzuche.order.commons.entity.dto;

import com.atzuche.order.commons.PageBean;
import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class MemberDebtListReqDTO implements Serializable {
    @AutoDocProperty("会员号")
    private String memNo;
    @AutoDocProperty("手机号")
    private String mobile;
    @AutoDocProperty("姓名")
    private String realName;
    @AutoDocProperty(value = "页码")
    @NotNull(message="页码不允许为空")
    private Integer pageNum;
    @AutoDocProperty(value = "每页大小")
    private Integer pageSize = 10;//默认10
}
