package com.example.examprepbackend.service.impl;

import com.example.examprepbackend.dto.response.questions.QuestionResponse;
import com.example.examprepbackend.entity.Question;
import com.example.examprepbackend.repository.QuestionRepository;
import com.example.examprepbackend.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;

    //Map question -> questionResponse
    private QuestionResponse convertToDto(Question question) {
        QuestionResponse questionResponse = new QuestionResponse();

        BeanUtils.copyProperties(question, questionResponse);

        questionResponse.setCategory(question.getCategory().getName());
        questionResponse.setDifficulty(question.getDifficultyLevel());
        return questionResponse;
    }


    @Override
    public Page<QuestionResponse> getAllQuestions(Pageable pageable) {
        return questionRepository.findAll(pageable).map(this::convertToDto);
    }
}
