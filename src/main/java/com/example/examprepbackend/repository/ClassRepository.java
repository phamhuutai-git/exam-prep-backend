package com.example.examprepbackend.repository;

import com.example.examprepbackend.entity.Classes;
import org.modelmapper.internal.bytebuddy.implementation.Implementation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ClassRepository extends JpaRepository<Classes, Integer>, JpaSpecificationExecutor<Classes> {

    Classes findByName(String name);


}
