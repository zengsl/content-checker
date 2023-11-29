package com.eva.check.common.enums;

import com.eva.check.common.enums.common.IBaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文本颜色
 *
 * @author zzz
 * @date 2023/11/27 21:31
 */
@Getter
@AllArgsConstructor
public enum TextColor implements IBaseEnum<String> {

    /**
     * 黑色
     */
    BLACK("黑色", "1") {
        @Override
        public String getCssClass() {
            return "black";
        }
    },
    GREEN("绿色", "2") {
        @Override
        public  String getCssClass() {
            return "green";
        }
    },

    RED("红色", "3") {
        @Override
        public String getCssClass() {
            return "red";
        }
    },
    ORANGE("橙色", "4") {
        @Override
        public String getCssClass() {
            return "orange";
        }
    };

    public abstract String getCssClass();

    public String renderSentenceHtml(String text, String id, String similarity) {
        return "<a href='javascript:void(0);' data-color-class=" + getCssClass() + " data-similarity=" + similarity + " data-id=" + id + " id='words_" + id + "'  class='similar-word " + this.getCssClass() + "' onclick='showDetailContainer(this)'>" + text + "</a>";
    }

    private final String name;

    private final String value;
}
