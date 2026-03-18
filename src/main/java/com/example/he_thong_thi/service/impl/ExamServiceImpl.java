package com.example.he_thong_thi.service.impl;

import com.example.he_thong_thi.entity.*;
import com.example.he_thong_thi.repository.CategoryQuestionRepository;
import com.example.he_thong_thi.repository.ExamQuestionRepository;
import com.example.he_thong_thi.repository.ExamRepository;
import com.example.he_thong_thi.service.ExamService;
import dto.Request.ExamUpdateRequest;
import dto.Response.ExamResponse;
import dto.Response.ScoreDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExamServiceImpl implements ExamService {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private CategoryQuestionRepository categoryRepository;

    @Autowired
    private ExamQuestionRepository examQuestionRepository;

    // ================= CODE CŨ =================
    @Override
    public List<ScoreDTO> getScoreByClass(int classId) {
        return examRepository.getScoreByClass(classId);
    }

    // ================= UPDATE EXAM =================
    @Override
    @Transactional
    public ExamResponse updateExam(int examId, ExamUpdateRequest req) {

        // 1. tìm exam
        Exam exam = examRepository.findById(examId).orElse(null);

        if (exam == null) {
            throw new RuntimeException("Không tìm thấy đề thi");
        }

        // 2. update info
        exam.setCode(req.getCode());
        exam.setTitle(req.getTitle());

        if (req.getDuration() != null) {
            exam.setDuration(LocalTime.of(0, req.getDuration()));
        }

        // 3. category
        CategoryQuestion category = categoryRepository
                .findById(req.getCategoryId())
                .orElse(null);

        if (category != null) {
            exam.setCategory(category);
        }

        // 4. save exam (JPA tự update)
        examRepository.save(exam);

        // 5. xóa câu hỏi cũ
        examQuestionRepository.deleteByExamId(examId);

        // 6. thêm câu hỏi mới
        List<ExamQuestion> newList = new ArrayList<>();

        if (req.getQuestionIds() != null) {
            for (Integer qId : req.getQuestionIds()) {

                ExamQuestion eq = new ExamQuestion();

                // ✅ FIX CHÍNH Ở ĐÂY
                ExamQuestionId id = new ExamQuestionId();
                id.setExamId(exam.getId());
                id.setQuestionId(qId);
                eq.setId(id);

                // =====================

                eq.setExam(exam);

                Question q = new Question();
                q.setId(qId);
                eq.setQuestion(q);

                newList.add(eq);
            }
        }

        // 7. trả về response
        return mapToResponse(exam, req.getQuestionIds());
    }

    // ================= GET DETAIL =================
    @Override
    public ExamResponse getExamDetail(int examId) {

        Exam exam = examRepository.findById(examId).orElse(null);

        if (exam == null) {
            throw new RuntimeException("Không tìm thấy đề thi");
        }

        List<ExamQuestion> list = examQuestionRepository.findByExamId(examId);

        List<Integer> questionIds = new ArrayList<>();

        for (ExamQuestion eq : list) {
            questionIds.add(eq.getQuestion().getId());
        }

        return mapToResponse(exam, questionIds);
    }

    // ================= MAPPER =================
    private ExamResponse mapToResponse(Exam exam, List<Integer> questionIds) {

        ExamResponse res = new ExamResponse();

        res.setId(exam.getId());
        res.setCode(exam.getCode());
        res.setTitle(exam.getTitle());

        if (exam.getDuration() != null) {
            res.setDuration(exam.getDuration().getMinute());
        } else {
            res.setDuration(0);
        }

        if (exam.getCategory() != null) {
            res.setCategoryId(exam.getCategory().getId());
            res.setCategoryName(exam.getCategory().getName());
        }

        res.setQuestionIds(questionIds);

        return res;
    }
}