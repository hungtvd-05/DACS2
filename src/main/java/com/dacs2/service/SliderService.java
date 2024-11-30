package com.dacs2.service;

import com.dacs2.model.Slider;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface SliderService {

    public List<Slider> getSliderList();

    public void addSlider(MultipartFile img, String url) throws IOException;

    public void deleteSlider(Integer id);

}
