package Online.IService;

import Online.Entity.BillingAddress;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IBillingAddressService {

    List<BillingAddress> getAllBillingAddresses();
    Optional<BillingAddress> getBillingAddressById(UUID id);
    BillingAddress addBillingAddress(BillingAddress billingAddress);
    void updateBillingAddress(UUID id, BillingAddress billingAddress);
    void deleteBillingAddress(UUID id);
}
