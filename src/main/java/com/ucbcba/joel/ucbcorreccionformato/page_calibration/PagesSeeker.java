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
        boolean bool1 = generalSeeker.isTheWordInThePage(page,"Boliviana");
        boolean bool2 = generalSeeker.isTheWordInThePage(page,"boliviana");
        boolean bool3 = generalSeeker.isTheWordInThePage(page,"BOLIVIANA");
        boolean bool4 = generalSeeker.isTheWordInThePage(page,"Regional");
        boolean bool5 = generalSeeker.isTheWordInThePage(page,"regional");
        boolean bool6 = generalSeeker.isTheWordInThePage(page,"REGIONAL");
        boolean bool7 = generalSeeker.isTheWordInThePage(page,"Departamento");
        boolean bool8 = generalSeeker.isTheWordInThePage(page,"departamento");
        boolean bool9 = generalSeeker.isTheWordInThePage(page,"DEPARTAMENTO");
        boolean bool10 = generalSeeker.isTheWordInThePage(page,"Carrera");
        boolean bool11 = generalSeeker.isTheWordInThePage(page,"carrera");
        boolean bool12 = generalSeeker.isTheWordInThePage(page,"CARRERA");
        boolean bool13 = generalSeeker.isTheWordInThePage(page,"– Bolivia");
        boolean bool14 = generalSeeker.isTheWordInThePage(page,"– bolivia");
        boolean bool15 = generalSeeker.isTheWordInThePage(page,"– BOLIVIA");
        return getNumberOfTrues(bool1,bool2, bool3, bool4, bool5, bool6,bool7, bool8, bool9, bool10,bool11,bool12, bool13, bool14, bool15) >= 3;
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
        boolean bool1 = generalSeeker.isTheWordInThePage(page,"Figura");
        boolean bool2 = generalSeeker.isTheWordInThePage(page,"FIGURA");
        boolean bool3 = generalSeeker.isTheWordInThePage(page,"TABLA");
        boolean bool4 = generalSeeker.isTheWordInThePage(page,"Tabla");
        return getNumberOfTrues(bool1,bool2,bool3,bool4)>=1;
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
        boolean bool1,bool2,bool3,bool4;
        bool1 = generalSeeker.isTheWordInThePage(page,"BIBLIOGRAFÍA");
        bool2 = generalSeeker.isTheWordInThePage(page,"Bibliografía");
        String word = Normalizer.normalize("BIBLIOGRAFÍA", Normalizer.Form.NFD);
        word = word.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        bool3 = generalSeeker.isTheWordInThePage(page,word);
        word = Normalizer.normalize("Bibliografía", Normalizer.Form.NFD);
        word = word.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        bool4 = generalSeeker.isTheWordInThePage(page,word);
        return getNumberOfTrues(bool1,bool2,bool3,bool4) >= 1;
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
        boolean bool1 = generalSeeker.isTheWordInThePage(page,"Anexo 1 ");
        boolean bool2 = generalSeeker.isTheWordInThePage(page,"anexo 1 ");
        boolean bool3 = generalSeeker.isTheWordInThePage(page,"ANEXO 1 ");
        boolean bool4 = generalSeeker.isTheWordInThePage(page,"Anexo 1:");
        boolean bool5 = generalSeeker.isTheWordInThePage(page,"anexo 1:");
        boolean bool6 = generalSeeker.isTheWordInThePage(page,"ANEXO 1:");
        boolean bool7 = generalSeeker.isTheWordInThePage(page,"Anexo 1,");
        boolean bool8 = generalSeeker.isTheWordInThePage(page,"anexo 1,");
        boolean bool9 = generalSeeker.isTheWordInThePage(page,"ANEXO 1,");
        boolean bool10 = generalSeeker.isTheWordInThePage(page,"Anexo 1.");
        boolean bool11 = generalSeeker.isTheWordInThePage(page,"anexo 1.");
        boolean bool12 = generalSeeker.isTheWordInThePage(page,"ANEXO 1.");
        return getNumberOfTrues(bool1,bool2,bool3,bool4,bool5,bool6,bool7,bool8,bool9,bool10,bool11,bool12) >= 1;
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
