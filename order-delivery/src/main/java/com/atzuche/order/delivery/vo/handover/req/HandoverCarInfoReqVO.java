package com.atzuche.order.delivery.vo.handover.req;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author 胡春林
 * 前端参数
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class HandoverCarInfoReqVO {

    @AutoDocProperty("车主取还车")
    private HandoverCarInfoReqDTO ownerHandoverCarDTO;
    @AutoDocProperty("租客取还车")
    private HandoverCarInfoReqDTO renterHandoverCarDTO;
}
