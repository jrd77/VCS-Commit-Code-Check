package com.atzuche.order.admin.vo.request;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * Created by qincai.lin on 2019/12/30.
 */
@Data
@ToString
public class WalletRequestVO implements Serializable {
    @AutoDocProperty(value = "用户注册号")
    private String memNo;

}
