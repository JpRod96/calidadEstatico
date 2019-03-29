package com.ucbcba.joel.ucbcorreccionformato.page_calibration;

import com.ucbcba.joel.ucbcorreccionformato.General.GeneralSeeker;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.IOException;
import java.text.Normalizer;

public class PagesSeeker {

    private PDDocument pdfdocument;
    private GeneralSeeker generalSeeker;

    public PagesSeeker(PDDocument pdfdocument){
        this.pdfdocument = pdfdocument;
        this.generalSeeker = new GeneralSeeker(pdfdocument);
    }

    public boolean isTheCoverInThisPage(int page) throws IOException {
        boolean[] isTheWordInTheTitle = {
                generalSeeker.isTheWordInThePage(page,"Boliviana"),
                generalSeeker.isTheWordInThePage(page,"Boliviana"),
                generalSeeker.isTheWordInThePage(page,"boliviana"),
                generalSeeker.isTheWordInThePage(page,"BOLIVIANA"),
                generalSeeker.isTheWordInThePage(page,"Regional"),
                generalSeeker.isTheWordInThePage(page,"regional"),
                generalSeeker.isTheWordInThePage(page,"REGIONAL"),
                generalSeeker.isTheWordInThePage(page,"Departamento"),
                generalSeeker.isTheWordInThePage(page,"departamento"),
                generalSeeker.isTheWordInThePage(page,"DEPARTAMENTO"),
                generalSeeker.isTheWordInThePage(page,"Carrera"),
                generalSeeker.isTheWordInThePage(page,"carrera"),
                generalSeeker.isTheWordInThePage(page,"CARRERA"),
                generalSeeker.isTheWordInThePage(page,"– Bolivia"),
                generalSeeker.isTheWordInThePage(page,"– bolivia"),
                generalSeeker.isTheWordInThePage(page,"– BOLIVIA")};

        return getNumberOfTrues(isTheWordInTheTitle) >= 3;
    }


    public int getCoverPage() throws IOException {
        int resp = 0;
        for (int page = 1; page <= pdfdocument.getNumberOfPages(); page++) {
            if ( isTheCoverInThisPage(page) ){
                return page;
            }
        }
        return resp;
    }


    public boolean isTheGeneralIndexInThisPage(int page) throws IOException {
        return generalSeeker.isTheWordInThePage(page,"..........");
    }

    public int getFirstGeneralIndexPage() throws IOException {
        int resp = 0;
        for (int page = 1; page <= pdfdocument.getNumberOfPages(); page++) {
            if ( isTheGeneralIndexInThisPage(page) ){
                return page;
            }
        }
        return resp;
    }


    public boolean isTheFigureTableIndexInThisPage(int page) throws IOException {
        boolean[] isWordReference = {
                generalSeeker.isTheWordInThePage(page,"Figura"),
                generalSeeker.isTheWordInThePage(page,"FIGURA"),
                generalSeeker.isTheWordInThePage(page,"TABLA"),
                generalSeeker.isTheWordInThePage(page,"Tabla")};

        return getNumberOfTrues(isWordReference)>=1;
    }

    public int getLastGeneralIndexPage(int generalIndexPageStart) throws IOException {
        int resp = 0;
        if(generalIndexPageStart == 0){
            return resp;
        }
        for (int page = generalIndexPageStart; page <= pdfdocument.getNumberOfPages(); page++) {
            if ( isTheGeneralIndexInThisPage(page) && !isTheFigureTableIndexInThisPage(page)){
                resp = page;
            }else{
                return resp;
            }
        }
        return resp;
    }


    public int getLastFigureTableIndexPage (int generalIndexPageStart) throws IOException {
        int resp = 0;
        if(generalIndexPageStart == 0){
            return resp;
        }
        for (int page = generalIndexPageStart; page <= pdfdocument.getNumberOfPages(); page++) {
            if ( isTheGeneralIndexInThisPage(page) ){
                resp = page;
            }else{
                return resp;
            }
        }
        return resp;
    }

    public boolean isTheFirsBiographyInThisPage(int page) throws IOException {
        boolean[] isTheWordInThePage = {
                generalSeeker.isTheWordInThePage(page,"BIBLIOGRAFÍA"),
                generalSeeker.isTheWordInThePage(page,"Bibliografía"),
                generalSeeker.isTheWordInThePage(page,getWord("BIBLIOGRAFÍA")),
                generalSeeker.isTheWordInThePage(page,getWord("Bibliografía"))};
        return getNumberOfTrues(isTheWordInThePage) >= 1;
    }

    private String getWord(String wordFormat) {
        String word = Normalizer.normalize(wordFormat, Normalizer.Form.NFD);
        word = word.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return word;
    }


    public int getFirstBiographyPage() throws IOException {
        int resp = pdfdocument.getNumberOfPages()+1;
        for (int page = pdfdocument.getNumberOfPages(); page >= 1; page--) {
            if ( isTheFirsBiographyInThisPage(page) ){
                return page;
            }
        }
        return resp;
    }


    public boolean isTheFirstAnnexInThisPage(int page) throws IOException {
        boolean[] isAnnex = {
                generalSeeker.isTheWordInThePage(page,"Anexo 1 "),
                generalSeeker.isTheWordInThePage(page,"anexo 1 "),
                generalSeeker.isTheWordInThePage(page,"ANEXO 1 "),
                generalSeeker.isTheWordInThePage(page,"Anexo 1:"),
                generalSeeker.isTheWordInThePage(page,"anexo 1:"),
                generalSeeker.isTheWordInThePage(page,"ANEXO 1:"),
                generalSeeker.isTheWordInThePage(page,"Anexo 1,"),
                generalSeeker.isTheWordInThePage(page,"anexo 1,"),
                generalSeeker.isTheWordInThePage(page,"ANEXO 1,"),
                generalSeeker.isTheWordInThePage(page,"Anexo 1."),
                generalSeeker.isTheWordInThePage(page,"anexo 1."),
                generalSeeker.isTheWordInThePage(page,"ANEXO 1.")
        };
        return getNumberOfTrues(isAnnex) >= 1;
    }


    public int getFirstAnnexedPage() throws IOException {
        int resp = pdfdocument.getNumberOfPages()+1;
        for (int page = pdfdocument.getNumberOfPages(); page >= 1; page--) {
            if ( isTheFirstAnnexInThisPage(page) ){
                return page;
            }
        }
        return resp;
    }



    public int getNumberOfTrues(boolean... vars) {
        int count = 0;
        for (boolean var : vars) {
            count += (var ? 1 : 0);
        }
        return count;
    }
}
