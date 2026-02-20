package com.example.monumentinfo.repository;

import com.example.monumentinfo.model.Monument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MonumentRepository extends JpaRepository<Monument, Long> {

    List<Monument> findByCity(String city);

}