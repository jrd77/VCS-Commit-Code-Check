package com.atzuche.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RenterOrderComposeService {

	@Autowired
	private RenterOrderService renterOrderService;
}
