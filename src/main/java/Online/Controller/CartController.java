package Online.Controller;

import Online.Entity.Cart;
import Online.Entity.CartItems;
import Online.IService.ICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    @Autowired
    private ICartService cartService;

    @PostMapping("/create/{user}")
    public ResponseEntity<Cart> createCart(@PathVariable UUID user) {
        Cart createdCart = cartService.createCart(user);
        return new ResponseEntity<>(createdCart, HttpStatus.CREATED);
    }

    @PostMapping("/{cartId}/add/{productId}")
    public ResponseEntity<String> addToCart(
            @PathVariable UUID cartId,
            @PathVariable UUID productId,
            @RequestParam Integer quantity
    ) {
        try {
            CartItems cartItems = cartService.addToCart(cartId, productId, quantity);
            return ResponseEntity.ok("Item added to cart successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @GetMapping("/{cartId}/items")
    public ResponseEntity<List<CartItems>> getCartItems(@PathVariable UUID cartId) {
        List<CartItems> cartItems = cartService.getCartItems(cartId);
        return new ResponseEntity<>(cartItems, HttpStatus.OK);
    }

    @DeleteMapping("/item/{cartItemId}")
    public ResponseEntity<String> removeFromCart(@PathVariable UUID cartItemId) {
        cartService.removeFromCart(cartItemId);
        return new ResponseEntity<>("Item removed from cart successfully", HttpStatus.OK);
    }


    @PutMapping("/items/{cartItemId}/{newQuantity}")
    public ResponseEntity<String> updateCartItemQuantity(
            @PathVariable UUID cartItemId,
            @PathVariable Integer newQuantity
    ) {
        boolean isUpdated = cartService.updateCartItemQuantity(cartItemId, newQuantity);

        if (isUpdated) {
            return new ResponseEntity<>("Cart item quantity updated successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Failed to update cart item quantity", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<String> clearCart(@PathVariable UUID cartId) {
        cartService.clearCart(cartId);
        return ResponseEntity.ok("Cart with ID: " + cartId + " has been cleared.");
    }

    @GetMapping("/length")
    public ResponseEntity<?> getCartLength() {
        try {
            Long cartLength = cartService.getCartLength();
            if (cartLength != null) {
                return ResponseEntity.ok(cartLength);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to retrieve cart length");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }



}
