package org.example.ecommerceauthenticationservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.example.ecommerceauthenticationservice.configs.JwtConfigs;
import org.example.ecommerceauthenticationservice.configs.KafkaClient;
import org.example.ecommerceauthenticationservice.dtos.EmailDto;
import org.example.ecommerceauthenticationservice.dtos.HandleLoginWithPasswordResponseServiceDto;

import org.example.ecommerceauthenticationservice.exceptions.*;
import org.example.ecommerceauthenticationservice.models.*;
import org.example.ecommerceauthenticationservice.repositories.RoleRepository;
import org.example.ecommerceauthenticationservice.repositories.SessionRepository;
import org.example.ecommerceauthenticationservice.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final KafkaClient kafkaClient;
    private final ObjectMapper objectMapper;
    private final JwtConfigs jwtConfigs;
    private final SessionRepository sessionRepository;



    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder,
                       KafkaClient kafkaClient, ObjectMapper objectMapper,
                       JwtConfigs jwtConfigs,
                       SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.kafkaClient = kafkaClient;
        this.objectMapper = objectMapper;
        this.jwtConfigs = jwtConfigs;
        this.sessionRepository = sessionRepository;

    }

    private String generateOtp() {
        Random random = new Random();
        return String.valueOf(random.nextInt(100000, 999999));
    }

    private void pushOtpToKafka(EmailDto emailDto) throws JsonProcessingException {
        kafkaClient.send("user_verification", objectMapper.writeValueAsString(emailDto));
    }

    private void pushClientRegistrationToKafka(EmailDto emailDto) throws JsonProcessingException {
        kafkaClient.send("client_registration", objectMapper.writeValueAsString(emailDto));
    }

    private Boolean ValidateJwt(String token) {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(jwtConfigs.getSecretKey());

            SecretKey key = Keys.hmacShaKeyFor(decodedKey);

            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);  // parseClaimsJws for verifying signed tokens

            String userId = claims.getBody().getSubject();
            Optional<Session> optionalSession = sessionRepository.findByToken(token);
            if (optionalSession.isEmpty()) {
                System.out.println("Session not found for the provided token");
                return false;
            }

            Session session = optionalSession.get();
            if (session.getSessionStatus() != SessionStatus.ACTIVE) {
                System.out.println("Session is not active");
                return false;
            }

            if (session.getExpiryDate().before(new Date())) {
                System.out.println("Session has expired");
                return false;
            }

            return true;
        } catch (JwtException e) {
            System.out.println("Invalid JWT: " + e.getMessage());
            return false;
        }

    }

    public String createUser(User user) throws UserAlreadyExistException, JsonProcessingException {

        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistException("User with this email already exists");
        }


        String hashedPassword = bCryptPasswordEncoder.encode(user.getPassword());

        List<Roles> roles = roleRepository.findAll();

        List<Roles> userRoles = roles.stream().filter(role -> role.getRole().equals("USER")).collect(Collectors.toList());

        user.setRoles(userRoles);
        user.setPassword(hashedPassword);
        user.setVerificationStatus(VerificationStatus.UNVERIFIED);

        String otp = generateOtp(); // Generate a random OTP
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 10);
        Date tenMinutesLater = calendar.getTime();
        user.setOtpExpiry(tenMinutesLater); // Set OTP expiry to 10 minutes from now
        // Set OTP expiry to null or a specific time if needed
        String body = "Your verification code is: " + otp;

        EmailDto emailDto = EmailDto.toDto("shubhamgupta746690@gmail.com", user.getEmail(), "Authentication Service", body);

        pushOtpToKafka(emailDto);
        user.setOtp(bCryptPasswordEncoder.encode(otp)); // Store the hashed OTP in the user object
        userRepository.save(user);

        return "Verification code sent to your email, please verify to complete registration";
        // return userRepository.save(user); // Uncomment this line if you want to save the user after sending the email
    }

    // function to create a new role
    public String createRoles(Roles role, Long userId, String token) throws RoleAlreadyExistException, UserNotFoundExceptions, UserNotAllowedException, UserNotLoggedInExceptions {


        if (token == null || !ValidateJwt(token)) {
            throw new UserNotLoggedInExceptions("Unable to verify your identity, please login again");
        }
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundExceptions("User not found with this ID");
        }

        User user = userOptional.get();
        List<Roles> userRoles = user.getRoles();
        Boolean isAllowed = false;

        for (Roles userRole : userRoles) {
            if (userRole.getRole().equals("ADMIN")) {
                isAllowed = true;
                break;
            }
        }

        if (!isAllowed) {
            throw new UserNotAllowedException("You are not authorized to create roles");
        }
        Optional<Roles> existingRole = roleRepository.findByRole((role.getRole()));
        if (existingRole.isPresent()) {
            throw new RoleAlreadyExistException("Role with this name already exists");
        }

        roleRepository.save(role);
        return "Role created successfully";
    }


    public String validateOtp(String email, String otp) throws UserNotFoundExceptions, JsonProcessingException, otpExpiredException, InvalidOtpException {
        Optional<User> userOptional = userRepository.findByEmail(email);
        // Check if user exists
        if (userOptional.isEmpty()) {
            throw new UserNotFoundExceptions("User not found with this email"); // User not found
        }

        if (userOptional.get().getOtpExpiry() == null || userOptional.get().getOtpExpiry().before(new Date())) {
            throw new otpExpiredException("OTP has expired, please request a new one");
        }

        User user = userOptional.get();


        if (bCryptPasswordEncoder.matches(otp, user.getOtp())) {
            user.setVerificationStatus(VerificationStatus.VERIFIED);
            user.setOtp(null); // Clear the OTP after successful verification
            userRepository.save(user);
            return createJwtToken(user); // Generate and return JWT token
        }

        throw new InvalidOtpException("Invalid OTP provided"); // OTP does not match

        // User not found or OTP does not match

    }


    public String resendOtp(String email) throws UserAlreadyExistException, JsonProcessingException {
        System.out.println(email);

        Optional<User> existingUser = userRepository.findByEmail(email);
//        if (existingUser.isPresent()) {
//            throw new UserAlreadyExistException("User with this email already exists");
//        }

        String otp = generateOtp();
        String body = "Your verification code is: " + otp;
        EmailDto emailDto = EmailDto.toDto("shubhamgupta746690@gmail.com", email, "Authentication Service", body);
        pushOtpToKafka(emailDto);
        User user = existingUser.get();
        user.setOtp(bCryptPasswordEncoder.encode(otp)); // Store the hashed OTP in the user object
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 10);
        Date tenMinutesLater = calendar.getTime();
        user.setOtpExpiry(tenMinutesLater); // Set OTP expiry to 10 minutes from now
        userRepository.save(user);
        // Optionally, you can return a message indicating that the OTP has been resent
        return "otp has been sent";

    }

    public HandleLoginWithPasswordResponseServiceDto loginWithPassword(User user) throws UserNotFoundExceptions, IncorrectCredentialExceptions, JsonProcessingException {
        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());
        if (optionalUser.isEmpty()) {
            throw new IncorrectCredentialExceptions("Either email or password is incorrect");
        }

        User userDetails = optionalUser.get();
        if (!bCryptPasswordEncoder.matches(user.getPassword(), userDetails.getPassword())) {
            throw new IncorrectCredentialExceptions("Either email or password is incorrect");
        }

        HandleLoginWithPasswordResponseServiceDto responseDto = new HandleLoginWithPasswordResponseServiceDto();

        if (userDetails.getVerificationStatus() == VerificationStatus.UNVERIFIED) {

            String otp = generateOtp();
            String body = "Your verification code is: " + otp;
            EmailDto emailDto = EmailDto.toDto("shubhamgupta746690@gmail.com", userDetails.getEmail(), "Authentication Service", body);
            pushOtpToKafka(emailDto);
            userDetails.setOtp(bCryptPasswordEncoder.encode(otp)); // Store the hashed OTP in the user object
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, 10);
            Date tenMinutesLater = calendar.getTime();
            user.setOtpExpiry(tenMinutesLater); // Set OTP expiry to 10 minutes from now
            userRepository.save(userDetails);

            responseDto.setMessage("Please verify your email to complete login");
            return responseDto;
        }

        responseDto.setToken(createJwtToken(userDetails));
        return responseDto;

    }

    private String createJwtToken(User user) {
        Map<String, Object> dataInJwt = new HashMap<>();
        dataInJwt.put("user_name", user.getName());
        dataInJwt.put("email", user.getEmail());

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 30); // Expire in 30 days
        Date expirationDate = calendar.getTime();

        byte[] decodedKey = Base64.getDecoder().decode(jwtConfigs.getSecretKey());
        SecretKey key = Keys.hmacShaKeyFor(decodedKey);

        String token = Jwts.builder()
                .setClaims(dataInJwt)                      // custom data
                .setIssuedAt(new Date())                   // current time
                .setExpiration(expirationDate)             // 30 days later
                .signWith(SignatureAlgorithm.HS256, key) // use secure key
                .compact();

        Session session = new Session();
        session.setUser(user);
        session.setToken(token);
        session.setCreatedAt(Calendar.getInstance().getTime());
        session.setSessionStatus(SessionStatus.ACTIVE);
        session.setExpiryDate(expirationDate);

        sessionRepository.save(session);
        return token;
    }

    public String updatePassword(String oldPassword, String newPassword, String email, String token) throws UserNotFoundExceptions, IncorrectCredentialExceptions, UserNotLoggedInExceptions {

        if (token == null || !ValidateJwt(token)) {
            throw new UserNotLoggedInExceptions("Unable to verify your identity, please login again");
        }

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundExceptions("User not found with this email");
        }

        User user = optionalUser.get();
        if (!bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IncorrectCredentialExceptions("Old password is incorrect");
        }

        String hashedNewPassword = bCryptPasswordEncoder.encode(newPassword);
        user.setPassword(hashedNewPassword);
        userRepository.save(user);

        return "Password updated successfully";
    }

    public String logout(String token) throws UserNotLoggedInExceptions {
        Optional<Session> optionalSession = sessionRepository.findByToken(token);
        if (optionalSession.isEmpty()) {
            throw new UserNotLoggedInExceptions("invalid logout request");
        }
        Session session = optionalSession.get();
        session.setSessionStatus(SessionStatus.EXPIRED);
        session.setExpiryDate(new Date()); // Set expiry date to current time
        sessionRepository.save(session);
        return "Logged out successfully";
    }

    public String assignRole(Long roleId, Long userId, Long adminId, String token) throws UserNotFoundExceptions, UserNotAllowedException, UserNotLoggedInExceptions, JsonProcessingException, RoleNotFoundExceptions {
        if (token == null || !ValidateJwt(token)) {
            throw new UserNotLoggedInExceptions("Unable to verify your identity, please login again");
        }

        Optional<User> userOptional = userRepository.findById(adminId);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundExceptions("User not found with this ID");
        }

        User user = userOptional.get();
        List<Roles> userRoles = user.getRoles();
        Boolean isAllowed = false;

        for (Roles userRole : userRoles) {
            if (userRole.getRole().equals("ADMIN")) {
                isAllowed = true;
                break;
            }
        }

        if (!isAllowed) {
            throw new UserNotAllowedException("You are not authorized to update roles");
        }

        Optional<User> targetUserOptional = userRepository.findById(userId);
        if (targetUserOptional.isEmpty()) {
            throw new UserNotFoundExceptions("User not found with this ID");
        }

        Optional<Roles> existingRole = roleRepository.findById(roleId);
        if (existingRole.isEmpty()) {
            throw new RoleNotFoundExceptions("Role not found with this name");
        }

        Roles roleToUpdate = existingRole.get();
        List<Roles> updatedRoles = new ArrayList<>(user.getRoles());
        updatedRoles.add(roleToUpdate);
        user.setRoles(updatedRoles);
        userRepository.save(user);

        return "User roles updated successfully";
    }

    public List<Roles> viewRoles(Long adminId, String token) throws UserNotFoundExceptions, UserNotAllowedException, UserNotLoggedInExceptions, JsonProcessingException {
        if (token == null || !ValidateJwt(token)) {
            throw new UserNotLoggedInExceptions("Unable to verify your identity, please login again");
        }

        Optional<User> userOptional = userRepository.findById(adminId);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundExceptions("User not found with this ID");
        }

        User user = userOptional.get();
        List<Roles> userRoles = user.getRoles();
        Boolean isAllowed = false;

        for (Roles userRole : userRoles) {
            if (userRole.getRole().equals("ADMIN")) {
                isAllowed = true;
                break;
            }
        }

        if (!isAllowed) {
            throw new UserNotAllowedException("You are not authorized to update roles");
        }

        return roleRepository.findAll();
        // Return the first role or modify as needed
    }

    public List<User> getUsersByRole(Long roleId, String token, Long adminId) throws UserNotFoundExceptions, UserNotAllowedException, UserNotLoggedInExceptions, JsonProcessingException {
        if (token == null || !ValidateJwt(token)) {
            throw new UserNotLoggedInExceptions("Unable to verify your identity, please login again");
        }

        Optional<User> userOptional = userRepository.findById(adminId);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundExceptions("User not found with this ID");
        }
        User user = userOptional.get();
        List<Roles> userRoles = user.getRoles();
        Boolean isAllowed = false;
        for (Roles userRole : userRoles) {
            if (userRole.getRole().equals("ADMIN")) {
                isAllowed = true;
                break;
            }
        }
        if (!isAllowed) {
            throw new UserNotAllowedException("You are not authorized to view users by role");
        }
        Optional<Roles> roleOptional = roleRepository.findById(roleId);
        if (roleOptional.isEmpty()) {
            throw new UserNotFoundExceptions("Role not found with this ID");
        }

        Roles role = roleOptional.get();
        List<User> usersWithRole = userRepository.findAll().stream()
                .filter(users -> users.getRoles().contains(role))
                .collect(Collectors.toList());

        return usersWithRole;
    }


    public String exchangeToken(String token,String keys) {
        byte[] decodedKey = Base64.getDecoder().decode(keys);

        SecretKey key = Keys.hmacShaKeyFor(decodedKey);

        Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);

        String email = claims.getBody().get("email", String.class);
        Optional<User> userOptional = userRepository.findByEmail(email);
        User newUser;
        if (userOptional.isEmpty()) {
            newUser = new User();
            newUser.setEmail(email);
            newUser.setName(claims.getBody().get("user_name", String.class));
            newUser.setVerificationStatus(VerificationStatus.VERIFIED);
            newUser.setPassword(bCryptPasswordEncoder.encode(UUID.randomUUID().toString())); // Set a random password
            newUser.setOtp(null); // Clear OTP as it's not needed for OAuth2
            List<Roles> roles =new ArrayList<>();
            roles.add(roleRepository.findByRole("USER").orElseThrow(() -> new RuntimeException("Default role not found")));
            newUser.setRoles(roles);
            userRepository.save(newUser);

        }
        else
        {
            newUser = userOptional.get();
        }

        String newToken = createJwtToken(newUser);
        Session session = new Session();
        session.setUser(newUser);
        session.setToken(newToken);
        session.setCreatedAt(Calendar.getInstance().getTime());
        session.setSessionStatus(SessionStatus.ACTIVE);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 30); // Expire in 30 days
        Date expirationDate = calendar.getTime();
        session.setExpiryDate(expirationDate);
        sessionRepository.save(session);
        return newToken;
    }
}