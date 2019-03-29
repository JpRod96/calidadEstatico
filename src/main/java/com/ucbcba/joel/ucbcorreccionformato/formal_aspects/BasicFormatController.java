package com.ucbcba.joel.ucbcorreccionformato.formal_aspects;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class BasicFormatController {
    @PostMapping(value = "/api/basicFormat/{fileName:.+}")
    public List<BasicFormatReport> getBasicMisstakes(@PathVariable String fileName, @RequestParam(value="figureTableIndexPageEnd") String figureTableIndexPageEnd
            , @RequestParam(value="annexedPage") String annexedPage) {
        Logger logger = Logger.getLogger("com.ucbcba.joel.ucbcorreccionformato.formal_aspects.BasicFormatController");
        List<BasicFormatReport> basicFormatReports = new ArrayList<>();
        String dirPdfFile = "uploads/"+fileName;
        PDDocument pdfdocument = null;
        try {
            pdfdocument = PDDocument.load( new File(dirPdfFile) );
            BasicFormatDetector formatErrorDetector = new BasicFormatDetector(pdfdocument);
            formatErrorDetector.analyzeBasicFormat(figureTableIndexPageEnd,annexedPage);
            basicFormatReports = formatErrorDetector.getBasicFormatReports();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "context", e);
        }
        return basicFormatReports;
    }
}
