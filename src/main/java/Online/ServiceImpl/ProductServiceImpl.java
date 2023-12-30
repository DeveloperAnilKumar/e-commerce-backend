package Online.ServiceImpl;

import Online.Entity.Product;
import Online.IService.ICategoryService;
import Online.IService.IProductService;
import Online.Repo.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class ProductServiceImpl implements IProductService {



    @Autowired
    private  ProductRepository productRepository;


    @Autowired
    private ICategoryService iCategoryService;

    public ProductServiceImpl() throws IOException {
    }


    @Override
    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }


    public String fileUpload(MultipartFile file) {
        if (file != null) {
            try {
                //String UPLOAD_DIR =  "D:\\SpringBoot&MSProjects\\Online\\src\\main\\resources\\static\\images";


               String UPLOAD_DIR = new ClassPathResource("static/images").getFile().getAbsolutePath();
                Files.copy(file.getInputStream(), Paths.get(UPLOAD_DIR + File.separator + file.getOriginalFilename()),
                        StandardCopyOption.REPLACE_EXISTING);
                System.out.println(UPLOAD_DIR);

                return ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/images/")
                        .path(Objects.requireNonNull(file.getOriginalFilename()))
                        .toUriString();
            } catch (IOException e) {
                return e.getMessage();
            }
        }
        return null;

    }


    @Override
    public List<Product> getAllProducts() {


        PageRequest pageRequest = PageRequest.of(1, 10);

        PageRequest next = pageRequest.next();

        PageRequest previous = pageRequest.previous();

        Page<Product> all = productRepository.findAll(pageRequest);


        return productRepository.findAll();
    }


    public Page<Product> getProducts(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return productRepository.findAll(pageRequest);
    }


    @Override
    public Product getProductById(UUID id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteProduct(UUID id) {
        productRepository.deleteById(id);
    }



    public  List<Product> getProductByCategory(String category){
        return productRepository.findByCategory(category);
    }
    @Override
    public List<Product> findProductsByCategoryWithLimit(String categoryName, int limit) {
        return productRepository.findProductsByCategoryWithLimit(categoryName, PageRequest.of(0, limit));
    }

    @Override
    public Page<Product> getProducts(Pageable pageRequest) {
        return productRepository.findAll(pageRequest);
    }


}



