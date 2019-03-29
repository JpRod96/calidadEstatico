package com.ucbcba.joel.ucbcorreccionformato.formaterrors.formatrules;

import com.ucbcba.joel.ucbcorreccionformato.formaterrors.Bibliographies.PatternBibliographyReferences;
import com.ucbcba.joel.ucbcorreccionformato.formaterrors.HighlightsReport.*;
import com.ucbcba.joel.ucbcorreccionformato.General.WordsProperties;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BiographyPageFormat extends  EssentialDocFormat {

    public BiographyPageFormat(PDDocument pdfdocument, AtomicLong counter){
        super(pdfdocument, counter);
    }
    @Override
    public List<FormatErrorReport> getFormatErrors(int page) throws IOException {
        defaultGetFormatError(page);
        List<String> refBibliography = new ArrayList<>();
        //Recorre la página linea por linea
        for (String line : pdfStripper.getText(pdfDocument).split(pdfStripper.getParagraphStart())) {
            String arr[] = line.split(" ", 2);
            // Condicional si encuentra una linea en blanco
            if (!arr[0].equals("")) {
                String wordLine = line.trim();
                if (!wordLine.contains("BIBLIOGRAFÍA") && !wordLine.contains("Bibliografía") && !wordLine.contains("BIBLIOGRAFÍA")) {
                    avoidPageControl(wordLine, refBibliography, page);
                }
            }
        }
        checkBibliographicFormat(refBibliography, formatErrors, pageWidth, pageHeight, page);
        return formatErrors;
    }

    private void avoidPageControl(String wordLine, List<String> refBibliography, int page)throws IOException{
        if ((wordLine.length() - wordLine.replaceAll(" ", "").length() >= 1) || wordLine.length() > 4) {
            if (wordLine.charAt(0) == '[') {
                checkBibliographicFormat(refBibliography, formatErrors, pageWidth, pageHeight, page);
                refBibliography = new ArrayList<>();
                refBibliography.add(wordLine);
            } else {
                refBibliography.add(wordLine);
            }
        }
    }
    private void checkBibliographicFormat(List<String> refBibliography, List<FormatErrorReport> formatErrors, float pageWidth, float pageHeight, int page) throws IOException{
        StringBuilder bibliographic = new StringBuilder();
        for (String lines : refBibliography) {
            bibliographic.append(lines).append(" ");
        }
        if(bibliographic.length()!=0){
            List<String> comments = new ArrayList<>();
            PatternBibliographyReferences pattern = getPattern(bibliographic.toString());
            if (pattern!=null) {
                Matcher matcher = pattern.getMatcher(bibliographic.toString());
                if (!matcher.find()) {
                    comments.add("La referencia en "+pattern.getName()+".");
                }
            }else{
                comments.add("Consultar la Guía para la presentación de trabajos académicos.");
            }
            reportFormatErrors(comments, refBibliography, formatErrors, pageWidth, pageHeight, page);
        }
    }

    private void reportFormatErrors(List<String> comments, List<String> ref_bibliografy, List<FormatErrorReport> formatErrors, float pageWidth, float pageHeight, int page) throws IOException {
        if (comments.size() != 0) {
    private void reportFormatErrors(List<String> comments, List<String> refBibliography, List<FormatErrorReport> formatErrors, float pageWidth, float pageHeight, int page) throws IOException {
        if (!comments.isEmpty()) {
            List<BoundingRect> boundingRects = new ArrayList<>();
            String contentText = "";
            float x = 0,y=0,endX=0,upperY=0;
            for (int i = 0;i<ref_bibliografy.size();i++){
                String lineWord = ref_bibliografy.get(i);
                List<WordsProperties> lineWordWithProperties = seeker.findWordsFromAPage(page,lineWord);
                if (lineWordWithProperties.size() == 0) {
                    lineWord = Normalizer.normalize(lineWord, Normalizer.Form.NFD);
                    lineWord = lineWord.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
                    lineWordWithProperties = seeker.findWordsFromAPage(page, lineWord);
                }
                if (lineWordWithProperties.size()!=0){
                    WordsProperties word = lineWordWithProperties.get(0);
                    BoundingRect boundingRect = new BoundingRect(word.getX(), word.getYPlusHeight(), word.getXPlusWidth(),word.getY(),pageWidth,pageHeight);
                    boundingRects.add(boundingRect);
                    if (i==0){
                        x = word.getX();
                        upperY = word.getYPlusHeight();
                        contentText = lineWord;
                    }
                    if (i==ref_bibliografy.size()-1){
                        endX = word.getXPlusWidth();
                        y = word.getY();
                    }
                }
            }
            BoundingRect mainBoundingRect = new BoundingRect(x, upperY, endX,y,pageWidth,pageHeight);
            Position position = new Position(mainBoundingRect,boundingRects,page);
            Content content = new Content(contentText);
            Comment comment = new Comment(comments.get(0),"");
            String id = String.valueOf(counter.incrementAndGet());
            formatErrors.add(new FormatErrorReport(content,position,comment,id));
        }
    }


    public PatternBibliographyReferences getPattern(String lineWord){

        Pattern discussion_list_bibliography = Pattern.compile("([^(]+\\([^)]+\\)\\.|[^(]+\\([dir.compe]+\\)[^(]*\\([^)]+\\)\\.)[^“]*“[^”]+”\\.[^<]*<[^>]+>[^<]*<[^>]+>[^(]*\\(fecha de consulta.+", Pattern.CASE_INSENSITIVE);
        PatternBibliographyReferences discussion_list = new PatternBibliographyReferences("Listas de discusión",discussion_list_bibliography);

        Pattern page_web_bibliography = Pattern.compile("([^(]+\\([^)]+\\)\\.|[^(]+\\([dir.compe]+\\)[^(]*\\([^)]+\\)\\.)[^“]*“[^”]+”\\.[^E]*En:[^<]*<[^>]+>[^,]*,[^(]*\\(fecha de consulta.+", Pattern.CASE_INSENSITIVE);
        PatternBibliographyReferences page_web = new PatternBibliographyReferences("Página web",page_web_bibliography);

        Pattern email_bibliography = Pattern.compile("[^(]+\\([^)]+\\)[^(]*\\([^)]+\\)\\.[^“]*“[^”]+”\\.[^(]*\\([^)]+\\)[^(]*\\(fecha del mensaje.+", Pattern.CASE_INSENSITIVE);
        PatternBibliographyReferences email = new PatternBibliographyReferences("Correo electrónico",email_bibliography);

        Pattern radio_bibliography = Pattern.compile("([^(]+\\([^)]+\\)\\.|[^(]+\\([dir.compe]+\\)[^(]*\\([^)]+\\)\\.)[^“]*“[^”]+”\\.[^(]*\\([^)]+\\).+", Pattern.CASE_INSENSITIVE);
        PatternBibliographyReferences radio = new PatternBibliographyReferences("Programa de radio ",radio_bibliography);

        Pattern cd_rom_dvd_bibliography = Pattern.compile("([^(]+\\([^)]+\\)\\.|[^(]+\\([dir.compe]+\\)[^(]*\\([^)]+\\)\\.)[^:]+:.+", Pattern.CASE_INSENSITIVE);
        PatternBibliographyReferences cd_rom_dvd = new PatternBibliographyReferences("Libro en soporte CD-ROM/DVD",cd_rom_dvd_bibliography);

        Pattern thesis_bibliography = Pattern.compile("[^(]+\\([^)]+\\)\\..+", Pattern.CASE_INSENSITIVE);
        PatternBibliographyReferences thesis = new PatternBibliographyReferences("Tesis/Trabajo de titulación",thesis_bibliography);

        Pattern article_magazine_bibliography = Pattern.compile("([^(]+\\([^)]+\\)\\.|[^(]+\\([dir.compe]+\\)[^(]*\\([^)]+\\)\\.)[^“]*“[^”]+”\\.[^E]*En:.+Año.+N.+", Pattern.CASE_INSENSITIVE);
        PatternBibliographyReferences article_magazine = new PatternBibliographyReferences("Artículo de revista",article_magazine_bibliography);

        Pattern chapter_book_bibliography = Pattern.compile("([^(]+\\([^)]+\\)\\.|[^(]+\\([dir.compe]+\\)[^(]*\\([^)]+\\)\\.)[^“]*“[^”]+”\\.[^E]*En:[^:]+:.+", Pattern.CASE_INSENSITIVE);
        PatternBibliographyReferences chapter_book = new PatternBibliographyReferences("Capítulo de libro",chapter_book_bibliography);

        Pattern article_newspaper_bibliography = Pattern.compile("([^(]+\\([^)]+\\)\\.|[^(]+\\([dir.compe]+\\)[^(]*\\([^)]+\\)\\.)[^“]*“[^”]+”\\.[^E]*En:[^(]+\\([^)]+\\).+", Pattern.CASE_INSENSITIVE);
        PatternBibliographyReferences article_newspaper = new PatternBibliographyReferences("Artículo de periódico",article_newspaper_bibliography);

        Pattern conference_artworks_bibliography = Pattern.compile("([^(]+\\([^)]+\\)\\.|[^(]+\\([dir.compe]+\\)[^(]*\\([^)]+\\)\\.)[^“]*“[^”]+”\\.[^E]*En:.+", Pattern.CASE_INSENSITIVE);
        PatternBibliographyReferences conference_artworks = new PatternBibliographyReferences("Congreso/Conferencia",conference_artworks_bibliography);

        Pattern movies_bibliography = Pattern.compile("([^(]+\\([^)]+\\)\\.|[^(]+\\([dir.compe]+\\)[^(]*\\([^)]+\\)\\.)[^“]*“[^”]+”\\.[^:]+:.+", Pattern.CASE_INSENSITIVE);
        PatternBibliographyReferences movies = new PatternBibliographyReferences("Película",movies_bibliography);

        Pattern book_bibliography = Pattern.compile("([^(]+\\([^)]+\\)\\.|[^(]+\\([dir.compe]+\\)[^(]*\\([^)]+\\)\\.)[^:]+:.+", Pattern.CASE_INSENSITIVE);
        PatternBibliographyReferences book = new PatternBibliographyReferences("Libro",book_bibliography);


        if (lineWord.contains("http")){
            if( lineWord.contains("@")){
                return discussion_list;
            }else{
                return page_web;
            }
        }

        if (lineWord.contains("@")){
            return email;
        }

        if (lineWord.contains("FM,") || lineWord.contains("AM,")){
            return radio;
        }

        if (lineWord.contains("CD-ROM") || lineWord.contains("DVD")){
            return cd_rom_dvd;
        }

        if (lineWord.contains("licenciatura") || lineWord.contains("Licenciatura") || lineWord.contains("titulación") || lineWord.contains("Titulación")){
            return thesis;
        }

        if (lineWord.contains("En:")){
            if( lineWord.contains("N°") || lineWord.contains(", Año")){
                return article_magazine;
            }
            Matcher matcher = chapter_book_bibliography.matcher(lineWord);
            if (matcher.find()){
                return chapter_book;
            }
            matcher = article_newspaper_bibliography.matcher(lineWord);
            if (matcher.find()){
                return article_newspaper;
            }
            matcher = conference_artworks_bibliography.matcher(lineWord);
            if (matcher.find()){
                return conference_artworks;
            }
            return null;
        }

        if (lineWord.contains(":")){
            if( lineWord.contains("“")){
                return movies;
            }else{
                return book;
            }
        }

        return null;
    }
}
