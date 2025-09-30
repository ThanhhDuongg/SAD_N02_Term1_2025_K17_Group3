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
-- TABLE: BUILDING
-- ============================================
CREATE TABLE building (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          code VARCHAR(50) UNIQUE NOT NULL,
                          name VARCHAR(255) NOT NULL,
                          address VARCHAR(255),
                          description TEXT,
                          total_floors INT
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ============================================
-- TABLE: ROOM
-- ============================================
CREATE TABLE room (
                      id        BIGINT AUTO_INCREMENT PRIMARY KEY,
                      number    VARCHAR(50) UNIQUE NOT NULL,
                      type      VARCHAR(50),
                      capacity  INT NOT NULL,
                      price     DECIMAL(10,2) DEFAULT 0.00,
                      building_id BIGINT,
                      CONSTRAINT fk_room_building FOREIGN KEY (building_id) REFERENCES building(id) ON DELETE SET NULL
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
                                     resolution_notes TEXT,
                                     handled_by_id BIGINT,
                                     created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                     updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                     FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
                                     FOREIGN KEY (room_id) REFERENCES room(id) ON DELETE SET NULL,
                                     FOREIGN KEY (handled_by_id) REFERENCES users(id) ON DELETE SET NULL
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ============================================
-- TABLE: DORM_REGISTRATION_REQUEST
-- ============================================
CREATE TABLE dorm_registration_request (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT,
    desired_room_type VARCHAR(100),
    preferred_room_number VARCHAR(50),
    expected_move_in_date DATE,
    additional_notes TEXT,
    status VARCHAR(50),
    admin_notes TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL,
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE
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
-- Các tài khoản đăng nhập mẫu để thử nghiệm nhanh:
--   • Quản lý KTX:    admin / password
--   • Nhân viên hỗ trợ: staff / password
--   • Sinh viên 1:     sv01 / password
--   • Sinh viên 2:     sv02 / password
INSERT INTO users (username, password, email) VALUES
                                                  ('admin','{noop}password','admin@example.com'),
                                                  ('staff','{noop}password','staff@example.com'),
                                                  ('sv01','{noop}password','sv01-login@example.com'),
                                                  ('sv02','{noop}password','sv02-login@example.com');

-- ============================================
-- SAMPLE DATA: USER_ROLES
-- ============================================
INSERT INTO user_roles (user_id, role_id) VALUES
                                              (1,1),
                                              (2,2),
                                              (3,3),
                                              (4,3);

-- ============================================
-- SAMPLE DATA: STUDENT
-- ============================================
-- Một số sinh viên mẫu để kiểm thử
INSERT INTO student (code, name, dob, gender, phone, address, email, department, year, user_id) VALUES
                                                                                                    ('SV01','Nguyễn Văn An','2005-03-15','Nam','0912345601','Hà Nội','sv01@example.com','Công nghệ Thông tin',3,
                                                                                                     (SELECT id FROM users WHERE username = 'sv01')),
                                                                                                    ('SV02','Trần Thị Bình','2005-07-22','Nữ','0912345602','Ninh Bình','sv02@example.com','Kinh tế',2,
                                                                                                     (SELECT id FROM users WHERE username = 'sv02')),
                                                                                                    ('SV03','Phạm Minh Châu','2004-11-05','Nữ','0912345603','Đà Nẵng','sv03@example.com','Du lịch',4,NULL),
                                                                                                    ('SV04','Lê Quốc Dũng','2005-01-30','Nam','0912345604','Hải Phòng','sv04@example.com','Điện tử',1,NULL);

-- ============================================
-- SAMPLE DATA: ROOM
-- ============================================
-- Danh sách tòa nhà mẫu
INSERT INTO building (code, name, address, description, total_floors) VALUES
                                                                         ('A', 'Tòa A', 'Khuôn viên chính', 'Khu dành cho sinh viên năm nhất', 6),
                                                                         ('B', 'Tòa B', 'Khuôn viên chính', 'Khu tiêu chuẩn dành cho sinh viên năm hai', 5);

INSERT INTO room (number, type, capacity, price, building_id) VALUES
                                                                  ('101', 'Phòng tám', 8, 1200000.00, (SELECT id FROM building WHERE code = 'A')),
                                                                  ('102', 'Phòng tám', 8, 1200000.00, (SELECT id FROM building WHERE code = 'A')),
                                                                  ('201', 'Phòng bốn', 4, 2000000.00, (SELECT id FROM building WHERE code = 'B')),
                                                                  ('202', 'Phòng bốn', 4, 2000000.00, (SELECT id FROM building WHERE code = 'B')),
                                                                  ('301', 'Phòng đôi', 2, 2500000.00, (SELECT id FROM building WHERE code = 'B'));

-- ============================================
-- SAMPLE DATA: CONTRACT
-- ============================================
-- Một vài hợp đồng minh họa
INSERT INTO contract (student_id, room_id, start_date, end_date, status) VALUES
                                                                             ((SELECT id FROM student WHERE code = 'SV01'), (SELECT id FROM room WHERE number = '101'), '2025-01-01', '2025-12-31', 'ACTIVE'),
                                                                             ((SELECT id FROM student WHERE code = 'SV02'), (SELECT id FROM room WHERE number = '102'), '2025-01-01', '2025-12-31', 'ACTIVE'),
                                                                             ((SELECT id FROM student WHERE code = 'SV03'), (SELECT id FROM room WHERE number = '201'), '2025-02-01', '2025-12-31', 'ACTIVE'),
                                                                             ((SELECT id FROM student WHERE code = 'SV04'), (SELECT id FROM room WHERE number = '202'), '2024-09-01', '2025-08-31', 'EXPIRED');

-- ============================================
-- SAMPLE DATA: MAINTENANCE REQUEST
-- ============================================
INSERT INTO maintenance_request (student_id, room_id, description, request_type, desired_room_number, status, handled_by_id, resolution_notes)
VALUES
    ((SELECT id FROM student WHERE code = 'SV01'), (SELECT id FROM room WHERE number = '101'),
     'Đèn phòng bị hỏng, cần thay mới', 'MAINTENANCE', NULL, 'PENDING', NULL, NULL),
    ((SELECT id FROM student WHERE code = 'SV02'), (SELECT id FROM room WHERE number = '102'),
     'Xin chuyển sang phòng 201 để học nhóm', 'ROOM_TRANSFER', '201', 'IN_PROGRESS', (SELECT id FROM users WHERE username = 'staff'), 'Đang liên hệ bố trí phòng phù hợp');

-- ============================================
-- SAMPLE DATA: DORM REGISTRATION REQUEST
-- ============================================
INSERT INTO dorm_registration_request (student_id, desired_room_type, preferred_room_number, expected_move_in_date, additional_notes, status, admin_notes)
VALUES
    ((SELECT id FROM student WHERE code = 'SV01'), 'Phòng 4 người', NULL, DATE_ADD(CURDATE(), INTERVAL 14 DAY),
     'Muốn chuyển sang phòng ít người để thuận tiện học nhóm', 'PENDING', NULL),
    ((SELECT id FROM student WHERE code = 'SV03'), 'Phòng 2 người', '301', DATE_ADD(CURDATE(), INTERVAL 1 MONTH),
     'Cần không gian yên tĩnh để chuẩn bị đồ án', 'NEEDS_UPDATE', 'Vui lòng bổ sung giấy xác nhận của khoa');

-- ============================================
-- SAMPLE DATA: VIOLATION
-- ============================================
INSERT INTO violation (student_id, room_id, description, severity, date)
VALUES
    ((SELECT id FROM student WHERE code = 'SV01'), (SELECT id FROM room WHERE number = '101'),
     'Tụ tập quá giờ quy định', 'MEDIUM', '2025-02-15'),
    ((SELECT id FROM student WHERE code = 'SV03'), (SELECT id FROM room WHERE number = '201'),
     'Không tuân thủ quy định dọn vệ sinh', 'LOW', '2025-03-01');

-- ============================================
-- SAMPLE DATA: FEE
-- ============================================
INSERT INTO fee (contract_id, type, amount, due_date, payment_status) VALUES
                                                                          -- Hợp đồng của SV01
                                                                          ((SELECT id FROM contract WHERE student_id = (SELECT id FROM student WHERE code = 'SV01')), 'RENT',        1200000, '2025-03-01', 'PAID'),
                                                                          ((SELECT id FROM contract WHERE student_id = (SELECT id FROM student WHERE code = 'SV01')), 'ELECTRICITY', 180000,  '2025-03-05', 'UNPAID'),

                                                                          -- Hợp đồng của SV02
                                                                          ((SELECT id FROM contract WHERE student_id = (SELECT id FROM student WHERE code = 'SV02')), 'RENT',        1200000, '2025-03-01', 'PAID'),
                                                                          ((SELECT id FROM contract WHERE student_id = (SELECT id FROM student WHERE code = 'SV02')), 'WATER',        90000,  '2025-03-05', 'PAID'),

                                                                          -- Hợp đồng của SV03
                                                                          ((SELECT id FROM contract WHERE student_id = (SELECT id FROM student WHERE code = 'SV03')), 'RENT',        2000000, '2025-03-10', 'UNPAID'),
                                                                          ((SELECT id FROM contract WHERE student_id = (SELECT id FROM student WHERE code = 'SV03')), 'MAINTENANCE',  85000,  '2025-03-15', 'PAID'),

                                                                          -- Hợp đồng của SV04
                                                                          ((SELECT id FROM contract WHERE student_id = (SELECT id FROM student WHERE code = 'SV04')), 'RENT',        2000000, '2024-10-01', 'PAID'),
                                                                          ((SELECT id FROM contract WHERE student_id = (SELECT id FROM student WHERE code = 'SV04')), 'ELECTRICITY', 150000,  '2024-10-05', 'PAID');



-- Tắt safe mode tạm thời
SET SQL_SAFE_UPDATES = 0;

-- Xóa dữ liệu cũ
DELETE FROM user_roles;
DELETE FROM users;

-- Reset AUTO_INCREMENT để ID bắt đầu từ 1
ALTER TABLE users AUTO_INCREMENT = 1;

-- Thêm users với mật khẩu BCrypt (password = "password")
INSERT INTO users (username, password, email) VALUES
                                                  ('admin','$2a$10$X5wFWtximQCM1RcRKS6IP.7bwqjrmnUe5k9C6X5n3JJNbDVAyBrXi','admin@example.com'),
                                                  ('staff','$2a$10$X5wFWtximQCM1RcRKS6IP.7bwqjrmnUe5k9C6X5n3JJNbDVAyBrXi','staff@example.com'),
                                                  ('sv01','$2a$10$X5wFWtximQCM1RcRKS6IP.7bwqjrmnUe5k9C6X5n3JJNbDVAyBrXi','sv01-login@example.com'),
                                                  ('sv02','$2a$10$X5wFWtximQCM1RcRKS6IP.7bwqjrmnUe5k9C6X5n3JJNbDVAyBrXi','sv02-login@example.com');

-- Thêm user_roles
INSERT INTO user_roles (user_id, role_id) VALUES
                                              (1,1), -- admin -> ROLE_ADMIN
                                              (2,2), -- staff -> ROLE_STAFF
                                              (3,3), -- sv01 -> ROLE_STUDENT
                                              (4,3); -- sv02 -> ROLE_STUDENT

-- Bật lại safe mode
SET SQL_SAFE_UPDATES = 1;

-- Kiểm tra users
SELECT id, username, password, email, enabled FROM users;

-- Kiểm tra roles
SELECT id, name, description FROM role;

-- Kiểm tra user_roles mapping
SELECT ur.user_id, u.username, ur.role_id, r.name as role_name
FROM user_roles ur
         JOIN users u ON ur.user_id = u.id
         JOIN role r ON ur.role_id = r.id;

-- Kiểm tra student mapping
SELECT s.id, s.code, s.name, s.user_id, u.username
FROM student s
         LEFT JOIN users u ON s.user_id = u.id;

SELECT u.id, u.username, u.password, u.enabled, r.name
FROM users u
         JOIN user_roles ur ON u.id = ur.user_id
         JOIN role r ON ur.role_id = r.id
WHERE u.username = 'admin';


USE dormitory_db;
SELECT DATABASE();  -- kiểm tra đã chọn đúng DB

UPDATE users
SET password = '$2b$10$M3oiNW5nwPFCycTQFeU1MO8QFSHE.y5QlcvQcqbji6ZPFVU9H5OP6'
WHERE username = 'admin';

UPDATE users
SET password = '$2b$10$M3oiNW5nwPFCycTQFeU1MO8QFSHE.y5QlcvQcqbji6ZPFVU9H5OP6'
WHERE username IN ('staff','sv01','sv02');

-- kiểm tra lại
SELECT username, password FROM users
WHERE username IN ('admin','staff','sv01','sv02');