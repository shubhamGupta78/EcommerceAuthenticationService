package org.example.ecommerceauthenticationservice.dtos;

import lombok.Getter;
import lombok.Setter;
import org.example.ecommerceauthenticationservice.models.User;

@Getter
@Setter
public class RegisterUserRequestDto {

    private String name;
    private String email;
    private String password;
    private String phoneNumber;


    public User toUser() {
        User user = new User();
        user.setName(this.name);
        user.setEmail(this.email);
        user.setPassword(this.password);
        user.setPhoneNumber(this.phoneNumber);
        return user;
    }

}
