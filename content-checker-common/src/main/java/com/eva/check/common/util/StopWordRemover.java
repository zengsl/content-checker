package com.eva.check.common.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * https://github.com/goto456/stopwords
 *
 * @author zzz
 */
public class StopWordRemover {
    private static class TrieNode {
        Map<Character, TrieNode> children;
        boolean isEndOfWord;

        //      String word;

        TrieNode() {
            children = new HashMap<>();
            isEndOfWord = false;
        }

        TrieNode(Collection<String> words) {
            this.children = new HashMap<>();
//            CollUtil.sort(words, String::compareTo);
            for (String str : words) {
                // root node
                Map<Character, TrieNode> child = this.children;
                // 遍历每个字符
                TrieNode currentNode = null;
                for (Character c : str.toCharArray()) {
                    currentNode = child.computeIfAbsent(c, k -> new TrieNode());
                    child = currentNode.children;
                }
                if (currentNode != null) {
                    currentNode.isEndOfWord = true;
                }
                // 结束
//                child.put(CHARACTER_END, null);
            }
        }

    }

    private static TrieNode root;

    static {
        try (Stream<String> lines = Files.lines(Paths.get(StopWordRemover.class.getClassLoader().getResource("cn_stopwords.txt").toURI()))) {
            Collection<String> words = lines.toList();
            root = new TrieNode(words);
            /*lines.forEach(line -> {
                String word = line.trim();
                TrieNode currentNode = root;
                for (char c : word.toCharArray()) {
                    currentNode = currentNode.children.computeIfAbsent(c, k -> new TrieNode());
                }
                currentNode.isEndOfWord = true;
            });*/
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String text = "这是一个停用词处理示例。我们在这里演示如何使用停用词表删除停用词。";
        String[] words = text.split("\\s+");

        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (!isStopWord(word)) {
                result.append(word).append(" ");
            }
        }

        System.out.println("处理后的文本: " + result);
    }

    public static boolean isStopWord(String word) {
        TrieNode currentNode = root;
        for (char c : word.toCharArray()) {
            currentNode = currentNode.children.get(c);
            if (currentNode == null) {
                return false;
            }
        }
        return currentNode.isEndOfWord;
    }

    public static boolean isNotStopWord(String word) {
        return !isStopWord(word);
    }

}
