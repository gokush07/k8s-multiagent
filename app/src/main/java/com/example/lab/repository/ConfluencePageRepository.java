package com.example.lab.repository;

import com.example.lab.model.ConfluencePageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfluencePageRepository extends JpaRepository<ConfluencePageEntity, Long> {
    List<ConfluencePageEntity> findAllByOrderByUpdatedAtDesc();
}
