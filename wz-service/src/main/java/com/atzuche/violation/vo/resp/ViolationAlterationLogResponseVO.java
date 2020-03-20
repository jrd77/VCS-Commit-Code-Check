package com.atzuche.violation.vo.resp;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ViolationAlterationLogResponseVO {

    @AutoDocProperty(value = "操作内容")
    private String content;

    @AutoDocProperty(value = "操作类型")
    private String operationType;

    @AutoDocProperty(value="操作人")
    private String operator;

    @AutoDocProperty(value = "操作时间")
    private String createTime;


}
