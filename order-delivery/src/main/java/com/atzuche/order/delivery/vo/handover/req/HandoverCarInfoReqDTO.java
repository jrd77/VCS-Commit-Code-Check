package com.atzuche.order.delivery.vo.handover.req;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author 胡春林
 * 配送数据
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class HandoverCarInfoReqDTO {

    @AutoDocProperty("车主取车里程数/租客收车里程数")
    public String renterRetrunKM;
    @AutoDocProperty("车主还车里程数/租客交车里程数")
    public String ownReturnKM;
    @AutoDocProperty("车主取车油表刻度/租客交车油表刻度")
    public String renterReturnOil;
    @AutoDocProperty("车主还车油表刻度/租客收车油表刻度")
    public String ownReturnOil;
    @AutoDocProperty("orderNo不能为空")
    @NotBlank(message="orderNo不能为空")
    private String orderNo;

}
