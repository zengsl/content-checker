package com.eva.check.test.draft;

import java.io.IOException;

public class LuceneExample {

    public static void main(String[] args) throws IOException {
      /*  // 创建一个内存索引
        Directory directory = new ByteBuffersDirectory();

        // 配置IndexWriter
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        IndexWriter indexWriter = new IndexWriter(directory, config);

        // 创建一个文档
        Document doc1 = new Document();
        doc1.add(new TextField("content", "This is a test.", Field.Store.YES));

        // 添加文档到索引
        indexWriter.addDocument(doc1);

        // 创建另一个文档
        Document doc2 = new Document();
        doc2.add(new TextField("content", "Another test.", Field.Store.YES));

        // 添加文档到索引
        indexWriter.addDocument(doc2);

        // 提交更改并关闭IndexWriter
        indexWriter.commit();
        indexWriter.close();

        // 创建一个IndexReader
        DirectoryReader indexReader = DirectoryReader.open(indexWriter, false);

        // 创建一个IndexSearcher
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        // 创建一个查询
        Term term = new Term("content", "test");
        Query query = new TermQuery(term);

        // 执行查询
        TopDocs topDocs = indexSearcher.search(query, 10); // 返回前10个结果

        // 输出查询结果
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document document = indexSearcher.doc(scoreDoc.doc);
            System.out.println(document.get("content"));
        }*/
    }
}
