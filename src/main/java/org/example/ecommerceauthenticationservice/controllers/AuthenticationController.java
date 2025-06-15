package org.example.ecommerceauthenticationservice.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.GeneratedValue;
import org.example.ecommerceauthenticationservice.dtos.*;
import org.example.ecommerceauthenticationservice.exceptions.*;
import org.example.ecommerceauthenticationservice.models.Roles;
import org.example.ecommerceauthenticationservice.models.User;
import org.example.ecommerceauthenticationservice.services.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthService authService;

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String rsaKey;

    public AuthenticationController(AuthService authService) {
        this.authService = authService;
        // Constructor logic if needed
    }

    @GetMapping("/token/exchange")
    public ResponseEntity<String> exchangeToken(@RequestHeader("Authorization") String token) throws UserNotFoundExceptions, JsonProcessingException, UserNotLoggedInExceptions {
        token = token.substring(7); // Remove "Bearer " prefix
        String newToken = authService.exchangeToken(token,rsaKey);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + newToken);
        return ResponseEntity.ok()
                .headers(headers)
                .body("Token exchanged successfully");
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponseDto> registerUser(@RequestBody RegisterUserRequestDto registerUserRequestDto) throws UserAlreadyExistException, JsonProcessingException {
       String message=authService.createUser(registerUserRequestDto.toUser());
        RegisterUserResponseDto responseDto = new RegisterUserResponseDto();
        responseDto.setMessage(message);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/create/roles")
    public ResponseEntity<String> createRoles(@RequestHeader("Authorization") String token, @RequestBody CreateRoleRequestDto createRoleRequestDto) throws RoleAlreadyExistException, UserNotFoundExceptions, UserNotAllowedException, UserNotLoggedInExceptions {
        token= token.substring(7); // Remove "Bearer " prefix
       String message=authService.createRoles(createRoleRequestDto.toRole(), createRoleRequestDto.getUserId(),token);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/validate/otp")
    public ResponseEntity<String> validateOtp(@RequestBody ValidateOtpRequestDto validateOtpRequestDto) throws UserNotFoundExceptions, JsonProcessingException, otpExpiredException, InvalidOtpException {
        String token = authService.validateOtp(validateOtpRequestDto.getEmail(), validateOtpRequestDto.getOtp());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        return ResponseEntity.ok()
                .headers(headers)
                .body("Login successful");
    }

    @PostMapping("/resend/otp")
    public ResponseEntity<String> resendOtp(@RequestBody ResendOtpRequestDto resendOtpRequestDto) throws UserNotFoundExceptions, JsonProcessingException, UserAlreadyExistException {
        String message = authService.resendOtp(resendOtpRequestDto.getEmail());
        return ResponseEntity.ok(message);
    }



    @PostMapping("/login/password")
    public ResponseEntity<String> loginWithPassword(@RequestBody LoginPasswordRequestDto loginPasswordRequestDto) throws UserNotFoundExceptions, IncorrectCredentialExceptions, JsonProcessingException {
        HandleLoginWithPasswordResponseServiceDto response = authService.loginWithPassword(loginPasswordRequestDto.toUser());
        if (response.getMessage() != null) {
            return ResponseEntity.ok(response.getMessage());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + response.getToken());
        return ResponseEntity.ok()
                .headers(headers)
                .body("Login successful");

    }

    @PostMapping("/login/otp")
    public ResponseEntity<String> loginWithOtp(@RequestBody ResendOtpRequestDto resendOtpRequestDto) throws UserNotFoundExceptions, IncorrectCredentialExceptions, JsonProcessingException, UserAlreadyExistException {
        System.out.println("email"+resendOtpRequestDto.getEmail());
       String message=authService.resendOtp(resendOtpRequestDto.getEmail());
        return ResponseEntity.ok(message);

    }


    @PostMapping("/update/password")
        public ResponseEntity<String> updatePassword(@RequestHeader("Authorization") String token ,@RequestBody UpdatePasswordRequestDto updatePasswordRequestDto) throws UserNotFoundExceptions, IncorrectCredentialExceptions, JsonProcessingException, UserNotLoggedInExceptions {
        token=token.substring(7);
        String message = authService.updatePassword(updatePasswordRequestDto.getOldPassword(), updatePasswordRequestDto.getNewPassword(), updatePasswordRequestDto.getEmail(),token);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) throws UserNotFoundExceptions, JsonProcessingException, UserNotLoggedInExceptions {
        token = token.substring(7);
        String message = authService.logout(token);
        return ResponseEntity.ok(message);
    }

    @PatchMapping("/update/roles")
    public ResponseEntity<String> updateRoles(@RequestHeader("Authorization") String token, @RequestBody UpdateRoleRequestDto updateRoleRequestDto) throws UserNotFoundExceptions, UserNotAllowedException, UserNotLoggedInExceptions, JsonProcessingException, RoleNotFoundExceptions {
        token = token.substring(7);
        String message = authService.assignRole(updateRoleRequestDto.getRoleId(), updateRoleRequestDto.getUserId(),updateRoleRequestDto.getAdminId(), token);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/view/roles")
    public ResponseEntity<List<Roles>> viewRoles(@RequestHeader("Authorization") String token, @RequestBody ViewRolesRequestDto viewRolesRequestDto) throws UserNotFoundExceptions, UserNotAllowedException, UserNotLoggedInExceptions, JsonProcessingException {
        token = token.substring(7);
       List<Roles> roles = authService.viewRoles(viewRolesRequestDto.getAdminId(), token);
        return ResponseEntity.ok(roles);
    }



    @GetMapping("/users/{roleId}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable Long roleId, @RequestHeader("Authorization") String token,@RequestBody ViewRolesRequestDto viewRolesRequestDto) throws UserNotFoundExceptions, UserNotAllowedException, UserNotLoggedInExceptions, JsonProcessingException {
        token = token.substring(7);
        List<User> users = authService.getUsersByRole(roleId, token,viewRolesRequestDto.getAdminId());
        return ResponseEntity.ok(users);
    }


}
