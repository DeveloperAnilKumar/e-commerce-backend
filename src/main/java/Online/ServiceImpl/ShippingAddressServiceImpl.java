package Online.ServiceImpl;

import Online.Entity.ShippingAddress;
import Online.IService.IShippingAddressService;
import Online.Repo.ShippingAddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ShippingAddressServiceImpl implements IShippingAddressService {


    private final ShippingAddressRepository shippingAddressRepository;

    @Autowired
    public ShippingAddressServiceImpl(ShippingAddressRepository shippingAddressRepository
                                      ) {
        this.shippingAddressRepository = shippingAddressRepository;
    }

    @Override
    public List<ShippingAddress> getAllShippingAddresses() {
        return shippingAddressRepository.findAll();
    }

    @Override
    public Optional<ShippingAddress> getShippingAddressById(UUID id) {
        return shippingAddressRepository.findById(id);
    }

    @Override
    public ShippingAddress addShippingAddress(ShippingAddress shippingAddress) {
        return shippingAddressRepository.save(shippingAddress);
    }

    @Override
    public void updateShippingAddress(UUID id, ShippingAddress shippingAddress) {
        if (shippingAddressRepository.existsById(id)) {
            shippingAddress.setId(id);
            shippingAddressRepository.save(shippingAddress);
        } else {
        }
    }

    @Override
    public void deleteShippingAddress(UUID id) {
        shippingAddressRepository.deleteById(id);
    }


}
