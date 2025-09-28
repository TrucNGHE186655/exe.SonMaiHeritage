package exe.SonMaiHeritage.service;

import exe.SonMaiHeritage.entity.Admin;
import exe.SonMaiHeritage.repository.AdminRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class AdminServiceImpl implements AdminService {
    
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    
    public AdminServiceImpl(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public Admin createAdmin(String username, String password, String email, String fullName) {
        log.info("Creating admin with username: {}", username);
        
        // Check if username already exists
        if (adminRepository.existsByUsername(username)) {
            throw new RuntimeException("Admin username already exists: " + username);
        }
        
        // Check if email already exists
        if (adminRepository.existsByEmail(email)) {
            throw new RuntimeException("Admin email already exists: " + email);
        }
        
        // Create new admin
        Admin admin = Admin.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .fullName(fullName)
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();
        
        Admin savedAdmin = adminRepository.save(admin);
        log.info("Admin created successfully with ID: {}", savedAdmin.getId());
        
        return savedAdmin;
    }
    
    @Override
    public Admin getAdminByUsername(String username) {
        log.info("Fetching admin by username: {}", username);
        return adminRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found with username: " + username));
    }
    
    @Override
    public Admin getAdminById(Integer id) {
        log.info("Fetching admin by ID: {}", id);
        return adminRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found with ID: " + id));
    }
    
    @Override
    public boolean existsByUsername(String username) {
        return adminRepository.existsByUsername(username);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return adminRepository.existsByEmail(email);
    }
    
    @Override
    public Admin loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading admin by username: {}", username);
        return adminRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found with username: " + username));
    }
}
