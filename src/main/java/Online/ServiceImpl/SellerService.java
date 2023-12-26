package Online.ServiceImpl;

import Online.DTO.ActiveAccount;
import Online.DTO.ForgetPassword;
import Online.DTO.UserLogin;
import Online.DTO.UserSignup;
import Online.EmailUtlity.EmailConfig;
import Online.Entity.Seller;
import Online.Entity.User;
import Online.IService.ISeller;
import Online.Repo.SellerRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class SellerService implements ISeller {

    @Autowired
    private SellerRepository sellerRepository;
    @Autowired
    private EmailConfig emailConfig;

    @Autowired
    private PasswordEncoder passwordEncoder;


    private String generateRandomPassword(Integer len) {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            int randomIndex = random.nextInt(chars.length());
            sb.append(chars.charAt(randomIndex));
        }
        return sb.toString();
    }

    @Override
    public Seller findByEmail(String email) {
        Optional<Seller> seller = sellerRepository.findByEmail(email);
        return seller.orElse(null);
    }

    @Override
    public boolean createSeller(UserSignup signup) {

        Integer count = sellerRepository.getEmailCount(signup.getEmail());

        if (count > 0) {
            return false;
        } else {
            Seller seller = new Seller();



            BeanUtils.copyProperties(signup, seller);
            String  hashPassword = passwordEncoder.encode(signup.getPassword());
            seller.setPassword(hashPassword);
            seller.setStatus("inActive");
            String otp = generateRandomPassword(6);
            seller.setOtp(otp);

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
            return sellerRepository.save(seller).getId() != null;


        }

    }

    @Override
    public String userLogin(UserLogin login) {
        Seller seller = new Seller();
        seller.setEmail(login.getEmail());

        Example<Seller> sellerExample = Example.of(seller);
        List<Seller> sellers = sellerRepository.findAll(sellerExample);

        if (sellers.isEmpty()) {
            return "invalid email or password";
        } else {
            Seller storedSeller = sellers.get(0);

            // Use BCryptPasswordEncoder to match hashed passwords
            if (passwordEncoder.matches(login.getPassword(), storedSeller.getPassword())) {
                if (storedSeller.getStatus().equals("Active")) {
                    return storedSeller.getId().toString();
                } else {
                    return "account not active";
                }
            } else {
                return "invalid email or password";
            }
        }
    }



    @Override
    public UserSignup getUserById(UUID id) {
        Optional<Seller> seller = sellerRepository.findById(id);
        if (seller.isPresent()) {
            UserSignup userSignup = new UserSignup();
            Seller seller1 = seller.get();
            BeanUtils.copyProperties(seller1, userSignup);
            return userSignup;
        }
        return null;


    }

    @Override
    public void deleteUserById(UUID uuid) {
        sellerRepository.deleteById(uuid);

    }

    @Override
    public List<UserSignup> getAllUser() {
        List<Seller> all = sellerRepository.findAll();
        List<UserSignup> sellerList = new ArrayList<>();

        for (Seller seller : all) {
            UserSignup user1 = new UserSignup();
            BeanUtils.copyProperties(seller, user1);
            sellerList.add(user1);
        }
        return sellerList;
    }

    @Override
    public boolean forgetPassword(String email) {

        Optional<Seller> seller = sellerRepository.findByEmail(email);
        if (seller.isPresent()) {
            Seller seller1 = seller.get();
            String otp = generateRandomPassword(6);
            seller1.setOtp(otp);
            new Thread(() -> {
                emailConfig.sendEmail(
                        seller1.getEmail(),
                        "Forget Password",
                        "Hi " + seller1.getFullName() + ", your OTP is: " + otp
                );
            }).start();
            sellerRepository.save(seller1);
            return true;
        }


        return false;
    }

    @Override
    public boolean ActiveUserAcc(ActiveAccount account) {

        Seller seller = new Seller();
        seller.setEmail(account.getEmail());
        seller.setOtp(account.getOtp());
        Example<Seller> sellerExample = Example.of(seller);
        List<Seller> all = sellerRepository.findAll(sellerExample);

        if (all.isEmpty()) {
            return false;
        } else {
            Seller seller1 = all.get(0);
            seller1.setStatus("Active");
            sellerRepository.save(seller1);
            return true;
        }

    }

    @Override
    public boolean resetPassword(ForgetPassword forgetPassword) {
        Seller seller = findByEmail(forgetPassword.getEmail());
        if (seller != null && seller.getOtp().equals(forgetPassword.getOtp())) {
            // Hash the new password before saving
            String newPassword = forgetPassword.getPassword();
            String hashedPassword = passwordEncoder.encode(newPassword);

            seller.setPassword(hashedPassword);
            sellerRepository.save(seller);
            return true;
        }
        return false;
    }
}
