package exe.SonMaiHeritage.service;

import exe.SonMaiHeritage.entity.Order;
import exe.SonMaiHeritage.model.CheckoutRequest;
import exe.SonMaiHeritage.model.GuestCheckoutRequest;
import exe.SonMaiHeritage.model.OrderResponse;

import java.util.List;

public interface OrderService {
    Order createGuestOrder(CheckoutRequest checkoutRequest, GuestCheckoutRequest guestInfo);
    Order getOrderById(int orderId);
    void updateOrderStatus(int orderId, Order.OrderStatus status);
    OrderResponse getOrderResponseById(int orderId);
    List<OrderResponse> getAllOrders();
}
