package org.example.ecommerceauthenticationservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePasswordRequestDto {

    private String oldPassword;
    private String newPassword;
    private String email;


}
