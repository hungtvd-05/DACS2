package com.dacs2.service.impl;

import com.dacs2.model.WebInfo;
import com.dacs2.repository.WebInfoRepository;
import com.dacs2.service.WebInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.NoSuchElementException;

@Service
public class WebInfoServiceImpl implements WebInfoService {

    String imgPath = System.getProperty("user.dir") + File.separator
            + "src" + File.separator + "main" + File.separator + "resources"
            + File.separator + "static" + File.separator + "img";

    @Autowired
    private WebInfoRepository webInfoRepository;

    @Override
    public WebInfo updateWebInfo(WebInfo webInfo, MultipartFile file) throws IOException {

        WebInfo newWebInfo = null;

        try {
            newWebInfo = webInfoRepository.findById(webInfo.getId()).get();
        } catch (NoSuchElementException n) {

        }

        if (!file.isEmpty()) {
            webInfo.setLogo(file.getOriginalFilename());
            Path path = Paths.get(imgPath + File.separator + "logos" + File.separator + file.getOriginalFilename());
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        }

        if (ObjectUtils.isEmpty(newWebInfo)) {
            return webInfoRepository.save(webInfo);
        } else {
            newWebInfo.setName(webInfo.getName());
            newWebInfo.setLogo(webInfo.getLogo());
            newWebInfo.setDescription(webInfo.getDescription());
            newWebInfo.setAddress(webInfo.getAddress());
            newWebInfo.setPhone(webInfo.getPhone());
            newWebInfo.setEmail(webInfo.getEmail());

            return webInfoRepository.save(newWebInfo);
        }
    }

    @Override
    public WebInfo getWebInfo() {
        return webInfoRepository.findAll().get(0);
    }
}
