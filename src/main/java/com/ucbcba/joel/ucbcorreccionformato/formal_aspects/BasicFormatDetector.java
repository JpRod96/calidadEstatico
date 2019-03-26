package com.ucbcba.joel.ucbcorreccionformato.formal_aspects;

import com.ucbcba.joel.ucbcorreccionformato.General.GeneralSeeker;
import com.ucbcba.joel.ucbcorreccionformato.General.WordsProperties;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class BasicFormatDetector {
    private PDDocument pdfdocument;
    private GeneralSeeker seeker;
    private List<BasicFormatReport> basicFormatReports = new ArrayList<>();

    public BasicFormatDetector(PDDocument pdfdocument) {
        this.pdfdocument = pdfdocument;
        this.seeker = new GeneralSeeker(pdfdocument);
    }

    public void analyzeBasicFormat(String figureTableIndexPageEnd, String annexedPage) throws IOException {
        int indexPageEndI = Integer.parseInt(figureTableIndexPageEnd);
        int annexedPageI = Integer.parseInt(annexedPage);
        int midlePage = indexPageEndI+annexedPageI;
        midlePage = midlePage/2;
        basicFormatReports.addAll(getBasicFormatReport(midlePage));
    }

    public List<BasicFormatReport> getBasicFormatReport(int page) throws IOException {
        List<BasicFormatReport> resp = new ArrayList<>();

        getCorrectFormatSize(page, resp);


        String formatMargin = "Margen 3cm (derecho, inferior y superior) 3.5cm (izquierdo)";
        boolean isCorrectMargin = true;

        String formatFont = "Tipo de letra: Times New Roman 12";
        boolean isCorrectFont = true;

        String formatNumeration = "Numeración parte inferior";
        boolean isCorrectNumeration = false;

        // Recorre el PDF linea por linea
        PDFTextStripper pdfStripper = getPdfTextStripper(page);

        for (String line : pdfStripper.getText(pdfdocument).split(pdfStripper.getParagraphStart())) {
            String[] arr = line.split(" ", 2);
            if (!arr[0].equals("")) {
                String wordLine = line.trim();
                // En caso que encuentre la numeración de la página
                if (isPageNumeration(wordLine)) {
                    List<WordsProperties> words = seeker.findWordsFromAPage(page,wordLine);
                    // En caso que no se encuentre la linea del PDF la vuelve a buscar normalizandola
                    words = normalizeWords(page, wordLine, words);

                    if (words == null) continue;

                    isCorrectMargin = getWrongMargin(isCorrectMargin, words);

                    isCorrectFont = getWrongFont(isCorrectFont, words);
                }
                else {
                    List<WordsProperties> words = seeker.findWordsFromAPage( page,wordLine);

                        if (!words.isEmpty() && isWordsCorrectPosition(words)) {
                            isCorrectNumeration = true;
                        }
                }
            }
        }
        resp.add(new BasicFormatReport(formatMargin,isCorrectMargin));
        resp.add(new BasicFormatReport(formatFont,isCorrectFont));
        resp.add(new BasicFormatReport(formatNumeration,isCorrectNumeration));

        return resp;
    }

    private PDFTextStripper getPdfTextStripper(int page) throws IOException {
        PDFTextStripper pdfStripper = new PDFTextStripper();
        pdfStripper.setStartPage(page);
        pdfStripper.setEndPage(page);
        pdfStripper.setParagraphStart("\n");
        pdfStripper.setSortByPosition(true);
        return pdfStripper;
    }

    private boolean isPageNumeration(String wordLine) {
        return wordLine.length() - wordLine.replaceAll(" ", "").length() >= 1;
    }

    private boolean getWrongFont(boolean isCorrectFont, List<WordsProperties> words) {
        for(WordsProperties word:words){
            if(isWrongFont(word)){
                isCorrectFont = false;
            }
        }
        return isCorrectFont;
    }

    private boolean getWrongMargin(boolean isCorrectMargin, List<WordsProperties> words) {
        for(WordsProperties word:words) {
            if (isWrongMargin(word)) {
                isCorrectMargin = false;
            }
        }
        return isCorrectMargin;
    }

    private boolean isWrongFont(WordsProperties word) {
        return !word.getFontBassic().contains("Times") || !word.getFontBassic().contains("New") || !word.getFontBassic().contains("Roman") || word.getFontSizeBasic()!=12;
    }

    private boolean isWrongMargin(WordsProperties word) {
        return word.getX() < 95 || word.getYPlusHeight() < 80 || word.getXPlusWidth() > 530 || word.getY() > 705;
    }

    private List<WordsProperties> normalizeWords(int page, String wordLine, List<WordsProperties> words) throws IOException {
        if (words.isEmpty()) {
            wordLine = Normalizer.normalize(wordLine, Normalizer.Form.NFD);
            wordLine = wordLine.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
            words = seeker.findWordsFromAPage(page, wordLine);
            if (words.isEmpty()) {
                return null;
            }
        }
        return words;
    }

    private void getCorrectFormatSize(int page, List<BasicFormatReport> resp) {
        String formatSize = "Tamaño de hoja carta";
        boolean isCorrectSize = false;
        float pageWidth = pdfdocument.getPage(page-1).getMediaBox().getWidth();
        float pageHeight = pdfdocument.getPage(page-1).getMediaBox().getHeight();
        if (pageWidth == 612.0 && pageHeight == 792.0){
            isCorrectSize = true;
        }
        resp.add(new BasicFormatReport(formatSize,isCorrectSize));
    }

    private boolean isWordsCorrectPosition(List<WordsProperties> words) {
        return words.get(words.size()-1).getY() > 720;
    }

    public List<BasicFormatReport> getBasicFormatReports() {
        return basicFormatReports;
    }

    public void setBasicFormatReports(List<BasicFormatReport> basicFormatReports) {
        this.basicFormatReports = basicFormatReports;
    }
}
