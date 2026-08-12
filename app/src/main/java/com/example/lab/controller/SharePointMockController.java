package com.example.lab.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class SharePointMockController {

    @GetMapping("/api/sharepoint/items")
    public Map<String, Object> getSharePointItems() {
        List<Map<String, Object>> items = new ArrayList<>();

        items.add(createItem(101, "Database Latency Spike", "In Progress", "High", "Alice Smith", "Database", "2026-08-12T04:30:00Z"));
        items.add(createItem(102, "Kafka Consumer Lag Alert", "Resolved", "Medium", "Bob Jones", "Messaging", "2026-08-12T05:00:00Z"));
        items.add(createItem(103, "SSL Certificate Expiry Warning", "Open", "Low", "Charlie Brown", "Security", "2026-08-12T05:15:00Z"));
        items.add(createItem(104, "Pod OOMKilled in Namespace Lab", "Critical", "Critical", "Diana Prince", "Infrastructure", "2026-08-12T05:40:00Z"));
        items.add(createItem(105, "High CPU Utilization on Node 2", "In Progress", "High", "Evan Wright", "Compute", "2026-08-12T05:50:00Z"));

        Map<String, Object> dWrapper = new HashMap<>();
        dWrapper.put("results", items);

        Map<String, Object> response = new HashMap<>();
        response.put("d", dWrapper);
        response.put("value", items);

        return response;
    }

    private Map<String, Object> createItem(int id, String title, String status, String priority, String assignedTo, String category, String created) {
        Map<String, Object> item = new HashMap<>();
        item.put("ID", id);
        item.put("Title", title);
        item.put("Status", status);
        item.put("Priority", priority);
        item.put("AssignedTo", assignedTo);
        item.put("Category", category);
        item.put("Created", created);
        return item;
    }
}
