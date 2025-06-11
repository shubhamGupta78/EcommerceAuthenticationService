package org.example.ecommerceauthenticationservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HandleLoginWithPasswordResponseServiceDto {

    private String token;
    private String message;
}
