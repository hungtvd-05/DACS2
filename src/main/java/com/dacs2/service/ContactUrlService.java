package com.dacs2.service;

import com.dacs2.model.ContactUrl;

import java.io.IOException;
import java.util.List;

public interface ContactUrlService {

    public List<ContactUrl> getContactUrls();

    public void addContactUrl(String name, String url) throws IOException;

    public void deleteContactUrl(int id) throws IOException;

}
