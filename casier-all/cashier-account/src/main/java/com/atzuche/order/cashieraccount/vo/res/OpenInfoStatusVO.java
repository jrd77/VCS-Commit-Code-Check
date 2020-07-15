package com.atzuche.order.cashieraccount.vo.res;

import com.autoyol.doc.annotation.AutoDocProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OpenInfoStatusVO {

    @AutoDocProperty("该会员开户信息")
    private SecondOpenOwner secondOpenOwner;
    @AutoDocProperty("会员相关照片")
    private SecondOpenPhoto secondOpenPhoto;

}
