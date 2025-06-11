package org.example.ecommerceauthenticationservice.dtos;

import lombok.Getter;
import lombok.Setter;
import org.example.ecommerceauthenticationservice.models.Roles;

@Getter
@Setter
public class CreateRoleRequestDto {

    private Long userId;
    String role;



    public Roles toRole() {
        Roles role = new Roles();
        role.setRole(this.role);
        return role;
    }
}
