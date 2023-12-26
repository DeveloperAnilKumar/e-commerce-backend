package Online.DTO;

import lombok.Data;

@Data
public class ForgetPassword {

    private  String email;
    private  String otp;
    private  String password;
}
