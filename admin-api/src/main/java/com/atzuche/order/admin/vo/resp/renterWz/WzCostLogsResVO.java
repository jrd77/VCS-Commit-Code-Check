package com.atzuche.order.admin.vo.resp.renterWz;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * WzCostLogsResVO
 *
 * @author shisong
 * @date 2020/1/6
 */
@Data
@ToString
public class WzCostLogsResVO {

    @AutoDocProperty("违章费用修改日志")
    private List<WzCostLogResVO> wzCostLogs;
}
