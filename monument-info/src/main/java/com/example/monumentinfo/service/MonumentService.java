package com.example.monumentinfo.service;

import com.example.monumentinfo.model.Monument;
import com.example.monumentinfo.repository.MonumentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MonumentService {

    private final MonumentRepository repository;

    public MonumentService(MonumentRepository repository) {
        this.repository = repository;
    }

    public List<Monument> getAllMonuments() {
        return repository.findAll();
    }

    public Monument getMonument(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Monument saveMonument(Monument monument) {
        return repository.save(monument);
    }

    public void deleteMonument(Long id) {
        repository.deleteById(id);
    }

    public List<Monument> getByCity(String city) {
        return repository.findByCity(city);
    }
}