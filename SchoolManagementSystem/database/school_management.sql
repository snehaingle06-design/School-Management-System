-- ============================================================
-- School Management System - Database Creation Script
-- ============================================================
-- Run this script in MySQL to create the database and tables
-- MySQL 8.0+ recommended
-- ============================================================

DROP DATABASE IF EXISTS school_management;
CREATE DATABASE school_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE school_management;

-- ============================================================
-- Table: classes
-- ============================================================
CREATE TABLE classes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    class_name VARCHAR(50) NOT NULL,
    division VARCHAR(10) DEFAULT '',
    room_number VARCHAR(20),
    capacity INT DEFAULT 40,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_class_division (class_name, division)
) ENGINE=InnoDB;

-- ============================================================
-- Table: subjects
-- ============================================================
CREATE TABLE subjects (
    id INT AUTO_INCREMENT PRIMARY KEY,
    subject_name VARCHAR(100) NOT NULL,
    subject_code VARCHAR(20) NOT NULL UNIQUE,
    max_marks INT DEFAULT 100,
    pass_marks INT DEFAULT 35,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================================
-- Table: students
-- ============================================================
CREATE TABLE students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(20) NOT NULL UNIQUE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    date_of_birth DATE,
    gender ENUM('Male', 'Female', 'Other') DEFAULT 'Male',
    address TEXT,
    phone VARCHAR(15),
    email VARCHAR(100),
    guardian_name VARCHAR(100),
    guardian_phone VARCHAR(15),
    guardian_relation VARCHAR(50),
    class_id INT,
    enrollment_date DATE,
    is_active TINYINT(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ============================================================
-- Table: teachers
-- ============================================================
CREATE TABLE teachers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    teacher_id VARCHAR(20) NOT NULL UNIQUE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    date_of_birth DATE,
    gender ENUM('Male', 'Female', 'Other') DEFAULT 'Male',
    address TEXT,
    phone VARCHAR(15),
    email VARCHAR(100),
    qualification VARCHAR(100),
    experience_years INT DEFAULT 0,
    hire_date DATE,
    is_active TINYINT(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================================
-- Table: users (authentication)
-- ============================================================
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'TEACHER', 'STUDENT') NOT NULL,
    linked_id INT DEFAULT NULL,
    is_active TINYINT(1) DEFAULT 1,
    last_login TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================================
-- Table: teacher_subjects (many-to-many)
-- ============================================================
CREATE TABLE teacher_subjects (
    teacher_id INT NOT NULL,
    subject_id INT NOT NULL,
    PRIMARY KEY (teacher_id, subject_id),
    FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- Table: teacher_classes (many-to-many)
-- ============================================================
CREATE TABLE teacher_classes (
    teacher_id INT NOT NULL,
    class_id INT NOT NULL,
    PRIMARY KEY (teacher_id, class_id),
    FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE CASCADE,
    FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- Table: attendance
-- ============================================================
CREATE TABLE attendance (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    class_id INT NOT NULL,
    attendance_date DATE NOT NULL,
    status ENUM('PRESENT', 'ABSENT', 'LEAVE') NOT NULL DEFAULT 'PRESENT',
    remarks VARCHAR(255),
    marked_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE CASCADE,
    FOREIGN KEY (marked_by) REFERENCES teachers(id) ON DELETE SET NULL,
    UNIQUE KEY uk_student_date (student_id, attendance_date)
) ENGINE=InnoDB;

-- ============================================================
-- Table: examinations
-- ============================================================
CREATE TABLE examinations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    exam_name VARCHAR(100) NOT NULL,
    exam_type ENUM('UNIT_TEST', 'MID_TERM', 'FINAL', 'PRACTICE') NOT NULL,
    class_id INT NOT NULL,
    start_date DATE,
    end_date DATE,
    academic_year VARCHAR(9),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- Table: marks
-- ============================================================
CREATE TABLE marks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    exam_id INT NOT NULL,
    student_id INT NOT NULL,
    subject_id INT NOT NULL,
    marks_obtained DECIMAL(5,2) DEFAULT 0,
    max_marks INT DEFAULT 100,
    grade VARCHAR(5),
    remarks VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (exam_id) REFERENCES examinations(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE,
    UNIQUE KEY uk_exam_student_subject (exam_id, student_id, subject_id)
) ENGINE=InnoDB;

-- ============================================================
-- Table: fee_structure
-- ============================================================
CREATE TABLE fee_structure (
    id INT AUTO_INCREMENT PRIMARY KEY,
    class_id INT NOT NULL,
    fee_type VARCHAR(50) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    due_date DATE,
    academic_year VARCHAR(9),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- Table: fee_payments
-- ============================================================
CREATE TABLE fee_payments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    fee_structure_id INT NOT NULL,
    amount_paid DECIMAL(10,2) NOT NULL,
    payment_date DATE,
    payment_method ENUM('CASH', 'CARD', 'ONLINE', 'CHEQUE') DEFAULT 'CASH',
    transaction_ref VARCHAR(50),
    receipt_number VARCHAR(50) UNIQUE,
    remarks VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (fee_structure_id) REFERENCES fee_structure(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- Sample Data
-- ============================================================

-- Classes
INSERT INTO classes (class_name, division, room_number, capacity) VALUES
('Class 1', 'A', 'R101', 40),
('Class 1', 'B', 'R102', 40),
('Class 2', 'A', 'R201', 40),
('Class 2', 'B', 'R202', 40),
('Class 3', 'A', 'R301', 35),
('Class 4', 'A', 'R401', 35),
('Class 5', 'A', 'R501', 35),
('Class 6', 'A', 'R601', 30),
('Class 7', 'A', 'R701', 30),
('Class 8', 'A', 'R801', 30),
('Class 9', 'A', 'R901', 30),
('Class 10', 'A', 'R1001', 30);

-- Subjects
INSERT INTO subjects (subject_name, subject_code, max_marks, pass_marks) VALUES
('Mathematics', 'MATH101', 100, 35),
('English', 'ENG101', 100, 35),
('Science', 'SCI101', 100, 35),
('Social Studies', 'SOC101', 100, 35),
('Hindi', 'HIN101', 100, 35),
('Computer Science', 'CS101', 100, 35),
('Physical Education', 'PE101', 100, 35),
('Art', 'ART101', 100, 35);

-- Teachers
INSERT INTO teachers (teacher_id, first_name, last_name, date_of_birth, gender, address, phone, email, qualification, experience_years, hire_date) VALUES
('TCH001', 'Rajesh', 'Kumar', '1980-05-15', 'Male', '12 MG Road, Delhi', '9876543210', 'rajesh.kumar@school.com', 'M.Sc Mathematics', 15, '2010-06-01'),
('TCH002', 'Priya', 'Sharma', '1985-08-22', 'Female', '45 Park Avenue, Mumbai', '9876543211', 'priya.sharma@school.com', 'M.A English', 10, '2015-07-01'),
('TCH003', 'Amit', 'Singh', '1982-03-10', 'Male', '78 Lake Road, Bangalore', '9876543212', 'amit.singh@school.com', 'M.Sc Physics', 12, '2013-04-01'),
('TCH004', 'Sunita', 'Verma', '1988-11-05', 'Female', '23 Hill Street, Pune', '9876543213', 'sunita.verma@school.com', 'M.A History', 8, '2017-08-01'),
('TCH005', 'Vikram', 'Patel', '1979-07-18', 'Male', '56 River Lane, Ahmedabad', '9876543214', 'vikram.patel@school.com', 'M.Tech CS', 18, '2008-01-15');

-- Students
INSERT INTO students (student_id, first_name, last_name, date_of_birth, gender, address, phone, email, guardian_name, guardian_phone, guardian_relation, class_id, enrollment_date) VALUES
('STU001', 'Aarav', 'Mehta', '2012-03-15', 'Male', '10 Elm Street, Delhi', '9123456701', 'aarav.mehta@student.com', 'Suresh Mehta', '9123456700', 'Father', 1, '2024-04-01'),
('STU002', 'Diya', 'Joshi', '2012-07-22', 'Female', '22 Oak Avenue, Mumbai', '9123456702', 'diya.joshi@student.com', 'Meena Joshi', '9123456703', 'Mother', 1, '2024-04-01'),
('STU003', 'Kabir', 'Reddy', '2011-11-08', 'Male', '33 Pine Road, Bangalore', '9123456704', 'kabir.reddy@student.com', 'Ravi Reddy', '9123456705', 'Father', 3, '2024-04-01'),
('STU004', 'Ananya', 'Nair', '2011-05-30', 'Female', '44 Cedar Lane, Chennai', '9123456706', 'ananya.nair@student.com', 'Lakshmi Nair', '9123456707', 'Mother', 3, '2024-04-01'),
('STU005', 'Rohan', 'Gupta', '2010-09-12', 'Male', '55 Maple Drive, Kolkata', '9123456708', 'rohan.gupta@student.com', 'Anil Gupta', '9123456709', 'Father', 5, '2024-04-01'),
('STU006', 'Ishita', 'Bose', '2010-01-25', 'Female', '66 Birch Way, Pune', '9123456710', 'ishita.bose@student.com', 'Sumit Bose', '9123456711', 'Father', 5, '2024-04-01'),
('STU007', 'Arjun', 'Malhotra', '2009-06-18', 'Male', '77 Willow Court, Hyderabad', '9123456712', 'arjun.m@student.com', 'Neha Malhotra', '9123456713', 'Mother', 8, '2024-04-01'),
('STU008', 'Saanvi', 'Iyer', '2008-12-03', 'Female', '88 Ash Boulevard, Jaipur', '9123456714', 'saanvi.i@student.com', 'Ramesh Iyer', '9123456715', 'Father', 10, '2024-04-01'),
('STU009', 'Vivaan', 'Chatterjee', '2012-04-10', 'Male', '99 Teak Street, Lucknow', '9123456716', 'vivaan.c@student.com', 'Debarati Chatterjee', '9123456717', 'Mother', 2, '2024-04-01'),
('STU010', 'Myra', 'Kapoor', '2009-08-28', 'Female', '11 Banyan Road, Chandigarh', '9123456718', 'myra.k@student.com', 'Rajiv Kapoor', '9123456719', 'Father', 8, '2024-04-01');

-- Teacher-Subject assignments
INSERT INTO teacher_subjects (teacher_id, subject_id) VALUES
(1, 1), -- Rajesh -> Math
(2, 2), -- Priya -> English
(3, 3), -- Amit -> Science
(4, 4), -- Sunita -> Social Studies
(5, 6); -- Vikram -> CS

-- Teacher-Class assignments
INSERT INTO teacher_classes (teacher_id, class_id) VALUES
(1, 1), (1, 3), -- Rajesh -> Class 1A, 2A
(2, 1), (2, 5), -- Priya -> Class 1A, 3A
(3, 3), (3, 8), -- Amit -> Class 2A, 6A
(4, 5), (4, 10), -- Sunita -> Class 3A, 8A
(5, 8), (5, 10); -- Vikram -> Class 6A, 8A

-- Users (passwords are SHA-256 hashed)
-- admin/admin123 -> hash
-- teacher1/teacher123 -> hash
-- student1/student123 -> hash
INSERT INTO users (username, password_hash, role, linked_id) VALUES
('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ADMIN', NULL),
('teacher1', 'b8277cca737e4c18108f97e498c74b4e1f7b50c95e9394d68ea93fc9e6bf1e0d', 'TEACHER', 1),
('teacher2', 'b8277cca737e4c18108f97e498c74b4e1f7b50c95e9394d68ea93fc9e6bf1e0d', 'TEACHER', 2),
('student1', '25e49bc0dab5a7b75543be0a2a8133a8f89a1edf7ac0a68436566f6cf1e6345b', 'STUDENT', 1),
('student2', '25e49bc0dab5a7b75543be0a2a8133a8f89a1edf7ac0a68436566f6cf1e6345b', 'STUDENT', 2);

-- Examinations
INSERT INTO examinations (exam_name, exam_type, class_id, start_date, end_date, academic_year) VALUES
('Unit Test 1', 'UNIT_TEST', 1, '2024-07-15', '2024-07-20', '2024-2025'),
('Mid Term Exam', 'MID_TERM', 1, '2024-10-01', '2024-10-10', '2024-2025'),
('Unit Test 1', 'UNIT_TEST', 3, '2024-07-15', '2024-07-20', '2024-2025'),
('Final Exam', 'FINAL', 1, '2025-03-01', '2025-03-15', '2024-2025');

-- Sample Marks
INSERT INTO marks (exam_id, student_id, subject_id, marks_obtained, max_marks, grade) VALUES
(1, 1, 1, 85.00, 100, 'A'),
(1, 1, 2, 78.00, 100, 'B+'),
(1, 1, 3, 92.00, 100, 'A+'),
(1, 2, 1, 72.00, 100, 'B'),
(1, 2, 2, 88.00, 100, 'A'),
(1, 2, 3, 65.00, 100, 'B'),
(3, 3, 1, 90.00, 100, 'A+'),
(3, 3, 3, 78.00, 100, 'B+');

-- Fee Structure
INSERT INTO fee_structure (class_id, fee_type, amount, due_date, academic_year) VALUES
(1, 'Tuition Fee', 15000.00, '2024-06-30', '2024-2025'),
(1, 'Lab Fee', 2000.00, '2024-06-30', '2024-2025'),
(1, 'Library Fee', 1000.00, '2024-06-30', '2024-2025'),
(3, 'Tuition Fee', 18000.00, '2024-06-30', '2024-2025'),
(3, 'Lab Fee', 2500.00, '2024-06-30', '2024-2025'),
(5, 'Tuition Fee', 20000.00, '2024-06-30', '2024-2025'),
(8, 'Tuition Fee', 25000.00, '2024-06-30', '2024-2025'),
(10, 'Tuition Fee', 30000.00, '2024-06-30', '2024-2025');

-- Fee Payments
INSERT INTO fee_payments (student_id, fee_structure_id, amount_paid, payment_date, payment_method, transaction_ref, receipt_number) VALUES
(1, 1, 15000.00, '2024-06-15', 'ONLINE', 'TXN2024001', 'RCP-0001'),
(1, 2, 2000.00, '2024-06-15', 'ONLINE', 'TXN2024002', 'RCP-0002'),
(2, 1, 15000.00, '2024-06-20', 'CASH', NULL, 'RCP-0003'),
(3, 4, 18000.00, '2024-06-18', 'CARD', 'TXN2024003', 'RCP-0004'),
(5, 6, 20000.00, '2024-06-25', 'CHEQUE', 'CHQ-5001', 'RCP-0005');

-- Sample Attendance
INSERT INTO attendance (student_id, class_id, attendance_date, status, marked_by) VALUES
(1, 1, '2024-07-01', 'PRESENT', 1),
(1, 1, '2024-07-02', 'PRESENT', 1),
(1, 1, '2024-07-03', 'ABSENT', 1),
(1, 1, '2024-07-04', 'PRESENT', 1),
(1, 1, '2024-07-05', 'LEAVE', 1),
(2, 1, '2024-07-01', 'PRESENT', 1),
(2, 1, '2024-07-02', 'ABSENT', 1),
(2, 1, '2024-07-03', 'PRESENT', 1),
(2, 1, '2024-07-04', 'PRESENT', 1),
(2, 1, '2024-07-05', 'PRESENT', 1),
(3, 3, '2024-07-01', 'PRESENT', 3),
(3, 3, '2024-07-02', 'PRESENT', 3),
(4, 3, '2024-07-01', 'ABSENT', 3),
(4, 3, '2024-07-02', 'PRESENT', 3);

-- ============================================================
-- End of Script
-- ============================================================

