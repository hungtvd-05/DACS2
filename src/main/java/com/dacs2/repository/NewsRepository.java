package com.dacs2.repository;

import com.dacs2.model.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Integer> {

    Page<News> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    List<News> findByStyle(String style);

    Page<News> findByStatusTrue(Pageable pageable);

    Page<News> findByStatusTrueAndStyle(Boolean status, String style, Pageable pageable);

    Page<News> findByStatusAndStyle(Boolean status, String style, Pageable pageable);

    List<News> findByStyleAndStatus(String style, Boolean status);
}
