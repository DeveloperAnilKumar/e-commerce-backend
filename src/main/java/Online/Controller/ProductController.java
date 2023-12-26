package Online.Controller;

import Online.Entity.Category;
import Online.Entity.Product;
import Online.IService.IProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final IProductService productService;


    @Autowired
    public ProductController(IProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {



        List<Product> products = productService.getAllProducts();


        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/products")
    public ResponseEntity<Page<Product>> getPaginatedProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Product> products = productService.getProducts(pageRequest);

        return ResponseEntity.ok(products);
    }
    @PostMapping("/upload")
    public ResponseEntity<String> addProduct(@RequestParam("file") MultipartFile file, @RequestParam("productData") String product) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        if (file != null && !file.isEmpty()) {
            String fileUploadResult = productService.fileUpload(file);

            if (product != null && !product.isEmpty()) {
                Product productFromJson = objectMapper.readValue(product, Product.class);

                if (productFromJson != null) {
                    productFromJson.setImageUrl(fileUploadResult);
                    productService.updateProduct(productFromJson);
                    return new ResponseEntity<>("Product and file uploaded successfully", HttpStatus.CREATED);
                }
            } else {
                return new ResponseEntity<>("Invalid product data", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("File is empty", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Bad request", HttpStatus.BAD_REQUEST);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable UUID id) {
        Product product = productService.getProductById(id);
        return product != null ? new ResponseEntity<>(product, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



    @PostMapping("/edit")
    public  ResponseEntity<Product> editProduct(@RequestBody Product product){
        return  new ResponseEntity<>(productService.updateProduct(product), HttpStatus.OK);
    }


    @GetMapping("/category/{category}")
    public  ResponseEntity<?> getProductByCategory(@PathVariable String category){
        return  new ResponseEntity<>(productService.getProductByCategory(category), HttpStatus.OK);
    }
    @GetMapping("/products-by-category")
    public List<Product> getProductsByCategoryWithLimit(@RequestParam String category, @RequestParam int limit) {
        return productService.findProductsByCategoryWithLimit(category, limit);
    }

    @GetMapping("/page")
    public Page<Product> getProducts(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        Pageable pageRequest = PageRequest.of(page, size);
        return productService.getProducts(pageRequest);
    }


}
