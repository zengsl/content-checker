package com.eva.check.service.core;

import com.eva.check.common.enums.TextColor;

public interface SimilarTextRule {

     TextColor computeTextColor(Double similarity);

     boolean isSimilar(Double similarity);

     default boolean isNotSimilar(Double similarity){
          return !isSimilar(similarity);
     }
}
