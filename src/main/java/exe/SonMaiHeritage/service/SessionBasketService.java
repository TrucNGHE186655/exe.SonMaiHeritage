package exe.SonMaiHeritage.service;

import exe.SonMaiHeritage.model.SessionBasketRequest;
import exe.SonMaiHeritage.model.SessionBasketResponse;

public interface SessionBasketService {
    SessionBasketResponse getBasketBySessionId(String sessionId);
    SessionBasketResponse addItemToBasket(SessionBasketRequest request);
    SessionBasketResponse removeItemFromBasket(String sessionId, Integer productId);
    SessionBasketResponse updateItemQuantity(String sessionId, Integer productId, Integer quantity);
    void clearBasket(String sessionId);
    SessionBasketResponse createBasket(String sessionId);
}
