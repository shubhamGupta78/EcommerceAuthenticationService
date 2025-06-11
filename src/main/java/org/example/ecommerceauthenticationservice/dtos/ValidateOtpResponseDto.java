package org.example.ecommerceauthenticationservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateOtpResponseDto {

    private String token;
    private Boolean isValid;
}
