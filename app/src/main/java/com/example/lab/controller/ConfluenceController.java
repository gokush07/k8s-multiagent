package com.example.lab.controller;

import com.example.lab.model.ConfluencePageEntity;
import com.example.lab.service.ConfluencePageService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class ConfluenceController {

    private final ConfluencePageService pageService;

    public ConfluenceController(ConfluencePageService pageService) {
        this.pageService = pageService;
    }

    @GetMapping({"/confluence", "/portal"})
    public String confluencePortal(Model model) {
        model.addAttribute("spaceName", "SRE & Engineering Knowledge Base");
        model.addAttribute("pages", pageService.getAllPages());
        return "confluence";
    }

    @GetMapping("/api/confluence/pages")
    @ResponseBody
    public List<ConfluencePageEntity> getAllPagesApi() {
        return pageService.getAllPages();
    }

    @GetMapping("/api/confluence/pages/{id}")
    @ResponseBody
    public ResponseEntity<ConfluencePageEntity> getPageById(@PathVariable("id") Long id) {
        return pageService.getPageById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/confluence/pages")
    @ResponseBody
    public ResponseEntity<ConfluencePageEntity> createOrUpdatePage(@RequestBody ConfluencePageEntity page) {
        ConfluencePageEntity saved = pageService.savePage(page);
        return ResponseEntity.ok(saved);
    }

    // Grafana Transfer Endpoint (for Grafana Infinity / Table Panel)
    @GetMapping({"/api/confluence/table-data", "/api/confluence/grafana"})
    @ResponseBody
    public Map<String, Object> getGrafanaTableData() {
        return pageService.getGrafanaExportData();
    }
}
