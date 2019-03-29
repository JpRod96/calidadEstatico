package com.ucbcba.joel.ucbcorreccionformato.formaterrors.formatrules;

import com.ucbcba.joel.ucbcorreccionformato.formaterrors.bibliographies.PatternBibliographyReferences;
import com.ucbcba.joel.ucbcorreccionformato.formaterrors.highlightsreport.*;
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
            String[] arr = line.split(" ", 2);
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

    private void reportFormatErrors(List<String> comments, List<String> refBibliography, List<FormatErrorReport> formatErrors, float pageWidth, float pageHeight, int page) throws IOException {
        if (!comments.isEmpty()) {
            List<BoundingRect> boundingRects = new ArrayList<>();
            String contentText = "";
            float x = 0;
            float y=0;
            float endX=0;
            float upperY=0;
            for (int i = 0;i<refBibliography.size();i++){
                String lineWord = refBibliography.get(i);
                List<WordsProperties> lineWordWithProperties = seeker.findWordsFromAPage(page,lineWord);
                if (lineWordWithProperties.isEmpty()) {
                    lineWord = Normalizer.normalize(lineWord, Normalizer.Form.NFD);
                    lineWord = lineWord.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
                    lineWordWithProperties = seeker.findWordsFromAPage(page, lineWord);
                }
                if (!lineWordWithProperties.isEmpty()){
                    WordsProperties word = lineWordWithProperties.get(0);
                    BoundingRect boundingRect = new BoundingRect(word.getX(), word.getYPlusHeight(), word.getXPlusWidth(),word.getY(),pageWidth,pageHeight);
                    boundingRects.add(boundingRect);
                    if (i==0){
                        x = word.getX();
                        upperY = word.getYPlusHeight();
                        contentText = lineWord;
                    }
                    if (i==refBibliography.size()-1){
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

        Pattern discussionListBibliography = Pattern.compile("([^(]+\\([^)]+\\)\\.|[^(]+\\([dir.compe]+\\)[^(]*\\([^)]+\\)\\.)[^“]*“[^”]+”\\.[^<]*<[^>]+>[^<]*<[^>]+>[^(]*\\(fecha de consulta.+", Pattern.CASE_INSENSITIVE);
        PatternBibliographyReferences discussionList = new PatternBibliographyReferences("Listas de discusión",discussionListBibliography);

        Pattern pageWebBibliography = Pattern.compile("([^(]+\\([^)]+\\)\\.|[^(]+\\([dir.compe]+\\)[^(]*\\([^)]+\\)\\.)[^“]*“[^”]+”\\.[^E]*En:[^<]*<[^>]+>[^,]*,[^(]*\\(fecha de consulta.+", Pattern.CASE_INSENSITIVE);
        PatternBibliographyReferences pageWeb = new PatternBibliographyReferences("Página web",pageWebBibliography);

        Pattern emailBibliography = Pattern.compile("[^(]+\\([^)]+\\)[^(]*\\([^)]+\\)\\.[^“]*“[^”]+”\\.[^(]*\\([^)]+\\)[^(]*\\(fecha del mensaje.+", Pattern.CASE_INSENSITIVE);
        PatternBibliographyReferences email = new PatternBibliographyReferences("Correo electrónico",emailBibliography);

        Pattern radioBibliography = Pattern.compile("([^(]+\\([^)]+\\)\\.|[^(]+\\([dir.compe]+\\)[^(]*\\([^)]+\\)\\.)[^“]*“[^”]+”\\.[^(]*\\([^)]+\\).+", Pattern.CASE_INSENSITIVE);
        PatternBibliographyReferences radio = new PatternBibliographyReferences("Programa de radio ",radioBibliography);

        Pattern cdRomDvdBibliography = Pattern.compile("([^(]+\\([^)]+\\)\\.|[^(]+\\([dir.compe]+\\)[^(]*\\([^)]+\\)\\.)[^:]+:.+", Pattern.CASE_INSENSITIVE);
        PatternBibliographyReferences cdRomDvd = new PatternBibliographyReferences("Libro en soporte CD-ROM/DVD",cdRomDvdBibliography);

        Pattern thesisBibliography = Pattern.compile("[^(]+\\([^)]+\\)\\..+", Pattern.CASE_INSENSITIVE);
        PatternBibliographyReferences thesis = new PatternBibliographyReferences("Tesis/Trabajo de titulación",thesisBibliography);

        Pattern articleMagazineBibliography = Pattern.compile("([^(]+\\([^)]+\\)\\.|[^(]+\\([dir.compe]+\\)[^(]*\\([^)]+\\)\\.)[^“]*“[^”]+”\\.[^E]*En:.+Año.+N.+", Pattern.CASE_INSENSITIVE);
        PatternBibliographyReferences articleMagazine = new PatternBibliographyReferences("Artículo de revista",articleMagazineBibliography);

        Pattern chapterBookBibliography = Pattern.compile("([^(]+\\([^)]+\\)\\.|[^(]+\\([dir.compe]+\\)[^(]*\\([^)]+\\)\\.)[^“]*“[^”]+”\\.[^E]*En:[^:]+:.+", Pattern.CASE_INSENSITIVE);
        PatternBibliographyReferences chapterBook = new PatternBibliographyReferences("Capítulo de libro",chapterBookBibliography);

        Pattern articleNewspaperBibliography = Pattern.compile("([^(]+\\([^)]+\\)\\.|[^(]+\\([dir.compe]+\\)[^(]*\\([^)]+\\)\\.)[^“]*“[^”]+”\\.[^E]*En:[^(]+\\([^)]+\\).+", Pattern.CASE_INSENSITIVE);
        PatternBibliographyReferences articleNewspaper = new PatternBibliographyReferences("Artículo de periódico",articleNewspaperBibliography);

        Pattern conferenceArtworksBibliography = Pattern.compile("([^(]+\\([^)]+\\)\\.|[^(]+\\([dir.compe]+\\)[^(]*\\([^)]+\\)\\.)[^“]*“[^”]+”\\.[^E]*En:.+", Pattern.CASE_INSENSITIVE);
        PatternBibliographyReferences conferenceArtworks = new PatternBibliographyReferences("Congreso/Conferencia",conferenceArtworksBibliography);

        Pattern moviesBibliography = Pattern.compile("([^(]+\\([^)]+\\)\\.|[^(]+\\([dir.compe]+\\)[^(]*\\([^)]+\\)\\.)[^“]*“[^”]+”\\.[^:]+:.+", Pattern.CASE_INSENSITIVE);
        PatternBibliographyReferences movies = new PatternBibliographyReferences("Película",moviesBibliography);

        Pattern bookBibliography = Pattern.compile("([^(]+\\([^)]+\\)\\.|[^(]+\\([dir.compe]+\\)[^(]*\\([^)]+\\)\\.)[^:]+:.+", Pattern.CASE_INSENSITIVE);
        PatternBibliographyReferences book = new PatternBibliographyReferences("Libro",bookBibliography);


        if (lineWord.contains("http")){
            if( lineWord.contains("@")){
                return discussionList;
            }else{
                return pageWeb;
            }
        }

        if (lineWord.contains("@")){
            return email;
        }

        if (lineWord.contains("FM,") || lineWord.contains("AM,")){
            return radio;
        }

        if (lineWord.contains("CD-ROM") || lineWord.contains("DVD")){
            return cdRomDvd;
        }

        if (lineWord.contains("licenciatura") || lineWord.contains("Licenciatura") || lineWord.contains("titulación") || lineWord.contains("Titulación")){
            return thesis;
        }

        if (lineWord.contains("En:")){
            if( lineWord.contains("N°") || lineWord.contains(", Año")){
                return articleMagazine;
            }
            Matcher matcher = chapterBookBibliography.matcher(lineWord);
            if (matcher.find()){
                return chapterBook;
            }
            matcher = articleNewspaperBibliography.matcher(lineWord);
            if (matcher.find()){
                return articleNewspaper;
            }
            matcher = conferenceArtworksBibliography.matcher(lineWord);
            if (matcher.find()){
                return conferenceArtworks;
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
