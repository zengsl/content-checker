package com.eva.check.test.draft;

public class ContentDuplicateSystem {
    public static void main(String[] args) {
        String str1 = "123123";
        String str2 = "667777";
        System.out.println("Similarity: " + calculateSimilarity(str1, str2));
    }

    public static double calculateSimilarity(String str1, String str2) {
        int[][] dp = new int[str1.length() + 1][str2.length() + 1];
        for (int i = 0; i <= str1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= str2.length(); j++) {
            dp[0][j] = j;
        }
        for (int i = 1; i <= str1.length(); i++) {
            for (int j = 1; j <= str2.length(); j++) {
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1])) + 1;
                }
            }
        }
        return 1 - dp[str1.length()][str2.length()] / Math.max(str1.length(), str2.length());
    }



}