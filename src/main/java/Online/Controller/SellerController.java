package Online.Controller;

import Online.DTO.ActiveAccount;
import Online.DTO.ForgetPassword;
import Online.DTO.UserLogin;
import Online.DTO.UserSignup;
import Online.IService.ISeller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/sellers")
public class SellerController {

    private final ISeller sellerService;

    @Autowired
    public SellerController(ISeller sellerService) {
        this.sellerService = sellerService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createSeller(@RequestBody UserSignup signup) {
        boolean result = sellerService.createSeller(signup);
        if (result) {
            return ResponseEntity.ok("Seller created successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to create seller");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> userLogin(@RequestBody UserLogin login) {
        String result = sellerService.userLogin(login);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserSignup> getUserById(@PathVariable UUID id) {
        UserSignup userSignup = sellerService.getUserById(id);
        if (userSignup != null) {
            return ResponseEntity.ok(userSignup);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserSignup>> getAllSellers() {
        List<UserSignup> sellers = sellerService.getAllUser();
        return ResponseEntity.ok(sellers);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable UUID id) {
        sellerService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/forgetPassword")
    public ResponseEntity<String> forgetPassword(@RequestParam String email) {
        boolean result = sellerService.forgetPassword(email);
        if (result) {
            return ResponseEntity.ok("Password reset link sent successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to send reset link");
        }
    }

    @PostMapping("/activateAccount")
    public ResponseEntity<String> activateAccount(@RequestBody ActiveAccount account) {
        boolean result = sellerService.ActiveUserAcc(account);
        if (result) {
            return ResponseEntity.ok("Account activated successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to activate account");
        }
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestBody ForgetPassword forgetPassword) {
        boolean result = sellerService.resetPassword(forgetPassword);
        if (result) {
            return ResponseEntity.ok("Password reset successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to reset password");
        }
    }
}
