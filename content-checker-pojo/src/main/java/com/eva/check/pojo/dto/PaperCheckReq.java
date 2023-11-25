package com.eva.check.pojo.dto;

import com.eva.check.common.enums.AccountType;
import com.eva.check.pojo.common.PaperBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Paper验证请求
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PaperCheckReq extends PaperBaseEntity {

    /**
     * 校验编号
     * 每次发起校验请求之后返回
     */
    private String checkNo;

    /**
     * 请求来源: 1 web端调用 2 系统API
     */
    private String reqSource;

    private AccountType accountType;

    private String accountId;
}
