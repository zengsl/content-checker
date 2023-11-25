package com.eva.check.web.common;

/**
 * 通用常量信息
 *
 * @author ruoyi
 */
public interface Constants {
    /**
     * UTF-8 字符集
     */
    String UTF8 = "UTF-8";

    /**
     * GBK 字符集
     */
    String GBK = "GBK";

    /**
     * RMI 远程方法调用
     */
    String LOOKUP_RMI = "rmi:";

    /**
     * LDAP 远程方法调用
     */
    String LOOKUP_LDAP = "ldap:";

    /**
     * LDAPS 远程方法调用
     */
    String LOOKUP_LDAPS = "ldaps:";

    /**
     * http请求
     */
    String HTTP = "http://";

    /**
     * http
     */
    String HTTP_STR = "http";

    /**
     * https请求
     */
    String HTTPS = "https://";

    /**
     * 成功标记
     */
    Integer SUCCESS = 200;

    /**
     * 失败标记
     */
    Integer FAIL = 500;

    /**
     * 成功msg
     */
    String SUCCESS_MSG = "SUCCESS";


    /**
     * 失败标记
     */
    String FAIL_MSG = "FAILD";

    /**
     * 登录成功状态
     */
    String LOGIN_SUCCESS_STATUS = "0";

    /**
     * 登录失败状态
     */
    String LOGIN_FAIL_STATUS = "1";

    /**
     * 登录成功
     */
    String LOGIN_SUCCESS = "Success";

    /**
     * 注销
     */
    String LOGOUT = "Logout";

    /**
     * 注册
     */
    String REGISTER = "Register";

    /**
     * 登录失败
     */
    String LOGIN_FAIL = "Error";

    /**
     * 当前记录起始索引
     */
    String PAGE_NUM = "pageNum";

    /**
     * 每页显示记录数
     */
    String PAGE_SIZE = "pageSize";

    /**
     * 排序列
     */
    String ORDER_BY_COLUMN = "orderByColumn";

    /**
     * 排序的方向 "desc" 或者 "asc".
     */
    String IS_ASC = "isAsc";


    /**
     * 资源映射路径 前缀
     */
    String RESOURCE_PREFIX = "/profile";

    /**
     * 定时任务白名单配置（仅允许访问的包名，如其他需要可以自行添加）
     */
    String[] JOB_WHITELIST_STR = {"com.ruoyi"};

    /**
     * 定时任务违规的字符
     */
    String[] JOB_ERROR_STR = {"java.net.URL", "javax.naming.InitialContext", "org.yaml.snakeyaml",
        "org.springframework", "org.apache", "com.ruoyi.common.core.utils.file"};

    /**
     * 批量插入数量
     */
    int BATCH_INSERT_COUNT = 100;

    /**
     * 字符串1
     */
    String STR_ONE = "1";

    /**
     * 点
     */
    String DOT = ".";

    /**
     * html
     */
    String HTML = "html";
    String ASTERISK = "*";
    String TOTAL_ONLY = "totalOnly";
    /**
     * 防重提交 redis key
     */
    String REPEAT_SUBMIT_KEY = "repeat_submit:";

    /**
     * 逗号
     */
    String COMMA = ",";
    String DOLLAR = "$";
}
