package com.atzuche.violation.vo.req;

import com.autoyol.doc.annotation.AutoDocProperty;

/**
 * Created by qincai.lin on 2020/3/16.
 */
public class ViolationAdditionRequestVO {

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

}
