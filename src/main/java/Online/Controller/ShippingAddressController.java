
package Online.Controller;

import Online.Entity.ShippingAddress;
import Online.IService.IShippingAddressService;
import org.eclipse.angus.mail.imap.protocol.UID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/shipping-addresses")
public class ShippingAddressController {

    private final IShippingAddressService shippingAddressService;

    @Autowired
    public ShippingAddressController(IShippingAddressService shippingAddressService) {
        this.shippingAddressService = shippingAddressService;
    }

    @GetMapping
    public ResponseEntity<List<ShippingAddress>> getAllShippingAddresses() {
        List<ShippingAddress> shippingAddresses = shippingAddressService.getAllShippingAddresses();
        return new ResponseEntity<>(shippingAddresses, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShippingAddress> getShippingAddressById(@PathVariable UUID id) {
        return shippingAddressService.getShippingAddressById(id)
                .map(address -> new ResponseEntity<>(address, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<ShippingAddress> addShippingAddress(@RequestBody ShippingAddress shippingAddress) {
        ShippingAddress newShippingAddress = shippingAddressService.addShippingAddress(shippingAddress);
        return new ResponseEntity<>(newShippingAddress, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateShippingAddress(@PathVariable UUID id, @RequestBody ShippingAddress shippingAddress) {
        shippingAddressService.updateShippingAddress(id, shippingAddress);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShippingAddress(@PathVariable UUID id) {
        shippingAddressService.deleteShippingAddress(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
