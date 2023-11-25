package com.eva.check.test.draft;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.text.Simhash;
import cn.hutool.core.util.StrUtil;
import com.eva.check.common.util.StopWordRemover;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import com.vdurmont.emoji.EmojiParser;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.util.List;

import static com.eva.check.common.util.SimilarUtil.calSimHahSimilar;

/**
 * @author zzz
 */
public class ContentCheckerTest {
    private static final int BIT_NUM = 64;


    public static void main(String[] args) {
        // 文本分段、分句、分词

        // 短文本采用不同计算策略？


        // 先不考虑中英文混合的情况。 要不以中文为主，要不以英文为主，单独做分词。

        // 相似度：51%
//        String sourceContent = "Tody is a good day";

        String sourceContent = "客户关系管理（Customer Relationship Managemen-CRM）是指企业运用营销、关怀等手段时刻保持商业银行与实际客户和潜在客户交互的动作";
        String targetContent = "客户关系管理（Customer Relationship Management——CRM）是企业通过与客户的交流、沟通和理解，并影响客户行为，最终实现提高客户";

//       分句 BreakIterator


        // 数据清洗
        sourceContent = clearSpecialCharacters(sourceContent);
        targetContent = clearSpecialCharacters(targetContent);

        // 文本规范化
        //  1、中文繁体转简体
        sourceContent = HanLP.convertToSimplifiedChinese(sourceContent);
        targetContent = HanLP.convertToSimplifiedChinese(targetContent);
        /*sourceContent = ZhConverterUtil.toSimple(sourceContent);
        targetContent = ZhConverterUtil.toSimple(targetContent);*/
        //  2、全角字符转半角
        sourceContent = Convert.toDBC(sourceContent);
        targetContent = Convert.toDBC(targetContent);

        // 归一化处理：编码
        sourceContent = StrUtil.normalize(sourceContent);
        targetContent = StrUtil.normalize(targetContent);

        // 字符去重
//        sourceContent = StrUtil.removeDuplicate(sourceContent);

        // 变形词标准化


/*1、邮政编码

2、某一行单独的纯英文字符串

3、无法识别的特殊符号

4、繁体到简体的转换

5、全角到半角的转换

6、去除停用词

7、字符串之间无用的空格...*/


        /*1、删除特殊符号如■、©等

2、去除字符见多余的空格、删除连续出现的标点符号，删除不出现中文字符的数据行

3、删除长度较短的无用文本

4、按照。对每一行文本进行切分，并逐行存储在txt文件中

5、批量化处理*/

        // 分词 这里使用HanLP-v1，也可以考虑使用结巴分词
        // HanLP-v1抽取关键词的过程中会通过默认的停用词库去除停用词
        List<String> sourceKeywordList = HanLP.extractKeyword(sourceContent, sourceContent.length());
        List<String> targetKeywordList = HanLP.extractKeyword(targetContent, targetContent.length());
        System.out.println("sourceKeywordList:" + sourceKeywordList);
        System.out.println("targetKeywordList:" + targetKeywordList);

        HanLP.Config.ShowTermNature = false;
        List<Term> sourceSegment = HanLP.segment(sourceContent);
        List<Term> targetSegment = HanLP.segment(targetContent);
        System.out.println("sourceSegment:" + sourceSegment);
        System.out.println("targetSegment:" + targetSegment);


        // 降噪 去停用词 词库：https://github.com/goto456/stopwords
        List<String> sourceKeywordList2 = sourceKeywordList.stream().filter(StopWordRemover::isNotStopWord).toList();
        List<String> targetKeywordList2 = targetKeywordList.stream().filter(StopWordRemover::isNotStopWord).toList();

        List<String> sourceKeywordList3 = sourceSegment.stream().filter(s->{
           return StopWordRemover.isNotStopWord(s.word);
        }).map(s->s.word).toList();
        List<String> targetKeywordList3 = targetSegment.stream().filter(s->{
            return StopWordRemover.isNotStopWord(s.word);
        }).map(s->s.word).toList();

        // 词干提取
        System.out.println("===================================");
        System.out.println("sourceKeywordList2:" + sourceKeywordList2);
        System.out.println("targetKeywordList2:" + targetKeywordList2);

        System.out.println("sourceKeywordList3:" + sourceKeywordList3);
        System.out.println("targetKeywordList3:" + targetKeywordList3);

        // 计算文本向量 词袋模型、TF-IDF、Word2Vec（HanLP中有需要训练，包含语义，https://github.com/hankcs/HanLP/wiki/word2vec）
        // 使用simhash
        Simhash simhash1 = new Simhash();
        long simhash = simhash1.hash(sourceKeywordList2);
        long simhash2 = simhash1.hash(targetKeywordList2);

        System.out.println("simhash:" + simhash);
        System.out.println("simhash2:" + simhash2);
        System.out.println("calSimilar:" + calSimHahSimilar(simhash, simhash2));



        Simhash simhashObj = new Simhash();
        long simhash3 = simhashObj.hash(sourceKeywordList3);
        long simhash4 = simhashObj.hash(targetKeywordList3);

        System.out.println("simhash3:" + simhash3);
        System.out.println("simhash4:" + simhash4);
        System.out.println("calSimilar:" + calSimHahSimilar(simhash3, simhash4));
    }

    private static String clearSpecialCharacters(String text) {

        // 过滤HTML标签
        text = Jsoup.clean(text, Safelist.none());

        // 过滤特殊字符
        String[] strings = {" ", "\n", "\r", "\t", "\\r", "\\n", "\\t", "&nbsp;", "&amp;", "&lt;", "&gt;", "&quot;", "&qpos;"};
        for (String string : strings) {
            text = text.replaceAll(string, "");
        }
        // 将内容转换为小写
        text = StringUtils.lowerCase(text);

        // 去除表情符号
        text = EmojiParser.removeAllEmojis(text);
        return text;
    }
}
