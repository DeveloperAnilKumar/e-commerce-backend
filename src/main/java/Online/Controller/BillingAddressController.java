package Online.Controller;

import Online.Entity.BillingAddress;
import Online.IService.IBillingAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/billing-addresses")
public class BillingAddressController {

    private final IBillingAddressService billingAddressService;

    @Autowired
    public BillingAddressController(IBillingAddressService billingAddressService) {
        this.billingAddressService = billingAddressService;
    }

    @GetMapping
    public ResponseEntity<List<BillingAddress>> getAllBillingAddresses() {
        List<BillingAddress> billingAddresses = billingAddressService.getAllBillingAddresses();
        return new ResponseEntity<>(billingAddresses, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillingAddress> getBillingAddressById(@PathVariable UUID id) {
        return billingAddressService.getBillingAddressById(id)
                .map(address -> new ResponseEntity<>(address, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<BillingAddress> addBillingAddress(@RequestBody BillingAddress billingAddress) {
        BillingAddress newBillingAddress = billingAddressService.addBillingAddress(billingAddress);
        return new ResponseEntity<>(newBillingAddress, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateBillingAddress(@PathVariable UUID id, @RequestBody BillingAddress billingAddress) {
        billingAddressService.updateBillingAddress(id, billingAddress);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBillingAddress(@PathVariable UUID id) {
        billingAddressService.deleteBillingAddress(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
