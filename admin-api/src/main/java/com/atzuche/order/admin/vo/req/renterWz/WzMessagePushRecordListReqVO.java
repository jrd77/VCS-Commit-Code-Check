package com.atzuche.order.admin.vo.req.renterWz;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author ：changshu.xie
 * @date ：Created in 2020/05/17 13:16
 */
@Data
@EqualsAndHashCode
@ToString
public class WzMessagePushRecordListReqVO implements Serializable {
    private static final long serialVersionUID = 6277947143171115766L;
    @AutoDocProperty("订单号")
    private Long orderNo;
    @AutoDocProperty(value = "页码")
    @NotNull(message="页码不允许为空")
    private Integer pageNum;
    @AutoDocProperty(value = "每页大小")
    private Integer pageSize = 10;//默认10
    @AutoDocProperty("id排序 升序传ASC，降序传DESC")
    private String idSort;
    @AutoDocProperty("发送时间排序 升序传ASC，降序传DESC")
    private String sendTimeSort;
    @AutoDocProperty("违章序号")
    private Long orderViolationId;
}
