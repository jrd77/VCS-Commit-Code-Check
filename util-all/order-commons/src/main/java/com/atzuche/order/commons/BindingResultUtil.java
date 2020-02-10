package com.atzuche.order.commons;

import com.atzuche.order.commons.exceptions.InputErrorException;
import com.autoyol.commons.web.ErrorCode;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Optional;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/31 12:08 下午
 **/
public class BindingResultUtil {
    public static void checkBindingResult(BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            Optional<FieldError> error = bindingResult.getFieldErrors().stream().findFirst();
            throw new InputErrorException(error.isPresent()?error.get().getDefaultMessage() : ErrorCode.INPUT_ERROR.getText());
        }
    }
}
