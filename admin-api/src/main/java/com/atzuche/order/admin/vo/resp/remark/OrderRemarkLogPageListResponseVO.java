package com.atzuche.order.admin.vo.resp.remark;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class OrderRemarkLogPageListResponseVO {

    // 当前页
    @AutoDocProperty(value = "当前页")
    private String pageNumber;
    // 每页的数量
    @AutoDocProperty(value = "每页数量")
    private String pageSize;
    // 总记录数
    @AutoDocProperty(value = "总记录数")
    private String total;
    // 总页数
    @AutoDocProperty(value = "总页数")
    private String pages;

    @AutoDocProperty(value = "备注日志列表")
    private List<OrderRemarkLogListResponseVO> orderRemarkLogList;


}
