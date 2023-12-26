package Online.IService;

import Online.Entity.ShippingAddress;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IShippingAddressService {

    List<ShippingAddress> getAllShippingAddresses();
    Optional<ShippingAddress> getShippingAddressById(UUID id);
    ShippingAddress addShippingAddress(ShippingAddress shippingAddress);
    void updateShippingAddress(UUID id, ShippingAddress shippingAddress);
    void deleteShippingAddress(UUID id);




}
