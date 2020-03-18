package com.atzuche.violation.entity;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TestEntity {

    @AutoDocProperty(value = "收益编号")
    private String examineId;

    @AutoDocProperty(value = "收益审核结果")
    private String auditResult;

    @AutoDocProperty(value = "收益审核描述")
    private String auditExplain;


    @AutoDocProperty(value = "创建人")
    private String createOp;

    @AutoDocProperty(value = "操作人")
    private String updateOp;


    @AutoDocProperty(value = "创建时间")
    private String createTime;

    @AutoDocProperty(value = "修改时间")
    private String updateTime;




}
