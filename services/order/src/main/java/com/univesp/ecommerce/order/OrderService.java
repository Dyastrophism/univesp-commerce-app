package com.univesp.ecommerce.order;

import com.univesp.ecommerce.customer.CustomerClient;
import com.univesp.ecommerce.exception.BusinessException;
import com.univesp.ecommerce.orderline.OrderLineRequest;
import com.univesp.ecommerce.orderline.OrderLineService.OrderLineService;
import com.univesp.ecommerce.product.ProductClient;
import com.univesp.ecommerce.product.PurchaseRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderMapper orderMapper;
    private final OrderLineService orderLineService;

    public Integer createOrder(OrderRequest orderRequest) {
        /** TODO:
         * 1. Checar se o cliente existe - OpenFeign
         * 2. Comprar os produtos no serviço de produtos
         * 3. Salvar o pedido no banco de dados
         * 4. Salvar a linha de pedido no banco de dados
         * 5. Iniciar o processo de pagamento
         * 6. Enviar o pedido para o serviço de notificação
         */

        //pegando cliente
        var customer = this.customerClient.findCustomerById(orderRequest.customerId())
                .orElseThrow(() -> new BusinessException("Cannot create:: No Customer found for ID:: " + orderRequest.customerId()));

        this.productClient.purchaseProducts(orderRequest.products());

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
        return null;
    }
}
