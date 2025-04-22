package com.example.springbootsqlserver.controller;

import com.example.springbootsqlserver.entity.Major;
import com.example.springbootsqlserver.service.MajorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/major")
public class MajorController {

    @Autowired
    private MajorService majorService;

    @GetMapping
    public String listMajor(Model model) {
        model.addAttribute("majors", majorService.getAllMajors());
        return "major/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("major", new Major());
        return "major/form";
    }

    @PostMapping("/save")
    public String saveMajor(@ModelAttribute Major major) {
        if (major.getId() == null) {
            majorService.createMajor(major);
        } else {
            majorService.updateMajor(major.getId(), major);
        }
        return "redirect:/major";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable UUID id, Model model) {
        Major major = majorService.getMajorById(id);
        if (major != null) {
            model.addAttribute("major", major);
            return "major/form";
        }
        return "redirect:/major";
    }

    @GetMapping("/delete/{id}")
    public String deleteMajor(@PathVariable UUID id) {
        majorService.deleteMajor(id);
        return "redirect:/major";
    }
}
