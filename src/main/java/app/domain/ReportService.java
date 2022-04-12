package app.domain;

import app.service.CommandLine;

public interface ReportService {
    void createReport(Report targetReport, InputParameters inputParameters);
}
