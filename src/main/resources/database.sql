-- ============================================
-- CREATE DATABASE
-- ============================================
DROP DATABASE IF EXISTS dormitory_db;
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
-- TABLE: USERS (UPDATED WITH MISSING COLUMNS)
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
-- DATA: ROLE (CHỈ ADMIN)
-- ============================================
INSERT INTO role (name, description) VALUES
    ('ROLE_ADMIN','Quản lý KTX');

-- ============================================
-- DATA: USERS (CHỈ ADMIN)
-- ============================================
-- Tài khoản admin với password đã mã hóa BCrypt
-- Username: admin
-- Password: 12102005 (đã mã hóa)
INSERT INTO users (username, password, email, full_name, phone, avatar_filename, enabled) VALUES
    ('admin','$2b$10$M3oiNW5nwPFCycTQFeU1MO8QFSHE.y5QlcvQcqbji6ZPFVU9H5OP6','admin@example.com','Administrator',NULL,NULL,TRUE);

-- ============================================
-- DATA: USER_ROLES (CHỈ ADMIN)
-- ============================================
INSERT INTO user_roles (user_id, role_id) VALUES
    (1, 1); -- admin -> ROLE_ADMIN