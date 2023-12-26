package Online.IService;

import Online.Entity.Cart;
import Online.Entity.CartItems;

import java.util.List;
import java.util.UUID;

public interface ICartService {
    Cart createCart(UUID user);
    CartItems addToCart(UUID cartId, UUID productId, Integer quantity);
    List<CartItems> getCartItems(UUID cartId);
    void removeFromCart(UUID cartItemId);
    void clearCart(UUID cartId);

    public boolean updateCartItemQuantity(UUID cartItemId, Integer newQuantity) ;

    public Long getCartLength();

    }
