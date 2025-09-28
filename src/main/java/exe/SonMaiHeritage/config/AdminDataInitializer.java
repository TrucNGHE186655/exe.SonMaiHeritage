package exe.SonMaiHeritage.config;

import exe.SonMaiHeritage.service.AdminService;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class AdminDataInitializer implements CommandLineRunner {
    
    private final AdminService adminService;
    
    public AdminDataInitializer(AdminService adminService) {
        this.adminService = adminService;
    }
    
    @Override
    public void run(String... args) throws Exception {
        // Tạo admin mặc định nếu chưa có
        try {
            if (!adminService.existsByUsername("admin")) {
                adminService.createAdmin("admin", "admin123", "admin@sonmaiheritage.com", "Administrator");
                log.info("Default admin created: username=admin, password=admin123");
            } else {
                log.info("Admin already exists, skipping creation");
            }
        } catch (Exception e) {
            log.error("Error creating default admin: {}", e.getMessage());
        }
    }
}
