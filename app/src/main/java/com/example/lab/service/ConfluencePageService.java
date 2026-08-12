package com.example.lab.service;

import com.example.lab.model.ConfluencePageEntity;
import com.example.lab.repository.ConfluencePageRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ConfluencePageService {

    private final ConfluencePageRepository pageRepository;

    public ConfluencePageService(ConfluencePageRepository pageRepository) {
        this.pageRepository = pageRepository;
        initDefaultPages();
    }

    private void initDefaultPages() {
        if (pageRepository.count() == 0) {
            String sampleTable = "[{\"ID\":\"101\",\"Title\":\"Database Latency Spike\",\"Status\":\"In Progress\",\"Priority\":\"High\",\"Owner\":\"Alice Smith\"},{\"ID\":\"102\",\"Title\":\"Kafka Lag Monitoring\",\"Status\":\"Resolved\",\"Priority\":\"Medium\",\"Owner\":\"Bob Jones\"},{\"ID\":\"103\",\"Title\":\"Pod OOMKilled Alerts\",\"Status\":\"Critical\",\"Priority\":\"Critical\",\"Owner\":\"Diana Prince\"}]";

            pageRepository.save(new ConfluencePageEntity(
                "SRE Multi-Agent Kubernetes Lab Guide",
                "Runbooks",
                "APPROVED",
                "System architecture overview and operational guidance for Kubernetes 3-node cluster.",
                sampleTable
            ));
        }
    }

    public ConfluencePageEntity savePage(ConfluencePageEntity page) {
        page.setUpdatedAt(Instant.now());
        return pageRepository.save(page);
    }

    public List<ConfluencePageEntity> getAllPages() {
        return pageRepository.findAllByOrderByUpdatedAtDesc();
    }

    public Optional<ConfluencePageEntity> getPageById(Long id) {
        return pageRepository.findById(id);
    }

    public Map<String, Object> getGrafanaExportData() {
        List<ConfluencePageEntity> pages = pageRepository.findAllByOrderByUpdatedAtDesc();
        List<Map<String, Object>> records = new ArrayList<>();

        for (ConfluencePageEntity p : pages) {
            Map<String, Object> record = new HashMap<>();
            record.put("ID", p.getId());
            record.put("Title", p.getTitle());
            record.put("Category", p.getCategory());
            record.put("Status", p.getStatus());
            record.put("UpdatedAt", p.getUpdatedAt().toString());
            record.put("TableData", p.getTableJson());
            records.add(record);
        }

        Map<String, Object> dWrapper = new HashMap<>();
        dWrapper.put("results", records);

        Map<String, Object> response = new HashMap<>();
        response.put("d", dWrapper);
        response.put("value", records);
        return response;
    }
}
