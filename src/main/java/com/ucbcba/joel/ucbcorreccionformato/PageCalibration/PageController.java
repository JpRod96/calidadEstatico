package com.ucbcba.joel.ucbcorreccionformato.PageCalibration;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class PageController {
    private static final Logger LOGGER = Logger.getLogger("com.ucbcba.joel.ucbcorreccionformato.PageCalibration.PageCalibrationController");
    @PostMapping("/api/getPages/{fileName:.+}")
    public List<String> getPages(@PathVariable String fileName)  {
        List<String> pages = new ArrayList<>();
        String dirPdfFile = "uploads/" + fileName;
        PDDocument pdfdocument = null;
        try {
            pdfdocument = PDDocument.load(new File(dirPdfFile));
            PdfDocument document = new PdfDocument(pdfdocument);
            pages.add(Integer.toString(document.getCoverPage()));
            pages.add(Integer.toString(document.getGeneralIndexPageStart()));
            pages.add(Integer.toString(document.getGeneralIndexPageEnd()));
            pages.add(Integer.toString(document.getFigureTableIndexPageEnd()));
            pages.add(Integer.toString(document.getBiographyPageStart()));
            pages.add(Integer.toString(document.getAnnexedPageStart()));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return pages;
    }
}
