package com.dacs2.repository;

import com.dacs2.model.ContactUrl;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactUrlRepository extends JpaRepository<ContactUrl, Integer> {
}
