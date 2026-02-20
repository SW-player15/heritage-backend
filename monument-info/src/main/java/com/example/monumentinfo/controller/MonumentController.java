package com.example.monumentinfo.controller;

import com.example.monumentinfo.model.Monument;
import com.example.monumentinfo.service.MonumentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/monuments")
@CrossOrigin
public class MonumentController {

    private final MonumentService service;

    public MonumentController(MonumentService service) {
        this.service = service;
    }

    @GetMapping
    public List<Monument> getAll() {
        return service.getAllMonuments();
    }

    @GetMapping("/{id}")
    public Monument getOne(@PathVariable Long id) {
        return service.getMonument(id);
    }

    @PostMapping
    public Monument create(@RequestBody Monument monument) {
        return service.saveMonument(monument);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteMonument(id);
    }

    @GetMapping("/city/{city}")
    public List<Monument> getByCity(@PathVariable String city) {
        return service.getByCity(city);
    }
}