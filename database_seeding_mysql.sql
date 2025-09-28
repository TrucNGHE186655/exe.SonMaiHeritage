-- Database Seeding Script for Son Mai Heritage - Sản Phẩm Sơn Mài
-- Chạy script này để tạo dữ liệu mẫu cho hệ thống bán sản phẩm sơn mài

-- 1. Tạo bảng Type (Danh mục sản phẩm sơn mài)
INSERT INTO Type (Name) VALUES
('Hộp Sơn Mài'),
('Khay Sơn Mài'),
('Bát Đĩa Sơn Mài'),
('Tranh Sơn Mài'),
('Đồ Gỗ Sơn Mài'),
('Tượng Sơn Mài'),
('Bình Lọ Sơn Mài'),
('Đồ Trang Trí Sơn Mài');

-- 2. Tạo bảng Product (Sản phẩm sơn mài)
INSERT INTO Product (Name, Description, Price, PictureUrl, Quantity, ProductTypeId) VALUES
-- Hộp Sơn Mài
('Hộp Sơn Mài Hoa Sen', 'Hộp sơn mài truyền thống với họa tiết hoa sen, kích thước 20x15x8cm', 850000, 'https://example.com/hoop-son-mai-hoa-sen.jpg', 10, 1),
('Hộp Sơn Mài Rồng Phượng', 'Hộp sơn mài cao cấp với họa tiết rồng phượng, kích thước 25x18x10cm', 1200000, 'https://example.com/hoop-son-mai-rong-phuong.jpg', 5, 1),
('Hộp Sơn Mài Làng Quê', 'Hộp sơn mài với cảnh làng quê Việt Nam, kích thước 18x12x6cm', 650000, 'https://example.com/hoop-son-mai-lang-que.jpg', 15, 1),

-- Khay Sơn Mài
('Khay Sơn Mài Tròn', 'Khay sơn mài tròn đường kính 30cm, màu đỏ truyền thống', 450000, 'https://example.com/khay-son-mai-tron.jpg', 20, 2),
('Khay Sơn Mài Vuông', 'Khay sơn mài vuông 25x25cm với họa tiết hoa văn', 380000, 'https://example.com/khay-son-mai-vuong.jpg', 25, 2),
('Khay Sơn Mài Hình Chữ Nhật', 'Khay sơn mài chữ nhật 35x20cm, màu đen bóng', 520000, 'https://example.com/khay-son-mai-chu-nhat.jpg', 12, 2),

-- Bát Đĩa Sơn Mài
('Bộ Bát Sơn Mài 6 Cái', 'Bộ 6 bát sơn mài với họa tiết hoa sen, đường kính 12cm', 280000, 'https://example.com/bo-bat-son-mai-6-cai.jpg', 30, 3),
('Đĩa Sơn Mài Lớn', 'Đĩa sơn mài đường kính 25cm với họa tiết rồng', 180000, 'https://example.com/dia-son-mai-lon.jpg', 40, 3),
('Bộ Đĩa Sơn Mài 4 Cái', 'Bộ 4 đĩa sơn mài với họa tiết hoa văn truyền thống', 320000, 'https://example.com/bo-dia-son-mai-4-cai.jpg', 25, 3),

-- Tranh Sơn Mài
('Tranh Sơn Mài Hoa Sen', 'Tranh sơn mài kích thước 40x30cm với chủ đề hoa sen', 1500000, 'https://example.com/tranh-son-mai-hoa-sen.jpg', 3, 4),
('Tranh Sơn Mài Phong Cảnh', 'Tranh sơn mài phong cảnh làng quê 50x35cm', 2200000, 'https://example.com/tranh-son-mai-phong-canh.jpg', 2, 4),
('Tranh Sơn Mài Rồng', 'Tranh sơn mài rồng bay 60x40cm', 3500000, 'https://example.com/tranh-son-mai-rong.jpg', 1, 4),

