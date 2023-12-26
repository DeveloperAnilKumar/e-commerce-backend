package Online.IService;

import Online.DTO.ActiveAccount;
import Online.DTO.ForgetPassword;
import Online.DTO.UserLogin;
import Online.DTO.UserSignup;
import Online.Entity.Seller;
import Online.Entity.User;

import java.util.List;
import java.util.UUID;

public interface ISeller {


    public abstract Seller findByEmail(String email);

    public  abstract  boolean  createSeller(UserSignup signup);

    public  abstract String userLogin(UserLogin login);

    public  abstract UserSignup getUserById(UUID id);

    public  abstract  void   deleteUserById(UUID uuid);

    public  abstract List<UserSignup> getAllUser();


    public  abstract boolean forgetPassword(String email);


    public  boolean ActiveUserAcc(ActiveAccount account);


    public boolean resetPassword(ForgetPassword forgetPassword);
}
