package com.ucbcba.joel.ucbcorreccionformato.formaterrors.formatrules;

import com.ucbcba.joel.ucbcorreccionformato.formaterrors.HighlightsReport.FormatErrorReport;
import com.ucbcba.joel.ucbcorreccionformato.General.GeneralSeeker;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Jp on 27/03/2019.
 */
public abstract class EssentialDocFormat implements FormatRule{

    PDDocument pdfDocument;
    GeneralSeeker seeker;
    AtomicLong counter;
    float pageWidth;
    float pageHeight;
    List<FormatErrorReport> formatErrors;
    PDFTextStripper pdfStripper;

    public EssentialDocFormat(PDDocument pdfdocument, AtomicLong counter){
        this.pdfDocument = pdfdocument;
        this.seeker = new GeneralSeeker(pdfdocument);
        this.counter = counter;
    }

    public void defaultGetFormatError(int page) throws IOException {
        pageWidth = pdfDocument.getPage(page-1).getMediaBox().getWidth();
        pageHeight = pdfDocument.getPage(page-1).getMediaBox().getHeight();
        formatErrors = new ArrayList<>();

        pdfStripper = new PDFTextStripper();
        pdfStripper.setStartPage(page);
        pdfStripper.setEndPage(page);
        pdfStripper.setParagraphStart("\n");
        pdfStripper.setSortByPosition(true);
    }

}
