package com.atzuche.order.coreapi.controller;

import com.atzuche.order.commons.entity.dto.RentCityAndRiskAccidentReqDTO;
import com.atzuche.order.coreapi.service.OrderUpdateService;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/order/update")
public class OrderUpdateController {
    @Autowired
    private OrderUpdateService orderUpdateService;

    @PostMapping("/rentCityAndRiskAccident")
    public ResponseData<?> rentCityAndRiskAccident(@Valid @RequestBody RentCityAndRiskAccidentReqDTO rentCityAndRiskAccidentReqDTO, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            Optional<FieldError> error = bindingResult.getFieldErrors().stream().findFirst();
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), error.isPresent() ?
                    error.get().getDefaultMessage() : ErrorCode.INPUT_ERROR.getText());
        }
        ResponseData<?> respData = orderUpdateService.rentCityAndRiskAccident(rentCityAndRiskAccidentReqDTO);
        return respData;
    }

}
