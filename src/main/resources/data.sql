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

    foreign key (question_id) references question (id)
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

    foreign key (exam_id) references exam (id),
    foreign key (question_id) references question (id)
);

-- ================= FAVORITE EXAM =================
create table favorite_exam
(
    exam_id    int not null,
    student_id int not null,

    primary key (exam_id, student_id),

    foreign key (exam_id) references exam (id),
    foreign key (student_id) references users (id)
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

    foreign key (exam_id) references exam (id),
    foreign key (student_id) references users (id)
);

-- ================= STUDENT ANSWER =================
create table student_answer
(
    id         int primary key auto_increment,
    attempt_id int not null,
    answer_id  int not null,

    foreign key (attempt_id) references exam_attempt (id),
    foreign key (answer_id) references answer (id)
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
VALUES ('admin@mail.com', 'admin', '123456', 'An', 'Nguyen', 'ADMIN', true, 'ACTIVED', NULL, NULL,
        '2024-01-01 10:00:00'),
       ('teacher1@mail.com', 'teacher1', '123456', 'Binh', 'Tran', 'TEACHER', true, 'ACTIVED', NULL, 1,
        '2024-01-02 10:00:00'),
       ('teacher2@mail.com', 'teacher2', '123456', 'Cuong', 'Le', 'TEACHER', true, 'ACTIVED', NULL, 2,
        '2024-01-03 10:00:00'),
       ('student1@mail.com', 'student1', '123456', 'Dung', 'Pham', 'STUDENT', true, 'ACTIVED', 1, NULL,
        '2024-01-04 10:00:00'),
       ('student2@mail.com', 'student2', '123456', 'Huy', 'Hoang', 'STUDENT', true, 'ACTIVED', 2, NULL,
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
