package com.eva.check.common.util;

import cn.hutool.core.date.DateUtil;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.util.Date;

@UtilityClass
public class FileUtil {

    public String generatePathByDate() {
        Date date = DateUtil.date();
        int year = DateUtil.year(date);
        int month = DateUtil.month(date);
        int day = DateUtil.dayOfMonth(date);
        return year + File.separator + month + File.separator + day;
    }

    public String generatePathByDate(String basePath) {
        return basePath + File.separator + generatePathByDate();
    }

    public String generatePathByDate(String basePath, String fileName) {
        return generatePathByDate(basePath) + File.separator + fileName;
    }
}
