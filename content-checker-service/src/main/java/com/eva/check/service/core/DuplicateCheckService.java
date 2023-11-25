package com.eva.check.service.core;

import com.eva.check.pojo.CheckTask;

public interface DuplicateCheckService {

    void findSimilarParagraph(CheckTask checkTask);

    void doPragraphCheck(CheckTask checkTask);

    void collectResult(CheckTask checkTask);
}
