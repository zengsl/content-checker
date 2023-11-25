package com.eva.check.test.draft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextMatcher {
    public List<String> findSimilarTexts(List<String> texts, double threshold) {
        List<String> similarTexts = new ArrayList<>();
        for (int i = 0; i < texts.size(); i++) {
            String text = texts.get(i);
            List<String> currentText = new ArrayList<>(Arrays.asList(text.split(" ")));
            for (int j = i + 1; j < texts.size(); j++) {
                String similarText = texts.get(j);
                List<String> similarTextList = new ArrayList<>(Arrays.asList(similarText.split(" ")));
                double similarity = calculateSimilarity(currentText, similarTextList);
                if (similarity >= threshold) {
                    similarTexts.add(similarText);
                }
            }
        }
        return similarTexts;
    }

    private double calculateSimilarity(List<String> text1, List<String> text2) {
        int intersection = 0;
        int total1 = 0;
        int total2 = 0;

        for (String word : text1) {
            if (text2.contains(word)) {
                intersection++;
            }
            total1++;
        }

        for (String word : text2) {
            total2++;
        }

        return (double) intersection / (total1 + total2 - intersection);
    }

    public void markSimilarTexts(List<String> texts, double threshold) {
        List<String> markedTexts = findSimilarTexts(texts, threshold);
        for (int i = 0; i < texts.size(); i++) {
            StringBuilder sb = new StringBuilder();
            for (String word : texts.get(i).split(" ")) {
                sb.append(word).append(" ");
            }
            String text = sb.toString().trim();
            if (markedTexts.contains(text)) {
                System.out.println("<span style='color: red;'>[Marked Text]: " + text + "</span>");
            } else {
                System.out.println("[Original Text]: " + text);
            }
        }
    }

    public static void main(String[] args) {
        List<String> texts = Arrays.asList(
                "This is the first text",
                "This is the second text",
                "This is a similar text",
                "Another similar text",
                " unrelated text"
        );

        TextMatcher matcher = new TextMatcher();
        matcher.markSimilarTexts(texts, 0.5);
    }
}