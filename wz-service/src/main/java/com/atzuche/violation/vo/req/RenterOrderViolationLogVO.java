package com.atzuche.violation.vo.req;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

@Data
@ToString
public class RenterOrderViolationLogVO {

    @AutoDocProperty(value = "违章处理方,1:租客自行办理违章 2:凹凸代为办理违章 3:车主自行办理 4:无数据")
    private String managementMode; //违章处理方

    @AutoDocProperty(value = "违章处理备注")
    private String wzRemarks; //违章处理备注

    @AutoDocProperty(value = "违章处理办理完成时间,格式yyyy-MM-dd HH:mm:ss")
    private String wzHandleCompleteTime;

    @AutoDocProperty(value = "租客最晚处理时间,yyyy-MM-dd HH:mm:ss")
    private String wzRenterLastTime;

    @AutoDocProperty(value = "平台最晚处理时间,yyyy-MM-dd HH:mm:ss")
    private String wzPlatformLastTime;


}
