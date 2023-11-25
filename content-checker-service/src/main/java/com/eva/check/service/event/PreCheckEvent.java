package com.eva.check.service.event;

import com.eva.check.pojo.CheckTask;
import lombok.Builder;
import lombok.Data;

/**
 *
 *
 *
 * @param null
 * @return
 * @throws
 * @author zzz
 * @date 2023/11/25 16:12
 */
@Data
@Builder
public class PreCheckEvent {

    private CheckTask checkTask;

}
