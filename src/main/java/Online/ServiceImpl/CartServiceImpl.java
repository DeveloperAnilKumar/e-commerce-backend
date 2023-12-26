package Online.ServiceImpl;

import Online.Entity.Cart;
import Online.Entity.CartItems;
import Online.Entity.Product;
import Online.Entity.User;
import Online.IService.ICartService;
import Online.Repo.CartItemsRepository;
import Online.Repo.CartRepository;
import Online.Repo.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemsRepository cartItemsRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private  UserService userService;

    @Override
    public Cart createCart(UUID uuid) {
        Optional<User> userByUuid = userService.getUserByUuid(uuid);

        // Check if a cart already exists for the user
        Optional<Cart> existingCart = cartRepository.findByUserId(uuid);
        if (existingCart.isPresent()) {
            return existingCart.get(); // Return the existing cart if found
        }

        Cart cart = new Cart();
        userByUuid.ifPresent(cart::setUser);
        return cartRepository.save(cart);
    }

    @Override
    public CartItems addToCart(UUID cartId, UUID productId, Integer quantity) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        int availableStock = product.getStock();

        if (availableStock < quantity) {
            throw new RuntimeException("Insufficient stock for the product: " + product.getProductName());
        }

        // Check if the product already exists in the cart
        CartItems existingCartItem = cartItemsRepository.findByCartAndProduct(cart, product);

        if (existingCartItem != null) {
            // If the product exists in the cart, update its quantity
            existingCartItem.setQuantity(existingCartItem.getQuantity() + quantity);
            return cartItemsRepository.save(existingCartItem);
        } else {
            // If the product doesn't exist, create a new entry
            CartItems newCartItem = new CartItems();
            newCartItem.setCart(cart);
            newCartItem.setProduct(product);
            newCartItem.setQuantity(quantity);
            return cartItemsRepository.save(newCartItem);
        }
    }


    @Override
    public List<CartItems> getCartItems(UUID cartId) {
        return cartItemsRepository.findAllByCartId(cartId);
    }

    @Override
    public void removeFromCart(UUID cartItemId) {
        cartItemsRepository.deleteById(cartItemId);
    }

    @Override
    public void clearCart(UUID cartId) {
        List<CartItems> allByCartId = cartItemsRepository.findAllByCartId(cartId);
        cartItemsRepository.deleteAll(allByCartId);
    }


    public boolean updateCartItemQuantity(UUID cartItemId, Integer newQuantity) {
        Optional<CartItems> optionalCartItem = cartItemsRepository.findById(cartItemId);
        if (optionalCartItem.isPresent()) {
            CartItems cartItem = optionalCartItem.get();
            cartItem.setQuantity(newQuantity);
            cartItemsRepository.save(cartItem);
            return true;
        }

        return false;
    }

    @Override
    public Long getCartLength() {
        return cartItemsRepository.count();
    }


}
