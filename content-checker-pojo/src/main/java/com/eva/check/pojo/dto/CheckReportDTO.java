package com.eva.check.pojo.dto;

import lombok.Data;

@Data
public class CheckReportDTO {
    private String status;
    private String reportName;
    private String fileCode;
    private String filePath;
}
