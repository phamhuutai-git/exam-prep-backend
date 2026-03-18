package com.example.he_thong_thi.repository;


import dto.ScoreDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ExamRepository {

    @Autowired
    private DataSource dataSource;

    public List<ScoreDTO> getScoreByClass(int classId) {

        List<ScoreDTO> list = new ArrayList<>();

        String sql = """
    SELECT u.first_name, u.last_name, e.title, ea.score
    FROM exam_attempt ea
    JOIN users u ON ea.student_id = u.id
    JOIN exam e ON ea.exam_id = e.id
    JOIN exam_class ec ON e.id = ec.exam_id
    WHERE ec.class_id = ?
""";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, classId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                String fullName = rs.getString("first_name") + " " + rs.getString("last_name");

                ScoreDTO dto = new ScoreDTO(
                        fullName,
                        rs.getString("title"),
                        rs.getDouble("score")
                );

                list.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}