package com.springboot.teamalbam;

import com.springboot.teamalbam.viewer.ocr.ClovaOcrService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

@SpringBootTest
public class ClovaOcrServiceTest {

    @Autowired
    private ClovaOcrService clovaOcrService;

    @Test
    void testOcrFromPdf() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File testPdf = new File(classLoader.getResource("test.pdf").getFile());

        String result = clovaOcrService.requestOcrFromPdf(testPdf);
        System.out.println("📄 OCR 결과:\n" + result);
    }
}

