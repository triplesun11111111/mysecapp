package ru.kata.spring.boot_security.demo.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.repo.RoleRepository;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;
import java.util.*;

@Controller
public class MainController {
    private UserService userService;
    private RoleRepository roleRepository;
    private final PasswordEncoder bCryptPasswordEncoder;

    public MainController(PasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login(HttpServletRequest request) {
        return "login";
    }

    @GetMapping("/user")
    public String userPage(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = userService.findByUsername(principal.getName());
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "user";
    }

    @GetMapping("/admin")
    public String showAllUsers(Model model) {
        List<User> allUsers = userService.listUsers();
        model.addAttribute("allUsers", allUsers);
        return "admin";
    }

    @GetMapping("/admin/new")
    public String newUser(Model model) {
        model.addAttribute("user", new User());
        List<Role> roles = roleRepository.findAll();
        model.addAttribute("roles", roles);
        return "new";
    }

    @PostMapping("/admin/save")
    public String saveUser(@ModelAttribute("user") User user) {
        String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userService.saveUser(user);
        return "redirect:/admin";
    }

    @GetMapping("/admin/edit/")
    public String editUser(@RequestParam("id") Integer id, Model model) {
        User user = userService.findById(Math.toIntExact(Long.valueOf(id)));
        List<Role> roles = roleRepository.findAll();
        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        return "edit";
    }

    @PostMapping("/admin/update")
    public String updateUser(@RequestParam(value = "roles", required = false) String[] roles, @ModelAttribute("user") User user) {
        Set<Role> userRoles = new HashSet<>();
        if (roles != null) {
            for (String roleId : roles) {
                try {
                    int id = Integer.parseInt(roleId);
                    Optional<Role> role = roleRepository.findById((long) id);
                    role.ifPresent(userRoles::add);
                } catch (NumberFormatException ignored) {
                }
            }
        } else {
            userRoles = Collections.emptySet();
        }
        user.setRoles(userRoles);
        userService.updateUser(user);
        return "redirect:/admin";
    }

    @GetMapping("/admin/delete")
    public String deleteUser(@RequestParam("id") Integer id) {
        userService.deleteById(Math.toIntExact(Long.valueOf(id)));
        return "redirect:/admin";
    }

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
}