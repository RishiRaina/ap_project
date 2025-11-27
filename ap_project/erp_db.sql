CREATE DATABASE ERP_DB;
USE ERP_DB;

CREATE TABLE IF NOT EXISTS students (
  user_id INT PRIMARY KEY,
  roll_no VARCHAR(50),
  program VARCHAR(50),
  year INT,
  FOREIGN KEY (user_id)
    REFERENCES auth_db.users_auth(user_id)
    ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS instructors (
  user_id INT PRIMARY KEY,
  department VARCHAR(100),
  FOREIGN KEY (user_id)
    REFERENCES auth_db.users_auth(user_id)
    ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS courses (
  course_id INT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(20) UNIQUE NOT NULL,
  title VARCHAR(255) NOT NULL,
  credits INT NOT NULL
);


CREATE TABLE IF NOT EXISTS sections (
  section_id INT PRIMARY KEY AUTO_INCREMENT,
  course_id INT NOT NULL,
  instructor_id INT NULL,
  day_time VARCHAR(100),
  room VARCHAR(50),
  capacity INT NOT NULL,
  semester VARCHAR(20),
  year INT,
  registration_deadline DATE,
  FOREIGN KEY (course_id)
    REFERENCES courses(course_id)
    ON DELETE CASCADE,
  FOREIGN KEY (instructor_id)
    REFERENCES instructors(user_id)
    ON DELETE SET NULL
);



CREATE TABLE IF NOT EXISTS enrollments (
  enrollment_id INT PRIMARY KEY AUTO_INCREMENT,
  student_id INT NOT NULL,
  section_id INT NOT NULL,
  status VARCHAR(20) DEFAULT 'ENROLLED',
  UNIQUE KEY unique_enrollment (student_id, section_id),
  FOREIGN KEY (student_id)
    REFERENCES students(user_id)
    ON DELETE CASCADE,
  FOREIGN KEY (section_id)
    REFERENCES sections(section_id)
    ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS grades (
  grade_id INT PRIMARY KEY AUTO_INCREMENT,
  enrollment_id INT NOT NULL,
  component VARCHAR(50) NOT NULL,
  score DECIMAL(6,2) NOT NULL,
  final_grade VARCHAR(5),        
  FOREIGN KEY (enrollment_id)
    REFERENCES enrollments(enrollment_id)
    ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS settings (
  key_name VARCHAR(100) PRIMARY KEY,
  value VARCHAR(255)
);

CREATE TABLE notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    target_user_id INT NULL,       -- null = broadcast by role / all
    target_role VARCHAR(20) NULL,  -- 'STUDENT', 'INSTRUCTOR', 'ADMIN', 'ALL'
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


INSERT INTO settings (key_name, value)
SELECT 'maintenance_on', 'false'
WHERE NOT EXISTS (SELECT 1 FROM settings WHERE key_name = 'maintenance_on');