package com.atzuche.order.commons.entity.dto;

import java.util.Date;

import com.atzuche.order.commons.DateUtils;
import com.autoyol.doc.annotation.AutoDocProperty;

public class AddIncomeExcelEntity {
	@AutoDocProperty(value = "收益记录id")
    private Long id;
	@AutoDocProperty(value = "文件名称")
    private String fileName;
	@AutoDocProperty(value = "文件路径")
    private String excelHref;
	
    private Date uploadTime;
    @AutoDocProperty(value = "上传时间")
	private String uploadTimeStr;
	@AutoDocProperty(value = "上传人")
    private String uploder;
	@AutoDocProperty(value = "状态，0:未审核 1:审核通过 2：驳回 3：撤回,4:已删除")
    private Integer status;
    @AutoDocProperty(value = "审核状态中文描述")
    private String statusStr;
    
    @AutoDocProperty(value = "操作人")
    private String operate;

    private Date operateTime;
    @AutoDocProperty(value = "操作时间")
    private String operateTimeStr;

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
    	if (delFlag != null && delFlag.intValue() == 1) {
    		status = 4;
		}
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

	public String getUploadTimeStr() {
		if (uploadTime != null) {
			uploadTimeStr = DateUtils.formate(uploadTime, DateUtils.DATE_DEFAUTE1);
		}
		return uploadTimeStr;
	}

	public void setUploadTimeStr(String uploadTimeStr) {
		this.uploadTimeStr = uploadTimeStr;
	}

	public String getStatusStr() {
		if (delFlag != null && delFlag.intValue() == 1) {
			statusStr = "已删除";
			return statusStr;
		}
		if (status != null) {
			if (status.intValue() == 0) {
				statusStr = "未审核";
			} else if (status.intValue() == 1) {
				statusStr = "审核通过";
			} else if (status.intValue() == 2) {
				statusStr = "驳回";
			} else if (status.intValue() == 3) {
				statusStr = "撤回";
			}
		}
		return statusStr;
	}

	public void setStatusStr(String statusStr) {
		this.statusStr = statusStr;
	}

	public String getOperateTimeStr() {
		if (operateTime != null) {
			operateTimeStr = DateUtils.formate(operateTime, DateUtils.DATE_DEFAUTE1);
		}
		return operateTimeStr;
	}

	public void setOperateTimeStr(String operateTimeStr) {
		this.operateTimeStr = operateTimeStr;
	}
    
}