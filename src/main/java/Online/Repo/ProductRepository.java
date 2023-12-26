package Online.Repo;

import Online.Entity.Category;
import Online.Entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {


    List<Product> findByCategory(String category);

    @Query("SELECT p FROM Product p WHERE p.category = :categoryName")
    List<Product> findProductsByCategoryWithLimit( String categoryName, Pageable pageable);


}