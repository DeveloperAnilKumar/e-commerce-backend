package Online.Repo;

import Online.Entity.Cart;
import Online.Entity.CartItems;
import Online.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CartItemsRepository extends JpaRepository<CartItems, UUID> {
    List<CartItems> findAllByCartId(UUID cartId);

    CartItems findByCartAndProduct(Cart cart, Product product);



}