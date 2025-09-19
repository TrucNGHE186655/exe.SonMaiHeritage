package exe.SonMaiHeritage.service;

import exe.SonMaiHeritage.entity.Order;
import exe.SonMaiHeritage.model.CheckoutRequest;

public interface OrderService {
    Order createOrder(CheckoutRequest checkoutRequest);
    Order getOrderByCode(String orderCode);
    void updateOrderStatus(String orderCode, Order.OrderStatus status);
}
