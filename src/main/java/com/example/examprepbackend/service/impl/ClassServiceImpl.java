package com.example.examprepbackend.service.impl;

import com.example.examprepbackend.constant.Role;
import com.example.examprepbackend.dto.request.clazz.ClassRequest;
import com.example.examprepbackend.dto.request.clazz.ClassRequestParam;
import com.example.examprepbackend.dto.response.clazz.ClassResponse;
import com.example.examprepbackend.entity.Classes;
import com.example.examprepbackend.exception.ApplicationException;
import com.example.examprepbackend.repository.ClassRepository;
import com.example.examprepbackend.repository.UsersRepository;
import com.example.examprepbackend.service.ClassService;
import com.example.examprepbackend.specification.ClassSpecification;
import com.example.examprepbackend.specification.ExamSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClassServiceImpl implements ClassService {

    private final ClassRepository classRepository;
    private final ModelMapper modelMapper;
    private final UsersRepository usersRepository;

    private ClassResponse convertToDto(Classes classes) {
        ClassResponse classResponse = new ClassResponse();

        BeanUtils.copyProperties(classes, classResponse);
        classResponse.setStudentCount(usersRepository.countByRoleAndClasses_Id(Role.valueOf("STUDENT"), classes.getId()));

        return classResponse;
    }

    @Override
    public Page<ClassResponse> getAllClasses(ClassRequestParam classRequestParam, Pageable pageable) {

        String name = classRequestParam.getName();
        LocalDate minDate = classRequestParam.getMinDate();
        LocalDate maxDate = classRequestParam.getMaxDate();

        Specification<Classes> spec = Specification.unrestricted();

        if (name != null && !name.isBlank()) {
            spec = spec.and(ClassSpecification.hasNameLike(name));
        }

        if (minDate != null) {
            spec = spec.and(ClassSpecification.hasAfterMinDate(minDate));
        }

        if (maxDate != null) {
            spec = spec.and(ClassSpecification.hasBeforeMinDate(maxDate));
        }

        if (minDate != null && maxDate != null) {
            spec = spec.and(ClassSpecification.hasCreateDate(minDate, maxDate));
        }

        return classRepository.findAll(spec, pageable).map(this::convertToDto);
    }

    @Transactional
    @Override
    public ClassResponse createClass(ClassRequest classRequest) {

        Classes existsByName = classRepository.findByName(classRequest.getName());

        if (existsByName != null) {
            throw new ApplicationException("Class name existed");
        }

        log.info("aaaa" + classRequest.getName());

        Classes classes = new Classes();
        classes.setName(classRequest.getName());
        classes.setCreateDate(LocalDateTime.now());

        classRepository.save(classes);

        return modelMapper.map(classes, ClassResponse.class);
    }

    @Transactional
    @Override
    public ClassResponse updateClass(Integer id, ClassRequest classRequest) {

        Optional<Classes> classesOptional = classRepository.findById(id);

        if (classesOptional.isEmpty()) {
            throw new ApplicationException("Class not found");
        }

        Classes clazz = classesOptional.get();

        String currentName = clazz.getName();
        String newName = classRequest.getName();

        if (!newName.equals(currentName)) {
            Classes existsByName = classRepository.findByName(newName);

            if (existsByName != null) {
                throw new ApplicationException("Class name existed");
            }
        }

        clazz.setName(newName);
        classRepository.save(clazz);

        return modelMapper.map(clazz, ClassResponse.class);
    }

    @Transactional
    @Override
    public Boolean deleteById(Integer id) {
        Optional<Classes> classesOptional = classRepository.findById(id);

        if (classesOptional.isEmpty()) {
            throw new ApplicationException("Class not found");
        }

        classRepository.delete(classesOptional.get());

        return true;
    }
}
