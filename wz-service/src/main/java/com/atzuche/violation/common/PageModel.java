package com.atzuche.violation.common;

import com.autoyol.doc.annotation.AutoDocProperty;
import com.github.pagehelper.Page;

import java.io.Serializable;
import java.util.List;

/**
 * @param <T>
 * @author 胡春林
 */
public class PageModel<T> implements Serializable {
    private static final long serialVersionUID = -1816511141859390841L;

    /**
     * 当前页码
     */
    @AutoDocProperty(value="当前页码")
    private Integer pageNo;

    /**
     * 每页大小
     */
    @AutoDocProperty(value="每页大小")
    private Integer pageSize;

    /**
     * 总页数
     */
    @AutoDocProperty(value="总页数")
    private Integer pageNum;

    /**
     * 总条数
     */
    @AutoDocProperty(value="总条数")
    private Long totalCount;

    /**
     * 结果集
     */
    @AutoDocProperty(value="结果集")
    private List<T> list;

    public PageModel() {

    }

    public PageModel(List<T> list) {
        if (list instanceof Page) {
            Page<T> page = (Page) list;
            this.pageNo = page.getPageNum();
            this.pageSize = page.getPageSize();
            this.totalCount = page.getTotal();
            this.pageNum = page.getPages();
            this.list = page;
        }

    }

    public PageModel(Integer pageNo, Integer pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public PageModel(Integer pageNo, Integer pageSize, Long totalCount, List<T> list) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        this.list = list;
    }


    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public Long getPageNum() {
        if (getTotalCount() == null || getPageSize() == null) {
            return 0L;
        }
        return getTotalCount() / getPageSize() + 1;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }
}
