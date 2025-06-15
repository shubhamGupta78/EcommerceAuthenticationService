package org.example.ecommerceauthenticationservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRoleRequestDto {
    private Long userId;;
    private Long adminId;
    private Long roleId;

}
