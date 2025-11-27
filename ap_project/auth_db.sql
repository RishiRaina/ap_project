CREATE DATABASE AUTH_DB;
USE AUTH_DB;

CREATE TABLE IF NOT EXISTS users_auth (
  user_id INT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(100) UNIQUE NOT NULL,
  role ENUM('ADMIN','INSTRUCTOR','STUDENT') NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  status VARCHAR(20) DEFAULT 'ACTIVE',
  last_login DATETIME NULL
);



--sample data

INSERT INTO users_auth (username, role, password_hash, status)
VALUES ('admin1', 'ADMIN', '$2a$10$f8c0gaXhAZX9rhaHVZx03ON6cuuY2pv6MNBLTf3kCIIU0.RhlLFWi', 'ACTIVE'); --password = admin1


INSERT INTO users_auth (username, role, password_hash, status)
VALUES ('instructor1', 'INSTRUCTOR', '$2a$10$Cx3S3PDItjsGCpfhVJOiJOdIk2pze43heZUE1A1x/IxgJSJF3pH4S', 'ACTIVE'); --password = student1


INSERT INTO users_auth (username, role, password_hash, status)
VALUES ('student1', 'STUDENT', '$2a$10$h8FKLpfJEJ6e7hHzanMLuuKGgVmzcM.ldI2qhuUFHicd1li3KEtnO', 'ACTIVE'); --password = instructor1
