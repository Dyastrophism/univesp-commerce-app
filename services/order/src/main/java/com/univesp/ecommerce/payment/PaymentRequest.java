package com.univesp.ecommerce.payment;

import com.univesp.ecommerce.customer.CustomerResponse;
import com.univesp.ecommerce.order.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
        BigDecimal amount,
        PaymentMethod paymentMethod,
        Integer orderId,
        String orderReference,
        CustomerResponse customer
) {
}
