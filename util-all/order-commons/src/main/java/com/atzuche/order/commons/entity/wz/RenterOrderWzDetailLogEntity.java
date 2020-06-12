package com.atzuche.order.commons.entity.wz;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 违章明细日志表
 * 
 * @author ZhangBin
 * @date 2020-06-08 14:23:43
 * @Description:
 */
@Data
public class RenterOrderWzDetailLogEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 违章费用日志表主键
	 */
	private Long id;
	/**
	 * 订单号
	 */
	private String orderNo;
	/**
	 * 违章序号
	 */
	private Long wzDetailId;
	/**
	 * 操作类型
	 */
	private Integer operateType;
	/**
	 * 内容
	 */
	private String content;
	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;
	/**
	 * 创建人
	 */
	private String createOp;
	/**
	 * 修改时间
	 */
	private LocalDateTime updateTime;
	/**
	 * 更新人
	 */
	private String updateOp;
	/**
	 * 0-正常，1-已逻辑删除
	 */
	private Integer isDelete;

	/*
	 * @Author ZhangBin
	 * @Date 2020/6/8 14:33
	 * @Description: 拼接content
	 *
	 **/
	public static String getWzContent(String wzTime,String wzAddr,String wzDesc,Integer wzFineAmt,String score,Integer status){
	    String statusDesc = "";
	    if(status != null){
            if(status == 1){
                statusDesc = "已处理";
            }else if(status == 0){
                statusDesc = "未处理";
            }
        }
	    String content =  "【违章时间】："+(wzTime==null?"":wzTime)+"；\n" +
                "【违章地点】："+(wzAddr==null?"":wzAddr)+"；\n" +
                "【违章内容】："+(wzDesc==null?"":wzDesc)+"；\n" +
                "【违章罚金】："+(wzFineAmt==null?"0":wzFineAmt)+"；\n" +
                "【扣分】："+(score==null?"0":score)+"；\n" +
                "【违章状态】："+statusDesc+"；";
	    return content;
    }

}
