package org.example.ecommerceauthenticationservice.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.ecommerceauthenticationservice.dtos.*;
import org.example.ecommerceauthenticationservice.exceptions.*;
import org.example.ecommerceauthenticationservice.models.Roles;
import org.example.ecommerceauthenticationservice.models.User;
import org.example.ecommerceauthenticationservice.services.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthService authService;

    public AuthenticationController(AuthService authService) {
        this.authService = authService;
        // Constructor logic if needed
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




}
