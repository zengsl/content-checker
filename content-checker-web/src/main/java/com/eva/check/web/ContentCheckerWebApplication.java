package com.eva.check.web;

import cn.hutool.core.date.StopWatch;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;

/**
 * 内容检测Web应用
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@SpringBootApplication
public class ContentCheckerWebApplication {

    public static void main(String[] args) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("【ContentCheckerWebApplication】启动");
        SpringApplication application = new SpringApplication(ContentCheckerWebApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
        stopWatch.stop();
        System.out.println("(♥◠‿◠)ﾉﾞ  【ContentCheckerWebApplication】启动成功   ლ(´ڡ`ლ)ﾞ ，耗时：" + stopWatch.getTotalTimeSeconds() + "s");
    }
}
