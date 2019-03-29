package com.ucbcba.joel.ucbcorreccionformato.formatErrors.formatRules;

import com.ucbcba.joel.ucbcorreccionformato.formatErrors.HighlightsReport.FormatErrorReport;

import java.io.IOException;
import java.util.List;

public interface FormatRule {
    List<FormatErrorReport> getFormatErrors(int page) throws IOException;
}
