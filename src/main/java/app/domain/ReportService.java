package app.domain;

import app.service.CommandLine;

public interface ReportService {
    void createMarkdownReport(Report targetReport, CommandLine commandLineInput);
}
