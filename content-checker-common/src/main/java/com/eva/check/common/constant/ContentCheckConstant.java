package com.eva.check.common.constant;

/**
 * 内容查重常量
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
public interface ContentCheckConstant {

    double PRE_CHECK_THRESHOLD = 0.5D;

    /**
     * 数据来源默认值：互联网
     */
    String DATA_SOURCE_DEFAULT = "1";

    Double SIMILARITY_ZERO = 0D;
    Double SIMILARITY_INIT = -1D;

    Integer SENTENCE_BATCH_SIZE = 10000;
    Integer IMPORT_BATCH_SIZE = 10000;
}
