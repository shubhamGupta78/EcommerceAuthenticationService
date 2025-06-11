package org.example.ecommerceauthenticationservice.dtos;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateOtpRequestDto {

    private String email;
    private String otp;


}
