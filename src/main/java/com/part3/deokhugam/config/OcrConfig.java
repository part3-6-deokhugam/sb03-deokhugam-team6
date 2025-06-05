package com.part3.deokhugam.config;

import net.sourceforge.tess4j.Tesseract;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OcrConfig {

    @Value("${ocr.traineddata}")
    public String TESSDATA_PREFIX;

    @Bean
    public Tesseract tesseract() {
        if (TESSDATA_PREFIX == null || TESSDATA_PREFIX.trim().isEmpty()) {
            throw new IllegalStateException("TESSDATA_PREFIX is not set. Please check the 'ocr.traineddata' configuration.");
        }

        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(TESSDATA_PREFIX);
        tesseract.setLanguage("eng+kor");
        return tesseract;
    }
}