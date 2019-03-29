package com.ucbcba.joel.ucbcorreccionformato.page_calibration;

import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.IOException;

public class PdfDocument {
    private PDDocument document;
    private int coverPage;
    private int generalIndexPageStart;
    private int generalIndexPageEnd;
    private int figureTableIndexPageEnd;
    private int biographyPageStart;
    private int annexedPageStart;

    public PdfDocument(PDDocument document) throws IOException {
        this.document = document;
        PagesSeeker seeker = new PagesSeeker(document);
        this.coverPage = seeker.getCoverPage();
        this.generalIndexPageStart = seeker.getFirstGeneralIndexPage();
        this.generalIndexPageEnd = seeker.getLastGeneralIndexPage(generalIndexPageStart);
        this.figureTableIndexPageEnd = seeker.getLastFigureTableIndexPage(generalIndexPageStart);
        this.biographyPageStart = seeker.getFirstBiographyPage();
        this.annexedPageStart = seeker.getFirstAnnexedPage();
    }

    public int getCoverPage() {
        return coverPage;
    }

    public void setCoverPage(int coverPage) {
        this.coverPage = coverPage;
    }

    public int getGeneralIndexPageStart() {
        return generalIndexPageStart;
    }

    public void setGeneralIndexPageStart(int generalIndexPageStart) {
        this.generalIndexPageStart = generalIndexPageStart;
    }

    public int getGeneralIndexPageEnd() {
        return generalIndexPageEnd;
    }

    public void setGeneralIndexPageEnd(int generalIndexPageEnd) {
        this.generalIndexPageEnd = generalIndexPageEnd;
    }

    public int getAnnexedPageStart() {
        return annexedPageStart;
    }

    public void setAnnexedPageStart(int annexedPageStart) {
        this.annexedPageStart = annexedPageStart;
    }

    public PDDocument getDocument() {
        return document;
    }

    public int getBiographyPageStart() {
        return biographyPageStart;
    }

    public void setBiographyPageStart(int biographyPageStart) {
        this.biographyPageStart = biographyPageStart;
    }

    public int getFigureTableIndexPageEnd() {
        return figureTableIndexPageEnd;
    }

    public void setFigureTableIndexPageEnd(int figureTableIndexPageEnd) {
        this.figureTableIndexPageEnd = figureTableIndexPageEnd;
    }
}
