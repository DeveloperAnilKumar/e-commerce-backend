package Online.ServiceImpl;

import Online.DTO.ActiveAccount;
import Online.DTO.ForgetPassword;
import Online.DTO.UserLogin;
import Online.DTO.UserSignup;
import Online.EmailUtlity.EmailConfig;
import Online.Entity.ShippingAddress;
import Online.Entity.User;
import Online.IService.IUser;
import Online.Repo.ShippingAddressRepository;
import Online.Repo.UserRepository;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;


@Service
public class UserService implements IUser {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailConfig emailConfig;


    @Autowired
    private  ShippingAddressRepository shippingAddressRepository;


    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public User findByEmail(String email) {
      Optional<User> user = userRepository.findByEmail(email);
        return user.orElse(null);

    }



    @Override
    public boolean createUser(UserSignup signup) {
        Integer count = userRepository.getEmailCount(signup.getEmail());

        if (count > 0) {
            return false; // Email already exists, return false
        } else {
            User user = new User();
            BeanUtils.copyProperties(signup, user);
            String encodedPassword = passwordEncoder.encode(signup.getPassword());
            user.setPassword(encodedPassword);
            user.setStatus("inActive");
            String otp = generateRandomPassword(6);
            user.setOtp(otp);

            String fullName = signup.getFullName();
            String email = signup.getEmail();
            try {
                // Sending an email with an activation message
                String emailContent = "Hi " + fullName + ",\n\nThank you for signing up! Please use the following OTP to activate your account: " + otp + "\n\nBest regards,\nThe Team";
                new Thread(() -> {
                    emailConfig.sendEmail(email, "Account Activation - OTP", emailContent);
                }).start();
            } catch (Exception e) {
                e.printStackTrace(); // Handle the exception appropriately (logging, etc.)
                return false; // Return false if email sending fails
            }

            return userRepository.save(user).getId() != null;
        }
    }



    public String userLogin(UserLogin login) {
        User user = new User();
        user.setEmail(login.getEmail());

        Example<User> userExample = Example.of(user);
        List<User> users = userRepository.findAll(userExample);

        if (users.isEmpty()) {
            return "invalid email id or password";
        } else {
            User storedUser = users.get(0);

            if (passwordEncoder.matches(login.getPassword(), storedUser.getPassword())) {
                if (storedUser.getStatus().equals("Active")) {
                    return storedUser.getId().toString();
                } else {
                    return "account not active";
                }
            } else {
                return "invalid email id or password";
            }
        }
    }





    @Override
    public UserSignup getUserById(UUID id) {
        Optional<User> byId = userRepository.findById(id);
        if (byId.isPresent()){
            UserSignup userSignup = new UserSignup();
            User user = byId.get();
            BeanUtils.copyProperties(user, userSignup);
            return  userSignup;
        }
        return null;
    }

    @Override
    public void deleteUserById(UUID uuid) {
        userRepository.deleteById(uuid);
    }

    @Override
    public List<UserSignup> getAllUser() {
        List<User> all = userRepository.findAll();
        List<UserSignup> userList = new ArrayList<>();
        for (User user : all) {
            UserSignup user1 = new UserSignup();
            BeanUtils.copyProperties(user, user1);
            userList.add(user1);
        }
        return  userList;


    }

    @Override
    public boolean forgetPassword(String email) {

        User byEmail = findByEmail(email);

        if (byEmail!=null){
            String  otp = generateRandomPassword(6);
            byEmail.setOtp(otp);
            new Thread(() -> {
                emailConfig.sendEmail(
                        byEmail.getEmail(),
                        "Forget Password",
                        "Hi " + byEmail.getFullName() + ", your OTP is: " + otp
                );
            }).start();
            userRepository.save(byEmail);
            return  true;
        }
        return  false;
    }

    @Override
    public boolean resetPassword(ForgetPassword forgetPassword) {
        User user = findByEmail(forgetPassword.getEmail());
        if (user != null && user.getOtp().equals(forgetPassword.getOtp())) {
            // Hash the new password before saving
            String newPassword = forgetPassword.getPassword();
            String hashedPassword = passwordEncoder.encode(newPassword);

            user.setPassword(hashedPassword);
            System.out.println("New Password: " + hashedPassword);
            userRepository.save(user);
            return true;
        }
        return false;
    }





    @Override
    public  boolean ActiveUserAcc(ActiveAccount account){
        User user = new User();
        user.setEmail(account.getEmail());
        user.setOtp(account.getOtp());
        Example<User> userExample = Example.of(user);
        List<User> all = userRepository.findAll(userExample);
        if (all.isEmpty()){
            return  false;
        }else{
            User user1 = all.get(0);
            user1.setStatus("Active");
            userRepository.save(user1);
            return  true;
        }
    }

    public ShippingAddress updateShippingAddress(UUID userId, ShippingAddress shippingAddress) {
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Get the user's existing shipping addresses
            Set<ShippingAddress> shippingAddresses = user.getShippingAddresses();

            // Add the new shipping address to the existing list
            shippingAddresses.add(shippingAddress);

            // Set the updated list of shipping addresses to the user
            user.setShippingAddresses(shippingAddresses);

            // Set the user for the new shipping address
            shippingAddress.setUser(user);

            // Save the updated user along with the new shipping address
            userRepository.save(user);

            // Return the added shipping address
            return shippingAddress;
        } else {
            throw new NoSuchElementException("User not found with ID: " + userId);
        }
    }




    public  Optional<User> getUserByUuid(UUID uuid){
       return userRepository.findById(uuid);
    }

    private String generateRandomPassword(Integer len)
        {
            final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            SecureRandom random = new SecureRandom();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < len; i++)
            {
                int randomIndex = random.nextInt(chars.length());
                sb.append(chars.charAt(randomIndex));
            }
            return sb.toString();
        }

}
