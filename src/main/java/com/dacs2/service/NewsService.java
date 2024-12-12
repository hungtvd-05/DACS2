package com.dacs2.service;

import com.dacs2.model.News;
import org.springframework.data.domain.Page;

import java.util.List;

public interface NewsService {

    News saveNews(News news);

    Page<News> getNewsByPage(Integer page, Integer size);

    News getNewsById(int id);

    News updateNews(News news);

    Boolean deleteNews(int id);

    Page<News> searchNews(String keyword, Integer page, Integer size);

    List<News> getNewsByStyle(String style);

    Page<News> getAllNewsForHome(Integer page, Integer size);

}
