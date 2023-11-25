package com.eva.check.service.core.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.id.NanoId;
import cn.hutool.core.util.StrUtil;
import com.eva.check.common.enums.CheckReqStatus;
import com.eva.check.common.enums.CheckTaskStatus;
import com.eva.check.common.enums.DataType;
import com.eva.check.common.enums.PaperErrorCode;
import com.eva.check.common.exception.SystemException;
import com.eva.check.pojo.CheckRequest;
import com.eva.check.pojo.CheckTask;
import com.eva.check.pojo.converter.PaperCheckConverter;
import com.eva.check.pojo.converter.PaperCollectConverter;
import com.eva.check.pojo.dto.PaperAddReq;
import com.eva.check.pojo.dto.PaperCheckReq;
import com.eva.check.service.core.PaperCheckService;
import com.eva.check.service.core.PaperCollectService;
import com.eva.check.service.event.CheckTaskStartEvent;
import com.eva.check.service.mq.producer.SendMqService;
import com.eva.check.service.support.CheckRequestService;
import com.eva.check.service.support.CheckTaskService;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 论文检测服务
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaperCheckServiceImpl implements PaperCheckService {

    private final PaperCollectService paperCollectService;
    private final CheckRequestService checkRequestService;
    private final CheckTaskService checkTaskService;

    private final SendMqService sendMqService;

    @Override
    public String createPaperCheck(PaperCheckReq paperCheckReq) throws SystemException {
        checkParams(paperCheckReq);
        // 拆解验证任务 先只处理正文检测
        CheckRequest checkRequest = PaperCheckConverter.INSTANCE.paperCheckReq2CheckReq(paperCheckReq);
        String checkNo = StringUtils.hasText(paperCheckReq.getCheckNo()) ? paperCheckReq.getCheckNo() : NanoId.randomNanoId();
        // 设置checkNo
        checkRequest.setCheckNo(checkNo);
        checkRequest.setStatus(CheckReqStatus.INIT.getValue());
        // 保存check_request
        boolean save = this.checkRequestService.save(checkRequest);

        // 按需生成task
        List<CheckTask> checkTaskList = Lists.newArrayListWithCapacity(16);
        if (StringUtils.hasText(paperCheckReq.getContent())) {
            CheckTask contentCheckTask = new CheckTask();
            contentCheckTask.setCheckId(checkRequest.getCheckId())
                    .setCheckNo(checkRequest.getCheckNo())
                    .setPaperNo(checkRequest.getPaperNo())
                    .setCheckType(DataType.FULL_TEXT.getValue())
                    .setContent(paperCheckReq.getContent())
                    .setStatus(CheckTaskStatus.INIT.getValue());
            checkTaskList.add(contentCheckTask);
        }

        if (StringUtils.hasText(paperCheckReq.getContent())) {
            CheckTask titleCheckTask = new CheckTask();
            titleCheckTask.setCheckId(checkRequest.getCheckId())
                    .setCheckNo(checkRequest.getCheckNo())
                    .setPaperNo(checkRequest.getPaperNo())
                    .setCheckType(DataType.TITLE.getValue())
                    .setContent(paperCheckReq.getTitle())
                    .setStatus(CheckTaskStatus.INIT.getValue());
            checkTaskList.add(titleCheckTask);
        }

        checkTaskService.saveBatch(checkTaskList);
        checkRequest.setTaskNum(checkTaskList.size());
        checkRequest.setStatus(CheckReqStatus.DOING.getValue());
        this.checkRequestService.updateById(checkRequest);
        // 将任务推送MQ 进行异步处理
        CheckTaskStartEvent checkTaskStartEvent = CheckTaskStartEvent.builder()
                .checkTasks(checkTaskList)
                .checkId(checkRequest.getCheckId())
                .taskNum(checkRequest.getTaskNum())
                .build();
        sendMqService.startTask(checkTaskStartEvent);
        return checkRequest.getCheckNo();
    }

    private static void checkParams(PaperCheckReq paperCheckReq) {
        boolean b = paperCheckReq == null || CollectionUtil.isEmpty(paperCheckReq.getPaperExtList()) && StrUtil.isBlank(paperCheckReq.getContent()) && StrUtil.isBlank(paperCheckReq.getTitle());
        if (b) {
            throw new SystemException(PaperErrorCode.PARAM_INVALID);
        }
    }

    @Override
    public String createPaperCheckAndCollect(PaperCheckReq paperCheckReq) throws SystemException {
        PaperAddReq paperAddReq = PaperCollectConverter.INSTANCE.check2AddReq(paperCheckReq);
        // 收录至文档库
        // TODO 可以异步
        paperCollectService.addNewPaper(paperAddReq);

        return this.createPaperCheck(paperCheckReq);
    }

    @Override
    public CheckRequest getPaperCheckResult(String checkNo) throws SystemException {
        return this.checkRequestService.getByCheckNo(checkNo);
    }

    @Override
    public String getPaperCheckReport(String checkNo) throws SystemException {
        return null;
    }
}
