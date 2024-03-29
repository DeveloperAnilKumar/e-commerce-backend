package Online.Repo;

import Online.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {


   Optional<Category> findByCategoryName(String categoryName);



}