package com.univesp.ecommerce.order;

import com.univesp.ecommerce.customer.CustomerClient;
import com.univesp.ecommerce.exception.BusinessException;
import com.univesp.ecommerce.kafka.OrderConfirmation;
import com.univesp.ecommerce.kafka.OrderProducer;
import com.univesp.ecommerce.orderline.OrderLineRequest;
import com.univesp.ecommerce.orderline.OrderLineService;
import com.univesp.ecommerce.product.ProductClient;
import com.univesp.ecommerce.product.PurchaseRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderMapper orderMapper;
    private final OrderLineService orderLineService;
    private final OrderProducer orderProducer;

    public Integer createOrder(OrderRequest orderRequest) {
        var customer = this.customerClient.findCustomerById(orderRequest.customerId())
                .orElseThrow(() -> new BusinessException("Cannot create:: No Customer found for ID:: " + orderRequest.customerId()));

        var purchasedProducts = this.productClient.purchaseProducts(orderRequest.products());

        var order = this.orderRepository.save(orderMapper.toOrder(orderRequest));

        for (PurchaseRequest purchaseRequest: orderRequest.products()) {
            orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            null,
                            order.getId(),
                            purchaseRequest.productId(),
                            purchaseRequest.quantity()
                    )
            );
        }

        orderProducer.sendOrderConfirmation(
                new OrderConfirmation(
                        orderRequest.reference(),
                        orderRequest.amount(),
                        orderRequest.paymentMethod(),
                        customer,
                        purchasedProducts
                )
        );


        return order.getId();
    }

    public List<OrderResponse> findAll() {
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::fromOrder)
                .collect(Collectors.toList());
    }

    public OrderResponse findById(Integer orderId) {
        return orderRepository.findById(orderId)
                .map(orderMapper::fromOrder)
                .orElseThrow(() -> new BusinessException("Order not found for ID:: " + orderId));
    }
}
