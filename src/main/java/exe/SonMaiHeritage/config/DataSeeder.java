package exe.SonMaiHeritage.config;

import exe.SonMaiHeritage.entity.*;
import exe.SonMaiHeritage.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Data Seeder for Son Mai Heritage Application
 * This class automatically seeds the database with sample data on application startup
 * Only runs if the database is empty
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final TypeRepository typeRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (shouldSeedData()) {
            log.info("Starting database seeding...");
            seedProductTypes();
            seedProducts();
            seedUsers();
            seedAddresses();
            log.info("Database seeding completed successfully!");
        } else {
            log.info("Database already contains data. Skipping seeding.");
        }
    }

    private boolean shouldSeedData() {
        return typeRepository.count() == 0 && userRepository.count() == 0;
    }

    private void seedProductTypes() {
        log.info("Seeding product types...");
        
        List<String> typeNames = Arrays.asList(
            "Áo dài truyền thống",
            "Trang sức bạc", 
            "Phụ kiện thổ cẩm",
            "Quần áo lụa",
            "Gốm sứ truyền thống",
            "Đồ gỗ thủ công",
            "Tranh dân gian",
            "Đồ thêu",
            "Mũ nón lá",
            "Giày dép thủ công"
        );

        for (String typeName : typeNames) {
            Type type = Type.builder()
                    .name(typeName)
                    .build();
            typeRepository.save(type);
        }
        
        log.info("Seeded {} product types", typeNames.size());
    }

    private void seedProducts() {
        log.info("Seeding products...");
        
        List<Type> types = typeRepository.findAll();
        
        // Áo dài truyền thống
        Type aoDaiType = types.stream().filter(t -> t.getName().equals("Áo dài truyền thống")).findFirst().orElse(null);
        if (aoDaiType != null) {
            createProduct("Áo dài lụa tơ tằm Hà Nội", 
                "Áo dài truyền thống được may từ lụa tơ tằm cao cấp với họa tiết hoa sen tinh tế", 
                2500000L, "https://example.com/ao-dai-1.jpg", aoDaiType);
            
            createProduct("Áo dài gấm Huế", 
                "Áo dài hoàng gia phong cách Huế với chất liệu gấm thêu rồng phượng", 
                3200000L, "https://example.com/ao-dai-hue.jpg", aoDaiType);
        }

        // Trang sức bạc
        Type silverType = types.stream().filter(t -> t.getName().equals("Trang sức bạc")).findFirst().orElse(null);
        if (silverType != null) {
            createProduct("Dây chuyền bạc hình rồng", 
                "Dây chuyền bạc ta thủ công với thiết kế rồng bay, biểu tượng quyền lực", 
                850000L, "https://example.com/silver-dragon.jpg", silverType);
            
            createProduct("Nhẫn bạc khắc chữ Hán", 
                "Nhẫn bạc với chữ Hán cổ, mang ý nghĩa phong thủy tốt lành", 
                420000L, "https://example.com/silver-ring.jpg", silverType);
        }

        // Phụ kiện thổ cẩm
        Type thoCamType = types.stream().filter(t -> t.getName().equals("Phụ kiện thổ cẩm")).findFirst().orElse(null);
        if (thoCamType != null) {
            createProduct("Túi xách thổ cẩm Sapa", 
                "Túi xách thổ cẩm được dệt thủ công bởi người H'Mông Sapa", 
                450000L, "https://example.com/tho-cam-bag.jpg", thoCamType);
            
            createProduct("Khăn quàng cổ thổ cẩm", 
                "Khăn quàng cổ với họa tiết thổ cẩm truyền thống của người Thái", 
                320000L, "https://example.com/tho-cam-scarf.jpg", thoCamType);
        }

        // Quần áo lụa
        Type silkType = types.stream().filter(t -> t.getName().equals("Quần áo lụa")).findFirst().orElse(null);
        if (silkType != null) {
            createProduct("Váy lụa thêu hoa", 
                "Váy lụa nữ thêu tay họa tiết hoa cúc, phong cách thanh lịch", 
                1600000L, "https://example.com/silk-dress.jpg", silkType);
            
            createProduct("Áo sơ mi lụa nam", 
                "Áo sơ mi lụa tự nhiên cho nam giới, thoáng mát và sang trọng", 
                1200000L, "https://example.com/silk-shirt-men.jpg", silkType);
        }

        // Gốm sứ truyền thống
        Type ceramicType = types.stream().filter(t -> t.getName().equals("Gốm sứ truyền thống")).findFirst().orElse(null);
        if (ceramicType != null) {
            createProduct("Bình hoa gốm Chu Đậu", 
                "Bình hoa gốm Chu Đậu với men xanh ngọc đặc trưng", 
                1200000L, "https://example.com/chu-dau-vase.jpg", ceramicType);
            
            createProduct("Tách trà gốm Bát Tràng", 
                "Bộ tách trà gốm Bát Tràng họa tiết hoa sen", 
                450000L, "https://example.com/bat-trang-tea.jpg", ceramicType);
        }

        // Đồ gỗ thủ công
        Type woodType = types.stream().filter(t -> t.getName().equals("Đồ gỗ thủ công")).findFirst().orElse(null);
        if (woodType != null) {
            createProduct("Tượng Phật gỗ mun", 
                "Tượng Phật Di Lặc bằng gỗ mun quý, chạm khắc tinh xảo", 
                2800000L, "https://example.com/buddha-statue.jpg", woodType);
            
            createProduct("Khay trà gỗ hương", 
                "Khay trà bằng gỗ hương thơm, thiết kế cổ điển", 
                850000L, "https://example.com/wooden-tray.jpg", woodType);
        }

        // Tranh dân gian
        Type paintingType = types.stream().filter(t -> t.getName().equals("Tranh dân gian")).findFirst().orElse(null);
        if (paintingType != null) {
            createProduct("Tranh Đông Hồ cá chép", 
                "Tranh Đông Hồ truyền thống với họa tiết cá chép hoa sen", 
                320000L, "https://example.com/dong-ho-fish.jpg", paintingType);
            
            createProduct("Tranh Hàng Trống gà trống", 
                "Tranh Hàng Trống với hình ảnh gà trống báo hiểu", 
                280000L, "https://example.com/hang-trong-rooster.jpg", paintingType);
        }

        log.info("Seeded {} products", productRepository.count());
    }

    private void createProduct(String name, String description, Long price, String pictureUrl, Type type) {
        Product product = Product.builder()
                .name(name)
                .description(description)
                .price(price)
                .pictureUrl(pictureUrl)
                .type(type)
                .build();
        productRepository.save(product);
    }

    private void seedUsers() {
        log.info("Seeding users...");
        
        // Admin user
        User admin = User.builder()
                .username("admin")
                .email("admin@sonmai.com")
                .password(passwordEncoder.encode("123456"))
                .firstName("Admin")
                .lastName("System")
                .phone("0123456789")
                .role(User.Role.ADMIN)
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();
        userRepository.save(admin);

        // Customer users
        List<User> customers = Arrays.asList(
            createUser("customer1", "nguyen.van.a@gmail.com", "Nguyễn", "Văn A", "0987654321"),
            createUser("customer2", "tran.thi.b@gmail.com", "Trần", "Thị B", "0976543210"),
            createUser("customer3", "le.van.c@gmail.com", "Lê", "Văn C", "0965432109"),
            createUser("customer4", "pham.thi.d@gmail.com", "Phạm", "Thị D", "0954321098")
        );

        userRepository.saveAll(customers);
        log.info("Seeded {} users", userRepository.count());
    }

    private User createUser(String username, String email, String firstName, String lastName, String phone) {
        return User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode("123456"))
                .firstName(firstName)
                .lastName(lastName)
                .phone(phone)
                .role(User.Role.USER)
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();
    }

    private void seedAddresses() {
        log.info("Seeding addresses...");
        
        List<User> users = userRepository.findAll();
        
        for (User user : users) {
            if (!user.getRole().equals(User.Role.ADMIN)) {
                Address address = Address.builder()
                        .fullName(user.getFirstName() + " " + user.getLastName())
                        .phone(user.getPhone())
                        .street("123 Đường " + user.getFirstName())
                        .ward("Phường Trung Tâm")
                        .district("Quận 1")
                        .province("TP. Hồ Chí Minh")
                        .user(user)
                        .build();
                addressRepository.save(address);
            }
        }
        
        log.info("Seeded {} addresses", addressRepository.count());
    }
}

