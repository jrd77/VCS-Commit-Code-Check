package com.atzuche.order.admin.vo.resp.remark;

import com.autoyol.doc.annotation.AutoDocProperty;

import java.util.List;

/**
 * Created by qincai.lin on 2020/1/2.
 */
public class OrderRemarkLogPageListResponseVO {

    // 当前页
    @AutoDocProperty(value = "当前页")
    private int pageNum;
    // 每页的数量
    @AutoDocProperty(value = "每页数量")
    private int pageSize;
    // 总记录数
    @AutoDocProperty(value = "总记录数")
    private long total;
    // 总页数
    @AutoDocProperty(value = "总页数")
    private int pages;

    @AutoDocProperty(value = "备注日志列表")
    private List<OrderRemarkLogListResponseVO> orderRemarkLogList;


    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public List<OrderRemarkLogListResponseVO> getOrderRemarkLogList() {
        return orderRemarkLogList;
    }

    public void setOrderRemarkLogList(List<OrderRemarkLogListResponseVO> orderRemarkLogList) {
        this.orderRemarkLogList = orderRemarkLogList;
    }

}
