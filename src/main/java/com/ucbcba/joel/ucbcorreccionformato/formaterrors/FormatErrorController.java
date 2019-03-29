package com.ucbcba.joel.ucbcorreccionformato.formaterrors;

import com.ucbcba.joel.ucbcorreccionformato.formaterrors.highlightsreport.FormatErrorReport;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class FormatErrorController {
    private static final Logger LOGGER = Logger.getLogger("com.ucbcba.joel.ucbcorreccionformato.FormatErrors.FormatErrorController");
    @PostMapping("/api/formatErrors/{fileName:.+}")
    public List<FormatErrorReport> greeting(@PathVariable String fileName, @RequestParam(value="coverPage") String coverPage
            , @RequestParam(value="generalIndexPageStart") String generalIndexPageStart, @RequestParam(value="generalIndexPageEnd") String generalIndexPageEnd
            , @RequestParam(value="figureTableIndexPageEnd") String figureTableIndexPageEnd, @RequestParam(value="biographyPage") String biographyPage
            , @RequestParam(value="annexedPage") String annexedPage)  {
        List<FormatErrorReport> formatErrors = new ArrayList<>();
        String dirPdfFile = "uploads/" + fileName;
        PDDocument pdfdocument = null;
        try {
            pdfdocument = PDDocument.load(new File(dirPdfFile));
            FormatErrorDetector formatErrorDetector = new FormatErrorDetector(pdfdocument);
            formatErrorDetector.analyzeFormatPdf(coverPage,generalIndexPageStart,generalIndexPageEnd,figureTableIndexPageEnd,biographyPage,annexedPage);
            formatErrors = formatErrorDetector.getFormatErrorReports();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return formatErrors;
    }
}
