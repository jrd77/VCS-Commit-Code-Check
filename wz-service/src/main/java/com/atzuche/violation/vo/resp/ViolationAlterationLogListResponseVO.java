package com.atzuche.violation.vo.resp;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class ViolationAlterationLogListResponseVO {

    @AutoDocProperty(value = "订单违章信息变更记录列表")
    private List<ViolationAlterationLogResponseVO> alterationLogList;

}
