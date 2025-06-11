package org.example.ecommerceauthenticationservice.exceptions;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HandleError {
    String message;
    String errorDescription;
}
