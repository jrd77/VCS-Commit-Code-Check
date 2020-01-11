package order.atzuche.order.mem;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class RenterDriverErrException extends OrderException {

    public RenterDriverErrException() {
        super(ErrorCode.FEIGN_MEMBER_DRIVER_ERROR.getCode(), ErrorCode.FEIGN_MEMBER_DRIVER_ERROR.getText());
    }
}
