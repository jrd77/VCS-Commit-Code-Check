package com.atzuche.order.commons.entity.dto;

import java.util.Date;

public class AddIncomeExcelEntity {
    private Long id;

    private String fileName;

    private String excelHref;

    private Date uploadTime;

    private String uploder;

    private Integer status;

    private String operate;

    private Date operateTime;

    private Integer delFlag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName == null ? null : fileName.trim();
    }

    public String getExcelHref() {
        return excelHref;
    }

    public void setExcelHref(String excelHref) {
        this.excelHref = excelHref == null ? null : excelHref.trim();
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getUploder() {
        return uploder;
    }

    public void setUploder(String uploder) {
        this.uploder = uploder == null ? null : uploder.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getOperate() {
        return operate;
    }

    public void setOperate(String operate) {
        this.operate = operate == null ? null : operate.trim();
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }
}