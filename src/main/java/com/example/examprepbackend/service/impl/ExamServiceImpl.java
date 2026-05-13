package com.example.examprepbackend.service.impl;

import com.example.examprepbackend.constant.ExamType;
import com.example.examprepbackend.dto.request.exams.ExamCreateRequest;
import com.example.examprepbackend.dto.request.exams.ExamFastCreateRequest;
import com.example.examprepbackend.dto.request.exams.ExamRequestParam;
import com.example.examprepbackend.dto.request.exams.ExamUpdateRequest;
import com.example.examprepbackend.dto.response.exams.ExamAttemptResponse;
import com.example.examprepbackend.dto.response.exams.ExamResponse;
import com.example.examprepbackend.dto.response.exams.ExamSummaryResponse;
import com.example.examprepbackend.dto.response.teacher.CategoryResponse;
import com.example.examprepbackend.dto.response.users.StudentResponse;
import com.example.examprepbackend.entity.*;
import com.example.examprepbackend.exception.ApplicationException;
import com.example.examprepbackend.repository.*;
import com.example.examprepbackend.service.ExamQuestionService;
import com.example.examprepbackend.service.ExamService;
import com.example.examprepbackend.service.QuestionParserService;
import com.example.examprepbackend.specification.ExamSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final UsersRepository usersRepository;
    private final CategoryQuestionRepository categoryQuestionRepository;
    private final QuestionRepository questionRepository;
    private final ClassRepository classRepository;
    private final ClassExamRepository classExamRepository;

    private final ExamQuestionService examQuestionService;
    private final QuestionParserService questionParserService;
    private final ModelMapper modelMapper;

    private ExamResponse convertToDto(Exam exam) {
        ExamResponse examResponse = new ExamResponse();
        BeanUtils.copyProperties(exam, examResponse);
        examResponse.setCategory(exam.getCategory().getName());
        examResponse.setCreatorName(exam.getCreator().getUsername());
        examResponse.setQuestions(examQuestionRepository.countByExam_Id(exam.getId()));
        examResponse.setAttempts(examAttemptRepository.countByExam_Id(exam.getId()));
        return examResponse;
    }

    private ExamAttemptResponse convertToExamAttempt(ExamAttempt examAttempt) {
        ExamAttemptResponse examAttemptResponse = new ExamAttemptResponse();
        BeanUtils.copyProperties(examAttempt, examAttemptResponse);
        StudentResponse studentResponse = new StudentResponse();
        BeanUtils.copyProperties(examAttempt.getStudent(), studentResponse);
        examAttemptResponse.setStudent(studentResponse);
        ExamSummaryResponse examSummaryResponse = new ExamSummaryResponse();
        BeanUtils.copyProperties(examAttempt.getExam(), examSummaryResponse);
        examAttemptResponse.setExam(examSummaryResponse);
        return examAttemptResponse;
    }

    private Specification<Exam> buildExamFilter(ExamRequestParam requestParam) {
        Specification<Exam> spec = Specification.where(null);
        if (requestParam == null) return spec;
        String code = requestParam.getCode();
        String title = requestParam.getTitle();
        String categoryName = requestParam.getCategoryName();
        LocalDate minDate = requestParam.getMinDate();
        LocalDate maxDate = requestParam.getMaxDate();
        if (code != null && !code.isBlank()) spec = spec.and(ExamSpecification.hasCodeLike(code));
        if (title != null && !title.isBlank()) spec = spec.and(ExamSpecification.hasTitleLike(title));
        if (categoryName != null && !categoryName.isBlank()) spec = spec.and(ExamSpecification.hasCategoryName(categoryName));
        if (minDate != null && maxDate != null) {
            spec = spec.and(ExamSpecification.hasCreateDate(minDate, maxDate));
        } else {
            if (minDate != null) spec = spec.and(ExamSpecification.hasAfterMinDate(minDate));
            if (maxDate != null) spec = spec.and(ExamSpecification.hasBeforeMaxDate(maxDate));
        }
        return spec;
    }


    @Override
    public Page<ExamResponse> getAllExams(ExamRequestParam examRequestParam, Pageable pageable) {
        Specification<Exam> spec = buildExamFilter(examRequestParam);
        return examRepository.findAll(spec, pageable).map(this::convertToDto);
    }

    @Override
    public Page<ExamResponse> getExamsByTeacherName(Authentication authentication, ExamRequestParam examRequestParam, Pageable pageable) {
        if (authentication == null || !authentication.isAuthenticated()) throw new ApplicationException("Unauthorized");
        String username = authentication.getName();
        Specification<Exam> spec = Specification.where(ExamSpecification.hasCreatorUsername(username)).and(buildExamFilter(examRequestParam));
        return examRepository.findAll(spec, pageable).map(this::convertToDto);
    }

    @Override
    public Page<ExamAttemptResponse> getExamAttemptsByTeacher(Authentication authentication, Pageable pageable) {
        if (authentication == null || !authentication.isAuthenticated()) throw new ApplicationException("Unauthorized");
        String teacherUsername = authentication.getName();
        List<Exam> examList = examRepository.findExamsByCreator_Username(teacherUsername);
        return examAttemptRepository.findByExamIn(examList, pageable).map(this::convertToExamAttempt);
    }

    @Override
    public List<ExamSummaryResponse> getExamsByClassId(Integer classId) {
        Optional<Classes> classesOptional = classRepository.findById(classId);
        if (classesOptional.isEmpty()) throw new ApplicationException("Class not found");
        List<Integer> examIdList = classExamRepository.findByClassId(classId);
        return examRepository.findByIdIn(examIdList).stream().map(e -> modelMapper.map(e, ExamSummaryResponse.class)).toList();
    }

    @Override
    public Page<ExamResponse> getPracticeExamsByClassId(Integer classId, ExamRequestParam examRequestParam, Pageable pageable) {
        Optional<Classes> classesOptional = classRepository.findById(classId);
        if (classesOptional.isEmpty()) throw new ApplicationException("Class not found");
        List<Integer> examIdList = classExamRepository.findByClassId(classId);
        Specification<Exam> spec = Specification.where(ExamSpecification.hasIdIn(examIdList)).and(ExamSpecification.hasExamType(ExamType.PRACTICE)).and(buildExamFilter(examRequestParam));
        return examRepository.findAll(spec, pageable).map(this::convertToDto);
    }

    @Override
    public Page<ExamResponse> getOfficialExamsByClassId(Integer classId, ExamRequestParam examRequestParam, Pageable pageable) {
        Optional<Classes> classesOptional = classRepository.findById(classId);
        if (classesOptional.isEmpty()) throw new ApplicationException("Class not found");
        List<Integer> examIdList = classExamRepository.findByClassId(classId);
        Specification<Exam> spec = Specification.where(ExamSpecification.hasIdIn(examIdList)).and(ExamSpecification.hasExamType(ExamType.OFFICIAL)).and(buildExamFilter(examRequestParam));
        return examRepository.findAll(spec, pageable).map(this::convertToDto);
    }

    @Transactional
    @Override
    public ExamSummaryResponse createExam(Authentication authentication, ExamCreateRequest examCreateRequest) {
        if (authentication == null || !authentication.isAuthenticated()) throw new ApplicationException("Unauthorized");
        Optional<Users> optionalUsers = usersRepository.findByUsername(authentication.getName());
        if (optionalUsers.isEmpty()) throw new ApplicationException("User not found");
        if (examRepository.findByCode(examCreateRequest.getCode()) != null) throw new ApplicationException("Exam code exited");
        CategoryQuestion category = categoryQuestionRepository.findByName(examCreateRequest.getCategory());
        if (category == null) throw new ApplicationException("Category not found");

        Users creator = optionalUsers.get();
        Exam newExam = modelMapper.map(examCreateRequest, Exam.class);
        newExam.setCreator(creator);
        newExam.setCategory(category);

        try {
            String typeStr = examCreateRequest.getExamType().toUpperCase();
            newExam.setExamType(ExamType.valueOf(typeStr));
        } catch (Exception e) {
            log.warn("Invalid ExamType received: {}. Defaulting to PRACTICE.", examCreateRequest.getExamType());
            newExam.setExamType(ExamType.PRACTICE);
        }

        newExam.setCreateDate(LocalDateTime.now());
        newExam.setIsActive(true);
        examRepository.save(newExam);

        List<Integer> questionIds = examCreateRequest.getQuestionIds();
        if (questionIds != null) {
            List<Question> questionList = questionRepository.findByIdIn(examCreateRequest.getQuestionIds());
            examQuestionService.createExamQuestions(newExam, questionList);
        }
        return modelMapper.map(newExam, ExamSummaryResponse.class);
    }

    @Transactional
    @Override
    public ExamSummaryResponse createExamFast(Authentication authentication, ExamFastCreateRequest request) {
        if (authentication == null || !authentication.isAuthenticated()) throw new ApplicationException("Unauthorized");
        Users creator = usersRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ApplicationException("User not found"));

        List<Question> parsedQuestions = questionParserService.parseRawText(request.getRawText());
        if (parsedQuestions.isEmpty()) {
            throw new ApplicationException("Không tìm thấy câu hỏi hợp lệ trong nội dung gõ!");
        }

        CategoryQuestion category = categoryQuestionRepository.findByName(request.getCategoryName());
        if (category == null) throw new ApplicationException("Danh mục không tồn tại");

        Exam exam = new Exam();
        exam.setTitle(request.getTitle());
        exam.setCode("FAST-" + System.currentTimeMillis());

        try {
            exam.setDuration(LocalTime.parse(request.getDuration()));
        } catch (Exception e) {
            exam.setDuration(LocalTime.of(1, 0));
        }

        exam.setCategory(category);
        exam.setCreator(creator);
        exam.setCreateDate(LocalDateTime.now());
        exam.setIsActive(true);
        exam.setPassScore(request.getPassScore() != null ? request.getPassScore() : 5.0);
        exam.setReviewAllowed(request.getReviewAllowed() != null ? request.getReviewAllowed() : true);

        try {
            exam.setExamType(ExamType.valueOf(request.getExamType().toUpperCase()));
        } catch (Exception e) {
            exam.setExamType(ExamType.PRACTICE);
        }

        exam = examRepository.save(exam);

        for (Question q : parsedQuestions) {
            q.setCreator(creator);
            q.setCategory(category);
            q.setCreateDate(LocalDateTime.now());
            Question savedQuestion = questionRepository.save(q);

            ExamQuestion examQuestion = new ExamQuestion();
            ExamQuestionId eqId = new ExamQuestionId(exam.getId(), savedQuestion.getId());
            examQuestion.setId(eqId);
            examQuestion.setExam(exam);
            examQuestion.setQuestion(savedQuestion);

            examQuestionRepository.save(examQuestion);
        }

        return modelMapper.map(exam, ExamSummaryResponse.class);
    }

    @Transactional
    @Override
    public ExamSummaryResponse createExamFromWord(
            Authentication authentication,
            MultipartFile file,
            String title,
            String categoryName,
            String duration,
            String examType
    ) {
        // 1. Kiểm tra User
        if (authentication == null || !authentication.isAuthenticated()) throw new ApplicationException("Unauthorized");
        Users creator = usersRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ApplicationException("User not found"));

        // 2. Bóc tách file Word thành List Question
        List<Question> parsedQuestions;
        try {
            parsedQuestions = questionParserService.parseWordFile(file);
        } catch (Exception e) {
            throw new ApplicationException("Lỗi khi đọc file Word: " + e.getMessage());
        }

        if (parsedQuestions.isEmpty()) {
            throw new ApplicationException("Không tìm thấy câu hỏi hợp lệ trong file Word!");
        }

        // 3. Kiểm tra danh mục
        CategoryQuestion category = categoryQuestionRepository.findByName(categoryName);
        if (category == null) throw new ApplicationException("Danh mục không tồn tại");

        // 4. Khởi tạo và lưu Exam
        Exam exam = new Exam();
        exam.setTitle(title);
        exam.setCode("WORD-" + System.currentTimeMillis()); // Mã định danh tự động cho file Word

        try {
            exam.setDuration(LocalTime.parse(duration));
        } catch (Exception e) {
            exam.setDuration(LocalTime.of(1, 0)); // Mặc định 1 giờ
        }

        exam.setCategory(category);
        exam.setCreator(creator);
        exam.setCreateDate(LocalDateTime.now());
        exam.setIsActive(true);
        exam.setPassScore(5.0); // Mặc định điểm đạt là 5
        exam.setReviewAllowed(true);

        try {
            exam.setExamType(ExamType.valueOf(examType.toUpperCase()));
        } catch (Exception e) {
            exam.setExamType(ExamType.PRACTICE);
        }

        exam = examRepository.save(exam);

        // 5. Lưu từng câu hỏi và tạo liên kết trung gian ExamQuestion
        for (Question q : parsedQuestions) {
            q.setCreator(creator);
            q.setCategory(category);
            q.setCreateDate(LocalDateTime.now());

            // Lưu câu hỏi (Answers sẽ được cascade lưu theo)
            Question savedQuestion = questionRepository.save(q);

            ExamQuestion examQuestion = new ExamQuestion();
            ExamQuestionId eqId = new ExamQuestionId(exam.getId(), savedQuestion.getId());
            examQuestion.setId(eqId);
            examQuestion.setExam(exam);
            examQuestion.setQuestion(savedQuestion);

            examQuestionRepository.save(examQuestion);
        }

        return modelMapper.map(exam, ExamSummaryResponse.class);
    }

    @Transactional
    @Override
    public ExamSummaryResponse updateExamById(Integer id, ExamUpdateRequest examUpdateRequest) {
        Optional<Exam> examOptional = examRepository.findById(id);
        if (examOptional.isEmpty()) throw new ApplicationException("Exam not found");
        Exam exam = examOptional.get();

        String currentCode = exam.getCode();
        String newCode = examUpdateRequest.getCode();
        if (!newCode.equals(currentCode) && examRepository.findByCode(newCode) != null) {
            throw new ApplicationException("Exam code exited");
        }

        modelMapper.map(examUpdateRequest, exam);

        try {
            String typeStr = examUpdateRequest.getExamType().toUpperCase();
            exam.setExamType(ExamType.valueOf(typeStr));
        } catch (Exception e) {
            log.warn("Invalid ExamType in update: {}. Keep current type.", examUpdateRequest.getExamType());
        }

        examRepository.save(exam);
        examQuestionRepository.deleteByExam_Id(id);
        List<Integer> questionIds = examUpdateRequest.getQuestionIds();
        if (questionIds != null) {
            List<Question> questionList = questionRepository.findByIdIn(examUpdateRequest.getQuestionIds());
            examQuestionService.createExamQuestions(exam, questionList);
        }
        return modelMapper.map(exam, ExamSummaryResponse.class);
    }

    @Transactional
    @Override
    public Boolean deleteExamById(Integer id) {
        if (examRepository.findById(id).isEmpty()) throw new ApplicationException("Exam not found");
        examRepository.deleteById(id);
        return true;
    }

    @Override
    public List<CategoryResponse> getAllCategory() {
        return categoryQuestionRepository.findAll()
                .stream()
                .map(cat -> modelMapper.map(cat, CategoryResponse.class))
                .toList();
    }
}