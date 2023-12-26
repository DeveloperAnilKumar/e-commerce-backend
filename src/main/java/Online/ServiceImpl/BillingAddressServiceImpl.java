package Online.ServiceImpl;

import Online.Entity.BillingAddress;
import Online.IService.IBillingAddressService;
import Online.Repo.BillingAddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BillingAddressServiceImpl implements IBillingAddressService {


    private final BillingAddressRepository billingAddressRepository;

    @Autowired
    public BillingAddressServiceImpl(BillingAddressRepository billingAddressRepository) {
        this.billingAddressRepository = billingAddressRepository;
    }

    @Override
    public List<BillingAddress> getAllBillingAddresses() {
        return billingAddressRepository.findAll();
    }

    @Override
    public Optional<BillingAddress> getBillingAddressById(UUID id) {
        return billingAddressRepository.findById(id);
    }

    @Override
    public BillingAddress addBillingAddress(BillingAddress billingAddress) {
        return billingAddressRepository.save(billingAddress);
    }

    @Override
    public void updateBillingAddress(UUID id, BillingAddress billingAddress) {
        if (billingAddressRepository.existsById(id)) {
            billingAddress.setId(id);
            billingAddressRepository.save(billingAddress);
        } else {
        }
    }

    @Override
    public void deleteBillingAddress(UUID id) {
        billingAddressRepository.deleteById(id);
    }
}
