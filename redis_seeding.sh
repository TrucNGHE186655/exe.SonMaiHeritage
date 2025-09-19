#!/bin/bash

# =====================================================
# REDIS DATA SEEDING SCRIPT FOR SON MAI HERITAGE
# =====================================================
# This script creates sample basket data in Redis
# Run this after connecting to your Redis server

echo "Starting Redis data seeding for Son Mai Heritage..."

# Connect to Redis (adjust connection parameters as needed)
# redis-cli -h localhost -p 6379

# =====================================================
# 1. BASKET DATA SEEDING
# =====================================================

# Create Basket 1 for user customer1
redis-cli HSET "Basket:customer1" id "customer1"

# Create BasketItems for Basket 1
redis-cli HSET "BasketItem:1001" id 1001 name "Áo dài lụa tơ tằm Hà Nội" description "Áo dài truyền thống được may từ lụa tơ tằm cao cấp với họa tiết hoa sen" price 2500000 pictureUrl "https://example.com/ao-dai-1.jpg" productType "Áo dài truyền thống" quantity 1

redis-cli HSET "BasketItem:1002" id 1002 name "Dây chuyền bạc hình rồng" description "Dây chuyền bạc ta thủ công với thiết kế rồng bay" price 850000 pictureUrl "https://example.com/silver-dragon.jpg" productType "Trang sức bạc" quantity 2

redis-cli HSET "BasketItem:1003" id 1003 name "Túi xách thổ cẩm Sapa" description "Túi xách thổ cẩm được dệt thủ công bởi người H'Mông Sapa" price 450000 pictureUrl "https://example.com/tho-cam-bag.jpg" productType "Phụ kiện thổ cẩm" quantity 1

# Add BasketItems to Basket 1
redis-cli SADD "Basket:customer1:items" "BasketItem:1001" "BasketItem:1002" "BasketItem:1003"

# Create Basket 2 for user customer2
redis-cli HSET "Basket:customer2" id "customer2"

# Create BasketItems for Basket 2
redis-cli HSET "BasketItem:2001" id 2001 name "Váy lụa thêu hoa" description "Váy lụa nữ thêu tay họa tiết hoa cúc, phong cách thanh lịch" price 1600000 pictureUrl "https://example.com/silk-dress.jpg" productType "Quần áo lụa" quantity 1

redis-cli HSET "BasketItem:2002" id 2002 name "Bình hoa gốm Chu Đậu" description "Bình hoa gốm Chu Đậu với men xanh ngọc đặc trưng" price 1200000 pictureUrl "https://example.com/chu-dau-vase.jpg" productType "Gốm sứ truyền thống" quantity 1

# Add BasketItems to Basket 2
redis-cli SADD "Basket:customer2:items" "BasketItem:2001" "BasketItem:2002"

# Create Basket 3 for user customer3
redis-cli HSET "Basket:customer3" id "customer3"

# Create BasketItems for Basket 3
redis-cli HSET "BasketItem:3001" id 3001 name "Tượng Phật gỗ mun" description "Tượng Phật Di Lặc bằng gỗ mun quý, chạm khắc tinh xảo" price 2800000 pictureUrl "https://example.com/buddha-statue.jpg" productType "Đồ gỗ thủ công" quantity 1

redis-cli HSET "BasketItem:3002" id 3002 name "Tranh Đông Hồ cá chép" description "Tranh Đông Hồ truyền thống với họa tiết cá chép hoa sen" price 320000 pictureUrl "https://example.com/dong-ho-fish.jpg" productType "Tranh dân gian" quantity 3

redis-cli HSET "BasketItem:3003" id 3003 name "Nhẫn bạc khắc chữ Hán" description "Nhẫn bạc với chữ Hán cổ, mang ý nghĩa phong thủy tốt" price 420000 pictureUrl "https://example.com/silver-ring.jpg" productType "Trang sức bạc" quantity 2

# Add BasketItems to Basket 3
redis-cli SADD "Basket:customer3:items" "BasketItem:3001" "BasketItem:3002" "BasketItem:3003"

# =====================================================
# 2. SESSION DATA (Optional - for testing)
# =====================================================

# Create some session data for anonymous users
redis-cli HSET "Basket:anonymous_123" id "anonymous_123"

redis-cli HSET "BasketItem:4001" id 4001 name "Khăn quàng cổ thổ cẩm" description "Khăn quàng cổ với họa tiết thổ cẩm truyền thống của người Thái" price 320000 pictureUrl "https://example.com/tho-cam-scarf.jpg" productType "Phụ kiện thổ cẩm" quantity 1

redis-cli SADD "Basket:anonymous_123:items" "BasketItem:4001"

# =====================================================
# 3. VERIFICATION COMMANDS
# =====================================================

echo "=====================================================
REDIS DATA SEEDING COMPLETED!
=====================================================

To verify the data, run these Redis commands:

# List all baskets
KEYS Basket:*

# Get specific basket
HGETALL Basket:customer1

# Get basket items for a user
SMEMBERS Basket:customer1:items

# Get specific basket item details
HGETALL BasketItem:1001

# Get all basket items
KEYS BasketItem:*

# Count total baskets
EVAL \"return #redis.call('KEYS', 'Basket:*')\" 0

# Count total basket items
EVAL \"return #redis.call('KEYS', 'BasketItem:*')\" 0

=====================================================

Sample Redis Commands for Application Testing:

# 1. Create a new basket for a user
HSET Basket:testuser id testuser

# 2. Add an item to the basket
HSET BasketItem:5001 id 5001 name \"Test Product\" description \"Test Description\" price 100000 pictureUrl \"test.jpg\" productType \"Test Type\" quantity 1
SADD Basket:testuser:items BasketItem:5001

# 3. Update item quantity
HSET BasketItem:5001 quantity 3

# 4. Remove item from basket
SREM Basket:testuser:items BasketItem:5001
DEL BasketItem:5001

# 5. Clear entire basket
DEL Basket:testuser
EVAL \"local items = redis.call('SMEMBERS', KEYS[1] .. ':items') for i=1,#items do redis.call('DEL', items[i]) end return redis.call('DEL', KEYS[1] .. ':items')\" 1 Basket:testuser

====================================================="

echo "Redis seeding script completed successfully!"
