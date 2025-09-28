# Hướng Dẫn Sử Dụng - Son Mai Heritage

## Giới Thiệu

**Son Mai Heritage** là hệ thống bán hàng trực tuyến chuyên về các sản phẩm sơn mài truyền thống Việt Nam. Hệ thống cho phép khách hàng xem và mua sản phẩm mà không cần đăng nhập, chỉ cần cung cấp thông tin khi thanh toán.

## Sản Phẩm Sơn Mài

### Danh Mục Sản Phẩm

1. **Hộp Sơn Mài** - Các loại hộp sơn mài với họa tiết truyền thống
2. **Khay Sơn Mài** - Khay sơn mài đựng đồ trang trí
3. **Bát Đĩa Sơn Mài** - Bộ bát đĩa sơn mài dùng trong gia đình
4. **Tranh Sơn Mài** - Tranh sơn mài nghệ thuật cao cấp
5. **Đồ Gỗ Sơn Mài** - Bàn ghế, tủ sơn mài nội thất
6. **Tượng Sơn Mài** - Tượng Phật, rồng, hoa sen sơn mài
7. **Bình Lọ Sơn Mài** - Bình lọ trang trí sơn mài
8. **Đồ Trang Trí Sơn Mài** - Đèn, gương, đồng hồ sơn mài

### Đặc Điểm Sản Phẩm

- **Chất liệu**: Gỗ tự nhiên + sơn mài truyền thống
- **Họa tiết**: Hoa sen, rồng phượng, làng quê Việt Nam
- **Màu sắc**: Đỏ, đen, vàng truyền thống
- **Kích thước**: Đa dạng từ nhỏ đến lớn
- **Giá cả**: Từ 180,000 VNĐ đến 4,500,000 VNĐ
- **Số lượng**: Mỗi sản phẩm có số lượng có hạn trong kho

## Cách Sử Dụng Hệ Thống

### 1. Khách Hàng Mua Hàng

#### Bước 1: Xem Sản Phẩm
```bash
# Xem tất cả sản phẩm
GET /api/products

# Xem sản phẩm theo danh mục
GET /api/products?typeId=1

# Tìm kiếm sản phẩm
GET /api/products/search?keyword=hoa sen

# Xem chi tiết sản phẩm
GET /api/products/1
```

#### Bước 2: Thêm Vào Giỏ Hàng
```bash
POST /api/session-basket/add
{
  "productId": 1,
  "quantity": 2,
  "productName": "Hộp Sơn Mài Hoa Sen",
  "productDescription": "Hộp sơn mài truyền thống với họa tiết hoa sen",
  "productPrice": 850000,
  "productPictureUrl": "https://example.com/hoop-son-mai-hoa-sen.jpg",
  "productType": "Hộp Sơn Mài"
}
```

#### Bước 3: Thanh Toán
```bash
POST /api/checkout/vnpay
{
  "sessionId": "your-session-id",
  "totalAmount": 1700000,
  "items": [...],
  "guestEmail": "customer@gmail.com",
  "guestPhone": "0123456789",
  "guestFullName": "Nguyễn Văn A",
  "shipFullName": "Nguyễn Văn A",
  "shipPhone": "0123456789",
  "shipStreet": "123 Đường ABC",
  "shipWard": "Phường 1",
  "shipDistrict": "Quận 1",
  "shipProvince": "TP. Hồ Chí Minh"
}
```

### 2. Admin Quản Lý

#### Đăng Nhập Admin
```bash
POST /api/admin/auth/login
{
  "username": "admin",
  "password": "admin123"
}
```

#### Quản Lý Sản Phẩm
```bash
# Tạo sản phẩm mới
POST /api/admin/products
Authorization: Bearer <token>
{
  "name": "Hộp Sơn Mài Mới",
  "description": "Hộp sơn mài với họa tiết mới",
  "price": 750000,
  "pictureUrl": "https://example.com/new-product.jpg",
  "typeId": 1
}

# Cập nhật sản phẩm
PUT /api/admin/products/1
Authorization: Bearer <token>
{
  "name": "Hộp Sơn Mài Hoa Sen - Cập Nhật",
  "description": "Hộp sơn mài truyền thống với họa tiết hoa sen - Phiên bản mới",
  "price": 950000,
  "pictureUrl": "https://example.com/updated-product.jpg",
  "typeId": 1
}
```

