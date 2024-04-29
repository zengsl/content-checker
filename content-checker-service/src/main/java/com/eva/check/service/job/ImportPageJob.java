package com.eva.check.service.job;

import cn.hutool.core.date.StopWatch;
import com.eva.check.service.support.ImportPaperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 导入论文任务
 *
 * @author zengsl
 * @date 2024/4/25 11:40
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ImportPageJob {

    private final ImportPaperService importPaperService;

//    @Scheduled(cron = "0 0/1 * * * ? ")
    public void importPage() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("【ImportPageJob#importPage】任务");
        // 执行导入文本操作
        this.importPaperService.batchImportPaper(1000);

        stopWatch.stop();
        log.info("【ImportPageJob#importPage】任务执行结束   ლ(´ڡ`ლ)ﾞ ，耗时：{}s", stopWatch.getTotalTimeSeconds());
    }
}
