package Online.DTO;

import lombok.Data;

import java.util.UUID;

@Data
public class UserSignup {

    private UUID id;

    private  String fullName;

    private  String email;

    private  String password;
}