#### Xem Đơn Hàng
```bash
# Xem tất cả đơn hàng
GET /api/admin/orders
Authorization: Bearer <token>

# Xem thống kê
GET /api/admin/orders/statistics
Authorization: Bearer <token>
```

## Database Schema

### Bảng Type (Danh Mục)
```sql
CREATE TABLE Type (
    Id INT PRIMARY KEY AUTO_INCREMENT,
    Name VARCHAR(255) NOT NULL
);
```

### Bảng Product (Sản Phẩm)
```sql
CREATE TABLE Product (
    Id INT PRIMARY KEY AUTO_INCREMENT,
    Name VARCHAR(255) NOT NULL,
    Description TEXT,
    Price BIGINT NOT NULL,
    PictureUrl VARCHAR(500),
    ProductTypeId INT,
    FOREIGN KEY (ProductTypeId) REFERENCES Type(Id)
);
```

### Bảng Guest Customers (Khách Hàng)
```sql
CREATE TABLE guest_customers (
    id INT PRIMARY KEY AUTO_INCREMENT,
    session_id VARCHAR(255) UNIQUE,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    created_date TIMESTAMP,
    updated_date TIMESTAMP
);
```

### Bảng Orders (Đơn Hàng)
```sql
CREATE TABLE orders (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_code VARCHAR(255) UNIQUE,
    guest_customer_id INT,
    total_amount BIGINT,
    status ENUM('PENDING', 'CONFIRMED', 'SHIPPING', 'DELIVERED', 'CANCELLED'),
    payment_method VARCHAR(50),
    payment_status VARCHAR(50),
    created_date TIMESTAMP,
    updated_date TIMESTAMP,
    ship_full_name VARCHAR(255),
    ship_phone VARCHAR(255),
    ship_street VARCHAR(255),
    ship_ward VARCHAR(255),
    ship_district VARCHAR(255),
    ship_province VARCHAR(255),
    FOREIGN KEY (guest_customer_id) REFERENCES guest_customers(id)
);
```

## Dữ Liệu Mẫu

### Sản Phẩm Sơn Mài
- **24 sản phẩm** thuộc **8 danh mục** khác nhau
- **Giá từ 180,000 VNĐ đến 4,500,000 VNĐ**
- **Họa tiết**: Hoa sen, rồng phượng, làng quê, hoa văn truyền thống
- **Chất liệu**: Gỗ + sơn mài truyền thống Việt Nam

### Đơn Hàng Mẫu
- **3 đơn hàng** với các sản phẩm sơn mài khác nhau
- **Tổng giá trị**: Từ 1,200,000 VNĐ đến 3,500,000 VNĐ
- **Trạng thái**: CONFIRMED, PENDING

## Lưu Ý Quan Trọng

### Về Sản Phẩm Sơn Mài
1. **Chất lượng**: Tất cả sản phẩm đều được làm thủ công
2. **Bảo quản**: Tránh ánh nắng trực tiếp, độ ẩm cao
3. **Vận chuyển**: Cần đóng gói cẩn thận do dễ vỡ
4. **Bảo hành**: 1 năm cho sản phẩm sơn mài

### Về Hệ Thống
1. **Không cần đăng nhập**: Khách hàng có thể mua hàng ngay
2. **Thanh toán VNPay**: Hỗ trợ thanh toán trực tuyến
3. **Admin quản lý**: Cần đăng nhập để quản lý sản phẩm
4. **Session-based**: Giỏ hàng dựa trên session ID

## Troubleshooting

### Lỗi Thường Gặp
1. **Sản phẩm không hiển thị**: Kiểm tra database có dữ liệu không
2. **Thanh toán thất bại**: Kiểm tra cấu hình VNPay
3. **Admin không đăng nhập được**: Kiểm tra username/password
4. **Giỏ hàng trống**: Kiểm tra session ID
5. **Sản phẩm không đủ số lượng**: Kiểm tra quantity trong database
6. **Không thể thêm vào giỏ hàng**: Sản phẩm có thể đã hết hàng

### Liên Hệ Hỗ Trợ
- **Email**: support@sonmaiheritage.com
- **Hotline**: 1900-xxxx
- **Website**: https://sonmaiheritage.com

---

**Son Mai Heritage** - Bảo tồn và phát triển nghề sơn mài truyền thống Việt Nam 🇻🇳
