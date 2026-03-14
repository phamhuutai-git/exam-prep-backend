drop
database if exists exam_management_system;
create
database exam_management_system;
use
exam_management_system;

-- ================= CLASSES =================
create table classes
(
    id          int primary key auto_increment,
    name        varchar(255) not null unique,
    create_date datetime
);

-- ================= DEPARTMENT =================
create table department
(
    id          int primary key auto_increment,
    name        varchar(255) not null unique,
    create_date datetime
);

-- ================= USERS =================
create table users
(
    id            int primary key auto_increment,
    email         varchar(255) not null unique,
    username      varchar(255) not null unique,
    password      varchar(255) not null,
    first_name    varchar(255) not null,
    last_name     varchar(255) not null,
    role          enum('ADMIN','TEACHER','STUDENT') not null,
    is_active     boolean      not null default true,
    status        enum('ACTIVED','LOCKED') not null,
    class_id      int,
    department_id int,
    create_date   datetime,
    -- đếm số lần bị khóa
    fail_count INT DEFAULT 0,
    -- thời gian bị khóa
    lock_time  datetime,

    foreign key (class_id) references classes (id),
    foreign key (department_id) references department (id)
);
-- ================= CLASS TEACHER =================
create table class_teacher
(
    class_id   int not null,
    teacher_id int not null,

    primary key (class_id, teacher_id),

    foreign key (class_id) references classes (id),
    foreign key (teacher_id) references users (id)
);

-- ================= CATEGORY QUESTION =================
create table category_question
(
    id   int primary key auto_increment,
    name varchar(255) not null unique
);

-- ================= QUESTION =================
create table question
(
    id               int primary key auto_increment,
    content          text not null,
    difficulty_level enum('EASY','MEDIUM','HARD') not null,
    category_id      int  not null,
    creator_id       int  not null,
    create_date      datetime,

    foreign key (category_id) references category_question (id),
    foreign key (creator_id) references users (id)
);

-- ================= ANSWER =================
create table answer
(
    id          int primary key auto_increment,
    content     text    not null,
    question_id int     not null,
    is_correct  boolean not null,

    foreign key (question_id) references question (id) on delete cascade
);

-- ================= EXAM =================
create table exam
(
    id          int primary key auto_increment,
    code        varchar(255) not null unique,
    title       varchar(255) not null,
    duration    time         not null,
    category_id int          not null,
    creator_id  int          not null,
    create_date datetime,

    foreign key (category_id) references category_question (id),
    foreign key (creator_id) references users (id)
);

-- ================= EXAM QUESTION =================
create table exam_question
(
    exam_id     int not null,
    question_id int not null,

    primary key (exam_id, question_id),

    foreign key (exam_id) references exam (id) on delete cascade,
    foreign key (question_id) references question (id) on delete cascade
);

-- ================= FAVORITE EXAM =================
create table favorite_exam
(
    exam_id    int not null,
    student_id int not null,

    primary key (exam_id, student_id),


    foreign key (exam_id) references exam (id) on delete cascade,
    foreign key (student_id) references users (id) on delete cascade
);

-- ================= EXAM ATTEMPT =================
create table exam_attempt
(
    id         int primary key auto_increment,
    exam_id    int not null,
    student_id int not null,
    start_time datetime,
    end_time   datetime,
    score      decimal(4, 2),
    status     enum('IN_PROGRESS','SUBMITTED') not null,


    foreign key (exam_id) references exam (id) on delete cascade,
    foreign key (student_id) references users (id) on delete cascade
);

-- ================= STUDENT ANSWER =================
create table student_answer
(
    id         int primary key auto_increment,
    attempt_id int not null,
    answer_id  int not null,

    foreign key (attempt_id) references exam_attempt (id) on delete cascade,
    foreign key (answer_id) references answer (id) on delete cascade
);
CREATE TABLE otps
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    email      VARCHAR(50) NOT NULL,
    otp        INT         NOT NULL,
    type       VARCHAR(50),
    expire_at  datetime,
    created_at datetime,
    FOREIGN KEY (email) REFERENCES users (email)
);
INSERT INTO classes (name, create_date)
VALUES ('Railway01', '2024-01-01 08:00:00'),
       ('Railway02', '2024-01-02 08:00:00'),
       ('Railway03', '2024-01-03 08:00:00'),
       ('Rocket01', '2024-01-04 08:00:00'),
       ('Rocket02', '2024-01-05 08:00:00');

INSERT INTO department (name, create_date)
VALUES ('Backend', '2024-01-01 09:00:00'),
       ('Frontend', '2024-01-02 09:00:00'),
       ('Fullstack', '2024-01-03 09:00:00'),
       ('DevOps', '2024-01-04 09:00:00'),
       ('Testing', '2024-01-05 09:00:00');

