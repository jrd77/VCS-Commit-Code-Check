package com.atzuche.order.admin.vo.resp;

import com.atzuche.order.admin.dto.log.AdminOperateLogDTO;
import com.atzuche.order.admin.entity.AdminOperateLogEntity;
import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/15 2:52 下午
 **/
@Data
public class LogListRespVO {
    @AutoDocProperty(value = "日志记录列表")
    private List<AdminOperateLogDTO> logs;
}