-- Đồ Gỗ Sơn Mài
('Bàn Sơn Mài Nhỏ', 'Bàn sơn mài kích thước 60x40x35cm với họa tiết hoa văn', 2500000, 'https://example.com/ban-son-mai-nho.jpg', 4, 5),
('Ghế Sơn Mài', 'Ghế sơn mài truyền thống với họa tiết rồng phượng', 1800000, 'https://example.com/ghe-son-mai.jpg', 6, 5),
('Tủ Sơn Mài', 'Tủ sơn mài 3 ngăn kích thước 80x40x120cm', 4500000, 'https://example.com/tu-son-mai.jpg', 2, 5),

-- Tượng Sơn Mài
('Tượng Phật Sơn Mài', 'Tượng Phật sơn mài cao 25cm với họa tiết vàng', 1200000, 'https://example.com/tuong-phat-son-mai.jpg', 8, 6),
('Tượng Rồng Sơn Mài', 'Tượng rồng sơn mài cao 30cm màu đỏ vàng', 1800000, 'https://example.com/tuong-rong-son-mai.jpg', 5, 6),
('Tượng Hoa Sen Sơn Mài', 'Tượng hoa sen sơn mài cao 20cm', 950000, 'https://example.com/tuong-hoa-sen-son-mai.jpg', 12, 6),

-- Bình Lọ Sơn Mài
('Bình Sơn Mài Cao', 'Bình sơn mài cao 35cm với họa tiết hoa văn', 750000, 'https://example.com/binh-son-mai-cao.jpg', 15, 7),
('Lọ Sơn Mài Nhỏ', 'Lọ sơn mài nhỏ cao 15cm màu đen bóng', 350000, 'https://example.com/lo-son-mai-nho.jpg', 35, 7),
('Bình Sơn Mài Hình Tròn', 'Bình sơn mài hình tròn đường kính 20cm', 550000, 'https://example.com/binh-son-mai-hinh-tron.jpg', 18, 7),

-- Đồ Trang Trí Sơn Mài
('Đèn Sơn Mài', 'Đèn sơn mài với họa tiết hoa sen, chiều cao 25cm', 680000, 'https://example.com/den-son-mai.jpg', 10, 8),
('Gương Sơn Mài', 'Gương sơn mài kích thước 30x40cm với khung hoa văn', 420000, 'https://example.com/guong-son-mai.jpg', 8, 8),
('Đồng Hồ Sơn Mài', 'Đồng hồ sơn mài treo tường với họa tiết rồng', 850000, 'https://example.com/dong-ho-son-mai.jpg', 6, 8);

-- 3. Tạo admin mặc định
INSERT INTO admins (username, password, email, full_name, enabled, account_non_expired, account_non_locked, credentials_non_expired) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'admin@sonmaiheritage.com', 'Administrator', true, true, true, true);

-- 4. Tạo guest customers mẫu
INSERT INTO guest_customers (session_id, email, phone, full_name, first_name, last_name, created_date, updated_date) VALUES
('session-001', 'nguyenvana@gmail.com', '0123456789', 'Nguyễn Văn A', 'A', 'Nguyễn Văn', NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 2 DAY),
('session-002', 'tranthib@gmail.com', '0987654321', 'Trần Thị B', 'B', 'Trần Thị', NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY),
('session-003', 'levanc@gmail.com', '0369258147', 'Lê Văn C', 'C', 'Lê Văn', NOW() - INTERVAL 3 DAY, NOW() - INTERVAL 3 DAY);

