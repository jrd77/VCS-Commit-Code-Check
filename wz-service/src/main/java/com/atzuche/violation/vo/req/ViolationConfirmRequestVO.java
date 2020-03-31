package com.atzuche.violation.vo.req;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by qincai.lin on 2020/3/16.
 */
@Data
@ToString
public class ViolationConfirmRequestVO {

    @AutoDocProperty(value = "违章记录id")
    @NotBlank(message = "违章id不能为空")
    private String violationId;

}
