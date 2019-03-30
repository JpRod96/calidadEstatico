package com.ucbcba.joel.ucbcorreccionformato.formaterrors.formatrules;


import com.ucbcba.joel.ucbcorreccionformato.formaterrors.formatcontrol.CoverFormat;
import com.ucbcba.joel.ucbcorreccionformato.formaterrors.highlightsreport.FormatErrorReport;
import com.ucbcba.joel.ucbcorreccionformato.general.GeneralSeeker;
import com.ucbcba.joel.ucbcorreccionformato.general.ReportFormatError;
import com.ucbcba.joel.ucbcorreccionformato.general.WordsProperties;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class CoverPageFormat implements FormatRule {

    private PDDocument pdfdocument;
    private GeneralSeeker seeker;
    private AtomicLong counter;
    private static final String CENTERED = "Centrado";

    public CoverPageFormat(PDDocument pdfdocument, AtomicLong counter){
        this.pdfdocument = pdfdocument;
        this.seeker = new GeneralSeeker(pdfdocument);
        this.counter = counter;
    }

    @Override
    public List<FormatErrorReport> getFormatErrors(int page) throws IOException {
        float pageWidth = pdfdocument.getPage(page-1).getMediaBox().getWidth();
        float pageHeight = pdfdocument.getPage(page-1).getMediaBox().getHeight();
        List<FormatErrorReport> formatErrors = new ArrayList<>();
        int numberOfLines = getNumberOfLines(page);
        int cont=1;

        PDFTextStripper pdfStripper = new PDFTextStripper();
        pdfStripper.setStartPage(page);
        pdfStripper.setEndPage(page);
        pdfStripper.setParagraphStart("\n");
        pdfStripper.setSortByPosition(true);
        //Recorre la página linea por linea
        for (String line : pdfStripper.getText(pdfdocument).split(pdfStripper.getParagraphStart())) {
            String[] arr = line.split(" ", 2);
            // Condicional si encuentra una linea en blanco
            if (!arr[0].equals("")) {
                String wordLine = line.trim();
                List<WordsProperties> words = seeker.findWordsFromAPage(page, wordLine);
                if (!words.isEmpty()) {
                    List<String> comments;
                    if (cont == 1) {
                        comments = new CoverFormat(words.get(0), 18, CENTERED, true, false, true).getFormatErrors(pageWidth);
                        reportFormatErrors(comments, words, formatErrors, pageWidth, pageHeight, page);
                    }
                    if (cont == 2) {
                        comments = new CoverFormat(words.get(0), 16, CENTERED, true, false, true).getFormatErrors(pageWidth);
                        reportFormatErrors(comments, words, formatErrors, pageWidth, pageHeight, page);
                    }
                    if (cont == 3 || cont == 4) {
                        comments = new CoverFormat(words.get(0), 14, CENTERED, true, false, false).getFormatErrors(pageWidth);
                        reportFormatErrors(comments, words, formatErrors, pageWidth, pageHeight, page);
                    }

                    if (cont == 5) {
                        comments = new CoverFormat(words.get(0), 16, CENTERED, true, false, false).getFormatErrors(pageWidth);
                        reportFormatErrors(comments, words, formatErrors, pageWidth, pageHeight, page);
                    }

                    if (cont > 5 && cont <= numberOfLines - 4) {
                        comments = new CoverFormat(words.get(0), 16, CENTERED, true, false, false).getFormatErrors(pageWidth);
                        reportFormatErrors(comments, words, formatErrors, pageWidth, pageHeight, page);
                    }

                    if (cont == numberOfLines - 3) {
                        comments = new CoverFormat(words.get(0), 12, "Derecho", false, true, false).getFormatErrors(pageWidth);
                        reportFormatErrors(comments, words, formatErrors, pageWidth, pageHeight, page);
                    }

                    if (cont == numberOfLines - 2) {
                        comments = new CoverFormat(words.get(0), 14, CENTERED, true, false, false).getFormatErrors(pageWidth);
                        reportFormatErrors(comments, words, formatErrors, pageWidth, pageHeight, page);
                    }

                    if (cont == numberOfLines || cont == numberOfLines - 1) {
                        comments = new CoverFormat(words.get(0), 12, CENTERED, false, false, false).getFormatErrors(pageWidth);
                        reportFormatErrors(comments, words, formatErrors, pageWidth, pageHeight, page);
                    }
                }

                cont++;
            }
        }
        return formatErrors;
    }


    public int getNumberOfLines(int page) throws IOException {
        int cont=0;
        PDFTextStripper pdfStripper = new PDFTextStripper();
        pdfStripper.setStartPage(page);
        pdfStripper.setEndPage(page);
        pdfStripper.setParagraphStart("\n");
        pdfStripper.setSortByPosition(true);
        for (String line : pdfStripper.getText(pdfdocument).split(pdfStripper.getParagraphStart())) {
            String[] arr = line.split(" ", 2);
            if (!arr[0].equals("")) {
                cont++;
            }
        }
        return cont;
    }

    private void reportFormatErrors(List<String> comments, List<WordsProperties> words, List<FormatErrorReport> formatErrors, float pageWidth, float pageHeight, int page) {
        if (!comments.isEmpty()) {
            formatErrors.add(new ReportFormatError(counter).reportFormatError(comments, words.get(0), pageWidth, pageHeight, page));
        }
    }
}