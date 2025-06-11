package org.example.ecommerceauthenticationservice.dtos;

import lombok.Getter;
import lombok.Setter;
import org.example.ecommerceauthenticationservice.models.User;

@Getter
@Setter
public class LoginPasswordRequestDto {
    private String email;
    private String password;

    public User toUser() {
        User user = new User();
        user.setEmail(this.email);
        user.setPassword(this.password);
        return user;
    }


}
