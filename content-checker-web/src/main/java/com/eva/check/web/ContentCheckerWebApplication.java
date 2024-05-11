package com.eva.check.web;

import cn.hutool.core.date.StopWatch;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 内容检测Web应用
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@SpringBootApplication
public class ContentCheckerWebApplication {

    public static void main(String[] args) throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("【ContentCheckerWebApplication】启动");
        ConfigurableApplicationContext application = SpringApplication.run(ContentCheckerWebApplication.class);
//        application.setApplicationStartup(new BufferingApplicationStartup(2048));
//        application.run(args);
        Environment env = application.getEnvironment();
        String ip = InetAddress.getLocalHost().getHostAddress();
        String port = env.getProperty("server.port");
        if (port == null || port.isEmpty()) {
            port = "8080";
        }
        String property = env.getProperty("server.servlet.context-path");
        String path = property == null ? "" :  property;
        System.out.println(
                "\n\t" +
                        "----------------------------------------------------------\n\t" +
                        "Application Content-Checker is running! Access URLs:\n\t" +
                        "Local: \t\thttp://localhost:" + port + path + "/\n\t" +
                        "External: \thttp://" + ip + ":" + port + path + "/\n\t" +
                        "------------------------------------------------------------");

        stopWatch.stop();
        System.out.println("(♥◠‿◠)ﾉﾞ  【ContentCheckerWebApplication】启动成功   ლ(´ڡ`ლ)ﾞ ，耗时：" + stopWatch.getTotalTimeSeconds() + "s");
    }
}
