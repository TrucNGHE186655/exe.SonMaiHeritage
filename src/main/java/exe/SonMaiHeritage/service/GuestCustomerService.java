package exe.SonMaiHeritage.service;

import exe.SonMaiHeritage.entity.GuestCustomer;
import exe.SonMaiHeritage.model.GuestCheckoutRequest;

public interface GuestCustomerService {
    GuestCustomer createOrUpdateGuestCustomer(GuestCheckoutRequest guestInfo);
    GuestCustomer getGuestCustomerBySessionId(String sessionId);
    GuestCustomer getGuestCustomerByEmail(String email);
}
