package com.univesp.ecommerce.kafka;

import com.univesp.ecommerce.customer.CustomerResponse;
import com.univesp.ecommerce.order.PaymentMethod;
import com.univesp.ecommerce.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation(
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer,
        List<PurchaseResponse> products
) {
}
