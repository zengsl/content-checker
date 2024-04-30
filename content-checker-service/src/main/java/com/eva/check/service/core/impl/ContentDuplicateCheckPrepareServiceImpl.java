package com.eva.check.service.core.impl;

import cn.hutool.core.util.StrUtil;
import com.eva.check.common.util.ParagraphUtil;
import com.eva.check.common.util.SimHashUtil;
import com.eva.check.common.util.TextUtil;
import com.eva.check.pojo.CheckParagraph;
import com.eva.check.pojo.CheckSentence;
import com.eva.check.pojo.CheckTask;
import com.eva.check.service.core.DuplicateCheckPrepareService;
import com.eva.check.service.flow.IContentCheckTaskBaseFlow;
import com.eva.check.service.flow.enums.ContentCheckState;
import com.eva.check.service.support.CheckParagraphService;
import com.eva.check.service.support.CheckSentenceService;
import com.eva.check.service.support.CheckTaskService;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

/**
 * 内容数据检测准备服务
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ContentDuplicateCheckPrepareServiceImpl implements DuplicateCheckPrepareService {


    private final CheckParagraphService checkParagraphService;
    private final CheckSentenceService checkSentenceService;
    private final CheckTaskService checkTaskService;
    private IContentCheckTaskBaseFlow contentCheckTaskFlow;

    @Autowired
    @Lazy
    public void setContentCheckTaskFlow(IContentCheckTaskBaseFlow contentCheckTaskFlow) {
        this.contentCheckTaskFlow = contentCheckTaskFlow;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void execute(CheckTask checkTask) {
        if (checkTask == null) {
            log.error("检测任务内容checkTask为空");
            return;
        }
        if (StrUtil.isBlank(checkTask.getContent())) {
            log.error("检测任务内容为空，任务：{}", checkTask);
            this.contentCheckTaskFlow.processCancel(checkTask);
            return;
        }
        // 生成检测文本的指纹信息
        /*SimHashUtil.SimHash simHash = ParagraphUtil.buildFingerprint2(checkTask.getContent());*/
        // 生成句子
        List<String> sentenceList = TextUtil.smartSplitSentence(checkTask.getContent());
        // 生成检测段落
        CheckParagraph checkParagraph = CheckParagraph.builder()
                .paperNo(checkTask.getPaperNo())
                .paperId(checkTask.getPaperId())
                .paragraphNum(1)
                .sentenceCount(sentenceList.size())
                .wordCount(TextUtil.countWord(checkTask.getContent()))
                .checkId(checkTask.getCheckId())
                .taskId(checkTask.getTaskId())
                .content(checkTask.getContent())
                /*.hash(simHash.getSimHash())
                .hash1(simHash.getSimHash1())
                .hash2(simHash.getSimHash2())
                .hash3(simHash.getSimHash3())
                .hash4(simHash.getSimHash4())*/
                .build();
        this.checkParagraphService.save(checkParagraph);

        List<CheckSentence> checkSentenceList = Lists.newArrayListWithCapacity(sentenceList.size());
        for (int i = 0; i < sentenceList.size(); i++) {
            String sentence = sentenceList.get(i);
            // 生成检测句子
            CheckSentence checkSentence = CheckSentence.builder()
                    .sentenceNum(i + 1)
                    .paragraphId(checkParagraph.getParagraphId())
                    .wordCount(TextUtil.countWord(sentence))
                    .originContent(sentence)
                    .content(TextUtil.pretreatment(sentence))
                    .build();
            checkSentenceList.add(checkSentence);
        }
        // 批量插入句子
        this.checkSentenceService.saveBatch(checkSentenceList);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                TransactionSynchronization.super.afterCompletion(status);
            }

            @Override
            public void afterCommit() {
                // 触发预检测任务事件
                contentCheckTaskFlow.processStateNext(checkTask, ContentCheckState.PREPARE);
            }
        });

    }
}
