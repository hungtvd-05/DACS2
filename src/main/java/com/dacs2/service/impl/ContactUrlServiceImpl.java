package com.dacs2.service.impl;

import com.dacs2.model.ContactUrl;
import com.dacs2.repository.ContactUrlRepository;
import com.dacs2.service.ContactUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class ContactUrlServiceImpl implements ContactUrlService {

    String imgPath = System.getProperty("user.dir") + File.separator
            + "src" + File.separator + "main" + File.separator + "resources"
            + File.separator + "static" + File.separator + "img";

    @Autowired
    private ContactUrlRepository contactUrlRepository;

    @Override
    public List<ContactUrl> getContactUrls() {
        return contactUrlRepository.findAll();
    }

    @Override
    public void addContactUrl(String name, String url) throws IOException {

        ContactUrl contactUrl = new ContactUrl();

        contactUrl.setName(name);
        contactUrl.setUrl(url);
        contactUrlRepository.save(contactUrl);

    }

    @Override
    public void deleteContactUrl(int id) throws IOException {
        ContactUrl contactUrl = contactUrlRepository.findById(id).get();
        contactUrlRepository.delete(contactUrl);
    }
}
