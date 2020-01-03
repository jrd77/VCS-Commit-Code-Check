package com.atzuche.order.delivery.utils;

import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 胡春林
 */
@Component
public class CodeUtils {

	/**
	 * 生成唯一订单号
	 * @return
	 */
	public String createBizNumber() {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMdd");
		Date now2 = new Date();
		long a = getPK();
		return simpleDateFormat.format(now2)
				+ a;
	}

    /**
     * 生成配送订单号
     * @return
     */
    public String createDeliveryNumber() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMdd");
        Date now2 = new Date();
        long a = getPK();
        return "PS" +simpleDateFormat.format(now2)
                + a;
    }

	private synchronized long getPK() {
        IdGenerator idWorker = new IdGenerator(1, 2);
		return idWorker.nextId();
	}

}
