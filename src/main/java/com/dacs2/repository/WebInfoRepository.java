package com.dacs2.repository;

import com.dacs2.model.WebInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebInfoRepository extends JpaRepository<WebInfo, Integer> {
}
