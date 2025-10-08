-- ============================================
-- CREATE DATABASE
-- ============================================
DROP DATABASE IF EXISTS dormitory_db;
CREATE DATABASE IF NOT EXISTS dormitory_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE dormitory_db;

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
                       full_name VARCHAR(255),
                       phone VARCHAR(20),
                       avatar_filename VARCHAR(255),
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
                      id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                      number      VARCHAR(50) UNIQUE NOT NULL,
                      type        VARCHAR(50),
                      capacity    INT NOT NULL,
                      price       INT DEFAULT 0,
                      building_id BIGINT,
                      CONSTRAINT fk_room_building FOREIGN KEY (building_id) REFERENCES building(id) ON DELETE SET NULL
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ============================================
-- TABLE: STUDENT
-- ============================================
CREATE TABLE student (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         code VARCHAR(50) UNIQUE,
                         name VARCHAR(255) NOT NULL,
                         dob DATE,
                         gender ENUM('Nam', 'Nữ', 'Khác'),
                         phone VARCHAR(20),
                         address VARCHAR(255),
                         email VARCHAR(255),
                         department VARCHAR(255),
                         study_year INT,
                         room_id BIGINT,
                         user_id BIGINT UNIQUE,
                         CONSTRAINT chk_email UNIQUE (email),
                         CONSTRAINT fk_student_room FOREIGN KEY (room_id) REFERENCES room(id) ON DELETE SET NULL,
                         CONSTRAINT fk_student_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
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
                          status     VARCHAR(50),
                          FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
                          FOREIGN KEY (room_id)    REFERENCES room(id)    ON DELETE CASCADE
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ============================================
-- TABLE: FEE
-- ============================================
CREATE TABLE fee (
                     id             BIGINT AUTO_INCREMENT PRIMARY KEY,
                     contract_id    BIGINT,
                     type           VARCHAR(50),
                     scope          VARCHAR(20) DEFAULT 'INDIVIDUAL',
                     amount         DECIMAL(10,2),
                     total_amount   DECIMAL(10,2),
                     group_code     VARCHAR(100),
                     due_date       DATE,
                     payment_status VARCHAR(20) DEFAULT 'UNPAID',
                     FOREIGN KEY (contract_id) REFERENCES contract(id) ON DELETE CASCADE
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ============================================
-- TABLE: DORM_REGISTRATION_PERIOD
-- ============================================
CREATE TABLE dorm_registration_period (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          name VARCHAR(150) NOT NULL,
                                          start_time DATETIME,
                                          end_time DATETIME,
                                          capacity INT,
                                          notes TEXT,
                                          status VARCHAR(50) DEFAULT 'SCHEDULED',
                                          created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                          updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ============================================
-- TABLE: DORM_REGISTRATION_REQUEST
-- ============================================
CREATE TABLE dorm_registration_request (
                                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                           student_id BIGINT,
                                           period_id BIGINT,
                                           desired_room_type VARCHAR(100),
                                           preferred_room_number VARCHAR(50),
                                           expected_move_in_date DATE,
                                           additional_notes TEXT,
                                           status VARCHAR(50) DEFAULT 'PENDING',
                                           admin_notes TEXT,
                                           created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                           updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                           FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
                                           FOREIGN KEY (period_id) REFERENCES dorm_registration_period(id) ON DELETE SET NULL
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

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
                                     updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                     FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
                                     FOREIGN KEY (room_id) REFERENCES room(id) ON DELETE SET NULL,
                                     FOREIGN KEY (handled_by_id) REFERENCES users(id) ON DELETE SET NULL
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
                            date DATE DEFAULT CURRENT_DATE,
                            type VARCHAR(100),
                            created_by_id BIGINT,
                            FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
                            FOREIGN KEY (room_id) REFERENCES room(id) ON DELETE SET NULL,
                            FOREIGN KEY (created_by_id) REFERENCES users(id) ON DELETE SET NULL
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ============================================
-- DATA: ROLE
-- ============================================
INSERT INTO role (name, description) VALUES
    ('ROLE_ADMIN','Quản lý KTX'),
    ('ROLE_STAFF','Nhân viên hỗ trợ'),
    ('ROLE_STUDENT','Sinh viên');

-- ============================================
-- DATA: USERS
-- ============================================
-- Tài khoản admin với password đã mã hóa BCrypt
-- Username: admin
-- Password: 12102005 (đã mã hóa)
INSERT INTO users (username, password, email, full_name, phone, avatar_filename, enabled) VALUES
    ('admin','$2b$10$M3oiNW5nwPFCycTQFeU1MO8QFSHE.y5QlcvQcqbji6ZPFVU9H5OP6','admin@example.com','Administrator',NULL,NULL,TRUE);

-- ============================================
-- DATA: USER_ROLES
-- ============================================
INSERT INTO user_roles (user_id, role_id) VALUES
    (1, 1); -- admin -> ROLE_ADMIN
