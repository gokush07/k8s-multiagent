package com.example.lab.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ConfluenceController {

    @GetMapping({"/confluence", "/portal"})
    public String confluencePortal(Model model) {
        model.addAttribute("spaceName", "SRE & Engineering Knowledge Base");
        return "confluence";
    }
}
