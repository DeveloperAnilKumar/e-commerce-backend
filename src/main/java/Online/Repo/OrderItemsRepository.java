package Online.Repo;

import Online.Entity.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderItemsRepository extends JpaRepository<OrderItems, UUID> {

    List<OrderItems> findAllByUserId(UUID userId);

    List<OrderItems> findByUserIdAndCompletedFalse(UUID userId);

    OrderItems findByProductIdAndCompletedFalse(UUID productId);

}