package com.eva.check.service.core.impl;

import cn.hutool.core.util.StrUtil;
import com.eva.check.common.util.ParagraphUtil;
import com.eva.check.common.util.SimHashUtil;
import com.eva.check.common.util.TextUtil;
import com.eva.check.pojo.CheckParagraph;
import com.eva.check.pojo.CheckSentence;
import com.eva.check.pojo.CheckTask;
import com.eva.check.service.core.DuplicateCheckPrepareService;
import com.eva.check.service.event.CheckTaskCancelEvent;
import com.eva.check.service.event.PreCheckEvent;
import com.eva.check.service.mq.producer.SendMqService;
import com.eva.check.service.support.CheckParagraphService;
import com.eva.check.service.support.CheckSentenceService;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 内容数据检测准备服务
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class ContentDuplicateCheckPrepareServiceImpl implements DuplicateCheckPrepareService {


    private final CheckParagraphService checkParagraphService;
    private final CheckSentenceService checkSentenceService;
    private SendMqService sendMqService;

    @Autowired
    @Lazy
    public void setSendMqService(SendMqService sendMqService) {
        this.sendMqService = sendMqService;
    }
    @Override
    public void execute(CheckTask checkTask) {
        if (checkTask == null) {
            log.error("检测任务内容checkTask为空");
            return;
        }
        if (StrUtil.isBlank(checkTask.getContent())) {
            log.error("检测任务内容为空，任务ID：{}", checkTask.getTaskId());
            CheckTaskCancelEvent checkTaskCancelEvent = CheckTaskCancelEvent.builder().checkTask(checkTask).build();
            this.sendMqService.cancelTask(checkTaskCancelEvent);
            return;
        }
        // 生成检测文本的指纹信息
        SimHashUtil.SimHash simHash = ParagraphUtil.buildFingerprint2(checkTask.getContent());
//        SimHashUtil.SimHash simHash = ParagraphUtil.buildFingerprint(checkTask.getContent());
        // 生成检测段落
        CheckParagraph checkParagraph = CheckParagraph.builder()
                .paperNo(checkTask.getPaperNo())
                .paragraphNum(1)
                .checkId(checkTask.getCheckId())
                .taskId(checkTask.getTaskId())
                .content(checkTask.getContent())
                .hash(simHash.getSimHash())
                .hash1(simHash.getSimHash1())
                .hash2(simHash.getSimHash2())
                .hash3(simHash.getSimHash3())
                .hash4(simHash.getSimHash4())
                .build();
        this.checkParagraphService.save(checkParagraph);
        // 生成句子
        List<String> sentenceList = TextUtil.splitSentence(checkTask.getContent());
        List<CheckSentence> checkSentenceList = Lists.newArrayListWithCapacity(sentenceList.size());
        for (int i = 0; i < sentenceList.size(); i++) {
            String sentence = sentenceList.get(i);
            // 生成检测句子
            CheckSentence checkSentence = CheckSentence.builder()
                    .sentenceNum(i + 1)
                    .paragraphId(checkParagraph.getParagraphId())
                    .originContent(sentence)
                    .content(TextUtil.pretreatment(sentence))
                    .build();
            checkSentenceList.add(checkSentence);
        }
        // 批量插入句子
        this.checkSentenceService.saveBatch(checkSentenceList);
        // 触发预检测任务事件
        PreCheckEvent preCheckEvent = PreCheckEvent.builder()
               .checkTask(checkTask)
               .build();
        this.sendMqService.doContentPreCheck(preCheckEvent);
    }
}
