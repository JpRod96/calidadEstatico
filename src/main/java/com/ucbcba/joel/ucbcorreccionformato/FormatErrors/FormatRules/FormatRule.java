package com.ucbcba.joel.ucbcorreccionformato.FormatErrors.FormatRules;

import com.ucbcba.joel.ucbcorreccionformato.FormatErrors.HighlightsReport.FormatErrorReport;

import java.io.IOException;
import java.util.List;

public interface FormatRule {
    List<FormatErrorReport> getFormatErrors(int page) throws IOException;
}
