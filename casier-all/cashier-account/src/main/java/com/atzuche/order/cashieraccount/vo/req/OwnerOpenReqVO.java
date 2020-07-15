package com.atzuche.order.cashieraccount.vo.req;

import com.autoyol.doc.annotation.AutoDocProperty;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author 胡春林
 * 车主会员号信息
 */
@Data
@ToString
@NoArgsConstructor
public class OwnerOpenReqVO {

    @AutoDocProperty("会员号")
    private String memNo;
}
