package Online.Repo;

import Online.Entity.Order;
import Online.enums.DeliveryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findBySellerId(UUID sellerId);

    List<Order> findByUserId(UUID userId);

    Page<Order> findByUserId(UUID userId, Pageable pageable);






}