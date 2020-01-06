package com.atzuche.order.admin.vo.resp.renterWz;

import com.autoyol.doc.annotation.AutoDocIgnoreProperty;
import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * WzCostLogResVO
 *
 * @author shisong
 * @date 2020/1/6
 */
@Data
@ToString
public class WzCostLogResVO {

    @AutoDocIgnoreProperty()
    private Date createTime;

    @AutoDocProperty("操作时间")
    private String createTimeStr;

    @AutoDocProperty("操作人")
    private String operator;

    @AutoDocProperty("费用项")
    private String costItem;

    @AutoDocProperty("操作内容")
    private String operateContent;

}