-- 5. Tạo orders mẫu
INSERT INTO orders (order_code, guest_customer_id, total_amount, status, payment_method, payment_status, created_date, updated_date, ship_full_name, ship_phone, ship_street, ship_ward, ship_district, ship_province) VALUES
('ORD001', 1, 2050000, 'CONFIRMED', 'VNPAY', 'SUCCESS', NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 2 DAY, 'Nguyễn Văn A', '0123456789', '123 Đường ABC', 'Phường 1', 'Quận 1', 'TP. Hồ Chí Minh'),
('ORD002', 2, 1200000, 'PENDING', 'VNPAY', 'PENDING', NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY, 'Trần Thị B', '0987654321', '456 Đường XYZ', 'Phường 2', 'Quận 2', 'TP. Hồ Chí Minh'),
('ORD003', 3, 3500000, 'CONFIRMED', 'VNPAY', 'SUCCESS', NOW() - INTERVAL 3 DAY, NOW() - INTERVAL 3 DAY, 'Lê Văn C', '0369258147', '789 Đường DEF', 'Phường 3', 'Quận 3', 'TP. Hồ Chí Minh');

-- 6. Tạo order items mẫu
INSERT INTO order_items (order_id, product_id, product_name, product_price, quantity, total_price, product_image) VALUES
-- Order 1 items (Hộp sơn mài + Khay sơn mài)
(1, 1, 'Hộp Sơn Mài Hoa Sen', 850000, 1, 850000, 'https://example.com/hoop-son-mai-hoa-sen.jpg'),
(1, 4, 'Khay Sơn Mài Tròn', 450000, 1, 450000, 'https://example.com/khay-son-mai-tron.jpg'),
(1, 7, 'Bộ Bát Sơn Mài 6 Cái', 280000, 1, 280000, 'https://example.com/bo-bat-son-mai-6-cai.jpg'),
(1, 10, 'Tranh Sơn Mài Hoa Sen', 1500000, 1, 1500000, 'https://example.com/tranh-son-mai-hoa-sen.jpg'),

-- Order 2 items (Tượng sơn mài)
(2, 15, 'Tượng Phật Sơn Mài', 1200000, 1, 1200000, 'https://example.com/tuong-phat-son-mai.jpg'),

-- Order 3 items (Tranh sơn mài lớn)
(3, 12, 'Tranh Sơn Mài Rồng', 3500000, 1, 3500000, 'https://example.com/tranh-son-mai-rong.jpg');

-- 7. Tạo payments mẫu
INSERT INTO payments (order_id, payment_code, amount, payment_method, status, vnpay_transaction_id, vnpay_transaction_no, created_date, updated_date) VALUES
(1, 'PAY001', 2050000, 'VNPAY', 'SUCCESS', '12345678', '987654321', NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 2 DAY),
(2, 'PAY002', 1200000, 'VNPAY', 'PENDING', NULL, NULL, NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY),
(3, 'PAY003', 3500000, 'VNPAY', 'SUCCESS', '87654321', '123456789', NOW() - INTERVAL 3 DAY, NOW() - INTERVAL 3 DAY);

-- Kiểm tra dữ liệu đã được tạo
SELECT 'Types' as table_name, COUNT(*) as count FROM Type
UNION ALL
SELECT 'Products', COUNT(*) FROM Product
UNION ALL
SELECT 'Admins', COUNT(*) FROM admins
UNION ALL
SELECT 'Guest Customers', COUNT(*) FROM guest_customers
UNION ALL
SELECT 'Orders', COUNT(*) FROM orders
UNION ALL
SELECT 'Order Items', COUNT(*) FROM order_items
UNION ALL
SELECT 'Payments', COUNT(*) FROM payments;

-- Hiển thị thông tin chi tiết
SELECT '=== DANH MỤC SẢN PHẨM ===' as info;
SELECT * FROM Type;

SELECT '=== SẢN PHẨM SƠN MÀI ===' as info;
SELECT p.Id, p.Name, p.Price, t.Name as TypeName 
FROM Product p 
JOIN Type t ON p.ProductTypeId = t.Id 
ORDER BY p.Id;

SELECT '=== ĐƠN HÀNG MẪU ===' as info;
SELECT o.id, o.order_code, gc.full_name as customer_name, o.total_amount, o.status, o.payment_status
FROM orders o 
JOIN guest_customers gc ON o.guest_customer_id = gc.id 
ORDER BY o.id;
