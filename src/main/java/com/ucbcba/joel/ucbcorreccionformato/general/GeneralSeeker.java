package com.ucbcba.joel.ucbcorreccionformato.general;

import com.ucbcba.joel.ucbcorreccionformato.formaterrors.imagesonpdf.PdfImage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GeneralSeeker {
    private PDDocument pdfdocument;

    public GeneralSeeker(PDDocument pdfdocument){
        this.pdfdocument = pdfdocument;
    }

    public boolean isTheWordInThePage(int page, String searchWord) throws IOException {
        final boolean[] resp = {false};
        PDFTextStripper stripper = new PDFTextStripper() {
            @Override
            protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
                WordsProperties word = new WordsProperties(textPositions);
                String string = word.toString();
                int index = 0;
                int indexWordFound;
                while (((indexWordFound = string.indexOf(searchWord, index)) > -1) && !resp[0]) {
                    resp[0] = true;
                    index = indexWordFound + 1;
                }
                super.writeString(text, textPositions);
            }
        };
        stripper.setSortByPosition(true);
        stripper.setStartPage(page);
        stripper.setEndPage(page);
        stripper.getText(pdfdocument);
        return resp[0];
    }

    public List<WordsProperties> findWordsFromAPage(int page, String searchWord) throws IOException {
        final List<WordsProperties> listWordPositionSequences = new ArrayList<>();
        PDFTextStripper stripper = new PDFTextStripper() {
            @Override
            protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
                WordsProperties word = new WordsProperties(textPositions);
                String string = word.toString();
                int index = 0;
                int indexWordFound;
                while ((indexWordFound = string.indexOf(searchWord, index)) > -1) {
                    listWordPositionSequences.add(word.subSequence(indexWordFound, indexWordFound + searchWord.length()));
                    index = indexWordFound + 1;
                }
                super.writeString(text, textPositions);
            }
        };
        stripper.setSortByPosition(true);
        stripper.setStartPage(page);
        stripper.setEndPage(page);
        stripper.getText(pdfdocument);
        return listWordPositionSequences;
    }

    public String getLastWordsLine(int page) throws IOException {
        String resp = "";
        PDFTextStripper pdfStripper = getPdfTextStripper(page);
        for (String line: pdfStripper.getText(pdfdocument).split(pdfStripper.getParagraphStart()))
        {
            String[] arr = line.split(" ", 2);
            if (!arr[0].equals("")) {
                resp = line.trim();
            }

        }
        return resp;
    }

    public WordsProperties findFigureNumeration(PdfImage image, int pageNum) throws IOException {
        WordsProperties resp = null;
        PDFTextStripper pdfStripper = getPdfTextStripper(pageNum);

        for (String line : pdfStripper.getText(pdfdocument).split(pdfStripper.getParagraphStart())) {
            String[] arr = line.split(" ", 2);
            if (!arr[0].equals("")) {
                String wordLine = line.trim();
                List<WordsProperties> words = findWordsFromAPage(pageNum, wordLine);
                resp = getFigureNumeration(image, resp, arr[0], words);
            }
        }
        return resp;
    }

    private PDFTextStripper getPdfTextStripper(int pageNum) throws IOException {
        PDFTextStripper pdfStripper = new PDFTextStripper();
        pdfStripper.setStartPage(pageNum);
        pdfStripper.setEndPage(pageNum);
        pdfStripper.setParagraphStart("\n");
        pdfStripper.setSortByPosition(true);
        return pdfStripper;
    }

    private WordsProperties getFigureNumeration(PdfImage image, WordsProperties resp, String s, List<WordsProperties> words) {
        if (s.contains("Figura")) {
            for (WordsProperties word : words) {
                if ((image.getY() > word.getY()) && (image.getY() - 150 < word.getY())) {
                    resp = word;
                }
            }
        }
        return resp;
    }


    public WordsProperties findFigureSource(PdfImage image, int pageNum) throws IOException {
        PDFTextStripper pdfStripper = getPdfTextStripper(pageNum);

        for (String line : pdfStripper.getText(pdfdocument).split(pdfStripper.getParagraphStart())) {
            String[] arr = line.split(" ", 2);
            if (!arr[0].equals("")) {
                String wordLine = line.trim();
                List<WordsProperties> words = findWordsFromAPage(pageNum, wordLine);
                return getFigureSource(image, arr[0], words);
            }
        }
        return null;
    }

    private WordsProperties getFigureSource(PdfImage image, String s, List<WordsProperties> words) {
        if (s.contains("Fuente")) {
            for (WordsProperties word : words) {
                if ((image.getEndY() < word.getY()) && (image.getEndY() + 100 > word.getY())) {
                    return word;
                }
            }
        }
        return null;
    }
}
