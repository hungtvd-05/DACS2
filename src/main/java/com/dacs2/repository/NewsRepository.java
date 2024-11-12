package com.dacs2.repository;

import com.dacs2.model.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<News, Integer> {

    public Page<News> findByTitleContainingIgnoreCase(String title, Pageable pageable);

}
