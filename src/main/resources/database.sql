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
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) UNIQUE,
    name VARCHAR(255) NOT NULL,
    dob DATE,
    gender ENUM('Nam', 'Nữ'),
    phone VARCHAR(20),
    address VARCHAR(255),
    email VARCHAR(255),
    department VARCHAR(255),
    year INT,
    user_id BIGINT UNIQUE,
    CONSTRAINT chk_email UNIQUE (email)
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
-- TABLE: ROLE
-- ============================================
CREATE TABLE role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ============================================
-- TABLE: USERS
-- ============================================
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE,
    password VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    enabled BOOLEAN DEFAULT TRUE
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ============================================
-- TABLE: USER_ROLES
-- ============================================
CREATE TABLE user_roles (
    user_id BIGINT,
    role_id BIGINT,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

ALTER TABLE student
    ADD CONSTRAINT fk_student_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL;

-- ============================================
-- TABLE: MAINTENANCE_REQUEST
-- ============================================
CREATE TABLE maintenance_request (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT,
    room_id BIGINT,
    description TEXT,
    request_type VARCHAR(50),
    desired_room_number VARCHAR(50),
    status VARCHAR(50),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
    FOREIGN KEY (room_id) REFERENCES room(id) ON DELETE SET NULL
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ============================================
-- TABLE: VIOLATION
-- ============================================
CREATE TABLE violation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT,
    room_id BIGINT,
    description TEXT,
    severity VARCHAR(50),
    date DATE,
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
    FOREIGN KEY (room_id) REFERENCES room(id) ON DELETE SET NULL
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ============================================
-- SAMPLE DATA: ROLE
-- ============================================
INSERT INTO role (name, description) VALUES
    ('ROLE_ADMIN','Quản lý KTX'),
    ('ROLE_STAFF','Nhân viên hỗ trợ'),
    ('ROLE_STUDENT','Sinh viên');

-- ============================================
-- SAMPLE DATA: USERS
-- ============================================
INSERT INTO users (username, password, email) VALUES
    ('admin','{noop}password','admin@example.com'),
    ('staff','{noop}password','staff@example.com'),
    ('sv01','{noop}password','sv01-login@example.com');

-- ============================================
-- SAMPLE DATA: USER_ROLES
-- ============================================
INSERT INTO user_roles (user_id, role_id) VALUES
    (1,1),
    (2,2),
    (3,3);

UPDATE student SET user_id = 3 WHERE code = 'SV01';

-- ============================================
-- SAMPLE DATA: STUDENT
-- ============================================
-- 25 sinh viên, mã SV01 ... SV25
INSERT INTO student (code, name, dob, gender, phone, address, email, department, year) VALUES
    ('SV01','Nguyễn Văn An','2005-03-15','Nam','0912345601','Hà Nội','sv01@example.com','Công nghệ Thông tin',3),
    ('SV02','Trần Thị Bình','2005-07-22','Nữ','0912345602','Ninh Bình','sv02@example.com','Kinh tế',2);

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
-- SAMPLE DATA: MAINTENANCE REQUEST
-- ============================================
INSERT INTO maintenance_request (student_id, room_id, description, request_type, desired_room_number, status)
VALUES
    (1, 1, 'Đèn phòng bị hỏng, cần thay mới', 'MAINTENANCE', NULL, 'PENDING'),
    (2, 2, 'Xin chuyển sang phòng 201 để học nhóm', 'ROOM_TRANSFER', '201', 'IN_PROGRESS');

-- ============================================
-- SAMPLE DATA: VIOLATION
-- ============================================
INSERT INTO violation (student_id, room_id, description, severity, date)
VALUES
    (1, 1, 'Tụ tập quá giờ quy định', 'MEDIUM', '2025-02-15'),
    (2, 2, 'Không tuân thủ quy định dọn vệ sinh', 'LOW', '2025-03-01');

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



