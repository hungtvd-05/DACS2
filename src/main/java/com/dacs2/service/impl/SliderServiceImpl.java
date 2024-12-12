package com.dacs2.service.impl;

import com.dacs2.model.ContactUrl;
import com.dacs2.model.Slider;
import com.dacs2.repository.SliderRepository;
import com.dacs2.service.SliderService;
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
public class SliderServiceImpl implements SliderService {

    String imgPath = System.getProperty("user.dir") + File.separator
            + "src" + File.separator + "main" + File.separator + "resources"
            + File.separator + "static" + File.separator + "img";

    @Autowired
    private SliderRepository sliderRepository;

    @Override
    public List<Slider> getSliderList() {
        return sliderRepository.findAll();
    }

    @Override
    public void addSlider(MultipartFile img, String url) throws IOException {

        Slider slider = new Slider();

        if (!img.isEmpty()) {
            slider.setImage(img.getOriginalFilename());
            Path path = Paths.get(imgPath + File.separator + "slider" + File.separator + img.getOriginalFilename());
            Files.copy(img.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        }

        slider.setUrl(url);
        sliderRepository.save(slider);

    }

    @Override
    public void deleteSlider(Integer id) {
        Slider slider = sliderRepository.findById(id).get();
        sliderRepository.delete(slider);
    }
}
