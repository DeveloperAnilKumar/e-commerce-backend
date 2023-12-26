package Online.Repo;

import Online.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    @Query("SELECT  COUNT(email) FROM User  WHERE email=:email  ")
    Integer getEmailCount(String email);

    Optional<User> findByEmail(String email);
}