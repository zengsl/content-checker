package com.eva.check.pojo.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author zengsl
 */
@Data
public class CheckReportDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 6431207824747288710L;

    private String status;
    private String reportName;
    private String fileCode;
    private String filePath;
}
