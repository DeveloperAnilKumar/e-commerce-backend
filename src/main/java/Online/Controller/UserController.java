package Online.Controller;

import Online.DTO.ActiveAccount;
import Online.DTO.ForgetPassword;
import Online.DTO.UserLogin;
import Online.DTO.UserSignup;
import Online.Entity.ShippingAddress;
import Online.Entity.User;
import Online.Repo.UserRepository;
import Online.ServiceImpl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("api/v1/user")
public class UserController {


    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;


    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody UserSignup signup) {
        boolean user = userService.createUser(signup);
        if (!user) {
            return new ResponseEntity<>("email id already exits", HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity<>("account created", HttpStatus.CREATED);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> userLogin(@RequestBody UserLogin login) {
        return new ResponseEntity<>(userService.userLogin(login), HttpStatus.OK);
    }


    @PostMapping("/active")
    public ResponseEntity<String> activeUserAccount(@RequestBody ActiveAccount ActiveUserAcc) {
        boolean b = userService.ActiveUserAcc(ActiveUserAcc);
        if (b) {
            return new ResponseEntity<>("Active User Account", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("invalid otp", HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping
    public ResponseEntity<List<UserSignup>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUser(), HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserSignup> getUserById(@PathVariable UUID id) {
        return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserById(UUID id) {
        userService.deleteUserById(id);
        return new ResponseEntity<>("user deleted", HttpStatus.OK);
    }


    @GetMapping("/rest/{email}")
    public ResponseEntity<String> passwordRestLink(@PathVariable String email) {
        boolean b = userService.forgetPassword(email);
        if (b) {
            return new ResponseEntity<>("An email has been sent to your registered email account", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("invalid email id", HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/forgetPassword")
    public ResponseEntity<String> forgetPassword(@RequestBody ForgetPassword forgetPassword) {

        boolean b = userService.resetPassword(forgetPassword);

        if (b) {
            return new ResponseEntity<>("password rest successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("invalid otp", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{uuid}/user")
    public ResponseEntity<User> getUserByUuid(@PathVariable UUID uuid) {
        Optional<User> user = userRepository.findById(uuid);
        return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    @PutMapping("/{userId}/shipping-address")
    public ResponseEntity<?> updateShippingAddress(
            @PathVariable UUID userId,
            @RequestBody ShippingAddress shippingAddress) {

        try {
            ShippingAddress shippingAddress1 = userService.updateShippingAddress(userId, shippingAddress);
            return new ResponseEntity<>(shippingAddress1, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
