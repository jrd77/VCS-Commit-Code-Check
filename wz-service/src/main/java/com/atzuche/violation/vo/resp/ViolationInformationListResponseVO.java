package com.atzuche.violation.vo.resp;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class ViolationInformationListResponseVO {

    @AutoDocProperty(value = "违章信息列表")
    private List<ViolationInformationResponseVO> violationList;

}
