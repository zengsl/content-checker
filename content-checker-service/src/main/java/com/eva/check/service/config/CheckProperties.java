package com.eva.check.service.config;

import com.eva.check.common.constant.MessageQueueConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;

/**
 *
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@ConfigurationProperties(prefix = "content-check")
@Data
public class CheckProperties {
    private String mqType = MessageQueueConstants.EVENT_BUS;
    private String contentSimilarityThreshold = "50%";
    private Double sentenceSimilarityThreshold = 0.5D;
    private String reportPath = System.getProperty("user.dir") + File.separator + "report";
}
