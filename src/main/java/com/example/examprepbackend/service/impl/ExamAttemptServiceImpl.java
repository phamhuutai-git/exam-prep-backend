package com.example.examprepbackend.service.impl;

import com.example.examprepbackend.config.SecurityUtils;
import com.example.examprepbackend.constant.AttemptStatus;
import com.example.examprepbackend.constant.ExamType;
import com.example.examprepbackend.constant.Role;
import com.example.examprepbackend.dto.request.exams.SubmitAnswerRequest;
import com.example.examprepbackend.dto.request.exams.SubmitExamAttemptRequest;
import com.example.examprepbackend.dto.response.exams.*;
import com.example.examprepbackend.dto.response.questions.AttemptQuestionOptionResponse;
import com.example.examprepbackend.dto.response.questions.AttemptQuestionResponse;
import com.example.examprepbackend.dto.response.questions.AttemptQuestionsFullResponse;
import com.example.examprepbackend.dto.response.questions.QuestionPublicResponse;
import com.example.examprepbackend.dto.response.teacher.ScoreDistribution;
import com.example.examprepbackend.dto.response.teacher.TeacherStatsResponse;
import com.example.examprepbackend.dto.response.users.StudentResponse;
import com.example.examprepbackend.entity.*;
import com.example.examprepbackend.exception.*;
import com.example.examprepbackend.repository.*;
import com.example.examprepbackend.service.ExamAttemptService;
import com.example.examprepbackend.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamAttemptServiceImpl implements ExamAttemptService {

    private final ExamRepository examRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final UsersRepository usersRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final ClassExamRepository classExamRepository;
    private final QuestionService questionService;
    private final ModelMapper modelMapper;

    private ExamStartResponse convertToStartDto(ExamAttempt examAttempt) {
        ExamStartResponse startResponse = new ExamStartResponse();
        startResponse.setAttemptId(examAttempt.getId());
        startResponse.setExamCode(examAttempt.getExam().getCode());
        startResponse.setExamTitle(examAttempt.getExam().getTitle());
        startResponse.setDuration(examAttempt.getExam().getDuration());
        startResponse.setExamType(examAttempt.getExam().getExamType().toString());
        startResponse.setStartTime(examAttempt.getStartTime());

        List<QuestionPublicResponse> questionPublicResponses = questionService.getQuestionsPublicByExamId(examAttempt.getExam().getId());
        startResponse.setQuestions(questionPublicResponses);
        return startResponse;
    }

    @Override
    @Transactional
    public ExamStartResponse startExam(Integer examId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ApplicationException("Unauthorized");
        }

        Users student = usersRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ApplicationException("User not found"));

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ApplicationException("Exam not found"));

        // Kiểm tra đề thi có thuộc lớp của học sinh không
        ClassExam classExam = classExamRepository.findByClassIdAndExamId(student.getClasses().getId(), examId);
        if (classExam == null) {
            throw new ApplicationException("The selected exam does not belong to this class.");
        }

        // --- FIX LỖI Ở ĐÂY: Sử dụng existsByExamIdAndStudentId... để tránh so sánh Object ---
        if (examAttemptRepository.existsByExamIdAndStudentIdAndStatus(examId, student.getId(), AttemptStatus.IN_PROGRESS)) {
            throw new ApplicationException("You already have an ongoing attempt for this exam.");
        }

        ExamAttempt examAttempt = new ExamAttempt();
        examAttempt.setExam(exam);
        examAttempt.setStudent(student);
        examAttempt.setStartTime(LocalDateTime.now());
        examAttempt.setScore(0.0);
        examAttempt.setCorrectCount(0);
        examAttempt.setWrongCount(0);
        examAttempt.setBlankCount(0);
        examAttempt.setTimeSpentSeconds(0);
        examAttempt.setStatus(AttemptStatus.IN_PROGRESS);

        return convertToStartDto(examAttemptRepository.save(examAttempt));
    }

    @Transactional
    @Override
    public ExamStartResponse restartExam(Integer examId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ApplicationException("Unauthorized");
        }

        Users student = usersRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ApplicationException("User not found"));

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ApplicationException("Exam not found"));

        // --- FIX LỖI Ở ĐÂY: Tìm để xóa dựa trên cả Object hoặc ID ---
        // Nếu Repo của bạn vẫn bị lỗi khi truyền Object, hãy dùng findByIdAndStudentId...
        ExamAttempt examAttemptExits = examAttemptRepository.findByExamAndStudentAndStatus(exam, student, AttemptStatus.IN_PROGRESS);
        if (examAttemptExits != null) {
            examAttemptRepository.delete(examAttemptExits);
        }

        ExamAttempt examAttempt = new ExamAttempt();
        examAttempt.setExam(exam);
        examAttempt.setStudent(student);
        examAttempt.setStartTime(LocalDateTime.now());
        examAttempt.setScore(0.0);
        examAttempt.setCorrectCount(0);
        examAttempt.setWrongCount(0);
        examAttempt.setBlankCount(0);
        examAttempt.setTimeSpentSeconds(0);
        examAttempt.setStatus(AttemptStatus.IN_PROGRESS);

        return convertToStartDto(examAttemptRepository.save(examAttempt));
    }

    // Các hàm của Hieu
    @Transactional
    @Override
    public StartExamAttemptResponse startAttempt(Integer examId) {
        Users currentUser = getCurrentUser();
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + examId));

        validateExamCanBeStarted(exam);

        ExamAttempt attempt = new ExamAttempt();
        attempt.setStudent(currentUser);
        attempt.setExam(exam);
        attempt.setStatus(AttemptStatus.IN_PROGRESS);
        attempt.setStartTime(LocalDateTime.now());

        ExamAttempt savedAttempt = examAttemptRepository.save(attempt);

        return StartExamAttemptResponse.builder()
                .attemptId(savedAttempt.getId())
                .examId(exam.getId())
                .examTitle(exam.getTitle())
                .status(savedAttempt.getStatus().name())
                .startTime(savedAttempt.getStartTime())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AttemptQuestionsFullResponse getAttemptQuestions(Integer attemptId) {
        Users currentUser = getCurrentUser();
        ExamAttempt attempt = examAttemptRepository.findByIdAndStudentUsername(attemptId, currentUser.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found with id: " + attemptId));

        validateAttemptCanLoadQuestions(attempt);

        Exam exam = attempt.getExam();
        List<Question> questions = questionRepository.findQuestionsByExamId(exam.getId());
        List<Integer> questionIds = questions.stream().map(Question::getId).toList();
        Map<Integer, List<Answer>> answersByQuestionId = getAnswersByQuestionId(questionIds);

        List<AttemptQuestionResponse> questionResponses = questions.stream()
                .map(question -> toAttemptQuestionResponse(question, answersByQuestionId.getOrDefault(question.getId(), Collections.emptyList())))
                .toList();

        return AttemptQuestionsFullResponse.builder()
                .attemptId(attempt.getId())
                .examId(exam.getId())
                .examTitle(exam.getTitle())
                .status(attempt.getStatus())
                .startTime(attempt.getStartTime())
                .questions(questionResponses)
                .build();
    }

    @Override
    @Transactional
    public SubmitExamAttemptResponse submitAttempt(Integer attemptId, SubmitExamAttemptRequest request) {
        Users currentUser = getCurrentUser();
        ExamAttempt attempt = examAttemptRepository.findByIdAndStudentUsername(attemptId, currentUser.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found with id: " + attemptId));

        validateAttemptCanBeSubmitted(attempt);

        Exam exam = attempt.getExam();
        List<Question> questions = questionRepository.findQuestionsByExamId(exam.getId());
        if (questions.isEmpty()) throw new BadRequestException("This exam has no questions");

        List<Integer> questionIds = questions.stream().map(Question::getId).toList();
        List<Answer> allAnswers = answerRepository.findByQuestionIdIn(questionIds);
        Map<Integer, Answer> answerMap = allAnswers.stream().collect(Collectors.toMap(Answer::getId, Function.identity()));
        Map<Integer, SubmitAnswerRequest> submittedAnswerMap = normalizeSubmittedAnswers(request);

        ResultSummary result = gradeAndSaveAnswers(attempt, questions, answerMap, submittedAnswerMap);

        LocalDateTime endTime = LocalDateTime.now();
        int totalQuestions = questions.size();
        int timeSpentSeconds = calculateTimeSpentSeconds(attempt.getStartTime(), endTime);

        // --- CẬP NHẬT: Tính điểm về thang điểm 10 để khớp với ScoreDistribution ---
        double score = calculateScore(result.correctCount(), totalQuestions);

        Double passScore = exam.getPassScore();
        boolean passed = score >= (passScore != null ? passScore : 5.0);

        attempt.setEndTime(endTime);
        attempt.setStatus(AttemptStatus.SUBMITTED);
        attempt.setScore(score);
        attempt.setCorrectCount(result.correctCount());
        attempt.setWrongCount(result.wrongCount());
        attempt.setBlankCount(result.blankCount());
        attempt.setTimeSpentSeconds(timeSpentSeconds);

        examAttemptRepository.save(attempt);

        return SubmitExamAttemptResponse.builder()
                .attemptId(attempt.getId())
                .examId(exam.getId())
                .examTitle(exam.getTitle())
                .examType(exam.getExamType())
                .score(score)
                .passScore(passScore)
                .passed(passed)
                .resultStatus(passed ? "PASSED" : "FAILED")
                .totalQuestions(totalQuestions)
                .correctCount(result.correctCount())
                .wrongCount(result.wrongCount())
                .blankCount(result.blankCount())
                .timeSpentSeconds(timeSpentSeconds)
                .reviewAllowed(exam.getReviewAllowed())
                .endTime(endTime)
                .message("Submit exam successfully")
                .build();
    }

    private double calculateScore(int correctCount, int totalQuestions) {
        if (totalQuestions <= 0) return 0.0;
        // Tính điểm trên thang điểm 10 và làm tròn 2 chữ số thập phân
        double rawScore = (correctCount * 10.0) / totalQuestions;
        return Math.round(rawScore * 100.0) / 100.0;
    }

    // --- CÁC HÀM HỖ TRỢ GIỮ NGUYÊN LOGIC ---
    private Users getCurrentUser() {
        String currentUsername = SecurityUtils.getCurrentUsername();
        if (currentUsername == null || currentUsername.isBlank()) throw new BadRequestException("Not authenticated");
        return usersRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void validateExamCanBeStarted(Exam exam) {
        if (exam.getIsActive() == null || !exam.getIsActive()) throw new BadRequestException("Exam is not active");
    }

    private void validateAttemptCanLoadQuestions(ExamAttempt attempt) {
        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) throw new BadRequestException("Attempt not in progress");
    }

    private void validateAttemptCanBeSubmitted(ExamAttempt attempt) {
        if (attempt.getStatus() == AttemptStatus.SUBMITTED) throw new ConflictException("Already submitted");
    }

    private Map<Integer, List<Answer>> getAnswersByQuestionId(List<Integer> questionIds) {
        if (questionIds == null || questionIds.isEmpty()) return Collections.emptyMap();
        return answerRepository.findByQuestionIdIn(questionIds).stream()
                .collect(Collectors.groupingBy(answer -> answer.getQuestion().getId()));
    }

    private AttemptQuestionResponse toAttemptQuestionResponse(Question question, List<Answer> answers) {
        List<AttemptQuestionOptionResponse> options = answers.stream()
                .sorted(Comparator.comparing(a -> a.getLabel() != null ? a.getLabel() : ""))
                .map(a -> AttemptQuestionOptionResponse.builder()
                        .optionId(a.getId()).optionLabel(a.getLabel()).optionContent(a.getContent()).build())
                .toList();
        return AttemptQuestionResponse.builder().questionId(question.getId()).questionContent(question.getContent()).options(options).build();
    }

    private Map<Integer, SubmitAnswerRequest> normalizeSubmittedAnswers(SubmitExamAttemptRequest request) {
        if (request == null || request.getAnswers() == null) return Collections.emptyMap();
        Map<Integer, SubmitAnswerRequest> map = new HashMap<>();
        for (SubmitAnswerRequest ans : request.getAnswers()) {
            if (ans.getQuestionId() == null) throw new BadRequestException("Question ID required");
            map.put(ans.getQuestionId(), ans);
        }
        return map;
    }

    private ResultSummary gradeAndSaveAnswers(ExamAttempt attempt, List<Question> questions, Map<Integer, Answer> answerMap, Map<Integer, SubmitAnswerRequest> submittedMap) {
        int correct = 0, wrong = 0, blank = 0;
        List<StudentAnswer> studentAnswers = new ArrayList<>();
        for (Question q : questions) {
            SubmitAnswerRequest submitted = submittedMap.get(q.getId());
            if (submitted == null || submitted.getSelectedOptionId() == null) {
                blank++;
                studentAnswers.add(buildUserAnswer(attempt, q, null, false));
                continue;
            }
            Answer selected = answerMap.get(submitted.getSelectedOptionId());
            boolean isCorrect = selected != null && Boolean.TRUE.equals(selected.getIsCorrect());
            if (isCorrect) correct++; else wrong++;
            studentAnswers.add(buildUserAnswer(attempt, q, selected, isCorrect));
        }
        userAnswerRepository.saveAll(studentAnswers);
        return new ResultSummary(correct, wrong, blank);
    }

    private StudentAnswer buildUserAnswer(ExamAttempt attempt, Question question, Answer selectedAnswer, boolean isCorrect) {
        StudentAnswer sa = new StudentAnswer();
        sa.setAttempt(attempt); sa.setQuestion(question); sa.setSelectedAnswer(selectedAnswer); sa.setIsCorrect(isCorrect);
        return sa;
    }

    private int calculateTimeSpentSeconds(LocalDateTime start, LocalDateTime end) {
        return (start == null || end == null) ? 0 : (int) Math.max(Duration.between(start, end).getSeconds(), 0);
    }

    private record ResultSummary(int correctCount, int wrongCount, int blankCount) {}

    @Transactional(readOnly = true)
    @Override
    public AttemptResultResponse getAttemptResult(Integer attemptId) {
        Users currentUser = getCurrentUser();
        ExamAttempt attempt = examAttemptRepository.findByIdAndStudentUsername(attemptId, currentUser.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found"));

        validateAttemptCanViewResult(attempt);
        Exam exam = attempt.getExam();
        int correct = defaultIfNull(attempt.getCorrectCount());
        int wrong = defaultIfNull(attempt.getWrongCount());
        int blank = defaultIfNull(attempt.getBlankCount());
        int total = correct + wrong + blank;
        double score = attempt.getScore() != null ? attempt.getScore() : 0.0;
        double pass = exam.getPassScore() != null ? exam.getPassScore() : 5.0;

        return AttemptResultResponse.builder()
                .attemptId(attempt.getId()).examId(exam.getId()).examTitle(exam.getTitle()).examType(exam.getExamType())
                .score(score).passScore(pass).passed(score >= pass).resultStatus(score >= pass ? "PASSED" : "FAILED")
                .totalQuestions(total).correctCount(correct).wrongCount(wrong).blankCount(blank)
                .timeSpentSeconds(defaultIfNull(attempt.getTimeSpentSeconds())).reviewAllowed(isReviewAllowed(exam))
                .submittedAt(attempt.getEndTime()).build();
    }

    private void validateAttemptCanViewResult(ExamAttempt attempt) {
        if (attempt.getStatus() != AttemptStatus.SUBMITTED && attempt.getStatus() != AttemptStatus.GRADED) {
            throw new BadRequestException("Attempt not submitted yet");
        }
    }

    private void validateAttemptOwner(ExamAttempt attempt, Users currentUser) {
        if (!Objects.equals(attempt.getStudent().getId(), currentUser.getId())) throw new ForbiddenException("Access denied");
    }

    private int defaultIfNull(Integer value) { return value == null ? 0 : value; }

    private boolean isReviewAllowed(Exam exam) {
        return exam != null && exam.getExamType() != null && Set.of(ExamType.MOCK, ExamType.PRACTICE).contains(exam.getExamType());
    }

    @Override
    @Transactional(readOnly = true)
    public AttemptReviewDetailResponse getAttemptReviewDetail(Integer attemptId) {
        Users currentUser = getCurrentUser();
        ExamAttempt attempt = examAttemptRepository.findById(attemptId).orElseThrow(() -> new ResourceNotFoundException("Attempt not found"));
        validateAttemptOwner(attempt, currentUser);
        validateAttemptCanViewResult(attempt);
        Exam exam = attempt.getExam();
        if (!isReviewAllowed(exam)) throw new BadRequestException("Review not allowed");

        List<Question> questions = questionRepository.findQuestionsByExamId(exam.getId());
        List<Integer> qIds = questions.stream().map(Question::getId).toList();
        List<Answer> allAns = qIds.isEmpty() ? Collections.emptyList() : answerRepository.findByQuestionIdIn(qIds);
        Map<Integer, List<Answer>> ansMap = allAns.stream().collect(Collectors.groupingBy(a -> a.getQuestion().getId()));
        Map<Integer, StudentAnswer> studentAnsMap = userAnswerRepository.findByAttemptId(attemptId).stream()
                .collect(Collectors.toMap(sa -> sa.getQuestion().getId(), Function.identity(), (e, r) -> e));

        List<ReviewQuestionResponse> reviewQuestions = questions.stream()
                .map(q -> mapToReviewQuestionResponse(q, ansMap.getOrDefault(q.getId(), Collections.emptyList()), studentAnsMap.get(q.getId())))
                .toList();

        return AttemptReviewDetailResponse.builder().attemptId(attempt.getId()).examId(exam.getId()).examTitle(exam.getTitle())
                .examType(exam.getExamType()).reviewAllowed(true).submittedAt(attempt.getEndTime()).questions(reviewQuestions).build();
    }

    private ReviewQuestionResponse mapToReviewQuestionResponse(Question q, List<Answer> ans, StudentAnswer sa) {
        Integer selectedId = (sa != null && sa.getSelectedAnswer() != null) ? sa.getSelectedAnswer().getId() : null;
        Integer correctId = ans.stream().filter(a -> Boolean.TRUE.equals(a.getIsCorrect())).findFirst().map(Answer::getId).orElse(null);
        List<ReviewOptionResponse> options = ans.stream().sorted(Comparator.comparing(a -> a.getLabel() != null ? a.getLabel() : ""))
                .map(a -> ReviewOptionResponse.builder().optionId(a.getId()).optionLabel(a.getLabel()).optionContent(a.getContent()).isCorrect(Boolean.TRUE.equals(a.getIsCorrect())).build())
                .toList();
        return ReviewQuestionResponse.builder().questionId(q.getId()).questionContent(q.getContent()).selectedOptionId(selectedId).correctOptionId(correctId)
                .isCorrect(sa != null && Boolean.TRUE.equals(sa.getIsCorrect())).explanation(q.getExplanation()).options(options).build();
    }

    @Override
    public Page<ExamAttemptResponse> getAttemptsByExamType(Authentication authentication, Pageable pageable, String examType) {
        if (examType == null || examType.isBlank()) throw new ApplicationException("Type required");
        Users user = usersRepository.findByUsername(authentication.getName()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return examAttemptRepository.findByStudentAndExamExamType(user, ExamType.valueOf(examType.toUpperCase()), pageable)
                .map(a -> {
                    ExamAttemptResponse res = new ExamAttemptResponse();
                    res.setId(a.getId()); res.setExam(modelMapper.map(a.getExam(), ExamSummaryResponse.class));
                    res.setStudent(modelMapper.map(a.getStudent(), StudentResponse.class)); res.setStartTime(a.getStartTime());
                    res.setEndTime(a.getEndTime()); res.setScore(a.getScore()); res.setStatus(a.getStatus());
                    res.setBlankCount(a.getBlankCount()); res.setCorrectCount(a.getCorrectCount()); res.setWrongCount(a.getWrongCount());
                    res.setTimeSpentSeconds(a.getTimeSpentSeconds()); return res;
                });
    }

    @Override
    public List<ScoreDistribution> getScoreDistribution() {
        List<ScoreDistribution> data = examAttemptRepository.getScoreDistribution();
        Map<String, Long> map = data.stream().collect(Collectors.toMap(ScoreDistribution::getRange, ScoreDistribution::getCount));
        return List.of("0-4", "4-5", "5-6", "6-7", "7-8", "8-9", "9-10").stream()
                .map(r -> new ScoreDistribution(r, map.getOrDefault(r, 0L))).toList();
    }

    @Override
    public TeacherStatsResponse getTeacherStats() {
        return new TeacherStatsResponse(examRepository.count(), questionRepository.count(), usersRepository.countByRole(Role.STUDENT));
    }
}