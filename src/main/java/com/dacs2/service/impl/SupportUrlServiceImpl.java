package com.dacs2.service.impl;

import com.dacs2.model.SupportUrl;
import com.dacs2.repository.SupportUrlRepository;
import com.dacs2.service.SupportUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupportUrlServiceImpl implements SupportUrlService {

    @Autowired
    private SupportUrlRepository supportUrlRepository;

    @Override
    public void addSupportUrl(String title, String url, String position) {
        SupportUrl supportUrl = new SupportUrl();
        supportUrl.setTitle(title);
        supportUrl.setUrl(url);
        supportUrl.setPosition(position);
        supportUrlRepository.save(supportUrl);
    }

    @Override
    public List<SupportUrl> getSupportUrl() {
        return supportUrlRepository.findAll();
    }

    @Override
    public void deleteSupportUrl(Integer id) {
        supportUrlRepository.deleteById(id);
    }
}
