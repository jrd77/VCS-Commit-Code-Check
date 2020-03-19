package com.atzuche.violation.vo.resp;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

/**
 * Created by qincai.lin on 2020/3/16.
 */
@Data
@ToString
public class ViolationInformationResponseVO {


    @AutoDocProperty(value = "序号")
    private String violationId;

    @AutoDocProperty(value = "违章时间")
    private String violationTime;

    @AutoDocProperty(value = "违章地址")
    private String violationAddress;

    @AutoDocProperty(value = "违章内容")
    private String violationContent;

    @AutoDocProperty(value = "罚款")
    private String violationFine;

    @AutoDocProperty(value = "违章扣分")
    private String violationScore;

    @AutoDocProperty(value = "违章状态")
    private String violationStatus;

}