INSERT INTO users
(email, username, password, first_name, last_name, role, is_active, status, class_id, department_id, create_date)
VALUES ('admin@mail.com', 'admin', 'admin123', 'An', 'Nguyen', 'ADMIN', true, 'ACTIVED', NULL, NULL,
        '2024-01-01 10:00:00'),
       ('teacher1@mail.com', 'teacher1', '12345', 'Binh', 'Tran', 'TEACHER', true, 'ACTIVED', NULL, 1,
        '2024-01-02 10:00:00'),
       ('teacher2@mail.com', 'teacher2', '12345', 'Cuong', 'Le', 'TEACHER', true, 'ACTIVED', NULL, 2,
        '2024-01-03 10:00:00'),
       ('student1@mail.com', 'student1', '1234', 'Dung', 'Pham', 'STUDENT', true, 'ACTIVED', 1, NULL,
        '2024-01-04 10:00:00'),
       ('student2@mail.com', 'student2', '1234', 'Huy', 'Hoang', 'STUDENT', true, 'ACTIVED', 2, NULL,
        '2024-01-05 10:00:00');

INSERT INTO class_teacher (class_id, teacher_id)
VALUES (1, 2),
       (1, 3),
       (2, 2),
       (3, 3),
       (4, 2);

INSERT INTO category_question (name)
VALUES ('Java'),
       ('Spring'),
       ('SQL'),
       ('HTML'),
       ('JavaScript');

INSERT INTO question
    (content, difficulty_level, category_id, creator_id, create_date)
VALUES ('What is Java?', 'EASY', 1, 2, '2024-02-01'),
       ('Explain OOP principles', 'MEDIUM', 1, 2, '2024-02-02'),
       ('What is Spring Boot?', 'EASY', 2, 3, '2024-02-03'),
       ('What is Primary Key?', 'EASY', 3, 2, '2024-02-04'),
       ('What is HTML?', 'EASY', 4, 3, '2024-02-05');


INSERT INTO answer (content, question_id, is_correct)
VALUES
-- Question 1
('Programming Language', 1, true),
('Database', 1, false),
('Operating System', 1, false),
('Web Browser', 1, false),

-- Question 2
('Encapsulation', 2, true),
('Inheritance', 2, false),
('Compilation', 2, false),
('Indexing', 2, false),

-- Question 3
('Java Framework', 3, false),
('Spring Boot Framework', 3, true),
('Database Tool', 3, false),
('Programming Language', 3, false),

-- Question 4
('Primary key is unique identifier', 4, true),
('Primary key allows duplicate', 4, false),
('Primary key can be null', 4, false),
('Primary key is optional', 4, false),

-- Question 5
('Programming Language', 5, false),
('Markup Language', 5, true),
('Database System', 5, false),
('Operating System', 5, false);

INSERT INTO exam
    (code, title, duration, category_id, creator_id, create_date)
VALUES ('EX001', 'Java Basic Test', '00:30:00', 1, 2, '2024-03-01'),
       ('EX002', 'Spring Test', '00:40:00', 2, 3, '2024-03-02'),
       ('EX003', 'SQL Test', '00:30:00', 3, 2, '2024-03-03'),
       ('EX004', 'HTML Test', '00:20:00', 4, 3, '2024-03-04'),
       ('EX005', 'JS Test', '00:25:00', 5, 2, '2024-03-05');

INSERT INTO exam_question (exam_id, question_id)
VALUES (1, 1),
       (1, 2),
       (2, 3),
       (3, 4),
       (4, 5);

INSERT INTO favorite_exam (exam_id, student_id)
VALUES (1, 4),
       (2, 5),
       (3, 4),
       (4, 5),
       (5, 4);

INSERT INTO exam_attempt
    (exam_id, student_id, start_time, end_time, score, status)
VALUES (1, 4, '2024-04-01 09:00:00', '2024-04-01 09:25:00', 8, 'SUBMITTED'),
       (2, 5, '2024-04-01 10:00:00', '2024-04-01 10:35:00', 7, 'SUBMITTED'),
       (3, 4, '2024-04-02 09:00:00', '2024-04-02 09:30:00', 6, 'SUBMITTED'),
       (4, 5, '2024-04-02 10:00:00', '2024-04-02 10:20:00', 9, 'SUBMITTED'),
       (5, 4, '2024-04-03 09:00:00', '2024-04-03 09:25:00', 10, 'SUBMITTED');

INSERT INTO student_answer (attempt_id, answer_id)
VALUES (1, 1),
       (2, 10),
       (3, 13),
       (4, 18),
       (5, 5);


update users
set password ='$2a$10$PbUJonO1EEdsEinGijTCluiKlKAFTE8dwmdfYn9NPDb9s3t1TFqnW'
where id = 1; -- ADMIN:admin123
update users
set password ='$2a$10$GEgiP80cEPmuFx3Mo4A9OOFJ8OKcR7nDGR6P2ZBl7gRForMZg56Ei'
where id = 2; -- TEACHER:12345
update users
set password ='$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne'
where id = 4; -- STUDENT:1234
update users
set email ='ngoquangtruongjk05@gmail.com'
where id = 1;

select*
from users;
select id, username, email, password
from users;
SELECT *
FROM category_question;
