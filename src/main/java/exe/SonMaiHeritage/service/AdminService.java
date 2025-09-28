package exe.SonMaiHeritage.service;

import exe.SonMaiHeritage.entity.Admin;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AdminService extends UserDetailsService {
    Admin createAdmin(String username, String password, String email, String fullName);
    Admin getAdminByUsername(String username);
    Admin getAdminById(Integer id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
