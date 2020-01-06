package com.atzuche.order.admin.vo.resp.car;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author meng.xu
 * 托管车出入库响应信息-操作日志信息
 */
@Data
@ToString
public class ManagedCarOperateLogVO implements Serializable {
    @AutoDocProperty(value = "日期")
    private String operateTime;
    @AutoDocProperty(value = "操作人")
    private String operator;
    @AutoDocProperty(value = "操作内容")
    private String desc;
}
