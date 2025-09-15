-- ============================================
-- CREATE DATABASE
-- ============================================
CREATE DATABASE IF NOT EXISTS dormitory_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE dormitory_db;

-- ============================================
-- TABLE: STUDENT
-- ============================================
CREATE TABLE student (
<<<<<<< HEAD
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    code        VARCHAR(50) UNIQUE,
    name        VARCHAR(255) NOT NULL,
    dob         DATE,
    gender      ENUM('Nam', 'Nữ'),
    phone       VARCHAR(20),
    address     VARCHAR(255),
    email       VARCHAR(255),
    department  VARCHAR(255),
    study_year  INT, -- Suggest: Add CHECK (study_year BETWEEN 1 AND 4) if supported
    CONSTRAINT chk_email UNIQUE (email)
=======
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         code VARCHAR(50) UNIQUE,
                         name VARCHAR(255) NOT NULL,
                         dob DATE,
                         gender ENUM('Nam', 'Nữ'),
                         phone VARCHAR(20),
                         address VARCHAR(255),
                         email VARCHAR(255),
                         department VARCHAR(255),
                         year INT, -- Suggest: Add CHECK (year BETWEEN 1 AND 4) if supported
                         CONSTRAINT chk_email UNIQUE (email)
>>>>>>> codex/fix-thêm-sinh-viên-không-thành-công
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ============================================
-- TABLE: ROOM
-- ============================================
CREATE TABLE room (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    number    VARCHAR(50) UNIQUE NOT NULL,
    type      VARCHAR(50),
    capacity  INT NOT NULL,
    price     DECIMAL(10,2) DEFAULT 0.00 -- Suggest: Add CHECK (capacity > 0) if supported
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ============================================
-- TABLE: CONTRACT
-- ============================================
CREATE TABLE contract (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT,
    room_id    BIGINT,
    start_date DATE,
    end_date   DATE,
    status     ENUM('ACTIVE', 'EXPIRED'),
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
    FOREIGN KEY (room_id)    REFERENCES room(id)    ON DELETE CASCADE
    -- Suggest: Add trigger to ensure room capacity is not exceeded
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ============================================
-- TABLE: FEE
-- ============================================
CREATE TABLE fee (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    contract_id    BIGINT,
    type           VARCHAR(50),
    amount         DECIMAL(10,2),
    due_date       DATE,
    payment_status ENUM('PAID', 'UNPAID'),
    FOREIGN KEY (contract_id) REFERENCES contract(id) ON DELETE CASCADE
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ============================================
-- SAMPLE DATA: STUDENT
-- ============================================
-- 25 sinh viên, mã SV01 ... SV25
<<<<<<< HEAD
INSERT INTO student (code, name, dob, gender, phone, address, email, department, study_year) VALUES
    ('SV01', 'Nguyễn Văn An',     '2005-03-15', 'Nam', '0912345601', 'Hà Nội',      'sv01@example.com', 'Công nghệ Thông tin', 3),
    ('SV02', 'Trần Thị Bình',     '2005-07-22', 'Nữ',  '0912345602', 'Ninh Bình',   'sv02@example.com', 'Kinh tế',              2),
    ('SV03', 'Lê Minh Châu',      '2003-11-30', 'Nam', '0912345603', 'Nghệ An',     'sv03@example.com', 'Kỹ thuật Điện',        4),
    ('SV04', 'Phạm Quốc Đạt',     '2004-05-10', 'Nam', '0912345604', 'Thái Nguyên', 'sv04@example.com', 'Kỹ thuật Ô tô',        3),
    ('SV05', 'Hoàng Thị Mai',     '2005-09-25', 'Nữ',  '0912345605', 'Thanh Hóa',   'sv05@example.com', 'Tâm lý học',           2),
    ('SV06', 'Nguyễn Văn Hùng',   '2004-08-05', 'Nam', '0912345606', 'Hải Phòng',   'sv06@example.com', 'Sư phạm Toán',         3),
    ('SV07', 'Trần Thị Lan',      '2003-10-10', 'Nữ',  '0912345607', 'Đà Nẵng',     'sv07@example.com', 'Kỹ thuật phần mềm',    4),
    ('SV08', 'Lê Văn Nam',        '2005-04-20', 'Nam', '0912345608', 'Hồ Chí Minh', 'sv08@example.com', 'Khoa học Máy tính',    2),
    ('SV09', 'Phạm Thị Oanh',     '2004-09-30', 'Nữ',  '0912345609', 'Cần Thơ',     'sv09@example.com', 'Ngôn ngữ Anh',         3),
    ('SV10', 'Nguyễn Văn Quân',   '2003-10-10', 'Nam', '0912345610', 'Bắc Ninh',    'sv10@example.com', 'Quản trị Kinh doanh',  4),
    ('SV11', 'Đỗ Thị Yến',        '2005-03-21', 'Nữ',  '0912345611', 'Vĩnh Phúc',   'sv11@example.com', 'Kinh tế',              1),
    ('SV12', 'Vũ Hoàng Sơn',      '2004-06-16', 'Nam', '0912345612', 'Hải Dương',   'sv12@example.com', 'Kỹ thuật Điện',        2),
    ('SV13', 'Trịnh Văn Bình',    '2005-09-07', 'Nam', '0912345613', 'Bình Dương',  'sv13@example.com', 'Kỹ thuật Điện',        3),
    ('SV14', 'Mai Thị Hạnh',      '2003-05-28', 'Nữ',  '0912345614', 'Nghệ An',     'sv14@example.com', 'Quản trị Kinh doanh',  2),
    ('SV15', 'Nguyễn Tuấn Dũng',  '2004-07-13', 'Nam', '0912345615', 'Phú Thọ',     'sv15@example.com', 'Công nghệ Thông tin',  4),
    ('SV16', 'Trần Minh Hòa',     '2005-01-09', 'Nam', '0912345616', 'Quảng Ninh',  'sv16@example.com', 'Công nghệ Thông tin',  1),
    ('SV17', 'Lê Thị Xuân',       '2004-02-15', 'Nữ',  '0912345617', 'Hà Tĩnh',     'sv17@example.com', 'Tâm lý học',           3),
    ('SV18', 'Ngô Quang Phúc',    '2005-04-26', 'Nam', '0912345618', 'Nam Định',    'sv18@example.com', 'Ngôn ngữ Anh',         2),
    ('SV19', 'Phạm Thị Thảo',     '2003-12-20', 'Nữ',  '0912345619', 'Sơn La',      'sv19@example.com', 'Kỹ thuật phần mềm',    4),
    ('SV20', 'Vũ Minh Tuấn',      '2004-03-11', 'Nam', '0912345620', 'Bắc Giang',   'sv20@example.com', 'Kỹ thuật phần mềm',    3),
    ('SV21', 'Đặng Thị Thu',      '2005-05-18', 'Nữ',  '0912345621', 'Hà Nam',      'sv21@example.com', 'Quản trị Kinh doanh',  2),
    ('SV22', 'Trịnh Văn Toàn',    '2005-08-14', 'Nam', '0912345622', 'Quảng Bình',  'sv22@example.com', 'Tâm lý học',           1),
    ('SV23', 'Nguyễn Thu Hằng',   '2004-10-05', 'Nữ',  '0912345623', 'Thái Bình',   'sv23@example.com', 'Ngôn ngữ Anh',         4),
    ('SV24', 'Phan Quang Huy',    '2003-09-23', 'Nam', '0912345624', 'Bình Phước',  'sv24@example.com', 'Kinh tế',              3),
    ('SV25', 'Lê Thị Hường',      '2005-11-02', 'Nữ',  '0912345625', 'Long An',     'sv25@example.com', 'Công nghệ Thông tin',  2);
=======
INSERT INTO student (code, name, dob, gender, phone, address, email, department, year) VALUES
                                                                                                 ('SV01','Nguyễn Văn An', '2005-03-15', 'Nam', '0912345601', 'Hà Nội', 'sv01@example.com', 'Công nghệ Thông tin', 3),
                                                                                                 ('SV02','Trần Thị Bình', '2005-07-22', 'Nữ', '0912345602', 'Ninh Bình', 'sv02@example.com', 'Kinh tế', 2),
                                                                                                 ('SV03','Lê Minh Châu', '2003-11-30', 'Nam', '0912345603', 'Nghệ An', 'sv03@example.com', 'Kỹ thuật Điện', 4),
                                                                                                 ('SV04','Phạm Quốc Đạt', '2004-05-10', 'Nam', '0912345604', 'Thái Nguyên', 'sv04@example.com', 'Kỹ thuật Ô tô', 3),
                                                                                                 ('SV05','Hoàng Thị Mai', '2005-09-25', 'Nữ', '0912345605', 'Thanh Hóa', 'sv05@example.com', 'Tâm lý học', 2),
                                                                                                 ('SV06','Nguyễn Văn Hùng', '2004-08-05', 'Nam', '0912345606', 'Hải Phòng', 'sv06@example.com', 'Sư phạm Toán', 3),
                                                                                                 ('SV07','Trần Thị Lan', '2003-10-10', 'Nữ', '0912345607', 'Đà Nẵng', 'sv07@example.com', 'Kỹ thuật phần mềm', 4),
                                                                                                 ('SV08','Lê Văn Nam', '2005-04-20', 'Nam', '0912345608', 'Hồ Chí Minh', 'sv08@example.com', 'Khoa học Máy tính', 2),
                                                                                                 ('SV09','Phạm Thị Oanh', '2004-09-30', 'Nữ', '0912345609', 'Cần Thơ', 'sv09@example.com', 'Ngôn ngữ Anh', 3),
                                                                                                 ('SV10','Nguyễn Văn Quân', '2003-10-10', 'Nam', '0912345610', 'Bắc Ninh', 'sv10@example.com', 'Quản trị Kinh doanh', 4),
                                                                                                 ('SV11','Đỗ Thị Yến', '2005-03-21', 'Nữ', '0912345611', 'Vĩnh Phúc', 'sv11@example.com', 'Kinh tế', 1),
                                                                                                 ('SV12','Vũ Hoàng Sơn', '2004-06-16', 'Nam', '0912345612', 'Hải Dương', 'sv12@example.com', 'Kỹ thuật Điện', 2),
                                                                                                 ('SV13','Trịnh Văn Bình', '2005-09-07', 'Nam', '0912345613', 'Bình Dương', 'sv13@example.com', 'Kỹ thuật Điện', 3),
                                                                                                 ('SV14','Mai Thị Hạnh', '2003-05-28', 'Nữ', '0912345614', 'Nghệ An', 'sv14@example.com', 'Quản trị Kinh doanh', 2),
                                                                                                 ('SV15','Nguyễn Tuấn Dũng', '2004-07-13', 'Nam', '0912345615', 'Phú Thọ', 'sv15@example.com', 'Công nghệ Thông tin', 4),
                                                                                                 ('SV16','Trần Minh Hòa', '2005-01-09', 'Nam', '0912345616', 'Quảng Ninh', 'sv16@example.com', 'Công nghệ Thông tin', 1),
                                                                                                 ('SV17','Lê Thị Xuân', '2004-02-15', 'Nữ', '0912345617', 'Hà Tĩnh', 'sv17@example.com', 'Tâm lý học', 3),
                                                                                                 ('SV18','Ngô Quang Phúc', '2005-04-26', 'Nam', '0912345618', 'Nam Định', 'sv18@example.com', 'Ngôn ngữ Anh', 2),
                                                                                                 ('SV19','Phạm Thị Thảo', '2003-12-20', 'Nữ', '0912345619', 'Sơn La', 'sv19@example.com', 'Kỹ thuật phần mềm', 4),
                                                                                                 ('SV20','Vũ Minh Tuấn', '2004-03-11', 'Nam', '0912345620', 'Bắc Giang', 'sv20@example.com', 'Kỹ thuật phần mềm', 3),
                                                                                                 ('SV21','Đặng Thị Thu', '2005-05-18', 'Nữ', '0912345621', 'Hà Nam', 'sv21@example.com', 'Quản trị Kinh doanh', 2),
                                                                                                 ('SV22','Trịnh Văn Toàn', '2005-08-14', 'Nam', '0912345622', 'Quảng Bình', 'sv22@example.com', 'Tâm lý học', 1),
                                                                                                 ('SV23','Nguyễn Thu Hằng', '2004-10-05', 'Nữ', '0912345623', 'Thái Bình', 'sv23@example.com', 'Ngôn ngữ Anh', 4),
                                                                                                 ('SV24','Phan Quang Huy', '2003-09-23', 'Nam', '0912345624', 'Bình Phước', 'sv24@example.com', 'Kinh tế', 3),
                                                                                                 ('SV25','Lê Thị Hường', '2005-11-02', 'Nữ', '0912345625', 'Long An', 'sv25@example.com', 'Công nghệ Thông tin', 2);
>>>>>>> codex/fix-thêm-sinh-viên-không-thành-công

-- ============================================
-- SAMPLE DATA: ROOM
-- ============================================
INSERT INTO room (number, type, capacity, price) VALUES
    ('101', 'Phòng tám', 8, 1200000.00),
    ('102', 'Phòng tám', 8, 1200000.00),
    ('103', 'Phòng tám', 8, 1200000.00),
    ('104', 'Phòng tám', 8, 1200000.00),
    ('105', 'Phòng tám', 8, 1200000.00),
    ('106', 'Phòng tám', 8, 1200000.00),
    ('107', 'Phòng tám', 8, 1200000.00),
    ('201', 'Phòng bốn', 4, 2000000.00),
    ('202', 'Phòng bốn', 4, 2000000.00),
    ('203', 'Phòng bốn', 4, 2000000.00),
    ('204', 'Phòng bốn', 4, 2000000.00),
    ('205', 'Phòng bốn', 4, 2000000.00),
    ('206', 'Phòng bốn', 4, 2000000.00),
    ('207', 'Phòng bốn', 4, 2000000.00),
    ('208', 'Phòng bốn', 4, 2000000.00);

-- ============================================
-- SAMPLE DATA: CONTRACT
-- ============================================
-- 25 hợp đồng, mỗi hợp đồng gán lần lượt phòng_id từ 1 đến 15, sau đó quay lại phòng 1
INSERT INTO contract (student_id, room_id, start_date, end_date, status) VALUES
    ( 1,  1, '2025-01-01', '2025-12-31', 'ACTIVE'),
    ( 2,  2, '2025-01-01', '2025-12-31', 'ACTIVE'),
    ( 3,  3, '2025-01-01', '2025-12-31', 'ACTIVE'),
    ( 4,  4, '2025-01-01', '2025-12-31', 'ACTIVE'),
    ( 5,  5, '2025-01-01', '2025-12-31', 'ACTIVE'),
    ( 6,  6, '2025-01-01', '2025-12-31', 'ACTIVE'),
    ( 7,  7, '2025-01-01', '2025-12-31', 'ACTIVE'),
    ( 8,  8, '2025-01-01', '2025-12-31', 'ACTIVE'),
    ( 9,  9, '2025-01-01', '2025-12-31', 'ACTIVE'),
    (10, 10, '2025-01-01', '2025-12-31', 'ACTIVE'),
    (11, 11, '2025-01-01', '2025-12-31', 'ACTIVE'),
    (12, 12, '2025-01-01', '2025-12-31', 'ACTIVE'),
    (13, 13, '2025-01-01', '2025-12-31', 'ACTIVE'),
    (14, 14, '2025-01-01', '2025-12-31', 'ACTIVE'),
    (15, 15, '2025-01-01', '2025-12-31', 'ACTIVE'),
    (16,  1, '2025-01-01', '2025-12-31', 'ACTIVE'),
    (17,  2, '2025-01-01', '2025-12-31', 'ACTIVE'),
    (18,  3, '2025-01-01', '2025-12-31', 'ACTIVE'),
    (19,  4, '2025-01-01', '2025-12-31', 'ACTIVE'),
    (20,  5, '2025-01-01', '2025-12-31', 'ACTIVE'),
    (21,  6, '2025-01-01', '2025-12-31', 'ACTIVE'),
    (22,  7, '2025-01-01', '2025-12-31', 'ACTIVE'),
    (23,  8, '2025-01-01', '2025-12-31', 'ACTIVE'),
    (24,  9, '2025-01-01', '2025-12-31', 'ACTIVE'),
    (25, 10, '2025-01-01', '2025-12-31', 'ACTIVE');

-- ============================================
-- SAMPLE DATA: FEE
-- ============================================
INSERT INTO fee (contract_id, type, amount, due_date, payment_status) VALUES
    -- Contract 1
    ( 1, 'CLEANING',    120000, '2025-06-10', 'PAID'),
    ( 1, 'ELECTRICITY', 200000, '2025-06-15', 'UNPAID'),

    -- Contract 2
    ( 2, 'WATER',       150000, '2025-06-20', 'PAID'),
    ( 2, 'MAINTENANCE', 100000, '2025-06-25', 'UNPAID'),

    -- Contract 3
    ( 3, 'CLEANING',    122000, '2025-06-10', 'PAID'),
    ( 3, 'ELECTRICITY', 202000, '2025-06-15', 'PAID'),

    -- Contract 4
    ( 4, 'WATER',       152000, '2025-06-20', 'UNPAID'),
    ( 4, 'MAINTENANCE', 102000, '2025-06-25', 'PAID'),

    -- Contract 5
    ( 5, 'CLEANING',    124000, '2025-06-10', 'UNPAID'),
    ( 5, 'ELECTRICITY', 204000, '2025-06-15', 'PAID'),

    -- Contract 6
    ( 6, 'WATER',       154000, '2025-06-20', 'PAID'),
    ( 6, 'MAINTENANCE', 104000, '2025-06-25', 'UNPAID'),

    -- Contract 7
    ( 7, 'CLEANING',    126000, '2025-06-10', 'PAID'),
    ( 7, 'ELECTRICITY', 206000, '2025-06-15', 'PAID'),

    -- Contract 8
    ( 8, 'WATER',       156000, '2025-06-20', 'UNPAID'),
    ( 8, 'MAINTENANCE', 106000, '2025-06-25', 'PAID'),

    -- Contract 9
    ( 9, 'CLEANING',    128000, '2025-06-10', 'UNPAID'),
    ( 9, 'ELECTRICITY', 208000, '2025-06-15', 'PAID'),

    -- Contract 10
    (10, 'WATER',       158000, '2025-06-20', 'PAID'),
    (10, 'MAINTENANCE', 108000, '2025-06-25', 'UNPAID'),

    -- Contract 11
    (11, 'CLEANING',    130000, '2025-07-10', 'PAID'),
    (11, 'ELECTRICITY', 210000, '2025-07-15', 'UNPAID'),

    -- Contract 12
    (12, 'WATER',       160000, '2025-07-20', 'PAID'),
    (12, 'MAINTENANCE', 110000, '2025-07-25', 'PAID'),

    -- Contract 13
    (13, 'CLEANING',    132000, '2025-07-10', 'UNPAID'),
    (13, 'ELECTRICITY', 212000, '2025-07-15', 'PAID'),

    -- Contract 14
    (14, 'WATER',       162000, '2025-07-20', 'PAID'),
    (14, 'MAINTENANCE', 112000, '2025-07-25', 'UNPAID'),

    -- Contract 15
    (15, 'CLEANING',    134000, '2025-07-10', 'PAID'),
    (15, 'ELECTRICITY', 214000, '2025-07-15', 'PAID'),

    -- Contract 16
    (16, 'WATER',       164000, '2025-07-20', 'UNPAID'),
    (16, 'MAINTENANCE', 114000, '2025-07-25', 'PAID'),

    -- Contract 17
    (17, 'CLEANING',    136000, '2025-07-10', 'PAID'),
    (17, 'ELECTRICITY', 216000, '2025-07-15', 'UNPAID'),

    -- Contract 18
    (18, 'WATER',       166000, '2025-07-20', 'PAID'),
    (18, 'MAINTENANCE', 116000, '2025-07-25', 'PAID'),

    -- Contract 19
    (19, 'CLEANING',    138000, '2025-07-10', 'UNPAID'),
    (19, 'ELECTRICITY', 218000, '2025-07-15', 'PAID'),

    -- Contract 20
    (20, 'WATER',       168000, '2025-07-20', 'PAID'),
    (20, 'MAINTENANCE', 118000, '2025-07-25', 'UNPAID'),

    -- Contract 21
    (21, 'CLEANING',    140000, '2025-08-10', 'PAID'),
    (21, 'ELECTRICITY', 220000, '2025-08-15', 'PAID'),

    -- Contract 22
    (22, 'WATER',       170000, '2025-08-20', 'PAID'),
    (22, 'MAINTENANCE', 120000, '2025-08-25', 'UNPAID'),

    -- Contract 23
    (23, 'CLEANING',    142000, '2025-08-10', 'UNPAID'),
    (23, 'ELECTRICITY', 222000, '2025-08-15', 'PAID'),

    -- Contract 24
    (24, 'WATER',       172000, '2025-08-20', 'PAID'),
    (24, 'MAINTENANCE', 122000, '2025-08-25', 'PAID'),

    -- Contract 25
    (25, 'CLEANING',    144000, '2025-08-10', 'PAID'),
    (25, 'ELECTRICITY', 224000, '2025-08-15', 'UNPAID');



