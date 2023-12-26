package Online.Repo;

import Online.Entity.Seller;
import Online.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface SellerRepository extends JpaRepository<Seller, UUID> {
    Optional<Seller> findByEmail(String email);

    @Query("SELECT  COUNT(email) FROM User  WHERE email=:email  ")
    Integer getEmailCount(String email);
}