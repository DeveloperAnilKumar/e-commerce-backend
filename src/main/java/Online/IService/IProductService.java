package Online.IService;

import Online.Entity.Category;
import Online.Entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface IProductService {
    Product updateProduct(Product product);

    List<Product> getAllProducts();

    Product getProductById(UUID id);

    void deleteProduct(UUID id);

    public String fileUpload(MultipartFile file);

    public List<Product> getProductByCategory(String category);

    List<Product> findProductsByCategoryWithLimit(String categoryName, int limit);

    Page<Product> getProducts(Pageable pageable);

}

