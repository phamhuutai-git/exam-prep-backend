DROP DATABASE IF EXISTS exam_management_system;
CREATE DATABASE exam_management_system;
USE exam_management_system;

-- ================= CLASSES =================
CREATE TABLE classes
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(255) NOT NULL UNIQUE,
    create_date DATETIME
);

-- ================= USERS =================
CREATE TABLE users
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
    email       VARCHAR(255)                       NOT NULL UNIQUE,
    username    VARCHAR(255)                       NOT NULL UNIQUE,
    password    VARCHAR(255)                       NOT NULL,
    first_name  VARCHAR(255)                       NOT NULL,
    last_name   VARCHAR(255)                       NOT NULL,
    role        ENUM ('ADMIN','TEACHER','STUDENT') NOT NULL,
    is_active   BOOLEAN                            NOT NULL DEFAULT TRUE,
    status      ENUM ('ACTIVED','LOCKED')          NOT NULL DEFAULT 'ACTIVED',
    class_id    INT,
    create_date DATETIME,
    fail_count  INT                                         DEFAULT 0,
    lock_time   DATETIME,
    FOREIGN KEY (class_id) REFERENCES classes (id) ON DELETE SET NULL
);

-- ================= CLASS TEACHER =================
CREATE TABLE class_teacher
(
    class_id   INT NOT NULL,
    teacher_id INT NOT NULL,

    PRIMARY KEY (class_id, teacher_id),

    FOREIGN KEY (class_id) REFERENCES classes (id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES users (id) ON DELETE CASCADE
);

-- ================= CATEGORY QUESTION =================
CREATE TABLE category_question
(
    id   INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- ================= QUESTION =================
CREATE TABLE question
(
    id               INT PRIMARY KEY AUTO_INCREMENT,
    content          TEXT                          NOT NULL,
    difficulty_level ENUM ('EASY','MEDIUM','HARD') NOT NULL,
    category_id      INT                           NOT NULL,
    creator_id       INT                           NOT NULL,
    create_date      DATETIME,
    explanation      TEXT,

    FOREIGN KEY (category_id) REFERENCES category_question (id) ON DELETE CASCADE,
    FOREIGN KEY (creator_id) REFERENCES users (id) ON DELETE CASCADE
);

-- ================= ANSWER =================
CREATE TABLE answer
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
    content     TEXT         NOT NULL,
    question_id INT          NOT NULL,
    is_correct  BOOLEAN      NOT NULL,
    label       VARCHAR(255) NULL,

    FOREIGN KEY (question_id) REFERENCES question (id) ON DELETE CASCADE
);

-- ================= EXAM =================
CREATE TABLE exam
(
    id             INT PRIMARY KEY AUTO_INCREMENT,
    code           VARCHAR(255)                          NOT NULL UNIQUE,
    title          VARCHAR(255)                          NOT NULL,

    -- 🔥 ĐÃ SỬA THÀNH KIỂU INT ĐỂ LƯU SỐ PHÚT (KHÔNG BỊ GIỚI HẠN THỜI GIAN NỮA)
    duration       INT                                   NOT NULL COMMENT 'Thời gian làm bài tính bằng phút',

    category_id    INT                                   NOT NULL,
    creator_id     INT                                   NOT NULL,
    create_date    DATETIME,
    is_active      TINYINT(1)                            NOT NULL DEFAULT 1,
    exam_type      ENUM ('PRACTICE', 'OFFICIAL', 'MOCK') NOT NULL,
    pass_score     DOUBLE                                NOT NULL DEFAULT 50.0,
    review_allowed TINYINT(1)                            NOT NULL DEFAULT 1,

    -- 🔥 THÊM 2 TRƯỜNG QUẢN LÝ THỜI GIAN PHÁT ĐỀ VÀ THU ĐỀ
    start_time     DATETIME                              NULL,
    end_time       DATETIME                              NULL,

    FOREIGN KEY (category_id) REFERENCES category_question (id) ON DELETE CASCADE,
    FOREIGN KEY (creator_id) REFERENCES users (id) ON DELETE CASCADE
);

-- ================= EXAM QUESTION =================
CREATE TABLE exam_question
(
    exam_id     INT NOT NULL,
    question_id INT NOT NULL,

    PRIMARY KEY (exam_id, question_id),

    FOREIGN KEY (exam_id) REFERENCES exam (id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES question (id) ON DELETE CASCADE
);

-- ================= FAVORITE EXAM =================
CREATE TABLE favorite_exam
(
    exam_id    INT NOT NULL,
    student_id INT NOT NULL,

    PRIMARY KEY (exam_id, student_id),

    FOREIGN KEY (exam_id) REFERENCES exam (id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES users (id) ON DELETE CASCADE
);

-- ================= EXAM ATTEMPT =================
CREATE TABLE exam_attempt
(
    id                 INT PRIMARY KEY AUTO_INCREMENT,
    exam_id            INT                              NOT NULL,
    student_id         INT                              NOT NULL,
    start_time         DATETIME,
    end_time           DATETIME,
    score              DECIMAL(5, 2),
    correct_count      INT                              NOT NULL DEFAULT 0,
    wrong_count        INT                              NOT NULL DEFAULT 0,
    blank_count        INT                              NOT NULL DEFAULT 0,
    time_spent_seconds INT                              NOT NULL DEFAULT 0,
    status             ENUM ('IN_PROGRESS','SUBMITTED') NOT NULL,

    FOREIGN KEY (exam_id) REFERENCES exam (id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES users (id) ON DELETE CASCADE
);

-- ================= STUDENT ANSWER =================
CREATE TABLE student_answer
(
    id                 INT PRIMARY KEY AUTO_INCREMENT,
    attempt_id         INT     NOT NULL,
    question_id        INT     NOT NULL,
    selected_answer_id INT     NULL,
    is_correct         BOOLEAN NOT NULL DEFAULT FALSE,

    FOREIGN KEY (attempt_id) REFERENCES exam_attempt (id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES question (id) ON DELETE CASCADE,
    FOREIGN KEY (selected_answer_id) REFERENCES answer (id) ON DELETE CASCADE
);

-- ================= CLASS EXAM =================
CREATE TABLE class_exam
(
    id            INT PRIMARY KEY AUTO_INCREMENT,
    class_id      INT NOT NULL,
    exam_id       INT NOT NULL,
    attempt_count INT,

    CONSTRAINT unique_class_exam UNIQUE (class_id, exam_id),
    FOREIGN KEY (class_id) REFERENCES classes (id) ON DELETE CASCADE,
    FOREIGN KEY (exam_id) REFERENCES exam (id) ON DELETE CASCADE
);

-- ================= OTP TABLE =================
CREATE TABLE otps
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    email      VARCHAR(255) NOT NULL,
    otp        INT          NOT NULL,
    type       VARCHAR(50),
    expire_at  DATETIME,
    created_at DATETIME,
    FOREIGN KEY (email) REFERENCES users (email) ON DELETE CASCADE
);

-- ================= DATA INSERTION =================

-- 1. Khởi tạo 15 lớp học cấp 3
INSERT INTO classes (name, create_date)
VALUES ('10A1', NOW()), ('10A2', NOW()), ('10A3', NOW()), ('10A4', NOW()), ('10A5', NOW()),
       ('11A1', NOW()), ('11A2', NOW()), ('11A3', NOW()), ('11A4', NOW()), ('11A5', NOW()),
       ('12A1', NOW()), ('12A2', NOW()), ('12A3', NOW()), ('12A4', NOW()), ('12A5', NOW());

-- 2. Thêm người dùng (Gắn Học sinh vào lớp 10A1 và 12A1)
INSERT INTO users (email, username, password, first_name, last_name, role, is_active, status, class_id, create_date)
VALUES ('Trungquangle04@gmail.com', 'admin1', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Hải', 'Đồng', 'ADMIN', TRUE, 'ACTIVED', NULL, NOW()),
       ('admin2@mail.com', 'admin2', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Tài', 'Phạm', 'ADMIN', TRUE, 'ACTIVED', NULL, NOW()),
       ('teacher1@mail.com', 'teacher1', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Bình', 'Trần', 'TEACHER', TRUE, 'ACTIVED', NULL, NOW()),
       ('teacher2@mail.com', 'teacher2', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Cường', 'Lê', 'TEACHER', TRUE, 'ACTIVED', NULL, NOW()),
       ('student1@mail.com', 'student1', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Dũng', 'Phạm', 'STUDENT', TRUE, 'ACTIVED', 1, NOW()),  -- Lớp 10A1
       ('student2@mail.com', 'student2', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Huy', 'Hoàng', 'STUDENT', TRUE, 'ACTIVED', 11, NOW()); -- Lớp 12A1

-- 3. Phân công Giáo viên chủ nhiệm / Giảng dạy các lớp
INSERT INTO class_teacher (class_id, teacher_id)
VALUES (1, 3), (2, 3), (11, 4), (12, 4);

-- 4. Danh mục Môn học THPT
INSERT INTO category_question (name)
VALUES ('Toán Học'), ('Ngữ Văn'), ('Tiếng Anh'), ('Vật Lý'), ('Hóa Học'), ('Sinh Học');

-- 5. Bộ câu hỏi môn học chuẩn THPT
INSERT INTO question (content, difficulty_level, category_id, creator_id, create_date, explanation)
VALUES
    ('Đạo hàm của hàm số y = x^2 là gì?', 'EASY', 1, 3, NOW(), 'Áp dụng công thức đạo hàm cơ bản: (x^n)'' = n*x^(n-1). Với n=2 ta được 2x.'),
    ('Tác giả của kiệt tác Truyện Kiều là ai?', 'EASY', 2, 4, NOW(), 'Truyện Kiều (Đoạn trường tân thanh) do Đại thi hào Nguyễn Du sáng tác.'),
    ('Chọn đáp án đúng: She _____ to school every day.', 'EASY', 3, 3, NOW(), 'Chủ ngữ "She" là ngôi thứ 3 số ít, động từ "go" phải chia thành "goes" ở thì Hiện tại đơn.'),
    ('Đơn vị đo lực trong hệ SI là gì?', 'EASY', 4, 4, NOW(), 'Đơn vị đo lực được đặt theo tên nhà vật lý học Isaac Newton, ký hiệu là N.'),
    ('Ký hiệu hóa học của nguyên tố Sắt là gì?', 'EASY', 5, 3, NOW(), 'Sắt bắt nguồn từ chữ Ferrum trong tiếng Latinh, ký hiệu là Fe.'),
    ('Bào quan nào được ví như "nhà máy điện" của tế bào động vật?', 'EASY', 6, 4, NOW(), 'Ti thể có chức năng hô hấp tế bào, tạo ra năng lượng ATP cung cấp cho mọi hoạt động sống.');

-- 6. Câu trả lời trắc nghiệm
INSERT INTO answer (content, question_id, is_correct, label) VALUES
-- Q1: Toán (IDs 1-4)
('y'' = 2x', 1, TRUE, 'A'), ('y'' = x', 1, FALSE, 'B'), ('y'' = 2', 1, FALSE, 'C'), ('y'' = x^2', 1, FALSE, 'D'),
-- Q2: Văn (IDs 5-8)
('Nguyễn Du', 2, TRUE, 'A'), ('Nam Cao', 2, FALSE, 'B'), ('Nguyễn Trãi', 2, FALSE, 'C'), ('Hồ Xuân Hương', 2, FALSE, 'D'),
-- Q3: Tiếng Anh (IDs 9-12)
('goes', 3, TRUE, 'A'), ('go', 3, FALSE, 'B'), ('going', 3, FALSE, 'C'), ('gone', 3, FALSE, 'D'),
-- Q4: Vật Lý (IDs 13-16)
('Newton (N)', 4, TRUE, 'A'), ('Joule (J)', 4, FALSE, 'B'), ('Watt (W)', 4, FALSE, 'C'), ('Pascal (Pa)', 4, FALSE, 'D'),
-- Q5: Hóa Học (IDs 17-20)
('Fe', 5, TRUE, 'A'), ('F', 5, FALSE, 'B'), ('Cu', 5, FALSE, 'C'), ('Ag', 5, FALSE, 'D'),
-- Q6: Sinh Học (IDs 21-24)
('Ti thể', 6, TRUE, 'A'), ('Lục lạp', 6, FALSE, 'B'), ('Nhân tế bào', 6, FALSE, 'C'), ('Ribosome', 6, FALSE, 'D');

-- 7. Các Đề thi (Cập nhật thời gian làm bài thành SỐ PHÚT)
INSERT INTO exam (code, title, duration, category_id, creator_id, create_date, exam_type, start_time, end_time)
VALUES
    -- Thi Thử / Luyện Tập (Không giới hạn thời gian mở/đóng)
    ('EX001', 'Đề luyện tập Toán 10 Chương 1', 45, 1, 3, NOW(), 'PRACTICE', NULL, NULL),
    ('EX004', 'Luyện tập Vật Lý cơ bản', 15, 4, 4, NOW(), 'PRACTICE', NULL, NULL),

    -- Thi Thật (Có giới hạn thời gian mở từ HÔM QUA đến 7 NGÀY TỚI)
    ('EX002', 'Kiểm tra Giữa kỳ I - Tiếng Anh 12', 60, 3, 3, NOW(), 'OFFICIAL', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 7 DAY)),
    ('EX003', 'Thi Thử Đại Học Ngữ Văn', 120, 2, 4, NOW(), 'MOCK', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 7 DAY)),

    -- Thi Thật (Đã hết hạn thu bài từ HÔM QUA) -> Để test học sinh không nhìn thấy
    ('EX005', 'Kiểm tra 15 phút Hóa Học 10', 15, 5, 3, NOW(), 'OFFICIAL', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
    ('EX006', 'Trắc nghiệm Sinh Học', 30, 6, 4, NOW(), 'PRACTICE', NULL, NULL);

-- 8. Gắn câu hỏi vào đề thi
INSERT INTO exam_question (exam_id, question_id)
VALUES (1, 1), (2, 3), (3, 2), (4, 4), (5, 5), (6, 6);

-- 9. Gắn đề thi vào Lớp học
INSERT INTO class_exam (class_id, exam_id)
VALUES
    (1, 1), (1, 5),
    (11, 2), (11, 3), (11, 4), (11, 6);

-- 10. Đánh dấu Yêu thích
INSERT INTO favorite_exam (exam_id, student_id)
VALUES (1, 5), (2, 6);

-- 11. Dữ liệu làm bài thi của học sinh
INSERT INTO exam_attempt (exam_id, student_id, start_time, end_time, score, status, correct_count, wrong_count)
VALUES
    (1, 5, DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_SUB(NOW(), INTERVAL 1 HOUR), 10.0, 'SUBMITTED', 1, 0),
    (3, 6, DATE_SUB(NOW(), INTERVAL 5 HOUR), DATE_SUB(NOW(), INTERVAL 4 HOUR), 0.0, 'SUBMITTED', 0, 1);

-- 12. Chi tiết đáp án học sinh chọn
INSERT INTO student_answer (attempt_id, question_id, selected_answer_id, is_correct)
VALUES
    (1, 1, 1, TRUE),
    (2, 2, 6, FALSE);


-- =========================================================
-- GIÁO VIÊN (20 Người)
-- =========================================================
INSERT INTO users (email, username, password, first_name, last_name, role, is_active, status, class_id, create_date) VALUES
                                                                                                                         ('nguyenvanbinh@hoangcau.edu.vn', 'binhnv3', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Bình', 'Nguyễn Văn', 'TEACHER', TRUE, 'ACTIVED', NULL, NOW()),
                                                                                                                         ('lethituyet@hoangcau.edu.vn', 'tuyetlt4', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Tuyết', 'Lê Thị', 'TEACHER', TRUE, 'ACTIVED', NULL, NOW()),
                                                                                                                         ('phamminhcuong@hoangcau.edu.vn', 'cuongpm5', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Cường', 'Phạm Minh', 'TEACHER', TRUE, 'ACTIVED', NULL, NOW()),
                                                                                                                         ('tranngochai@hoangcau.edu.vn', 'haitn6', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Hải', 'Trần Ngọc', 'TEACHER', TRUE, 'ACTIVED', NULL, NOW()),
                                                                                                                         ('hoangducson@hoangcau.edu.vn', 'sonhd7', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Sơn', 'Hoàng Đức', 'TEACHER', TRUE, 'ACTIVED', NULL, NOW()),
                                                                                                                         ('vuthidieu@hoangcau.edu.vn', 'dieuvt8', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Diệu', 'Vũ Thị', 'TEACHER', TRUE, 'ACTIVED', NULL, NOW()),
                                                                                                                         ('nguyenthanhtung@hoangcau.edu.vn', 'tungnt9', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Tùng', 'Nguyễn Thanh', 'TEACHER', TRUE, 'ACTIVED', NULL, NOW()),
                                                                                                                         ('dangminhanh@hoangcau.edu.vn', 'anhdm10', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Anh', 'Đặng Minh', 'TEACHER', TRUE, 'ACTIVED', NULL, NOW()),
                                                                                                                         ('buiquanghuy@hoangcau.edu.vn', 'huybq11', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Huy', 'Bùi Quang', 'TEACHER', TRUE, 'ACTIVED', NULL, NOW()),
                                                                                                                         ('phanthihuong@hoangcau.edu.vn', 'huongpt12', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Hương', 'Phan Thị', 'TEACHER', TRUE, 'ACTIVED', NULL, NOW()),
                                                                                                                         ('nguyenmanhdung@hoangcau.edu.vn', 'dungnm13', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Dũng', 'Nguyễn Mạnh', 'TEACHER', TRUE, 'ACTIVED', NULL, NOW()),
                                                                                                                         ('tranthithanh@hoangcau.edu.vn', 'thanhtt14', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Thanh', 'Trần Thị', 'TEACHER', TRUE, 'ACTIVED', NULL, NOW()),
                                                                                                                         ('lequangnam@hoangcau.edu.vn', 'namlq15', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Nam', 'Lê Quang', 'TEACHER', TRUE, 'ACTIVED', NULL, NOW()),
                                                                                                                         ('phamngocmai@hoangcau.edu.vn', 'maipn16', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Mai', 'Phạm Ngọc', 'TEACHER', TRUE, 'ACTIVED', NULL, NOW()),
                                                                                                                         ('hoangphuongchi@hoangcau.edu.vn', 'chihp17', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Chi', 'Hoàng Phương', 'TEACHER', TRUE, 'ACTIVED', NULL, NOW()),
                                                                                                                         ('vuvanduc@hoangcau.edu.vn', 'ducvv18', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Đức', 'Vũ Văn', 'TEACHER', TRUE, 'ACTIVED', NULL, NOW()),
                                                                                                                         ('nguyenthihuyen@hoangcau.edu.vn', 'huyennt19', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Huyền', 'Nguyễn Thị', 'TEACHER', TRUE, 'ACTIVED', NULL, NOW()),
                                                                                                                         ('dangsyminh@hoangcau.edu.vn', 'minhds20', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Minh', 'Đặng Sỹ', 'TEACHER', TRUE, 'ACTIVED', NULL, NOW()),
                                                                                                                         ('doanthidiem@hoangcau.edu.vn', 'diemdt21', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Diễm', 'Đoàn Thị', 'TEACHER', TRUE, 'ACTIVED', NULL, NOW()),
                                                                                                                         ('nguyenquoclong@hoangcau.edu.vn', 'longnq22', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Long', 'Nguyễn Quốc', 'TEACHER', TRUE, 'ACTIVED', NULL, NOW());

-- =========================================================
-- HỌC SINH (400 Người)
-- =========================================================
INSERT INTO users (email, username, password, first_name, last_name, role, is_active, status, class_id, create_date) VALUES
                                                                                                                         ('nguyenvanan_10a1@hoangcau.edu.vn', 'annv3', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'An', 'Nguyễn Văn', 'STUDENT', TRUE, 'ACTIVED', 1, NOW()),
                                                                                                                         ('tranthibao_10a1@hoangcau.edu.vn', 'baott4', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Bảo', 'Trần Thị', 'STUDENT', TRUE, 'ACTIVED', 1, NOW()),
                                                                                                                         ('leminhchi_10a1@hoangcau.edu.vn', 'chilm5', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Chi', 'Lê Minh', 'STUDENT', TRUE, 'ACTIVED', 1, NOW()),
                                                                                                                         ('phamthuydung_10a1@hoangcau.edu.vn', 'dungpt6', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Dung', 'Phạm Thùy', 'STUDENT', TRUE, 'ACTIVED', 1, NOW()),
                                                                                                                         ('hoanggianghai_10a1@hoangcau.edu.vn', 'haihg7', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Hải', 'Hoàng Giang', 'STUDENT', TRUE, 'ACTIVED', 1, NOW()),
                                                                                                                         ('vuthikhanhhuyen_10a1@hoangcau.edu.vn', 'huyenvtk8', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Huyền', 'Vũ Thị Khánh', 'STUDENT', TRUE, 'ACTIVED', 1, NOW()),
                                                                                                                         ('phanngoclong_10a1@hoangcau.edu.vn', 'longpn9', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Long', 'Phan Ngọc', 'STUDENT', TRUE, 'ACTIVED', 1, NOW()),
                                                                                                                         ('dangnguyennam_10a1@hoangcau.edu.vn', 'namdn10', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Nam', 'Đặng Nguyên', 'STUDENT', TRUE, 'ACTIVED', 1, NOW()),
                                                                                                                         ('buithanhphong_10a1@hoangcau.edu.vn', 'phongbt11', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Phong', 'Bùi Thanh', 'STUDENT', TRUE, 'ACTIVED', 1, NOW()),
                                                                                                                         ('nguyenthibichquyen_10a1@hoangcau.edu.vn', 'quyentntb12', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Quyên', 'Nguyễn Thị Bích', 'STUDENT', TRUE, 'ACTIVED', 1, NOW()),
                                                                                                                         ('tranhoangson_10a1@hoangcau.edu.vn', 'santh13', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Sơn', 'Trần Hoàng', 'STUDENT', TRUE, 'ACTIVED', 1, NOW()),
                                                                                                                         ('lethutrang_10a1@hoangcau.edu.vn', 'tranglt14', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Trang', 'Lê Thu', 'STUDENT', TRUE, 'ACTIVED', 1, NOW()),
                                                                                                                         ('phamvietthanh_10a1@hoangcau.edu.vn', 'thanhpv15', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Thành', 'Phạm Việt', 'STUDENT', TRUE, 'ACTIVED', 1, NOW()),
                                                                                                                         ('hoangminhtu_10a1@hoangcau.edu.vn', 'tuhm16', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Tú', 'Hoàng Minh', 'STUDENT', TRUE, 'ACTIVED', 1, NOW()),
                                                                                                                         ('vumanhquyen_10a1@hoangcau.edu.vn', 'quyenvm17', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Quyền', 'Vũ Mạnh', 'STUDENT', TRUE, 'ACTIVED', 1, NOW()),
                                                                                                                         ('nguyenphuonguyet_10a1@hoangcau.edu.vn', 'nguyetnp18', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Nguyệt', 'Nguyễn Phương', 'STUDENT', TRUE, 'ACTIVED', 1, NOW()),
                                                                                                                         ('trantuankiet_10a1@hoangcau.edu.vn', 'kiettt19', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Kiệt', 'Trần Tuấn', 'STUDENT', TRUE, 'ACTIVED', 1, NOW()),
                                                                                                                         ('lethithuy_10a1@hoangcau.edu.vn', 'thuytl20', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Thủy', 'Lê Thị', 'STUDENT', TRUE, 'ACTIVED', 1, NOW()),
                                                                                                                         ('phamminhhoang_10a1@hoangcau.edu.vn', 'hoangpm21', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Hoàng', 'Phạm Minh', 'STUDENT', TRUE, 'ACTIVED', 1, NOW()),
                                                                                                                         ('hoangngocdiep_10a1@hoangcau.edu.vn', 'diephn22', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Diệp', 'Hoàng Ngọc', 'STUDENT', TRUE, 'ACTIVED', 1, NOW()),
                                                                                                                         ('vutrongphuc_10a1@hoangcau.edu.vn', 'phucvt23', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Phúc', 'Vũ Trọng', 'STUDENT', TRUE, 'ACTIVED', 1, NOW()),
                                                                                                                         ('nguyenkhanhvy_10a1@hoangcau.edu.vn', 'vynk24', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Vy', 'Nguyễn Khánh', 'STUDENT', TRUE, 'ACTIVED', 1, NOW()),
                                                                                                                         ('trananhkhoi_10a1@hoangcau.edu.vn', 'khoita25', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Khôi', 'Trần Anh', 'STUDENT', TRUE, 'ACTIVED', 1, NOW()),
                                                                                                                         ('lethiminh_10a1@hoangcau.edu.vn', 'minhlt26', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Minh', 'Lê Thị', 'STUDENT', TRUE, 'ACTIVED', 1, NOW()),
                                                                                                                         ('phamducdat_10a1@hoangcau.edu.vn', 'datpd27', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Đạt', 'Phạm Đức', 'STUDENT', TRUE, 'ACTIVED', 1, NOW()),
                                                                                                                         ('hoangbaolinh_10a1@hoangcau.edu.vn', 'linhhb28', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Linh', 'Hoàng Bảo', 'STUDENT', TRUE, 'ACTIVED', 1, NOW()),
                                                                                                                         ('vumanhcuong_10a1@hoangcau.edu.vn', 'cuongvm29', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Cường', 'Vũ Mạnh', 'STUDENT', TRUE, 'ACTIVED', 1, NOW()),

-- Lớp 10A2 (class_id = 2)
                                                                                                                         ('nguyenngocanh_10a2@hoangcau.edu.vn', 'anhnn30', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Anh', 'Nguyễn Ngọc', 'STUDENT', TRUE, 'ACTIVED', 2, NOW()),
                                                                                                                         ('tranvietbach_10a2@hoangcau.edu.vn', 'bachtv31', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Bách', 'Trần Việt', 'STUDENT', TRUE, 'ACTIVED', 2, NOW()),
                                                                                                                         ('lebaochau_10a2@hoangcau.edu.vn', 'chaulb32', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Châu', 'Lê Bảo', 'STUDENT', TRUE, 'ACTIVED', 2, NOW()),
                                                                                                                         ('phamtiendung_10a2@hoangcau.edu.vn', 'dungpt33', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Dũng', 'Phạm Tiến', 'STUDENT', TRUE, 'ACTIVED', 2, NOW()),
                                                                                                                         ('hoangthuha_10a2@hoangcau.edu.vn', 'haht34', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Hà', 'Hoàng Thu', 'STUDENT', TRUE, 'ACTIVED', 2, NOW()),
                                                                                                                         ('vuxuanhiep_10a2@hoangcau.edu.vn', 'hiepvx35', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Hiệp', 'Vũ Xuân', 'STUDENT', TRUE, 'ACTIVED', 2, NOW()),
                                                                                                                         ('phanthidieuhuyen_10a2@hoangcau.edu.vn', 'huyenptd36', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Huyền', 'Phan Thị Diệu', 'STUDENT', TRUE, 'ACTIVED', 2, NOW()),
                                                                                                                         ('dangquanglam_10a2@hoangcau.edu.vn', 'lamdq37', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Lâm', 'Đặng Quang', 'STUDENT', TRUE, 'ACTIVED', 2, NOW()),
                                                                                                                         ('buicongminh_10a2@hoangcau.edu.vn', 'minhbc38', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Minh', 'Bùi Công', 'STUDENT', TRUE, 'ACTIVED', 2, NOW()),
                                                                                                                         ('nguyenkhanhnam_10a2@hoangcau.edu.vn', 'namnk39', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Nam', 'Nguyễn Khánh', 'STUDENT', TRUE, 'ACTIVED', 2, NOW()),
                                                                                                                         ('tranbichngoc_10a2@hoangcau.edu.vn', 'ngoctb40', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Ngọc', 'Trần Bích', 'STUDENT', TRUE, 'ACTIVED', 2, NOW()),
                                                                                                                         ('lehoaiphong_10a2@hoangcau.edu.vn', 'phonglh41', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Phong', 'Lê Hoài', 'STUDENT', TRUE, 'ACTIVED', 2, NOW()),
                                                                                                                         ('phamsuongquynh_10a2@hoangcau.edu.vn', 'quynhps42', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Quỳnh', 'Phạm Sương', 'STUDENT', TRUE, 'ACTIVED', 2, NOW()),
                                                                                                                         ('hoangthanhson_10a2@hoangcau.edu.vn', 'sonht43', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Sơn', 'Hoàng Thanh', 'STUDENT', TRUE, 'ACTIVED', 2, NOW()),
                                                                                                                         ('vuminhthach_10a2@hoangcau.edu.vn', 'thachvm44', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Thạch', 'Vũ Minh', 'STUDENT', TRUE, 'ACTIVED', 2, NOW()),
                                                                                                                         ('nguyenthaotrang_10a2@hoangcau.edu.vn', 'trangnt45', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Trang', 'Nguyễn Thảo', 'STUDENT', TRUE, 'ACTIVED', 2, NOW()),
                                                                                                                         ('tranquocvinh_10a2@hoangcau.edu.vn', 'vinhtv46', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Vinh', 'Trần Quốc', 'STUDENT', TRUE, 'ACTIVED', 2, NOW()),
                                                                                                                         ('lehoangyen_10a2@hoangcau.edu.vn', 'yenlh47', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Yến', 'Lê Hoàng', 'STUDENT', TRUE, 'ACTIVED', 2, NOW()),
                                                                                                                         ('phamphuocduc_10a2@hoangcau.edu.vn', 'ducpp48', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Đức', 'Phạm Phước', 'STUDENT', TRUE, 'ACTIVED', 2, NOW()),
                                                                                                                         ('hoangthithuy_10a2@hoangcau.edu.vn', 'thuyht49', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Thủy', 'Hoàng Thị', 'STUDENT', TRUE, 'ACTIVED', 2, NOW()),
                                                                                                                         ('vuthanhlong_10a2@hoangcau.edu.vn', 'longvt50', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Long', 'Vũ Thanh', 'STUDENT', TRUE, 'ACTIVED', 2, NOW()),
                                                                                                                         ('nguyenductuan_10a2@hoangcau.edu.vn', 'tuannd51', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Tuấn', 'Nguyễn Đức', 'STUDENT', TRUE, 'ACTIVED', 2, NOW()),
                                                                                                                         ('tranthingoc_10a2@hoangcau.edu.vn', 'ngoctt52', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Ngọc', 'Trần Thị', 'STUDENT', TRUE, 'ACTIVED', 2, NOW()),
                                                                                                                         ('lequangtrung_10a2@hoangcau.edu.vn', 'trunglq53', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Trung', 'Lê Quang', 'STUDENT', TRUE, 'ACTIVED', 2, NOW()),
                                                                                                                         ('phamcamly_10a2@hoangcau.edu.vn', 'lypc54', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Ly', 'Phạm Cẩm', 'STUDENT', TRUE, 'ACTIVED', 2, NOW()),
                                                                                                                         ('hoangminhquan_10a2@hoangcau.edu.vn', 'quanhm55', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Quân', 'Hoàng Minh', 'STUDENT', TRUE, 'ACTIVED', 2, NOW()),
                                                                                                                         ('vubaolinh_10a2@hoangcau.edu.vn', 'linhvb56', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Linh', 'Vũ Bảo', 'STUDENT', TRUE, 'ACTIVED', 2, NOW()),

-- Lớp 10A3 (class_id = 3)
                                                                                                                         ('nguyentuananh_10a3@hoangcau.edu.vn', 'anhnt57', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Anh', 'Nguyễn Tuấn', 'STUDENT', TRUE, 'ACTIVED', 3, NOW()),
                                                                                                                         ('tranmanhcuong_10a3@hoangcau.edu.vn', 'cuongtm58', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Cường', 'Trần Xiu', 'STUDENT', TRUE, 'ACTIVED', 3, NOW()),
                                                                                                                         ('lethidieu_10a3@hoangcau.edu.vn', 'dieult59', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Diệu', 'Lê Thị', 'STUDENT', TRUE, 'ACTIVED', 3, NOW()),
                                                                                                                         ('phamquanghai_10a3@hoangcau.edu.vn', 'haipq60', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Hải', 'Phạm Quang', 'STUDENT', TRUE, 'ACTIVED', 3, NOW()),
                                                                                                                         ('hoangmaihoa_10a3@hoangcau.edu.vn', 'hoahm61', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Hoa', 'Hoàng Mai', 'STUDENT', TRUE, 'ACTIVED', 3, NOW()),
                                                                                                                         ('vungochuy_10a3@hoangcau.edu.vn', 'huyvn62', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Huy', 'Vũ Ngọc', 'STUDENT', TRUE, 'ACTIVED', 3, NOW()),
                                                                                                                         ('phanthilan_10a3@hoangcau.edu.vn', 'lanpt63', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Lan', 'Phan Thị', 'STUDENT', TRUE, 'ACTIVED', 3, NOW()),
                                                                                                                         ('dangbaolong_10a3@hoangcau.edu.vn', 'longdb64', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Long', 'Đặng Bảo', 'STUDENT', TRUE, 'ACTIVED', 3, NOW()),
                                                                                                                         ('buixuanmanh_10a3@hoangcau.edu.vn', 'manhbx65', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Mạnh', 'Bùi Xuân', 'STUDENT', TRUE, 'ACTIVED', 3, NOW()),
                                                                                                                         ('nguyenthanhnam_10a3@hoangcau.edu.vn', 'namnt66', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Nam', 'Nguyễn Thanh', 'STUDENT', TRUE, 'ACTIVED', 3, NOW()),
                                                                                                                         ('tranthinhu_10a3@hoangcau.edu.vn', 'nhutt67', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Như', 'Trần Thị', 'STUDENT', TRUE, 'ACTIVED', 3, NOW()),
                                                                                                                         ('lehoangphuc_10a3@hoangcau.edu.vn', 'phuclh68', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Phúc', 'Lê Hoàng', 'STUDENT', TRUE, 'ACTIVED', 3, NOW()),
                                                                                                                         ('phamthiquynh_10a3@hoangcau.edu.vn', 'quynhpt69', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Quỳnh', 'Phạm Thị', 'STUDENT', TRUE, 'ACTIVED', 3, NOW()),
                                                                                                                         ('hoangducson_10a3@hoangcau.edu.vn', 'sonhd70', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Sơn', 'Hoàng Đức', 'STUDENT', TRUE, 'ACTIVED', 3, NOW()),
                                                                                                                         ('vuthutrang_10a3@hoangcau.edu.vn', 'trangvt71', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Trang', 'Vũ Thu', 'STUDENT', TRUE, 'ACTIVED', 3, NOW()),
                                                                                                                         ('nguyenxuanvinh_10a3@hoangcau.edu.vn', 'vinhnx72', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Vinh', 'Nguyễn Xuân', 'STUDENT', TRUE, 'ACTIVED', 3, NOW()),
                                                                                                                         ('tranthuynhi_10a3@hoangcau.edu.vn', 'nhitt73', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Nhi', 'Trần Thúy', 'STUDENT', TRUE, 'ACTIVED', 3, NOW()),
                                                                                                                         ('leminhhoang_10a3@hoangcau.edu.vn', 'hoanglm74', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Hoàng', 'Lê Minh', 'STUDENT', TRUE, 'ACTIVED', 3, NOW()),
                                                                                                                         ('phamvandu_10a3@hoangcau.edu.vn', 'dupv75', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Du', 'Phạm Văn', 'STUDENT', TRUE, 'ACTIVED', 3, NOW()),
                                                                                                                         ('hoangkimvy_10a3@hoangcau.edu.vn', 'vyhk76', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Vy', 'Hoàng Kim', 'STUDENT', TRUE, 'ACTIVED', 3, NOW()),
                                                                                                                         ('vuhuuphuc_10a3@hoangcau.edu.vn', 'phuchv77', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Phúc', 'Vũ Hữu', 'STUDENT', TRUE, 'ACTIVED', 3, NOW()),
                                                                                                                         ('nguyenbaolong_10a3@hoangcau.edu.vn', 'longnb78', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Long', 'Nguyễn Bảo', 'STUDENT', TRUE, 'ACTIVED', 3, NOW()),
                                                                                                                         ('tranquangdat_10a3@hoangcau.edu.vn', 'dattq79', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Đạt', 'Trần Quang', 'STUDENT', TRUE, 'ACTIVED', 3, NOW()),
                                                                                                                         ('lephuonglinh_10a3@hoangcau.edu.vn', 'linhlp80', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Linh', 'Lê Phương', 'STUDENT', TRUE, 'ACTIVED', 3, NOW()),
                                                                                                                         ('phamquocuy_10a3@hoangcau.edu.vn', 'uypq81', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Uy', 'Phạm Quốc', 'STUDENT', TRUE, 'ACTIVED', 3, NOW()),
                                                                                                                         ('hoangdinhtien_10a3@hoangcau.edu.vn', 'tienhd82', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Tiến', 'Hoàng Đình', 'STUDENT', TRUE, 'ACTIVED', 3, NOW()),
                                                                                                                         ('vuthihoa_10a3@hoangcau.edu.vn', 'hoavt83', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Hoa', 'Vũ Thị', 'STUDENT', TRUE, 'ACTIVED', 3, NOW()),
-- Lớp 10A4 (class_id = 4)
                                                                                                                         ('nguyenhoangnam_10a4@hoangcau.edu.vn', 'namnh84', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Nam', 'Nguyễn Hoàng', 'STUDENT', TRUE, 'ACTIVED', 4, NOW()),
                                                                                                                         ('tranngoclinh_10a4@hoangcau.edu.vn', 'linhtn85', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Linh', 'Trần Ngọc', 'STUDENT', TRUE, 'ACTIVED', 4, NOW()),
                                                                                                                         ('lethuthao_10a4@hoangcau.edu.vn', 'thaolt86', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Thảo', 'Lê Thu', 'STUDENT', TRUE, 'ACTIVED', 4, NOW()),
                                                                                                                         ('phamnguyenbao_10a4@hoangcau.edu.vn', 'baopn87', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Bảo', 'Phạm Nguyên', 'STUDENT', TRUE, 'ACTIVED', 4, NOW()),
                                                                                                                         ('hoangtronghieu_10a4@hoangcau.edu.vn', 'hieuth88', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Hiếu', 'Hoàng Trọng', 'STUDENT', TRUE, 'ACTIVED', 4, NOW()),
                                                                                                                         ('vuleuyen_10a4@hoangcau.edu.vn', 'uyenvl89', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Uyên', 'Vũ Lê', 'STUDENT', TRUE, 'ACTIVED', 4, NOW()),
                                                                                                                         ('phanthuyvi_10a4@hoangcau.edu.vn', 'vypt90', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Vy', 'Phan Thúy', 'STUDENT', TRUE, 'ACTIVED', 4, NOW()),
                                                                                                                         ('danggiabao_10a4@hoangcau.edu.vn', 'baodg91', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Bảo', 'Đặng Gia', 'STUDENT', TRUE, 'ACTIVED', 4, NOW()),
                                                                                                                         ('buiminhnhat_10a4@hoangcau.edu.vn', 'nhatbm92', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Nhật', 'Bùi Minh', 'STUDENT', TRUE, 'ACTIVED', 4, NOW()),
                                                                                                                         ('nguyenthanhlam_10a4@hoangcau.edu.vn', 'lamnt93', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Lâm', 'Nguyễn Thanh', 'STUDENT', TRUE, 'ACTIVED', 4, NOW()),
                                                                                                                         ('trananhkhoa_10a4@hoangcau.edu.vn', 'khoata94', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Khoa', 'Trần Anh', 'STUDENT', TRUE, 'ACTIVED', 4, NOW()),
                                                                                                                         ('lehoangtuan_10a4@hoangcau.edu.vn', 'tuanlh95', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Tuấn', 'Lê Hoàng', 'STUDENT', TRUE, 'ACTIVED', 4, NOW()),
                                                                                                                         ('phamphuongtrinh_10a4@hoangcau.edu.vn', 'trinhpp96', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Trinh', 'Phạm Phương', 'STUDENT', TRUE, 'ACTIVED', 4, NOW()),
                                                                                                                         ('hoangminhdat_10a4@hoangcau.edu.vn', 'dathm97', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Đạt', 'Hoàng Minh', 'STUDENT', TRUE, 'ACTIVED', 4, NOW()),
                                                                                                                         ('vuduybinh_10a4@hoangcau.edu.vn', 'binhvd98', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Bình', 'Vũ Duy', 'STUDENT', TRUE, 'ACTIVED', 4, NOW()),
                                                                                                                         ('nguyenquynhchi_10a4@hoangcau.edu.vn', 'chinqu99', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Chi', 'Nguyễn Quỳnh', 'STUDENT', TRUE, 'ACTIVED', 4, NOW()),
                                                                                                                         ('tranhuuquan_10a4@hoangcau.edu.vn', 'quanth100', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Quân', 'Trần Hữu', 'STUDENT', TRUE, 'ACTIVED', 4, NOW()),
                                                                                                                         ('lethihong_10a4@hoangcau.edu.vn', 'honglt101', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Hồng', 'Lê Thị', 'STUDENT', TRUE, 'ACTIVED', 4, NOW()),
                                                                                                                         ('phamhoanglong_10a4@hoangcau.edu.vn', 'longph102', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Long', 'Phạm Hoàng', 'STUDENT', TRUE, 'ACTIVED', 4, NOW()),
                                                                                                                         ('hoangbaongoc_10a4@hoangcau.edu.vn', 'ngochb103', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Ngọc', 'Hoàng Bảo', 'STUDENT', TRUE, 'ACTIVED', 4, NOW()),
                                                                                                                         ('vutuanphong_10a4@hoangcau.edu.vn', 'phongvt104', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Phong', 'Vũ Tuấn', 'STUDENT', TRUE, 'ACTIVED', 4, NOW()),
                                                                                                                         ('nguyenthanhvy_10a4@hoangcau.edu.vn', 'vynt105', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Vy', 'Nguyễn Thanh', 'STUDENT', TRUE, 'ACTIVED', 4, NOW()),
                                                                                                                         ('tranvietkhoi_10a4@hoangcau.edu.vn', 'khoitv106', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Khôi', 'Trần Việt', 'STUDENT', TRUE, 'ACTIVED', 4, NOW()),
                                                                                                                         ('lethiquyet_10a4@hoangcau.edu.vn', 'quyetlt107', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Quyết', 'Lê Thị', 'STUDENT', TRUE, 'ACTIVED', 4, NOW()),
                                                                                                                         ('phamvanduong_10a4@hoangcau.edu.vn', 'duongpv108', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Dương', 'Phạm Văn', 'STUDENT', TRUE, 'ACTIVED', 4, NOW()),
                                                                                                                         ('hoangthuyvy_10a4@hoangcau.edu.vn', 'vyht109', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Vy', 'Hoàng Thủy', 'STUDENT', TRUE, 'ACTIVED', 4, NOW()),
                                                                                                                         ('vuxuandung_10a4@hoangcau.edu.vn', 'dungvx110', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Dũng', 'Vũ Xuân', 'STUDENT', TRUE, 'ACTIVED', 4, NOW()),
-- Lớp 10A5 (class_id = 5)
                                                                                                                         ('nguyenthanhan_10a5@hoangcau.edu.vn', 'annt111', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'An', 'Nguyễn Thành', 'STUDENT', TRUE, 'ACTIVED', 5, NOW()),
                                                                                                                         ('tranthibich_10a5@hoangcau.edu.vn', 'bichtt112', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Bích', 'Trần Thị', 'STUDENT', TRUE, 'ACTIVED', 5, NOW()),
                                                                                                                         ('lequangcuong_10a5@hoangcau.edu.vn', 'cuonglq113', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Cường', 'Lê Quang', 'STUDENT', TRUE, 'ACTIVED', 5, NOW()),
                                                                                                                         ('phamngocdung_10a5@hoangcau.edu.vn', 'dungpn114', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Dung', 'Phạm Ngọc', 'STUDENT', TRUE, 'ACTIVED', 5, NOW()),
                                                                                                                         ('hoangtienhai_10a5@hoangcau.edu.vn', 'haiht115', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDgtxb.ne', 'Hải', 'Hoàng Tiến', 'STUDENT', TRUE, 'ACTIVED', 5, NOW()),
                                                                                                                         ('vuthihoai_10a5@hoangcau.edu.vn', 'hoaivt116', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Hoài', 'Vũ Thị', 'STUDENT', TRUE, 'ACTIVED', 5, NOW()),
                                                                                                                         ('phankhanhlinh_10a5@hoangcau.edu.vn', 'linhpk117', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Linh', 'Phan Khánh', 'STUDENT', TRUE, 'ACTIVED', 5, NOW()),
                                                                                                                         ('dangminhmanh_10a5@hoangcau.edu.vn', 'manhdm118', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Mạnh', 'Đặng Minh', 'STUDENT', TRUE, 'ACTIVED', 5, NOW()),
                                                                                                                         ('buivannam_10a5@hoangcau.edu.vn', 'nambv119', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Nam', 'Bùi Văn', 'STUDENT', TRUE, 'ACTIVED', 5, NOW()),
                                                                                                                         ('nguyenthibichngoc_10a5@hoangcau.edu.vn', 'ngocntb120', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Ngọc', 'Nguyễn Thị Bích', 'STUDENT', TRUE, 'ACTIVED', 5, NOW()),
                                                                                                                         ('tranvanphong_10a5@hoangcau.edu.vn', 'phongtv121', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Phong', 'Trần Văn', 'STUDENT', TRUE, 'ACTIVED', 5, NOW()),
                                                                                                                         ('lekimquyen_10a5@hoangcau.edu.vn', 'quyenlk122', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Quyên', 'Lê Kim', 'STUDENT', TRUE, 'ACTIVED', 5, NOW()),
                                                                                                                         ('phamthai_10a5@hoangcau.edu.vn', 'thaip123', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Thái', 'Phạm', 'STUDENT', TRUE, 'ACTIVED', 5, NOW()),
                                                                                                                         ('hoangminhtrung_10a5@hoangcau.edu.vn', 'trunghm124', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Trung', 'Hoàng Minh', 'STUDENT', TRUE, 'ACTIVED', 5, NOW()),
                                                                                                                         ('vutuyet_10a5@hoangcau.edu.vn', 'tuyetv125', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Tuyết', 'Vũ', 'STUDENT', TRUE, 'ACTIVED', 5, NOW()),
                                                                                                                         ('nguyenquocuy_10a5@hoangcau.edu.vn', 'uynq126', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Uy', 'Nguyễn Quốc', 'STUDENT', TRUE, 'ACTIVED', 5, NOW()),
                                                                                                                         ('tranvinh_10a5@hoangcau.edu.vn', 'vinht127', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Vinh', 'Trần', 'STUDENT', TRUE, 'ACTIVED', 5, NOW()),
                                                                                                                         ('lephuongthao_10a5@hoangcau.edu.vn', 'thaolp128', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Thảo', 'Lê Phương', 'STUDENT', TRUE, 'ACTIVED', 5, NOW()),
                                                                                                                         ('phamduyanh_10a5@hoangcau.edu.vn', 'anhpd129', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Anh', 'Phạm Duy', 'STUDENT', TRUE, 'ACTIVED', 5, NOW()),
                                                                                                                         ('hoangthibao_10a5@hoangcau.edu.vn', 'baoht130', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Bảo', 'Hoàng Thị', 'STUDENT', TRUE, 'ACTIVED', 5, NOW()),
                                                                                                                         ('vutuancuong_10a5@hoangcau.edu.vn', 'cuongvt131', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Cường', 'Vũ Tuấn', 'STUDENT', TRUE, 'ACTIVED', 5, NOW()),
                                                                                                                         ('nguyenngocdiep_10a5@hoangcau.edu.vn', 'diepnn132', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Diep', 'Nguyễn Ngọc', 'STUDENT', TRUE, 'ACTIVED', 5, NOW()),
                                                                                                                         ('tranhoangdat_10a5@hoangcau.edu.vn', 'datth133', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Đạt', 'Trần Hoàng', 'STUDENT', TRUE, 'ACTIVED', 5, NOW()),
                                                                                                                         ('lethithanh_10a5@hoangcau.edu.vn', 'thanhlt134', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Thanh', 'Lê Thị', 'STUDENT', TRUE, 'ACTIVED', 5, NOW()),
                                                                                                                         ('phamvanlinh_10a5@hoangcau.edu.vn', 'linhpv135', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Linh', 'Phạm Văn', 'STUDENT', TRUE, 'ACTIVED', 5, NOW()),
                                                                                                                         ('hoangminhquan_10a5@hoangcau.edu.vn', 'quanhm136', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Quân', 'Hoàng Minh', 'STUDENT', TRUE, 'ACTIVED', 5, NOW()),
-- Lớp 11A1 (class_id = 6)
                                                                                                                         ('nguyenbich_11a1@hoangcau.edu.vn', 'bichn137', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Bích', 'Nguyễn', 'STUDENT', TRUE, 'ACTIVED', 6, NOW()),
                                                                                                                         ('tranlong_11a1@hoangcau.edu.vn', 'longt138', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Long', 'Trần', 'STUDENT', TRUE, 'ACTIVED', 6, NOW()),
                                                                                                                         ('lephuc_11a1@hoangcau.edu.vn', 'phucl139', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Phúc', 'Lê', 'STUDENT', TRUE, 'ACTIVED', 6, NOW()),
                                                                                                                         ('phamvy_11a1@hoangcau.edu.vn', 'vyp1140', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Vy', 'Phạm', 'STUDENT', TRUE, 'ACTIVED', 6, NOW()),
                                                                                                                         ('hoangyen_11a1@hoangcau.edu.vn', 'yenh141', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Yến', 'Hoàng', 'STUDENT', TRUE, 'ACTIVED', 6, NOW()),
                                                                                                                         ('vuanh_11a1@hoangcau.edu.vn', 'anhv142', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Anh', 'Vũ', 'STUDENT', TRUE, 'ACTIVED', 6, NOW()),
                                                                                                                         ('phanlinh_11a1@hoangcau.edu.vn', 'linhp143', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Linh', 'Phan', 'STUDENT', TRUE, 'ACTIVED', 6, NOW()),
                                                                                                                         ('dangtrang_11a1@hoangcau.edu.vn', 'trangd144', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Trang', 'Đặng', 'STUDENT', TRUE, 'ACTIVED', 6, NOW()),
                                                                                                                         ('buithanh_11a1@hoangcau.edu.vn', 'thanhb145', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Thành', 'Bùi', 'STUDENT', TRUE, 'ACTIVED', 6, NOW()),
                                                                                                                         ('nguyenbaolinh_11a1@hoangcau.edu.vn', 'linhnb146', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Linh', 'Nguyễn Bảo', 'STUDENT', TRUE, 'ACTIVED', 6, NOW()),
                                                                                                                         ('tranhoanglong_11a1@hoangcau.edu.vn', 'longth147', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Long', 'Trần Hoàng', 'STUDENT', TRUE, 'ACTIVED', 6, NOW()),
                                                                                                                         ('leminhtan_11a1@hoangcau.edu.vn', 'tanlm148', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Tân', 'Lê Minh', 'STUDENT', TRUE, 'ACTIVED', 6, NOW()),
                                                                                                                         ('phamphuongchi_11a1@hoangcau.edu.vn', 'chipm149', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Chi', 'Phạm Phương', 'STUDENT', TRUE, 'ACTIVED', 6, NOW()),
                                                                                                                         ('hoangminhtuan_11a1@hoangcau.edu.vn', 'tuanhm150', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Tuấn', 'Hoàng Minh', 'STUDENT', TRUE, 'ACTIVED', 6, NOW()),
                                                                                                                         ('vuxuanduc_11a1@hoangcau.edu.vn', 'ducvx151', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Đức', 'Vũ Xuân', 'STUDENT', TRUE, 'ACTIVED', 6, NOW()),
                                                                                                                         ('nguyenthithao_11a1@hoangcau.edu.vn', 'thaont152', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Thảo', 'Nguyễn Thị', 'STUDENT', TRUE, 'ACTIVED', 6, NOW()),
                                                                                                                         ('trantuandung_11a1@hoangcau.edu.vn', 'dungtt153', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Dũng', 'Trần Tuấn', 'STUDENT', TRUE, 'ACTIVED', 6, NOW()),
                                                                                                                         ('lephuongtrinh_11a1@hoangcau.edu.vn', 'trinhlp154', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Trinh', 'Lê Phương', 'STUDENT', TRUE, 'ACTIVED', 6, NOW()),
                                                                                                                         ('phamducphuc_11a1@hoangcau.edu.vn', 'phucpd155', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Phúc', 'Phạm Đức', 'STUDENT', TRUE, 'ACTIVED', 6, NOW()),
                                                                                                                         ('hoangcamtu_11a1@hoangcau.edu.vn', 'tuhc156', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Tú', 'Hoàng Cẩm', 'STUDENT', TRUE, 'ACTIVED', 6, NOW()),
                                                                                                                         ('vutrongnghia_11a1@hoangcau.edu.vn', 'nghiavt157', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Nghĩa', 'Vũ Trọng', 'STUDENT', TRUE, 'ACTIVED', 6, NOW()),
                                                                                                                         ('nguyenmaihuong_11a1@hoangcau.edu.vn', 'huongnm158', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Hương', 'Nguyễn Mai', 'STUDENT', TRUE, 'ACTIVED', 6, NOW()),
                                                                                                                         ('trankhanhly_11a1@hoangcau.edu.vn', 'lytk159', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Ly', 'Trần Khánh', 'STUDENT', TRUE, 'ACTIVED', 6, NOW()),
                                                                                                                         ('lequocbinh_11a1@hoangcau.edu.vn', 'binhlq160', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Bình', 'Lê Quốc', 'STUDENT', TRUE, 'ACTIVED', 6, NOW()),
                                                                                                                         ('phamthingoc_11a1@hoangcau.edu.vn', 'ngocpt161', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Ngọc', 'Phạm Thị', 'STUDENT', TRUE, 'ACTIVED', 6, NOW()),
                                                                                                                         ('hoangthaovy_11a1@hoangcau.edu.vn', 'vyht162', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Vy', 'Hoàng Thảo', 'STUDENT', TRUE, 'ACTIVED', 6, NOW()),

-- Lớp 11A2 (class_id = 7)
                                                                                                                         ('nguyenhoanganh_11a2@hoangcau.edu.vn', 'anhnh163', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Anh', 'Nguyễn Hoàng', 'STUDENT', TRUE, 'ACTIVED', 7, NOW()),
                                                                                                                         ('tranminhchien_11a2@hoangcau.edu.vn', 'chientm164', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Chien', 'Trần Minh', 'STUDENT', TRUE, 'ACTIVED', 7, NOW()),
                                                                                                                         ('lethaiha_11a2@hoangcau.edu.vn', 'halt165', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Hà', 'Lê Thái', 'STUDENT', TRUE, 'ACTIVED', 7, NOW()),
                                                                                                                         ('phamthuymie_11a2@hoangcau.edu.vn', 'miept166', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Mie', 'Phạm Thúy', 'STUDENT', TRUE, 'ACTIVED', 7, NOW()),
                                                                                                                         ('hoangxuannam_11a2@hoangcau.edu.vn', 'namhx167', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Nam', 'Hoàng Xuân', 'STUDENT', TRUE, 'ACTIVED', 7, NOW()),
                                                                                                                         ('vuthiquynh_11a2@hoangcau.edu.vn', 'quynhvt168', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Quỳnh', 'Vũ Thị', 'STUDENT', TRUE, 'ACTIVED', 7, NOW()),
                                                                                                                         ('phanquocvinh_11a2@hoangcau.edu.vn', 'vinhpq169', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Vinh', 'Phan Quốc', 'STUDENT', TRUE, 'ACTIVED', 7, NOW()),
                                                                                                                         ('dangthingoc_11a2@hoangcau.edu.vn', 'ngocdt170', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Ngọc', 'Đặng Thị', 'STUDENT', TRUE, 'ACTIVED', 7, NOW()),
                                                                                                                         ('buivietdung_11a2@hoangcau.edu.vn', 'dungbv171', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Dũng', 'Bùi Việt', 'STUDENT', TRUE, 'ACTIVED', 7, NOW()),
                                                                                                                         ('nguyenbaotrong_11a2@hoangcau.edu.vn', 'trongnb172', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Trọng', 'Nguyễn Bảo', 'STUDENT', TRUE, 'ACTIVED', 7, NOW()),
                                                                                                                         ('tranthanhphương_11a2@hoangcau.edu.vn', 'phươngtt173', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Phương', 'Trần Thanh', 'STUDENT', TRUE, 'ACTIVED', 7, NOW()),
                                                                                                                         ('leducquang_11a2@hoangcau.edu.vn', 'quangld174', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Quang', 'Lê Đức', 'STUDENT', TRUE, 'ACTIVED', 7, NOW()),
                                                                                                                         ('phamhuonggiang_12a4@hoangcau.edu.vn', 'giangph175', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Giang', 'Phạm Hương', 'STUDENT', TRUE, 'ACTIVED', 7, NOW()),
                                                                                                                         ('hoangminhkhai_11a2@hoangcau.edu.vn', 'khaihm176', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Khai', 'Hoàng Minh', 'STUDENT', TRUE, 'ACTIVED', 7, NOW()),
                                                                                                                         ('vutuongvi_11a2@hoangcau.edu.vn', 'vyvt177', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Vy', 'Vũ Tường', 'STUDENT', TRUE, 'ACTIVED', 7, NOW()),
                                                                                                                         ('nguyenlamtruong_11a2@hoangcau.edu.vn', 'truongnl178', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Trường', 'Nguyễn Lâm', 'STUDENT', TRUE, 'ACTIVED', 7, NOW()),
                                                                                                                         ('tranthinga_11a2@hoangcau.edu.vn', 'ngatt179', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Nga', 'Trần Thị', 'STUDENT', TRUE, 'ACTIVED', 7, NOW()),
                                                                                                                         ('lephuonglinh_11a2@hoangcau.edu.vn', 'linhlp180', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Linh', 'Lê Phương', 'STUDENT', TRUE, 'ACTIVED', 7, NOW()),
                                                                                                                         ('phambaochau_11a2@hoangcau.edu.vn', 'chaupb181', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Châu', 'Phạm Bảo', 'STUDENT', TRUE, 'ACTIVED', 7, NOW()),
                                                                                                                         ('hoangngocdat_11a2@hoangcau.edu.vn', 'dathn182', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Đạt', 'Hoàng Ngọc', 'STUDENT', TRUE, 'ACTIVED', 7, NOW()),
                                                                                                                         ('vuhongnhung_11a2@hoangcau.edu.vn', 'nhungvh183', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Nhung', 'Vũ Hồng', 'STUDENT', TRUE, 'ACTIVED', 7, NOW()),
                                                                                                                         ('nguyenthanhbinh_11a2@hoangcau.edu.vn', 'binhnt184', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Bình', 'Nguyễn Thanh', 'STUDENT', TRUE, 'ACTIVED', 7, NOW()),
                                                                                                                         ('tranthithuy_11a2@hoangcau.edu.vn', 'thuytt185', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Thủy', 'Trần Thị', 'STUDENT', TRUE, 'ACTIVED', 7, NOW()),
                                                                                                                         ('leducmanh_11a2@hoangcau.edu.vn', 'manhld186', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Mạnh', 'Lê Đức', 'STUDENT', TRUE, 'ACTIVED', 7, NOW()),
                                                                                                                         ('phamquocviet_11a2@hoangcau.edu.vn', 'vietpq187', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Việt', 'Phạm Quốc', 'STUDENT', TRUE, 'ACTIVED', 7, NOW()),
                                                                                                                         ('hoangminhtu_11a2@hoangcau.edu.vn', 'tuhm188', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Tú', 'Hoàng Minh', 'STUDENT', TRUE, 'ACTIVED', 7, NOW()),
-- Lớp 11A3 (class_id = 8)
                                                                                                                         ('nguyenthibao_11a3@hoangcau.edu.vn', 'baont189', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Bảo', 'Nguyễn Thị', 'STUDENT', TRUE, 'ACTIVED', 8, NOW()),
                                                                                                                         ('tranhoangquan_11a3@hoangcau.edu.vn', 'quanth190', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Quân', 'Trần Hoàng', 'STUDENT', TRUE, 'ACTIVED', 8, NOW()),
                                                                                                                         ('leminhhai_11a3@hoangcau.edu.vn', 'huilm191', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Hải', 'Lê Minh', 'STUDENT', TRUE, 'ACTIVED', 8, NOW()),
                                                                                                                         ('phamhuong_11a3@hoangcau.edu.vn', 'huongp192', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Hương', 'Phạm', 'STUDENT', TRUE, 'ACTIVED', 8, NOW()),
                                                                                                                         ('hoangphuc_11a3@hoangcau.edu.vn', 'phuch193', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Phúc', 'Hoàng', 'STUDENT', TRUE, 'ACTIVED', 8, NOW()),
                                                                                                                         ('vutram_11a3@hoangcau.edu.vn', 'tramv194', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Trâm', 'Vũ', 'STUDENT', TRUE, 'ACTIVED', 8, NOW()),
                                                                                                                         ('phanlinh_11a3@hoangcau.edu.vn', 'linhp195', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Linh', 'Phan', 'STUDENT', TRUE, 'ACTIVED', 8, NOW()),
                                                                                                                         ('dangvy_11a3@hoangcau.edu.vn', 'vyd196', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Vy', 'Đặng', 'STUDENT', TRUE, 'ACTIVED', 8, NOW()),
                                                                                                                         ('builong_11a3@hoangcau.edu.vn', 'longb197', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Long', 'Bùi', 'STUDENT', TRUE, 'ACTIVED', 8, NOW()),
                                                                                                                         ('nguyenngochuyen_11a3@hoangcau.edu.vn', 'huyenno198', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Huyền', 'Nguyễn Ngọc', 'STUDENT', TRUE, 'ACTIVED', 8, NOW()),
                                                                                                                         ('trantuấnđạt_11a3@hoangcau.edu.vn', 'đạttt199', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Đạt', 'Trần Tuấn', 'STUDENT', TRUE, 'ACTIVED', 8, NOW()),
                                                                                                                         ('lephuongthao_11a3@hoangcau.edu.vn', 'thaolp200', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Thảo', 'Lê Phương', 'STUDENT', TRUE, 'ACTIVED', 8, NOW()),
                                                                                                                         ('phamvanquyet_11a3@hoangcau.edu.vn', 'quyetpv201', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Quyết', 'Phạm Văn', 'STUDENT', TRUE, 'ACTIVED', 8, NOW()),
                                                                                                                         ('hoangbichngoc_11a3@hoangcau.edu.vn', 'ngochb202', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Ngọc', 'Hoàng Bích', 'STUDENT', TRUE, 'ACTIVED', 8, NOW()),
                                                                                                                         ('vuthanhbinh_11a3@hoangcau.edu.vn', 'binhvt203', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Bình', 'Vũ Thanh', 'STUDENT', TRUE, 'ACTIVED', 8, NOW()),
                                                                                                                         ('nguyenleanh_11a3@hoangcau.edu.vn', 'anhnl204', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Anh', 'Nguyễn Lê', 'STUDENT', TRUE, 'ACTIVED', 8, NOW()),
                                                                                                                         ('tranminhtu_11a3@hoangcau.edu.vn', 'tutm205', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Tú', 'Trần Minh', 'STUDENT', TRUE, 'ACTIVED', 8, NOW()),
                                                                                                                         ('lethithanh_11a3@hoangcau.edu.vn', 'thanhlt206', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Thanh', 'Lê Thị', 'STUDENT', TRUE, 'ACTIVED', 8, NOW()),
                                                                                                                         ('phamhoangtien_11a3@hoangcau.edu.vn', 'tienph207', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Tiến', 'Phạm Hoàng', 'STUDENT', TRUE, 'ACTIVED', 8, NOW()),
                                                                                                                         ('hoangnhuy_11a3@hoangcau.edu.vn', 'yhn208', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Ý', 'Hoàng Như', 'STUDENT', TRUE, 'ACTIVED', 8, NOW()),
                                                                                                                         ('vuduchuy_11a3@hoangcau.edu.vn', 'huyvd209', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Huy', 'Vũ Đức', 'STUDENT', TRUE, 'ACTIVED', 8, NOW()),
                                                                                                                         ('nguyenquocdung_11a3@hoangcau.edu.vn', 'dungnq210', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Dũng', 'Nguyễn Quốc', 'STUDENT', TRUE, 'ACTIVED', 8, NOW()),
                                                                                                                         ('tranthimy_11a3@hoangcau.edu.vn', 'mytt211', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Mỹ', 'Trần Thị', 'STUDENT', TRUE, 'ACTIVED', 8, NOW()),
                                                                                                                         ('lephuongvinh_11a3@hoangcau.edu.vn', 'vinhlp212', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Vinh', 'Lê Phương', 'STUDENT', TRUE, 'ACTIVED', 8, NOW()),
                                                                                                                         ('phamcongminh_11a3@hoangcau.edu.vn', 'minhpc213', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Minh', 'Phạm Công', 'STUDENT', TRUE, 'ACTIVED', 8, NOW()),
                                                                                                                         ('hoangbaotrang_11a3@hoangcau.edu.vn', 'tranghb214', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Trang', 'Hoàng Bảo', 'STUDENT', TRUE, 'ACTIVED', 8, NOW()),

-- Lớp 11A4 (class_id = 9)
                                                                                                                         ('nguyenthach_11a4@hoangcau.edu.vn', 'thachn215', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Thạch', 'Nguyễn', 'STUDENT', TRUE, 'ACTIVED', 9, NOW()),
                                                                                                                         ('tranbinh_11a4@hoangcau.edu.vn', 'binht216', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Bình', 'Trần', 'STUDENT', TRUE, 'ACTIVED', 9, NOW()),
                                                                                                                         ('lelinh_11a4@hoangcau.edu.vn', 'linhl217', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Linh', 'Lê', 'STUDENT', TRUE, 'ACTIVED', 9, NOW()),
                                                                                                                         ('phamtung_11a4@hoangcau.edu.vn', 'tungp218', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Tùng', 'Phạm', 'STUDENT', TRUE, 'ACTIVED', 9, NOW()),
                                                                                                                         ('hoangngoc_11a4@hoangcau.edu.vn', 'ngoch219', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Ngọc', 'Hoàng', 'STUDENT', TRUE, 'ACTIVED', 9, NOW()),
                                                                                                                         ('vuquang_11a4@hoangcau.edu.vn', 'quangv220', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Quang', 'Vũ', 'STUDENT', TRUE, 'ACTIVED', 9, NOW()),
                                                                                                                         ('phandiep_11a4@hoangcau.edu.vn', 'diepp221', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Diệp', 'Phan', 'STUDENT', TRUE, 'ACTIVED', 9, NOW()),
                                                                                                                         ('dangmai_11a4@hoangcau.edu.vn', 'maid222', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Mai', 'Đặng', 'STUDENT', TRUE, 'ACTIVED', 9, NOW()),
                                                                                                                         ('buithanh_11a4@hoangcau.edu.vn', 'thanhb223', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Thành', 'Bùi', 'STUDENT', TRUE, 'ACTIVED', 9, NOW()),
                                                                                                                         ('nguyendieuanh_11a4@hoangcau.edu.vn', 'anhnd224', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Anh', 'Nguyễn Diệu', 'STUDENT', TRUE, 'ACTIVED', 9, NOW()),
                                                                                                                         ('tranhoangdat_11a4@hoangcau.edu.vn', 'datth225', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Đạt', 'Trần Hoàng', 'STUDENT', TRUE, 'ACTIVED', 9, NOW()),
                                                                                                                         ('lethiyen_11a4@hoangcau.edu.vn', 'yenlt226', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Yến', 'Lê Thị', 'STUDENT', TRUE, 'ACTIVED', 9, NOW()),
                                                                                                                         ('phammanhhung_11a4@hoangcau.edu.vn', 'hungpm227', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Hùng', 'Phạm Mạnh', 'STUDENT', TRUE, 'ACTIVED', 9, NOW()),
                                                                                                                         ('hoangkimngan_11a4@hoangcau.edu.vn', 'nganhk228', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Ngân', 'Hoàng Kim', 'STUDENT', TRUE, 'ACTIVED', 9, NOW()),
                                                                                                                         ('vuminhtan_11a4@hoangcau.edu.vn', 'tanvm229', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Tân', 'Vũ Minh', 'STUDENT', TRUE, 'ACTIVED', 9, NOW()),
                                                                                                                         ('nguyenthaoquynh_11a4@hoangcau.edu.vn', 'quynhnt230', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Quỳnh', 'Nguyễn Thảo', 'STUDENT', TRUE, 'ACTIVED', 9, NOW()),
                                                                                                                         ('tranvietkhoa_11a4@hoangcau.edu.vn', 'khoatv231', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Khoa', 'Trần Việt', 'STUDENT', TRUE, 'ACTIVED', 9, NOW()),
                                                                                                                         ('lehoangviet_11a4@hoangcau.edu.vn', 'vietlh232', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Việt', 'Lê Hoàng', 'STUDENT', TRUE, 'ACTIVED', 9, NOW()),
                                                                                                                         ('phambichphương_11a4@hoangcau.edu.vn', 'phươngpb233', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Phương', 'Phạm Bích', 'STUDENT', TRUE, 'ACTIVED', 9, NOW()),
                                                                                                                         ('hoanggiabao_11a4@hoangcau.edu.vn', 'baohg234', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Bảo', 'Hoàng Gia', 'STUDENT', TRUE, 'ACTIVED', 9, NOW()),
                                                                                                                         ('vuthaolinh_11a4@hoangcau.edu.vn', 'linhvt235', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Linh', 'Vũ Thảo', 'STUDENT', TRUE, 'ACTIVED', 9, NOW()),
                                                                                                                         ('nguyenminhtuong_11a4@hoangcau.edu.vn', 'tuongnm236', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Tường', 'Nguyễn Minh', 'STUDENT', TRUE, 'ACTIVED', 9, NOW()),
                                                                                                                         ('tranthanhtruc_11a4@hoangcau.edu.vn', 'tructt237', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Trúc', 'Trần Thanh', 'STUDENT', TRUE, 'ACTIVED', 9, NOW()),
                                                                                                                         ('leminhtrung_11a4@hoangcau.edu.vn', 'trunglm238', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Trung', 'Lê Minh', 'STUDENT', TRUE, 'ACTIVED', 9, NOW()),
                                                                                                                         ('phamthihuong_11a4@hoangcau.edu.vn', 'huongpt239', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Hương', 'Phạm Thị', 'STUDENT', TRUE, 'ACTIVED', 9, NOW()),
                                                                                                                         ('hoangmaichi_11a4@hoangcau.edu.vn', 'chihm240', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Chi', 'Hoàng Mai', 'STUDENT', TRUE, 'ACTIVED', 9, NOW()),
-- Lớp 11A5 (class_id = 10)
                                                                                                                         ('nguyenhao_11a5@hoangcau.edu.vn', 'haon241', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Hào', 'Nguyễn', 'STUDENT', TRUE, 'ACTIVED', 10, NOW()),
                                                                                                                         ('tranphong_11a5@hoangcau.edu.vn', 'phongt242', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Phong', 'Trần', 'STUDENT', TRUE, 'ACTIVED', 10, NOW()),
                                                                                                                         ('lehang_11a5@hoangcau.edu.vn', 'hangl243', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Hằng', 'Lê', 'STUDENT', TRUE, 'ACTIVED', 10, NOW()),
                                                                                                                         ('phamgiang_11a5@hoangcau.edu.vn', 'giangp244', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Giang', 'Phạm', 'STUDENT', TRUE, 'ACTIVED', 10, NOW()),
                                                                                                                         ('hoangphuoc_11a5@hoangcau.edu.vn', 'phuoch245', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Phước', 'Hoàng', 'STUDENT', TRUE, 'ACTIVED', 10, NOW()),
                                                                                                                         ('vuduyen_11a5@hoangcau.edu.vn', 'duyenv246', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Duyên', 'Vũ', 'STUDENT', TRUE, 'ACTIVED', 10, NOW()),
                                                                                                                         ('phanmanh_11a5@hoangcau.edu.vn', 'manhp247', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Mạnh', 'Phan', 'STUDENT', TRUE, 'ACTIVED', 10, NOW()),
                                                                                                                         ('dangyen_11a5@hoangcau.edu.vn', 'yend248', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Yến', 'Đặng', 'STUDENT', TRUE, 'ACTIVED', 10, NOW()),
                                                                                                                         ('buidung_11a5@hoangcau.edu.vn', 'dungb249', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Dũng', 'Bùi', 'STUDENT', TRUE, 'ACTIVED', 10, NOW()),
                                                                                                                         ('nguyenthaolinh_11a5@hoangcau.edu.vn', 'linhnt250', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Linh', 'Nguyễn Thảo', 'STUDENT', TRUE, 'ACTIVED', 10, NOW()),
                                                                                                                         ('tranquanghai_11a5@hoangcau.edu.vn', 'haitq251', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Hải', 'Trần Quang', 'STUDENT', TRUE, 'ACTIVED', 10, NOW()),
                                                                                                                         ('lethiquynh_11a5@hoangcau.edu.vn', 'quynhlt252', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Quỳnh', 'Lê Thị', 'STUDENT', TRUE, 'ACTIVED', 10, NOW()),
                                                                                                                         ('phamquangmanh_11a5@hoangcau.edu.vn', 'manhpq253', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Mạnh', 'Phạm Quang', 'STUDENT', TRUE, 'ACTIVED', 10, NOW()),
                                                                                                                         ('hoangthuytieu_11a5@hoangcau.edu.vn', 'tienht254', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Tiên', 'Hoàng Thủy', 'STUDENT', TRUE, 'ACTIVED', 10, NOW()),
                                                                                                                         ('vutrungkiet_11a5@hoangcau.edu.vn', 'kietvt255', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Kiệt', 'Vũ Trọng', 'STUDENT', TRUE, 'ACTIVED', 10, NOW()),
                                                                                                                         ('nguyenhoangbach_11a5@hoangcau.edu.vn', 'bachnh256', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Bách', 'Nguyễn Hoàng', 'STUDENT', TRUE, 'ACTIVED', 10, NOW()),
                                                                                                                         ('tranthimien_11a5@hoangcau.edu.vn', 'mientt257', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Miên', 'Trần Thị', 'STUDENT', TRUE, 'ACTIVED', 10, NOW()),
                                                                                                                         ('leanhduc_11a5@hoangcau.edu.vn', 'ducla258', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Đức', 'Lê Anh', 'STUDENT', TRUE, 'ACTIVED', 10, NOW()),
                                                                                                                         ('phamthaihoc_11a5@hoangcau.edu.vn', 'hocpt259', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Học', 'Phạm Thái', 'STUDENT', TRUE, 'ACTIVED', 10, NOW()),
                                                                                                                         ('hoangbichthuy_11a5@hoangcau.edu.vn', 'thuyhb260', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Thủy', 'Hoàng Bích', 'STUDENT', TRUE, 'ACTIVED', 10, NOW()),
                                                                                                                         ('vutuanlong_11a5@hoangcau.edu.vn', 'longvt261', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Long', 'Vũ Tuấn', 'STUDENT', TRUE, 'ACTIVED', 10, NOW()),
                                                                                                                         ('nguyenthicam_11a5@hoangcau.edu.vn', 'camnt262', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Cẩm', 'Nguyễn Thị', 'STUDENT', TRUE, 'ACTIVED', 10, NOW()),
                                                                                                                         ('tranhonganh_11a5@hoangcau.edu.vn', 'anhta263', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Anh', 'Trần Hồng', 'STUDENT', TRUE, 'ACTIVED', 10, NOW()),
                                                                                                                         ('letrunghieu_11a5@hoangcau.edu.vn', 'hieult264', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Hiếu', 'Lê Trung', 'STUDENT', TRUE, 'ACTIVED', 10, NOW()),
                                                                                                                         ('phamthilan_11a5@hoangcau.edu.vn', 'lanpt265', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Lan', 'Phạm Thị', 'STUDENT', TRUE, 'ACTIVED', 10, NOW()),
                                                                                                                         ('hoangxuandat_11a5@hoangcau.edu.vn', 'dathx266', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Đạt', 'Hoàng Xuân', 'STUDENT', TRUE, 'ACTIVED', 10, NOW()),
-- Lớp 12A1 (class_id = 11)
                                                                                                                         ('nguyenquang_12a1@hoangcau.edu.vn', 'quangn267', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Quang', 'Nguyễn', 'STUDENT', TRUE, 'ACTIVED', 11, NOW()),
                                                                                                                         ('tranthithu_12a1@hoangcau.edu.vn', 'thutt268', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Thu', 'Trần Thị', 'STUDENT', TRUE, 'ACTIVED', 11, NOW()),
                                                                                                                         ('lehoang_12a1@hoangcau.edu.vn', 'hoangl269', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Hoàng', 'Lê', 'STUDENT', TRUE, 'ACTIVED', 11, NOW()),
                                                                                                                         ('phamhong_12a1@hoangcau.edu.vn', 'hongp270', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Hồng', 'Phạm', 'STUDENT', TRUE, 'ACTIVED', 11, NOW()),
                                                                                                                         ('hoangminh_12a1@hoangcau.edu.vn', 'minhh271', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Minh', 'Hoàng', 'STUDENT', TRUE, 'ACTIVED', 11, NOW()),
                                                                                                                         ('vunhi_12a1@hoangcau.edu.vn', 'nhiv272', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Nhi', 'Vũ', 'STUDENT', TRUE, 'ACTIVED', 11, NOW()),
                                                                                                                         ('phantrung_12a1@hoangcau.edu.vn', 'trungp273', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Trung', 'Phan', 'STUDENT', TRUE, 'ACTIVED', 11, NOW()),
                                                                                                                         ('danglinh_12a1@hoangcau.edu.vn', 'linhd274', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDgtxb.ne', 'Linh', 'Đặng', 'STUDENT', TRUE, 'ACTIVED', 11, NOW()),
                                                                                                                         ('buithanh_12a1@hoangcau.edu.vn', 'thanhb275', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Thành', 'Bùi', 'STUDENT', TRUE, 'ACTIVED', 11, NOW()),
                                                                                                                         ('nguyendieuhuong_12a1@hoangcau.edu.vn', 'huongnd276', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDgtxb.ne', 'Hương', 'Nguyễn Diệu', 'STUDENT', TRUE, 'ACTIVED', 11, NOW()),
                                                                                                                         ('tranhoangdat1_12a1@hoangcau.edu.vn', 'datth277', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Đạt', 'Trần Hoàng', 'STUDENT', TRUE, 'ACTIVED', 11, NOW()),
                                                                                                                         ('lethicamly_12a1@hoangcau.edu.vn', 'lyltc278', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Ly', 'Lê Thị Cẩm', 'STUDENT', TRUE, 'ACTIVED', 11, NOW()),
                                                                                                                         ('phamquocuy1_12a1@hoangcau.edu.vn', 'uypq279', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Uy', 'Phạm Quốc', 'STUDENT', TRUE, 'ACTIVED', 11, NOW()),
                                                                                                                         ('hoanggiaphu_12a1@hoangcau.edu.vn', 'phuhg280', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Phú', 'Hoàng Gia', 'STUDENT', TRUE, 'ACTIVED', 11, NOW()),
                                                                                                                         ('vutrongduc_12a1@hoangcau.edu.vn', 'ducvt281', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Đức', 'Vũ Trọng', 'STUDENT', TRUE, 'ACTIVED', 11, NOW()),
                                                                                                                         ('nguyenphuongvy_12a1@hoangcau.edu.vn', 'vynp282', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Vy', 'Nguyễn Phương', 'STUDENT', TRUE, 'ACTIVED', 11, NOW()),
                                                                                                                         ('tranvietanh_12a1@hoangcau.edu.vn', 'anhtv283', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Anh', 'Trần Việt', 'STUDENT', TRUE, 'ACTIVED', 11, NOW()),
                                                                                                                         ('lehoanglong_12a1@hoangcau.edu.vn', 'longlh284', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Long', 'Lê Hoàng', 'STUDENT', TRUE, 'ACTIVED', 11, NOW()),
                                                                                                                         ('phambichthuy_12a1@hoangcau.edu.vn', 'thuyph285', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Thủy', 'Phạm Bích', 'STUDENT', TRUE, 'ACTIVED', 11, NOW()),
                                                                                                                         ('hoangducphuc_12a1@hoangcau.edu.vn', 'phuchd286', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Phúc', 'Hoàng Đức', 'STUDENT', TRUE, 'ACTIVED', 11, NOW()),
                                                                                                                         ('vuthaotrang_12a1@hoangcau.edu.vn', 'trangvt287', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Trang', 'Vũ Thảo', 'STUDENT', TRUE, 'ACTIVED', 11, NOW()),
                                                                                                                         ('nguyenbaolam_12a1@hoangcau.edu.vn', 'lamnb288', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Lâm', 'Nguyễn Bảo', 'STUDENT', TRUE, 'ACTIVED', 11, NOW()),
                                                                                                                         ('tranthimy_12a1@hoangcau.edu.vn', 'mytt289', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Mỹ', 'Trần Thị', 'STUDENT', TRUE, 'ACTIVED', 11, NOW()),
                                                                                                                         ('leminhtrung_12a1@hoangcau.edu.vn', 'trunglm290', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Trung', 'Lê Minh', 'STUDENT', TRUE, 'ACTIVED', 11, NOW()),
                                                                                                                         ('phamhaimy_12a1@hoangcau.edu.vn', 'myph291', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'My', 'Phạm Hải', 'STUDENT', TRUE, 'ACTIVED', 11, NOW()),
                                                                                                                         ('hoangminhquan_12a1@hoangcau.edu.vn', 'quanhm292', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Quân', 'Hoàng Minh', 'STUDENT', TRUE, 'ACTIVED', 11, NOW()),
-- Lớp 12A2 (class_id = 12)
                                                                                                                         ('nguyendat_12a2@hoangcau.edu.vn', 'datn293', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Đạt', 'Nguyễn', 'STUDENT', TRUE, 'ACTIVED', 12, NOW()),
                                                                                                                         ('trankhoa_12a2@hoangcau.edu.vn', 'khoat294', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Khoa', 'Trần', 'STUDENT', TRUE, 'ACTIVED', 12, NOW()),
                                                                                                                         ('letuan_12a2@hoangcau.edu.vn', 'tuanl295', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Tuấn', 'Lê', 'STUDENT', TRUE, 'ACTIVED', 12, NOW()),
                                                                                                                         ('phamtrang_12a2@hoangcau.edu.vn', 'trangp296', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Trang', 'Phạm', 'STUDENT', TRUE, 'ACTIVED', 12, NOW()),
                                                                                                                         ('hoangphuong_12a2@hoangcau.edu.vn', 'phuongh297', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Phương', 'Hoàng', 'STUDENT', TRUE, 'ACTIVED', 12, NOW()),
                                                                                                                         ('vubinh_12a2@hoangcau.edu.vn', 'binhv298', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Bình', 'Vũ', 'STUDENT', TRUE, 'ACTIVED', 12, NOW()),
                                                                                                                         ('phanduy_12a2@hoangcau.edu.vn', 'duyp299', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Duy', 'Phan', 'STUDENT', TRUE, 'ACTIVED', 12, NOW()),
                                                                                                                         ('dangkhai_12a2@hoangcau.edu.vn', 'khaid300', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Khai', 'Đặng', 'STUDENT', TRUE, 'ACTIVED', 12, NOW()),
                                                                                                                         ('buitran_12a2@hoangcau.edu.vn', 'tranb301', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Trân', 'Bùi', 'STUDENT', TRUE, 'ACTIVED', 12, NOW()),
                                                                                                                         ('nguyenhoaiphong_12a2@hoangcau.edu.vn', 'phonglh302', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Phong', 'Lê Hoài', 'STUDENT', TRUE, 'ACTIVED', 12, NOW()),
                                                                                                                         ('tranquochuy_12a2@hoangcau.edu.vn', 'huytq303', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Huy', 'Trần Quốc', 'STUDENT', TRUE, 'ACTIVED', 12, NOW()),
                                                                                                                         ('lethithao_12a2@hoangcau.edu.vn', 'thaolt304', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Thảo', 'Lê Thị', 'STUDENT', TRUE, 'ACTIVED', 12, NOW()),
                                                                                                                         ('phamduyduc_12a2@hoangcau.edu.vn', 'ducpd305', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Đức', 'Phạm Duy', 'STUDENT', TRUE, 'ACTIVED', 12, NOW()),
                                                                                                                         ('hoangminhgiang_12a2@hoangcau.edu.vn', 'gianghm306', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Giang', 'Hoàng Minh', 'STUDENT', TRUE, 'ACTIVED', 12, NOW()),
                                                                                                                         ('vungochuyen_12a2@hoangcau.edu.vn', 'huyenvn307', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Huyền', 'Vũ Ngọc', 'STUDENT', TRUE, 'ACTIVED', 12, NOW()),
                                                                                                                         ('nguyenthanhtien_12a2@hoangcau.edu.vn', 'tiennt308', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Tiến', 'Nguyễn Thanh', 'STUDENT', TRUE, 'ACTIVED', 12, NOW()),
                                                                                                                         ('tranthithuy_12a2@hoangcau.edu.vn', 'thuytt309', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Thủy', 'Trần Thị', 'STUDENT', TRUE, 'ACTIVED', 12, NOW()),
                                                                                                                         ('lequangmanh_12a2@hoangcau.edu.vn', 'manhlq310', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Mạnh', 'Lê Quang', 'STUDENT', TRUE, 'ACTIVED', 12, NOW()),
                                                                                                                         ('phamquocviet_12a2@hoangcau.edu.vn', 'vietpq311', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Việt', 'Phạm Quốc', 'STUDENT', TRUE, 'ACTIVED', 12, NOW()),
                                                                                                                         ('hoangbaongoc_12a2@hoangcau.edu.vn', 'ngochb312', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Ngọc', 'Hoàng Bảo', 'STUDENT', TRUE, 'ACTIVED', 12, NOW()),
                                                                                                                         ('vutuanphong_12a2@hoangcau.edu.vn', 'phongvt313', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Phong', 'Vũ Tuấn', 'STUDENT', TRUE, 'ACTIVED', 12, NOW()),
                                                                                                                         ('nguyenthanhvy_12a2@hoangcau.edu.vn', 'vynt314', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Vy', 'Nguyễn Thanh', 'STUDENT', TRUE, 'ACTIVED', 12, NOW()),
                                                                                                                         ('tranvietkhoi_12a2@hoangcau.edu.vn', 'khoitv315', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Khôi', 'Trần Việt', 'STUDENT', TRUE, 'ACTIVED', 12, NOW()),
                                                                                                                         ('lethiquyet_12a2@hoangcau.edu.vn', 'quyetlt316', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Quyết', 'Lê Thị', 'STUDENT', TRUE, 'ACTIVED', 12, NOW()),
                                                                                                                         ('phamvanduong_12a2@hoangcau.edu.vn', 'duongpv317', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Dương', 'Phạm Văn', 'STUDENT', TRUE, 'ACTIVED', 12, NOW()),
                                                                                                                         ('hoangbaolinh_12a2@hoangcau.edu.vn', 'linhhb318', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Linh', 'Hoàng Bảo', 'STUDENT', TRUE, 'ACTIVED', 12, NOW()),
-- Lớp 12A3 (class_id = 13)
                                                                                                                         ('nguyenbaolinh_12a3@hoangcau.edu.vn', 'linhnb319', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Linh', 'Nguyễn Bảo', 'STUDENT', TRUE, 'ACTIVED', 13, NOW()),
                                                                                                                         ('tranhoanglong_12a3@hoangcau.edu.vn', 'longth320', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Long', 'Trần Hoàng', 'STUDENT', TRUE, 'ACTIVED', 13, NOW()),
                                                                                                                         ('leminhtan_12a3@hoangcau.edu.vn', 'tanlm321', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Tân', 'Lê Minh', 'STUDENT', TRUE, 'ACTIVED', 13, NOW()),
                                                                                                                         ('phamphuongchi_12a3@hoangcau.edu.vn', 'chipm322', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Chi', 'Phạm Phương', 'STUDENT', TRUE, 'ACTIVED', 13, NOW()),
                                                                                                                         ('hoangminhtuan_12a3@hoangcau.edu.vn', 'tuanhm323', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Tuấn', 'Hoàng Minh', 'STUDENT', TRUE, 'ACTIVED', 13, NOW()),
                                                                                                                         ('vuxuanduc_12a3@hoangcau.edu.vn', 'ducvx324', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Đức', 'Vũ Xuân', 'STUDENT', TRUE, 'ACTIVED', 13, NOW()),
                                                                                                                         ('nguyenthithao_12a3@hoangcau.edu.vn', 'thaont325', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Thảo', 'Nguyễn Thị', 'STUDENT', TRUE, 'ACTIVED', 13, NOW()),
                                                                                                                         ('trantuandung_12a3@hoangcau.edu.vn', 'dungtt326', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Dũng', 'Trần Tuấn', 'STUDENT', TRUE, 'ACTIVED', 13, NOW()),
                                                                                                                         ('lephuongtrinh_12a3@hoangcau.edu.vn', 'trinhlp327', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Trinh', 'Lê Phương', 'STUDENT', TRUE, 'ACTIVED', 13, NOW()),
                                                                                                                         ('phamducphuc_12a3@hoangcau.edu.vn', 'phucpd328', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Phúc', 'Phạm Đức', 'STUDENT', TRUE, 'ACTIVED', 13, NOW()),
                                                                                                                         ('hoangcamtu_12a3@hoangcau.edu.vn', 'tuhc329', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Tú', 'Hoàng Cẩm', 'STUDENT', TRUE, 'ACTIVED', 13, NOW()),
                                                                                                                         ('vutrongnghia_12a3@hoangcau.edu.vn', 'nghiavt330', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Nghĩa', 'Vũ Trọng', 'STUDENT', TRUE, 'ACTIVED', 13, NOW()),
                                                                                                                         ('nguyenmaihuong_12a3@hoangcau.edu.vn', 'huongnm331', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Hương', 'Nguyễn Mai', 'STUDENT', TRUE, 'ACTIVED', 13, NOW()),
                                                                                                                         ('trankhanhly_12a3@hoangcau.edu.vn', 'lytk332', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Ly', 'Trần Khánh', 'STUDENT', TRUE, 'ACTIVED', 13, NOW()),
                                                                                                                         ('lequocbinh_12a3@hoangcau.edu.vn', 'binhlq333', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Bình', 'Lê Quốc', 'STUDENT', TRUE, 'ACTIVED', 13, NOW()),
                                                                                                                         ('phamthingoc_12a3@hoangcau.edu.vn', 'ngocpt334', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Ngọc', 'Phạm Thị', 'STUDENT', TRUE, 'ACTIVED', 13, NOW()),
                                                                                                                         ('hoangthaovy_12a3@hoangcau.edu.vn', 'vyht335', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Vy', 'Hoàng Thảo', 'STUDENT', TRUE, 'ACTIVED', 13, NOW()),
                                                                                                                         ('vumanhquyen_12a3@hoangcau.edu.vn', 'quyenvm336', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Quyền', 'Vũ Mạnh', 'STUDENT', TRUE, 'ACTIVED', 13, NOW()),
                                                                                                                         ('nguyenphuong_12a3@hoangcau.edu.vn', 'nguyetnp337', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Nguyệt', 'Nguyễn Phương', 'STUDENT', TRUE, 'ACTIVED', 13, NOW()),
                                                                                                                         ('trantuankiet_12a3@hoangcau.edu.vn', 'kiettt338', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Kiệt', 'Trần Tuấn', 'STUDENT', TRUE, 'ACTIVED', 13, NOW()),
                                                                                                                         ('lethithuy_12a3@hoangcau.edu.vn', 'thuylt339', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Thủy', 'Lê Thị', 'STUDENT', TRUE, 'ACTIVED', 13, NOW()),
                                                                                                                         ('phamminhhoang_12a3@hoangcau.edu.vn', 'hoangpm340', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Hoàng', 'Phạm Minh', 'STUDENT', TRUE, 'ACTIVED', 13, NOW()),
                                                                                                                         ('hoangngocdiep_12a3@hoangcau.edu.vn', 'diephn341', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Diệp', 'Hoàng Ngọc', 'STUDENT', TRUE, 'ACTIVED', 13, NOW()),
                                                                                                                         ('vutrongphuc_12a3@hoangcau.edu.vn', 'phucvt342', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Phúc', 'Vũ Trọng', 'STUDENT', TRUE, 'ACTIVED', 13, NOW()),
                                                                                                                         ('nguyenkhanhvy_12a3@hoangcau.edu.vn', 'vynk343', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Vy', 'Nguyễn Khánh', 'STUDENT', TRUE, 'ACTIVED', 13, NOW()),
                                                                                                                         ('trananhkhoi_12a3@hoangcau.edu.vn', 'khoita344', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Khôi', 'Trần Anh', 'STUDENT', TRUE, 'ACTIVED', 13, NOW()),
-- Lớp 12A4 (class_id = 14)
                                                                                                                         ('nguyenhoanganh_12a4@hoangcau.edu.vn', 'anhnh345', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Anh', 'Nguyễn Hoàng', 'STUDENT', TRUE, 'ACTIVED', 14, NOW()),
                                                                                                                         ('tranminhchien_12a4@hoangcau.edu.vn', 'chientm346', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Chien', 'Trần Minh', 'STUDENT', TRUE, 'ACTIVED', 14, NOW()),
                                                                                                                         ('lethaiha_12a4@hoangcau.edu.vn', 'halt347', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Hà', 'Lê Thái', 'STUDENT', TRUE, 'ACTIVED', 14, NOW()),
                                                                                                                         ('phamthuymie_12a4@hoangcau.edu.vn', 'miept348', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Mie', 'Phạm Thúy', 'STUDENT', TRUE, 'ACTIVED', 14, NOW()),
                                                                                                                         ('hoangxuannam_12a4@hoangcau.edu.vn', 'namhx349', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Nam', 'Hoàng Xuân', 'STUDENT', TRUE, 'ACTIVED', 14, NOW()),
                                                                                                                         ('vuthiquynh_12a4@hoangcau.edu.vn', 'quynhvt350', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Quỳnh', 'Vũ Thị', 'STUDENT', TRUE, 'ACTIVED', 14, NOW()),
                                                                                                                         ('phanquocvinh_12a4@hoangcau.edu.vn', 'vinhpq351', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Vinh', 'Phan Quốc', 'STUDENT', TRUE, 'ACTIVED', 14, NOW()),
                                                                                                                         ('dangthingoc_12a4@hoangcau.edu.vn', 'ngocdt352', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Ngọc', 'Đặng Thị', 'STUDENT', TRUE, 'ACTIVED', 14, NOW()),
                                                                                                                         ('buivietdung_12a4@hoangcau.edu.vn', 'dungbv353', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Dũng', 'Bùi Việt', 'STUDENT', TRUE, 'ACTIVED', 14, NOW()),
                                                                                                                         ('nguyenbaotrong_12a4@hoangcau.edu.vn', 'trongnb354', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Trọng', 'Nguyễn Bảo', 'STUDENT', TRUE, 'ACTIVED', 14, NOW()),
                                                                                                                         ('tranthanhphuong_12a4@hoangcau.edu.vn', 'phuongtt355', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Phương', 'Trần Thanh', 'STUDENT', TRUE, 'ACTIVED', 14, NOW()),
                                                                                                                         ('leducquang_12a4@hoangcau.edu.vn', 'quangld356', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Quang', 'Lê Đức', 'STUDENT', TRUE, 'ACTIVED', 14, NOW()),
                                                                                                                         ('phamhuongyiang_12a4@hoangcau.edu.vn', 'giangph357', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Giang', 'Phạm Hương', 'STUDENT', TRUE, 'ACTIVED', 14, NOW()),
                                                                                                                         ('hoangminhkhai_12a4@hoangcau.edu.vn', 'khaihm358', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Khai', 'Hoàng Minh', 'STUDENT', TRUE, 'ACTIVED', 14, NOW()),
                                                                                                                         ('vutuongvi_12a4@hoangcau.edu.vn', 'vyvt359', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Vy', 'Vũ Tường', 'STUDENT', TRUE, 'ACTIVED', 14, NOW()),
                                                                                                                         ('nguyenlamtruong_12a4@hoangcau.edu.vn', 'truongnl360', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Trường', 'Nguyễn Lâm', 'STUDENT', TRUE, 'ACTIVED', 14, NOW()),
                                                                                                                         ('tranthinga_12a4@hoangcau.edu.vn', 'ngatt361', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Nga', 'Trần Thị', 'STUDENT', TRUE, 'ACTIVED', 14, NOW()),
                                                                                                                         ('lephuonglinh_12a4@hoangcau.edu.vn', 'linhlp362', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Linh', 'Lê Phương', 'STUDENT', TRUE, 'ACTIVED', 14, NOW()),
                                                                                                                         ('phambaochau_12a4@hoangcau.edu.vn', 'chaupb363', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Châu', 'Phạm Bảo', 'STUDENT', TRUE, 'ACTIVED', 14, NOW()),
                                                                                                                         ('hoangngocdat_12a4@hoangcau.edu.vn', 'dathn364', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Đạt', 'Hoàng Ngọc', 'STUDENT', TRUE, 'ACTIVED', 14, NOW()),
                                                                                                                         ('vuhongnhung_12a4@hoangcau.edu.vn', 'nhungvh365', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Nhung', 'Vũ Hồng', 'STUDENT', TRUE, 'ACTIVED', 14, NOW()),
                                                                                                                         ('nguyenthanhbinh_12a4@hoangcau.edu.vn', 'binhnt366', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Bình', 'Nguyễn Thanh', 'STUDENT', TRUE, 'ACTIVED', 14, NOW()),
                                                                                                                         ('tranthithuy_12a4@hoangcau.edu.vn', 'thuytt367', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Thủy', 'Trần Thị', 'STUDENT', TRUE, 'ACTIVED', 14, NOW()),
                                                                                                                         ('leducmanh_12a4@hoangcau.edu.vn', 'manhld368', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Mạnh', 'Lê Đức', 'STUDENT', TRUE, 'ACTIVED', 14, NOW()),
                                                                                                                         ('phamquocviet_12a4@hoangcau.edu.vn', 'vietpq369', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Việt', 'Phạm Quốc', 'STUDENT', TRUE, 'ACTIVED', 14, NOW()),
                                                                                                                         ('hoangminhtu_12a4@hoangcau.edu.vn', 'tuhm370', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Tú', 'Hoàng Minh', 'STUDENT', TRUE, 'ACTIVED', 14, NOW()),
-- Lớp 12A5 (class_id = 15)
                                                                                                                         ('nguyenthibao_12a5@hoangcau.edu.vn', 'baont371', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Bảo', 'Nguyễn Thị', 'STUDENT', TRUE, 'ACTIVED', 15, NOW()),
                                                                                                                         ('tranhoangquan_12a5@hoangcau.edu.vn', 'quanth372', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Quân', 'Trần Hoàng', 'STUDENT', TRUE, 'ACTIVED', 15, NOW()),
                                                                                                                         ('leminhhai_12a5@hoangcau.edu.vn', 'huilm373', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Hải', 'Lê Minh', 'STUDENT', TRUE, 'ACTIVED', 15, NOW()),
                                                                                                                         ('phamhuong_12a5@hoangcau.edu.vn', 'huongp374', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Hương', 'Phạm', 'STUDENT', TRUE, 'ACTIVED', 15, NOW()),
                                                                                                                         ('hoangphuc_12a5@hoangcau.edu.vn', 'phuch375', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDgtxb.ne', 'Phúc', 'Hoàng', 'STUDENT', TRUE, 'ACTIVED', 15, NOW()),
                                                                                                                         ('vutram_12a5@hoangcau.edu.vn', 'tramv376', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDgtxb.ne', 'Trâm', 'Vũ', 'STUDENT', TRUE, 'ACTIVED', 15, NOW()),
                                                                                                                         ('phanlinh_12a5@hoangcau.edu.vn', 'linhp377', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Linh', 'Phan', 'STUDENT', TRUE, 'ACTIVED', 15, NOW()),
                                                                                                                         ('dangvy_12a5@hoangcau.edu.vn', 'vyd378', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Vy', 'Đặng', 'STUDENT', TRUE, 'ACTIVED', 15, NOW()),
                                                                                                                         ('builong_12a5@hoangcau.edu.vn', 'longb379', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Long', 'Bùi', 'STUDENT', TRUE, 'ACTIVED', 15, NOW()),
                                                                                                                         ('nguyenngochuyen_12a5@hoangcau.edu.vn', 'huyenno380', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Huyền', 'Nguyễn Ngọc', 'STUDENT', TRUE, 'ACTIVED', 15, NOW()),
                                                                                                                         ('trantuanđat_12a5@hoangcau.edu.vn', 'dattt381', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDgtxb.ne', 'Đạt', 'Trần Tuấn', 'STUDENT', TRUE, 'ACTIVED', 15, NOW()),
                                                                                                                         ('lephuongthao_12a5@hoangcau.edu.vn', 'thaolp382', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDgtxb.ne', 'Thảo', 'Lê Phương', 'STUDENT', TRUE, 'ACTIVED', 15, NOW()),
                                                                                                                         ('phamvanquyet_12a5@hoangcau.edu.vn', 'quyetpv383', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Quyết', 'Phạm Văn', 'STUDENT', TRUE, 'ACTIVED', 15, NOW()),
                                                                                                                         ('hoangbichngoc_12a5@hoangcau.edu.vn', 'ngochb384', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Ngọc', 'Hoàng Bích', 'STUDENT', TRUE, 'ACTIVED', 15, NOW()),
                                                                                                                         ('vuthanhbinh_12a5@hoangcau.edu.vn', 'binhvt385', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Bình', 'Vũ Thanh', 'STUDENT', TRUE, 'ACTIVED', 15, NOW()),
                                                                                                                         ('nguyenleanh_12a5@hoangcau.edu.vn', 'anhnl386', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Anh', 'Nguyễn Lê', 'STUDENT', TRUE, 'ACTIVED', 15, NOW()),
                                                                                                                         ('tranminhtu_12a5@hoangcau.edu.vn', 'tutm387', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Tú', 'Trần Minh', 'STUDENT', TRUE, 'ACTIVED', 15, NOW()),
                                                                                                                         ('lethithanh_12a5@hoangcau.edu.vn', 'thanhlt388', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Thanh', 'Lê Thị', 'STUDENT', TRUE, 'ACTIVED', 15, NOW()),
                                                                                                                         ('phamhoangtien_12a5@hoangcau.edu.vn', 'tienph389', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Tiến', 'Phạm Hoàng', 'STUDENT', TRUE, 'ACTIVED', 15, NOW()),
                                                                                                                         ('hoangnhuy_12a5@hoangcau.edu.vn', 'yhn390', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Ý', 'Hoàng Như', 'STUDENT', TRUE, 'ACTIVED', 15, NOW()),
                                                                                                                         ('vuduchuy_12a5@hoangcau.edu.vn', 'huyvd391', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Huy', 'Vũ Đức', 'STUDENT', TRUE, 'ACTIVED', 15, NOW()),
                                                                                                                         ('nguyenquocdung_12a5@hoangcau.edu.vn', 'dungnq392', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Dũng', 'Nguyễn Quốc', 'STUDENT', TRUE, 'ACTIVED', 15, NOW()),
                                                                                                                         ('tranthimy_12a5@hoangcau.edu.vn', 'mytt393', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Mỹ', 'Trần Thị', 'STUDENT', TRUE, 'ACTIVED', 15, NOW()),
                                                                                                                         ('lephuongvinh_12a5@hoangcau.edu.vn', 'vinhlp394', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Vinh', 'Lê Phương', 'STUDENT', TRUE, 'ACTIVED', 15, NOW()),
                                                                                                                         ('phamcongminh_12a5@hoangcau.edu.vn', 'minhpc395', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Minh', 'Phạm Công', 'STUDENT', TRUE, 'ACTIVED', 15, NOW()),
                                                                                                                         ('hoangbaotrang_12a5@hoangcau.edu.vn', 'tranghb396', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Trang', 'Hoàng Bảo', 'STUDENT', TRUE, 'ACTIVED', 15, NOW()),
                                                                                                                         ('vumanhquyen_12a5@hoangcau.edu.vn', 'quyenvm397', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Quyền', 'Vũ Mạnh', 'STUDENT', TRUE, 'ACTIVED', 15, NOW()),
                                                                                                                         ('nguyenphuong_12a5@hoangcau.edu.vn', 'nguyetnp398', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Nguyệt', 'Nguyễn Phương', 'STUDENT', TRUE, 'ACTIVED', 15, NOW()),
                                                                                                                         ('trantuankiet_12a5@hoangcau.edu.vn', 'kiettt399', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Kiệt', 'Trần Tuấn', 'STUDENT', TRUE, 'ACTIVED', 15, NOW()),
                                                                                                                         ('lethithuy_12a5@hoangcau.edu.vn', 'thuylt400', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDgtxb.ne', 'Thủy', 'Lê Thị', 'STUDENT', TRUE, 'ACTIVED', 15, NOW()),
                                                                                                                         ('phamminhhoang_12a5@hoangcau.edu.vn', 'hoangpm401', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Hoàng', 'Phạm Minh', 'STUDENT', TRUE, 'ACTIVED', 15, NOW()),
                                                                                                                         ('hoangngocdiep_12a5@hoangcau.edu.vn', 'diephn402', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Diệp', 'Hoàng Ngọc', 'STUDENT', TRUE, 'ACTIVED', 15, NOW());