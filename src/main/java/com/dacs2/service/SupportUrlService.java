package com.dacs2.service;

import com.dacs2.model.SupportUrl;

import java.util.List;

public interface SupportUrlService {

    public void addSupportUrl(String title, String url, String position);

    public List<SupportUrl> getSupportUrl();

    public void deleteSupportUrl(Integer id);

}
