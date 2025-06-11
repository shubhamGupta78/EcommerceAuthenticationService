package org.example.ecommerceauthenticationservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Entity
@Table(name="user_roles")
/**
 * BaseModel serves as a base class for all models in the application.
 * It can be extended by other model classes to inherit common properties or methods.
 */
public class Roles extends BaseModel{
    private String role;
}
