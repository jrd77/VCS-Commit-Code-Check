package com.atzuche.order.admin.vo.resp.renterWz;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * TemporaryRefundLogsResVO
 *
 * @author shisong
 * @date 2020/1/6
 */
@Data
@ToString
public class TemporaryRefundLogsResVO {

    @AutoDocProperty("暂扣返还日志")
    private List<TemporaryRefundLogResVO> temporaryRefundLogs;

}
