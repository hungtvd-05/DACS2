package com.dacs2.service;

import com.dacs2.model.News;
import org.springframework.data.domain.Page;

public interface NewsService {

    public News saveNews(News news);

    public Page<News> getNewsByPage(Integer page, Integer size);

    public News getNewsById(int id);

    public News updateNews(News news);

    public Boolean deleteNews(int id);

    public Page<News> searchNews(String keyword, Integer page, Integer size);

}
