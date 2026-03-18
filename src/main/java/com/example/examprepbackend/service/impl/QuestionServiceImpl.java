package com.example.examprepbackend.service.impl;

import com.example.examprepbackend.config.SecurityUtils;
import com.example.examprepbackend.constant.DifficultyLevel;
import com.example.examprepbackend.dto.request.teacher.Question.CreateAnswerRequest;
import com.example.examprepbackend.dto.request.teacher.Question.CreateQuestionRequest;
import com.example.examprepbackend.dto.request.teacher.Question.QuestionRequestParam;
import com.example.examprepbackend.dto.response.teacher.AnswerResponse;
import com.example.examprepbackend.dto.response.teacher.QuestionCountResponse;
import com.example.examprepbackend.dto.response.teacher.QuestionResponse;
import com.example.examprepbackend.entity.Answer;
import com.example.examprepbackend.entity.CategoryQuestion;
import com.example.examprepbackend.entity.Question;
import com.example.examprepbackend.entity.Users;
import com.example.examprepbackend.exception.ApplicationException;
import com.example.examprepbackend.repository.AnswerRepository;
import com.example.examprepbackend.repository.CategoryQuestionRepository;
import com.example.examprepbackend.repository.QuestionRepository;
import com.example.examprepbackend.repository.UsersRepository;
import com.example.examprepbackend.service.QuestionService;
import com.example.examprepbackend.specification.QuestionSpecification;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final UsersRepository userRepository;
    private final CategoryQuestionRepository categoryRepository;
    private final ModelMapper modelMapper;

    //Map question -> questionResponse
    private QuestionResponse convertToDto(Question question) {

        QuestionResponse questionResponse = new QuestionResponse();

        BeanUtils.copyProperties(question, questionResponse);

        questionResponse.setCategory(question.getCategory().getName());

        questionResponse.setDifficulty(question.getDifficultyLevel());

        questionResponse.setCreator(question.getCreator().getUsername());

        questionResponse.setCreatedDate(
                question.getCreateDate().toLocalDate().toString()
        );

        // lấy answers
        List<Answer> answers = answerRepository.findByQuestion_Id(question.getId());

        List<AnswerResponse> answerResponses = answers.stream().map(a -> {
            AnswerResponse dto = new AnswerResponse();
            dto.setContent(a.getContent());
            dto.setIsCorrect(a.getIsCorrect());
            return dto;
        }).toList();
        questionResponse.setAnswers(answerResponses);

        questionResponse.setExplanation(question.getExplanation());
        return questionResponse;
    }


    @Override
    public Page<QuestionResponse> getAllQuestions(QuestionRequestParam param, Pageable pageable) {

        String content = param.getContent();
        DifficultyLevel difficulty = param.getDifficulty();
        Integer categoryId = param.getCategoryId();
        Integer creatorId = param.getCreatorId();
        LocalDate minDate = param.getMinDate();
        LocalDate maxDate = param.getMaxDate();

        Specification<Question> spec = Specification.unrestricted();

        if (content != null && !content.isBlank()) {
            spec = spec.and(QuestionSpecification.hasContentLike(content));
        }

        if (difficulty != null) {
            spec = spec.and(QuestionSpecification.hasDifficulty(difficulty));
        }

        if (categoryId != null) {
            spec = spec.and(QuestionSpecification.hasCategoryId(categoryId));
        }

        if (creatorId != null) {
            spec = spec.and(QuestionSpecification.hasCreatorId(creatorId));
        }

        if (minDate != null && maxDate != null) {
            spec = spec.and(QuestionSpecification.hasCreateDate(minDate, maxDate));
        }

        return questionRepository.findAll(spec, pageable)
                .map(this::convertToDto);
    }


    @Override
    public QuestionResponse getQuestionById(Integer id) {
        Optional<Question> question = questionRepository.findById(id);
        if (question.isEmpty()) {
            throw new ApplicationException("Question with id " + id + " not found");
        }
        return convertToDto(question.get());
    }

    @Transactional
    @Override
    public QuestionResponse createQuestion(CreateQuestionRequest request) {

        Question question = new Question();

        question.setContent(request.getContent());

        question.setDifficultyLevel(request.getDifficulty());

        Optional<CategoryQuestion> categoryQuestion  = categoryRepository.findById(request.getCategoryId());
        if (categoryQuestion.isEmpty()) {
            throw new ApplicationException("Category with id " + request.getCategoryId() + " not found");
        }
        question.setCategory(categoryQuestion.get());

        String username = SecurityUtils.getCurrentUsername();

        Optional<Users> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new ApplicationException("User with name " + username + " not found");
        }
        question.setCreator(user.get());

        question.setCreateDate(LocalDateTime.now());

        Question savedQuestion = questionRepository.save(question);

        List<Answer> answers = new ArrayList<>();

        for (CreateAnswerRequest a : request.getAnswers()) {

            Answer answer = new Answer();
            answer.setContent(a.getContent());
            answer.setIsCorrect(a.getIsCorrect());
            answer.setQuestion(savedQuestion);

            answers.add(answer);
        }

        question.setExplanation(request.getExplanation());
        answerRepository.saveAll(answers);

        return convertToDto(savedQuestion);
    }

    @Transactional
    @Override
    public QuestionResponse updateQuestion(Integer id, CreateQuestionRequest request) {

        Optional<Question> question = questionRepository.findById(id);
        if (question.isEmpty()) {
            throw new ApplicationException("Question with id " + id + " not found");
        }
        Question questions = question.get();

        questions.setContent(request.getContent());
        questions.setDifficultyLevel(request.getDifficulty());

        Optional<CategoryQuestion> categoryQuestion  = categoryRepository.findById(request.getCategoryId());
        if (categoryQuestion.isEmpty()) {
            throw new ApplicationException("Category with id " + request.getCategoryId() + " not found");
        }

        questions.setCategory(categoryQuestion.get());

        Question savedQuestion = questionRepository.save(questions);

        // xóa answers cũ
        answerRepository.deleteByQuestion_Id(id);

        // thêm answers mới
        List<Answer> answers = new ArrayList<>();

        for (CreateAnswerRequest a : request.getAnswers()) {

            Answer answer = new Answer();
            answer.setContent(a.getContent());
            answer.setIsCorrect(a.getIsCorrect());
            answer.setQuestion(savedQuestion);

            answers.add(answer);
        }
        questions.setExplanation(request.getExplanation());
        answerRepository.saveAll(answers);

        return convertToDto(savedQuestion);
    }

    @Transactional
    @Override
    public void deleteQuestion(Integer id) {

        Optional<Question> question = questionRepository.findById(id);
        if (question.isEmpty()) {
            throw new ApplicationException("Question with id " + id + " not found");
        }

        questionRepository.delete(question.get());
    }

    @Override
    public void exportQuestionToExcel(HttpServletResponse response) throws IOException {

        List<Question> questions = questionRepository.findAllWithAnswers();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Questions");

        Row header = sheet.createRow(0);

        String[] columns = {
                "ID","Question","A","B","C","D",
                "Correct","Explanation","Category","Difficulty"
        };

        for(int i=0;i<columns.length;i++){
            header.createCell(i).setCellValue(columns[i]);
        }

        int rowIndex = 1;

        for(Question q : questions){

            Row row = sheet.createRow(rowIndex++);

            row.createCell(0).setCellValue(q.getId());
            row.createCell(1).setCellValue(q.getContent());

            List<Answer> answers = q.getAnswers();

            String correct = "";
            String[] options = new String[4];

            int index = 0;

            for(Answer a : answers){

                if(index < 4){
                    options[index] = a.getContent();
                    index++;
                }

                if(a.getIsCorrect()){
                    correct = a.getContent();
                }
            }

            row.createCell(2).setCellValue(options[0]);
            row.createCell(3).setCellValue(options[1]);
            row.createCell(4).setCellValue(options[2]);
            row.createCell(5).setCellValue(options[3]);

            row.createCell(6).setCellValue(correct);

            row.createCell(7).setCellValue(
                    q.getExplanation() != null ? q.getExplanation() : ""
            );

            row.createCell(8).setCellValue(
                    q.getCategory()!=null ? q.getCategory().getName() : ""
            );

            row.createCell(9).setCellValue(
                    q.getDifficultyLevel()!=null ?
                            q.getDifficultyLevel().toString() : ""
            );
        }

        for(int i=0;i<columns.length;i++){
            sheet.autoSizeColumn(i);
        }

        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        );

        response.setHeader(
                "Content-Disposition",
                "attachment; filename=questions.xlsx"
        );

        ServletOutputStream outputStream = response.getOutputStream();

        workbook.write(outputStream);

        workbook.close();
        outputStream.close();
    }

    @Override
    @Transactional
    public void importQuestionFromExcel(MultipartFile file) throws IOException {

        if (!file.getOriginalFilename().endsWith(".xlsx")) {
            throw new ApplicationException("Only Excel file allowed");
        }

        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {

            Row row = sheet.getRow(i);
            if (row == null) continue;

            // ===== QUESTION =====
            Question question = new Question();

            String content = row.getCell(0).getStringCellValue();
            question.setContent(content);

            // ===== CATEGORY =====
            String categoryName = row.getCell(7).getStringCellValue();

            CategoryQuestion category = categoryRepository
                    .findByName(categoryName);
            if(category == null){
                throw  new ApplicationException("Category not found");
            }


            question.setCategory(category);

            // ===== DIFFICULTY =====
            String difficulty = row.getCell(8).getStringCellValue();
            question.setDifficultyLevel(DifficultyLevel.valueOf(difficulty));

            // ===== EXPLANATION =====
            question.setExplanation(
                    row.getCell(6) != null ? row.getCell(6).getStringCellValue() : null
            );

            // ===== CREATOR =====
            String username = SecurityUtils.getCurrentUsername();

            Users user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ApplicationException("User not found"));

            question.setCreator(user);

            question.setCreateDate(LocalDateTime.now());

            Question savedQuestion = questionRepository.save(question);

            // ===== ANSWERS =====
            String correctAnswer = row.getCell(5).getStringCellValue();

            List<Answer> answers = new ArrayList<>();

            for (int j = 0; j < 4; j++) {

                String option = row.getCell(1 + j).getStringCellValue();

                Answer answer = new Answer();

                answer.setContent(option);

                answer.setIsCorrect(option.equals(correctAnswer));

                answer.setQuestion(savedQuestion);

                answers.add(answer);
            }

            answerRepository.saveAll(answers);
        }

        workbook.close();
    }

    @Override
    public QuestionCountResponse getAllQuestionsCount() {
        QuestionCountResponse response = new QuestionCountResponse();
        response.setCountTotal(questionRepository.count());
        response.setCountEasy(questionRepository.countByDifficultyLevel(DifficultyLevel.EASY));
        response.setCountMedium(questionRepository.countByDifficultyLevel(DifficultyLevel.MEDIUM));
        response.setCountHard(questionRepository.countByDifficultyLevel(DifficultyLevel.HARD));
        return response;

    }
}
