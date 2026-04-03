package com.example.examprepbackend.service.impl;

import com.example.examprepbackend.config.SecurityUtils;
import com.example.examprepbackend.constant.AttemptStatus;
import com.example.examprepbackend.constant.ExamType;
import com.example.examprepbackend.dto.request.exams.SubmitAnswerRequest;
import com.example.examprepbackend.dto.request.exams.SubmitExamAttemptRequest;
import com.example.examprepbackend.dto.response.exams.*;
import com.example.examprepbackend.dto.response.questions.AttemptQuestionOptionResponse;
import com.example.examprepbackend.dto.response.questions.AttemptQuestionResponse;
import com.example.examprepbackend.dto.response.questions.AttemptQuestionsFullResponse;
import com.example.examprepbackend.dto.response.questions.QuestionPublicResponse;
import com.example.examprepbackend.dto.response.teacher.ScoreDistribution;
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

    private static final Set<ExamType> REVIEW_ALLOWED_TYPES =
            Set.of(ExamType.MOCK, ExamType.PRACTICE);

    private ExamStartResponse convertToStartDto(ExamAttempt examAttempt) {
        ExamStartResponse startResponse = new ExamStartResponse();

        startResponse.setAttemptId(examAttempt.getId());
        startResponse.setExamCode(examAttempt.getExam().getCode());
        startResponse.setExamTitle(examAttempt.getExam().getTitle());
        startResponse.setDuration(examAttempt.getExam().getDuration());
        startResponse.setExamType(examAttempt.getExam().getExamType().toString());
        startResponse.setStartTime(examAttempt.getStartTime());

        List<QuestionPublicResponse> questionPublicResponses =
                questionService.getQuestionsPublicByExamId(examAttempt.getExam().getId());
        startResponse.setQuestions(questionPublicResponses);

        return startResponse;
    }

    @Override
    @Transactional
    public ExamStartResponse startExam(Integer examId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ApplicationException("Unauthorized");
        }

        String username = authentication.getName();
        Users student = usersRepository.findByUsername(username)
                .orElseThrow(() -> new ApplicationException("User not found"));

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ApplicationException("Exam not found"));

        ClassExam classExam = classExamRepository.findByClassIdAndExamId(student.getClasses().getId(), examId);
        if (classExam == null) {
            throw new ApplicationException("The selected exam does not belong to this class.");
        }

        ExamAttempt existingAttempt =
                examAttemptRepository.findByExamAndStudentAndStatus(exam, student, AttemptStatus.IN_PROGRESS);

        if (existingAttempt != null) {
            throw new ApplicationException("You already have an ongoing attempt for this exam. Please complete or submit it before starting a new one.");
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

        try {
            examAttemptRepository.save(examAttempt);
            return convertToStartDto(examAttempt);
        } catch (RuntimeException exception) {
            throw new ApplicationException("Already has attempt");
        }
    }

    @Override
    @Transactional
    public ExamStartResponse restartExam(Integer examId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ApplicationException("Unauthorized");
        }

        String username = authentication.getName();
        Users student = usersRepository.findByUsername(username)
                .orElseThrow(() -> new ApplicationException("User not found"));

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ApplicationException("Exam not found"));

        ClassExam classExam = classExamRepository.findByClassIdAndExamId(student.getClasses().getId(), examId);
        if (classExam == null) {
            throw new ApplicationException("The selected exam does not belong to this class.");
        }

        ExamAttempt existingAttempt =
                examAttemptRepository.findByExamAndStudentAndStatus(exam, student, AttemptStatus.IN_PROGRESS);

        if (existingAttempt != null) {
            examAttemptRepository.delete(existingAttempt);
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

        try {
            examAttemptRepository.save(examAttempt);
            return convertToStartDto(examAttempt);
        } catch (RuntimeException exception) {
            throw new ApplicationException("Already has attempt");
        }
    }

    @Override
    @Transactional
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
        attempt.setScore(0.0);
        attempt.setCorrectCount(0);
        attempt.setWrongCount(0);
        attempt.setBlankCount(0);
        attempt.setTimeSpentSeconds(0);

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
        validateExamExists(exam, attemptId);

        List<Question> questions = questionRepository.findQuestionsByExamId(exam.getId());

        List<Integer> questionIds = questions.stream()
                .map(Question::getId)
                .toList();

        Map<Integer, List<Answer>> answersByQuestionId = getAnswersByQuestionId(questionIds);

        List<AttemptQuestionResponse> questionResponses = questions.stream()
                .map(question -> toAttemptQuestionResponse(
                        question,
                        answersByQuestionId.getOrDefault(question.getId(), Collections.emptyList())
                ))
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

        System.out.println("=== ENTER submitAttempt ===");
        System.out.println("attemptId = " + attemptId);

        if (request == null) {
            System.out.println("request = null");
        } else if (request.getAnswers() == null) {
            System.out.println("request.answers = null");
        } else {
            System.out.println("answers size = " + request.getAnswers().size());
            for (SubmitAnswerRequest a : request.getAnswers()) {
                System.out.println("questionId = " + a.getQuestionId());
                System.out.println("selectedAnswerIds = " + a.getSelectedAnswerIds());
            }
        }
        Users currentUser = getCurrentUser();

        ExamAttempt attempt = examAttemptRepository.findByIdAndStudentUsername(attemptId, currentUser.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found with id: " + attemptId));

        validateAttemptCanBeSubmitted(attempt);

        Exam exam = attempt.getExam();
        validateExamExists(exam, attemptId);

        List<Question> questions = questionRepository.findQuestionsByExamId(exam.getId());
        if (questions.isEmpty()) {
            throw new BadRequestException("This exam has no questions");
        }

        List<Integer> questionIds = questions.stream()
                .map(Question::getId)
                .toList();


        Map<Integer, List<Answer>> answersByQuestionId = getAnswersByQuestionId(questionIds);
        Map<Integer, SubmitAnswerRequest> submittedAnswerMap = normalizeSubmittedAnswers(request);

        ResultSummary result = gradeAndSaveAnswers(attempt, questions, answersByQuestionId, submittedAnswerMap);

        LocalDateTime endTime = LocalDateTime.now();
        int totalQuestions = questions.size();
        int timeSpentSeconds = calculateTimeSpentSeconds(attempt.getStartTime(), endTime);
        double score = calculateScore(result.correctCount(), totalQuestions);

        double passScore = defaultIfNull(exam.getPassScore());
        boolean passed = score >= passScore;
        String resultStatus = passed ? "PASSED" : "FAILED";

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
                .resultStatus(resultStatus)
                .totalQuestions(totalQuestions)
                .correctCount(result.correctCount())
                .wrongCount(result.wrongCount())
                .blankCount(result.blankCount())
                .timeSpentSeconds(timeSpentSeconds)
                .reviewAllowed(isReviewAllowed(exam))
                .endTime(endTime)
                .message("Submit exam successfully")
                .build();

    }


    @Override
    @Transactional(readOnly = true)
    public AttemptResultResponse getAttemptResult(Integer attemptId) {
        Users currentUser = getCurrentUser();

        ExamAttempt attempt = examAttemptRepository.findByIdAndStudentUsername(attemptId, currentUser.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found with id: " + attemptId));

        validateAttemptCanViewResult(attempt);

        Exam exam = attempt.getExam();
        validateExamExists(exam, attemptId);

        int correctCount = defaultIfNull(attempt.getCorrectCount());
        int wrongCount = defaultIfNull(attempt.getWrongCount());
        int blankCount = defaultIfNull(attempt.getBlankCount());

        int totalQuestions = calculateTotalQuestions(exam, correctCount, wrongCount, blankCount);

        double score = defaultIfNull(attempt.getScore());
        double passScore = defaultIfNull(exam.getPassScore());
        boolean passed = score >= passScore;
        String resultStatus = passed ? "PASSED" : "FAILED";
        boolean reviewAllowed = isReviewAllowed(exam);

        AttemptResultResponse.AttemptResultResponseBuilder responseBuilder = AttemptResultResponse.builder()
                .attemptId(attempt.getId())
                .examId(exam.getId())
                .examTitle(exam.getTitle())
                .examType(exam.getExamType())
                .score(score)
                .passScore(passScore)
                .passed(passed)
                .resultStatus(resultStatus)
                .totalQuestions(totalQuestions)
                .correctCount(correctCount)
                .wrongCount(wrongCount)
                .blankCount(blankCount)
                .timeSpentSeconds(defaultIfNull(attempt.getTimeSpentSeconds()))
                .reviewAllowed(reviewAllowed)
                .submittedAt(attempt.getEndTime());

        if (reviewAllowed) {
            responseBuilder.questions(buildReviewQuestionResponses(attempt, exam));
        }

        return responseBuilder.build();
    }

    @Override
    public Page<ExamAttemptResponse> getAttemptsByExamType(Authentication authentication, Pageable pageable, String examType) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ApplicationException("Unauthorized");
        }

        String username = authentication.getName();
        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Page<ExamAttempt> attempts = examAttemptRepository.findByStudentAndExamExamType(
                user,
                ExamType.valueOf(examType),
                pageable
        );

        return attempts.map(attempt -> {
            ExamAttemptResponse res = new ExamAttemptResponse();
            res.setId(attempt.getId());
            res.setExam(modelMapper.map(attempt.getExam(), ExamSummaryResponse.class));
            res.setStudent(modelMapper.map(attempt.getStudent(), StudentResponse.class));
            res.setStartTime(attempt.getStartTime());
            res.setEndTime(attempt.getEndTime());
            res.setScore(attempt.getScore());
            res.setStatus(attempt.getStatus());
            res.setBlankCount(attempt.getBlankCount());
            res.setCorrectCount(attempt.getCorrectCount());
            res.setWrongCount(attempt.getWrongCount());
            res.setTimeSpentSeconds(attempt.getTimeSpentSeconds());
            return res;
        });
    }

    @Override
    public List<ScoreDistribution> getScoreDistribution() {
        List<ScoreDistribution> data = examAttemptRepository.getScoreDistribution();

        Map<String, Long> map = new HashMap<>();
        for (ScoreDistribution d : data) {
            map.put(d.getRange(), d.getCount());
        }

        List<String> ranges = List.of("0-4", "4-5", "5-6", "6-7", "7-8", "8-9", "9-10");
        List<ScoreDistribution> result = new ArrayList<>();

        for (String r : ranges) {
            result.add(new ScoreDistribution(r, map.getOrDefault(r, 0L)));
        }

        return result;
    }

    private Users getCurrentUser() {
        String currentUsername = getValidatedCurrentUsername();

        return usersRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + currentUsername));
    }

    private String getValidatedCurrentUsername() {
        String currentUsername = SecurityUtils.getCurrentUsername();
        if (currentUsername == null || currentUsername.isBlank()) {
            throw new BadRequestException("Current user is not authenticated");
        }
        return currentUsername;
    }

    private void validateExamCanBeStarted(Exam exam) {
        if (exam.getIsActive() == null || !exam.getIsActive()) {
            throw new BadRequestException("Exam is not active");
        }
    }

    private void validateExamExists(Exam exam, Integer attemptId) {
        if (exam == null) {
            throw new ResourceNotFoundException("Exam not found for attempt id: " + attemptId);
        }
    }

    private void validateAttemptCanLoadQuestions(ExamAttempt attempt) {
        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            throw new BadRequestException("Cannot load questions because this attempt is no longer in progress");
        }
    }

    private void validateAttemptCanBeSubmitted(ExamAttempt attempt) {
        if (attempt.getStatus() == AttemptStatus.SUBMITTED) {
            throw new ConflictException("This attempt has already been submitted");
        }

        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            throw new BadRequestException("This attempt is not in a submittable state");
        }
    }

    private void validateAttemptCanViewResult(ExamAttempt attempt) {
        if (attempt.getStatus() != AttemptStatus.SUBMITTED
                && attempt.getStatus() != AttemptStatus.GRADED) {
            throw new BadRequestException("Cannot view result because this attempt has not been submitted yet");
        }
    }

    private Map<Integer, List<Answer>> getAnswersByQuestionId(List<Integer> questionIds) {
        if (questionIds == null || questionIds.isEmpty()) {
            return Collections.emptyMap();
        }

        return answerRepository.findByQuestionIdIn(questionIds)
                .stream()
                .collect(Collectors.groupingBy(answer -> answer.getQuestion().getId()));
    }

    private AttemptQuestionResponse toAttemptQuestionResponse(Question question, List<Answer> answers) {
        List<AttemptQuestionOptionResponse> optionResponses = answers.stream()
                .sorted(Comparator.comparing(answer -> {
                    String label = answer.getLabel();
                    return label == null ? "" : label;
                }))
                .map(answer -> AttemptQuestionOptionResponse.builder()
                        .optionId(answer.getId())
                        .optionLabel(answer.getLabel())
                        .optionContent(answer.getContent())
                        .build())
                .toList();

        return AttemptQuestionResponse.builder()
                .questionId(question.getId())
                .questionContent(question.getContent())
                .options(optionResponses)
                .build();
    }

    private Map<Integer, SubmitAnswerRequest> normalizeSubmittedAnswers(SubmitExamAttemptRequest request) {
        if (request == null || request.getAnswers() == null || request.getAnswers().isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Integer, SubmitAnswerRequest> result = new HashMap<>();

        for (SubmitAnswerRequest answer : request.getAnswers()) {
            if (answer == null || answer.getQuestionId() == null) {
                throw new BadRequestException("Question id is required in submitted answers");
            }

            if (result.containsKey(answer.getQuestionId())) {
                throw new BadRequestException("Duplicate answer detected for question id: " + answer.getQuestionId());
            }

            result.put(answer.getQuestionId(), answer);
        }

        return result;
    }

    private ResultSummary gradeAndSaveAnswers(
            ExamAttempt attempt,
            List<Question> questions,
            Map<Integer, List<Answer>> answersByQuestionId,
            Map<Integer, SubmitAnswerRequest> submittedAnswerMap
    ) {
        int correctCount = 0;
        int wrongCount = 0;
        int blankCount = 0;

        deleteExistingStudentAnswers(attempt.getId());

        for (Question question : questions) {
            SubmitAnswerRequest submitted = submittedAnswerMap.get(question.getId());

            if (submitted == null || submitted.getSelectedAnswerIds() == null || submitted.getSelectedAnswerIds().isEmpty()) {
                blankCount++;
                continue;
            }

            List<Integer> selectedIds = submitted.getSelectedAnswerIds().stream()
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();

            if (selectedIds.isEmpty()) {
                blankCount++;
                continue;
            }

            if (selectedIds.size() > 1) {
                throw new BadRequestException(
                        "Question id " + question.getId() + " currently supports only one selected answer"
                );
            }

            List<Answer> answersOfQuestion = answersByQuestionId.getOrDefault(question.getId(), Collections.emptyList());
            if (answersOfQuestion.isEmpty()) {
                throw new BadRequestException("No answers configured for question id: " + question.getId());
            }

            Integer selectedAnswerId = selectedIds.get(0);

            Answer selectedAnswer = findSelectedAnswer(answersOfQuestion, selectedAnswerId);
            validateSelectedAnswerBelongsToQuestion(selectedAnswer, question, selectedAnswerId);

            boolean isCorrect = Boolean.TRUE.equals(selectedAnswer.getIsCorrect());

            StudentAnswer studentAnswer = buildUserAnswer(attempt, question, selectedAnswer, isCorrect);
            userAnswerRepository.save(studentAnswer);

            if (isCorrect) {
                correctCount++;
            } else {
                wrongCount++;
            }
        }

        return new ResultSummary(correctCount, wrongCount, blankCount);
    }

    private Answer findSelectedAnswer(List<Answer> answersOfQuestion, Integer selectedAnswerId) {
        return answersOfQuestion.stream()
                .filter(answer -> Objects.equals(answer.getId(), selectedAnswerId))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Selected answer not found with id: " + selectedAnswerId));
    }

    private StudentAnswer buildUserAnswer(ExamAttempt attempt, Question question, Answer selectedAnswer, boolean isCorrect) {
        StudentAnswer studentAnswer = new StudentAnswer();
        studentAnswer.setAttempt(attempt);
        studentAnswer.setQuestion(question);
        studentAnswer.setSelectedAnswer(selectedAnswer);
        studentAnswer.setIsCorrect(isCorrect);
        return studentAnswer;
    }

    private void validateSelectedAnswerBelongsToQuestion(Answer selectedAnswer, Question question, Integer selectedAnswerId) {
        if (selectedAnswer == null) {
            throw new BadRequestException("Selected answer not found with id: " + selectedAnswerId);
        }

        if (!Objects.equals(selectedAnswer.getQuestion().getId(), question.getId())) {
            throw new BadRequestException(
                    "Selected answer id " + selectedAnswerId + " does not belong to question id " + question.getId()
            );
        }
    }

    private void deleteExistingStudentAnswers(Integer attemptId) {
        List<StudentAnswer> existingAnswers = userAnswerRepository.findByAttemptId(attemptId);
        if (!existingAnswers.isEmpty()) {
            userAnswerRepository.deleteAll(existingAnswers);
        }
    }

    private int calculateTotalQuestions(Exam exam,
                                        int correctCount,
                                        int wrongCount,
                                        int blankCount) {

        int total = correctCount + wrongCount + blankCount;

        if (total > 0) {
            return total;
        }

        return questionRepository.findQuestionsByExamId(exam.getId()).size();
    }

    private List<ReviewQuestionResponse> buildReviewQuestionResponses(ExamAttempt attempt, Exam exam) {
        List<Question> questions = questionRepository.findQuestionsByExamId(exam.getId());

        if (questions.isEmpty()) {
            return Collections.emptyList();
        }

        List<Integer> questionIds = questions.stream()
                .map(Question::getId)
                .toList();

        List<Answer> allAnswers = answerRepository.findByQuestionIdIn(questionIds);

        Map<Integer, List<Answer>> answersByQuestionId = allAnswers.stream()
                .collect(Collectors.groupingBy(answer -> answer.getQuestion().getId()));

        Map<Integer, Answer> correctAnswerByQuestionId = allAnswers.stream()
                .filter(answer -> Boolean.TRUE.equals(answer.getIsCorrect()))
                .collect(Collectors.toMap(
                        answer -> answer.getQuestion().getId(),
                        Function.identity(),
                        (existing, replacement) -> existing
                ));

        List<StudentAnswer> studentAnswers = userAnswerRepository.findByAttemptId(attempt.getId());

        Map<Integer, StudentAnswer> studentAnswerByQuestionId = studentAnswers.stream()
                .collect(Collectors.toMap(
                        studentAnswer -> studentAnswer.getQuestion().getId(),
                        Function.identity(),
                        (existing, replacement) -> existing
                ));

        return questions.stream()
                .map(question -> mapToReviewQuestionResponse(
                        question,
                        answersByQuestionId.getOrDefault(question.getId(), Collections.emptyList()),
                        correctAnswerByQuestionId.get(question.getId()),
                        studentAnswerByQuestionId.get(question.getId())
                ))
                .toList();
    }

    private ReviewQuestionResponse mapToReviewQuestionResponse(
            Question question,
            List<Answer> answers,
            Answer correctAnswer,
            StudentAnswer studentAnswer
    ) {
        Integer selectedOptionId = null;
        if (studentAnswer != null && studentAnswer.getSelectedAnswer() != null) {
            selectedOptionId = studentAnswer.getSelectedAnswer().getId();
        }

        Integer correctOptionId = correctAnswer != null ? correctAnswer.getId() : null;

        List<ReviewOptionResponse> optionResponses = answers.stream()
                .sorted(Comparator.comparing(answer -> {
                    String label = answer.getLabel();
                    return label == null ? "" : label;
                }))
                .map(answer -> ReviewOptionResponse.builder()
                        .optionId(answer.getId())
                        .optionLabel(answer.getLabel())
                        .optionContent(answer.getContent())
                        .isCorrect(Boolean.TRUE.equals(answer.getIsCorrect()))
                        .build())
                .toList();

        return ReviewQuestionResponse.builder()
                .questionId(question.getId())
                .questionContent(question.getContent())
                .selectedOptionId(selectedOptionId)
                .correctOptionId(correctOptionId)
                .isCorrect(studentAnswer != null && Boolean.TRUE.equals(studentAnswer.getIsCorrect()))
                .explanation(question.getExplanation())
                .options(optionResponses)
                .build();
    }

    private double calculateScore(int correctCount, int totalQuestions) {
        if (totalQuestions <= 0) {
            return 0.0;
        }
        return (correctCount * 100.0) / totalQuestions;
    }

    private int calculateTimeSpentSeconds(LocalDateTime startTime, LocalDateTime submittedAt) {
        if (startTime == null || submittedAt == null) {
            return 0;
        }

        long seconds = Duration.between(startTime, submittedAt).getSeconds();
        return (int) Math.max(seconds, 0);
    }

    private int defaultIfNull(Integer value) {
        return value == null ? 0 : value;
    }

    private double defaultIfNull(Double value) {
        return value == null ? 0.0 : value;
    }

    private boolean isReviewAllowed(Exam exam) {
        return exam != null
                && exam.getExamType() != null
                && REVIEW_ALLOWED_TYPES.contains(exam.getExamType());
    }

    private record ResultSummary(int correctCount, int wrongCount, int blankCount) {
    }
}