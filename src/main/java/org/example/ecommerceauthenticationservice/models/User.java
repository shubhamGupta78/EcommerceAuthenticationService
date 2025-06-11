package org.example.ecommerceauthenticationservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name="user_table")
public class User extends BaseModel {

    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private String otp;
    private Date otpExpiry;

    @ManyToMany
    private List<Roles> roles;


    private VerificationStatus verificationStatus;

}
