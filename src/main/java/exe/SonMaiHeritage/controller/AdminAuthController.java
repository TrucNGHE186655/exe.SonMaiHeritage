package exe.SonMaiHeritage.controller;

import exe.SonMaiHeritage.model.AdminLoginRequest;
import exe.SonMaiHeritage.model.AdminLoginResponse;
import exe.SonMaiHeritage.security.JwtHelper;
import exe.SonMaiHeritage.service.AdminService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/auth")
@Log4j2
public class AdminAuthController {
    
    private final AdminService adminService;
    private final AuthenticationManager authenticationManager;
    private final JwtHelper jwtHelper;
    
    public AdminAuthController(AdminService adminService, AuthenticationManager authenticationManager, JwtHelper jwtHelper) {
        this.adminService = adminService;
        this.authenticationManager = authenticationManager;
        this.jwtHelper = jwtHelper;
    }
    
    @PostMapping("/login")
    public ResponseEntity<AdminLoginResponse> login(@Valid @RequestBody AdminLoginRequest request) {
        try {
            log.info("Admin login attempt for username: {}", request.getUsername());
            
            // Authenticate admin
            this.authenticate(request.getUsername(), request.getPassword());
            
            // Load user details and generate token
            UserDetails userDetails = adminService.loadUserByUsername(request.getUsername());
            String token = this.jwtHelper.generateToken(userDetails);
            
            AdminLoginResponse response = AdminLoginResponse.builder()
                    .success(true)
                    .username(userDetails.getUsername())
                    .token(token)
                    .message("Login successful")
                    .build();
            
            log.info("Admin login successful for username: {}", request.getUsername());
            return ResponseEntity.ok(response);
            
        } catch (BadCredentialsException e) {
            log.warn("Admin login failed for username: {} - Invalid credentials", request.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AdminLoginResponse.builder()
                            .success(false)
                            .message("Invalid username or password")
                            .build());
        } catch (Exception e) {
            log.error("Admin login error for username: {} - {}", request.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AdminLoginResponse.builder()
                            .success(false)
                            .message("Internal server error")
                            .build());
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<AdminLoginResponse> logout(@RequestHeader("Authorization") String tokenHeader) {
        try {
            String token = extractTokenFromHeader(tokenHeader);
            if (token != null) {
                log.info("Admin logout successful");
                return ResponseEntity.ok(AdminLoginResponse.builder()
                        .success(true)
                        .message("Logout successful. Please remove the token on client side.")
                        .build());
            } else {
                return ResponseEntity.badRequest()
                        .body(AdminLoginResponse.builder()
                                .success(false)
                                .message("Invalid token")
                                .build());
            }
        } catch (Exception e) {
            log.error("Admin logout error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AdminLoginResponse.builder()
                            .success(false)
                            .message("Internal server error")
                            .build());
        }
    }
    
    private void authenticate(String username, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = 
                new UsernamePasswordAuthenticationToken(username, password);
        try {
            authenticationManager.authenticate(authenticationToken);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid Username or Password");
        }
    }
    
    private String extractTokenFromHeader(String tokenHeader) {
        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            return tokenHeader.substring(7); // Remove "Bearer " prefix
        }
        return null;
    }
}
