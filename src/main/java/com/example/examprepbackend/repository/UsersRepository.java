package com.example.examprepbackend.repository;

import com.example.examprepbackend.constant.Role;
import com.example.examprepbackend.constant.Status;
import com.example.examprepbackend.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Integer> {
    //auth
    Optional<Users> findByUsername(String username);

    Optional<Users> findByEmailOrUsername(String email, String username);

    Optional<Users> findByEmail(String email);

    Users findUsersByEmail(String email);

    // ===== CREATE =====
    boolean existsByEmail(String email);
    boolean existsByUsernameIgnoreCase(String username);
    boolean existsByEmailIgnoreCase(String email);

    // ===== UPDATE  =====
    boolean existsByEmailAndIdNot(String email, Integer id);

    boolean existsByUsernameAndIdNot(String username, Integer id);


    // ===== DELETE  =====
    Optional<Users> findByIdAndStatusNot(Integer id, Status status);

    boolean existsByIdAndStatusNot(Integer id, Status status);


    Optional<Users> findByUsernameOrEmail(String username, String email);

    // Thêm hàm đếm tổng số user theo Role (Dùng cho API thống kê Dashboard)
    long countByRole(Role role);

    Long countByRoleAndClasses_Id(Role role, Integer classes_id);

    List<Users> findByRole(Role role);
    @Query("SELECT u FROM Users u WHERE " +
            "(:role IS NULL OR u.role = :role) AND " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Users> searchUsers(@Param("keyword") String keyword,
                            @Param("role") Role role,
                            Pageable pageable);

    List<Users> findByRoleAndClasses_Id(Role role, Integer classesId);

    List<Users> findByRoleAndIdIn(Role role, List<Integer> ids);

    List<Users> findByIdIn(List<Integer> ids);

    @Modifying
    @Query("update Users u set u.classes.id = null where u.classes.id = :classId")
    void updateClassIdToNull(@Param("classId") Integer classId);

    @Modifying
    @Query("update Users u set u.classes.id = :classId where u.id in :studentIdList")
    void updateClassIdByIdIn(@Param("classId") Integer classId, @Param("studentIdList") List<Integer> studentIdList);

    //user
}