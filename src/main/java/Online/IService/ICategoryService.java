package Online.IService;
import Online.Entity.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ICategoryService {
    Category saveCategory(Category category);

    List<Category> getAllCategories();

    Category getCategoryById(UUID id);

    void deleteCategory(UUID id);

    Optional<Category> findByCategoryName(String categoryName);
}

