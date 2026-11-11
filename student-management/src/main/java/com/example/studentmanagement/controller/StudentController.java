package com.example.studentmanagement.controller;

import com.example.studentmanagement.exception.StudentException;
import com.example.studentmanagement.model.Student;
import com.example.studentmanagement.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    // Constructor injection: no "new StudentService()" anywhere.
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // GET /student/register -> register.html
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        if (!model.containsAttribute("student")) {
            model.addAttribute("student", new Student());
        }
        return "register";
    }

    // POST /student/register -> save, then redirect (Post/Redirect/Get)
    @PostMapping("/register")
    public String registerStudent(@Valid @ModelAttribute("student") Student student,
                                  BindingResult bindingResult,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        try {
            studentService.registerStudent(student);
        } catch (StudentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
        redirectAttributes.addFlashAttribute("successMessage",
                "Student " + student.getFullName() + " was registered successfully!");
        return "redirect:/student/register";
    }

    // GET /student/all -> students.html (all students)
    @GetMapping("/all")
    public String listStudents(Model model) {
        try {
            model.addAttribute("students", studentService.getAllStudents());
        } catch (StudentException e) {
            model.addAttribute("students", List.of());
            model.addAttribute("errorMessage", e.getMessage());
        }
        model.addAttribute("singleView", false);
        return "students";
    }

    // BONUS: GET /student/{id} -> same students.html, showing one student
    @GetMapping("/{id}")
    public String viewStudent(@PathVariable("id") int id, Model model) {
        try {
            studentService.getStudentById(id).ifPresentOrElse(
                    s -> model.addAttribute("students", List.of(s)),
                    () -> {
                        model.addAttribute("students", List.of());
                        model.addAttribute("notFoundMessage", "No student found with ID " + id + ".");
                    });
        } catch (StudentException e) {
            model.addAttribute("students", List.of());
            model.addAttribute("errorMessage", e.getMessage());
        }
        model.addAttribute("singleView", true);
        return "students";
    }
}
