package com.dacs2.service.impl;

import com.dacs2.model.News;
import com.dacs2.repository.NewsRepository;
import com.dacs2.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;

@Service
public class NewsServiceImpl implements NewsService {

    @Autowired
    private NewsRepository newsRepository;

    @Override
    public News saveNews(News news) {
        news.setDate(new Date());
        return newsRepository.save(news);
    }

    @Override
    public News updateNews(News news) {
        return newsRepository.save(news);
    }

    @Override
    public Boolean deleteNews(int id) {

        News news = newsRepository.findById(id).get();
        if (!ObjectUtils.isEmpty(news)) {
            newsRepository.delete(news);
            return true;
        }

        return false;
    }

    @Override
    public Page<News> getNewsByPage(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return newsRepository.findAll(pageable);
    }

    @Override
    public Page<News> getAllNewsForHome(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return newsRepository.findByStatusTrue(pageable);
    }

    @Override
    public News getNewsById(int id) {
        return newsRepository.findById(id).orElse(null);
    }

    @Override
    public Page<News> searchNews(String keyword, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return newsRepository.findByTitleContainingIgnoreCase(keyword, pageable);
    }

    @Override
    public List<News> getNewsByStyle(String style) {
        return newsRepository.findByStyle(style);
    }
}
