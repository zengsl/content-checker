# Content Checker

A study project of simple content checker for paper.

## Usage

You can use this project to check the similarity of the content of the paper.

This project use `More like this query` of Elasticsearch to find the similar paragraphs.And, it use `HanLp`
and `Cosine Similarity` to calculate the similarity.

### MySQL

> I tried to use simhash to find the paragraph which may be similar to the paper content that you input, but I haven't
> found a good way to implement it.Four-segment stored simhash does not detect potentially similar paragraphs quickly'.
> So, some tables have redundant simhash fields

#### Init table

use file `schema-mysql.sql` to init table

### Elasticsearch

#### Install

You can [install Elasticsearch by docker](https://www.elastic.co/guide/en/elasticsearch/reference/current/docker.html)

Install [Ik Analyzer](https://github.com/medcl/elasticsearch-analysis-ik#ik-analysis-for-elasticsearch) to segment
Chinese words

#### Init index

```http request
PUT paper-paragraph
{
  "settings": {
    "analysis": {
      "analyzer": {
        "my_analyzer": {
          "type": "ik_smart"
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "paragraphNum":{"type":"long"},
      "paperId":{"type":"long"},
      "paperNo":{"type":"keyword"},
      "createTime":{"type":"date","format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis"},
      "updateTime":{"type":"date","format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis"},
      "content": { 
        "type": "text",
        "analyzer": "my_analyzer"
      }
    }
  }
}
```

查询方法

- Http Request

```http request
GET /paper-paragraph/_search
```

- Idea Elasticsearch Plugin

### Run the web server

run the main method in `com.eva.check.web.ContentCheckerWebApplication`

## Development

You can init paper library with  `PaperDataCreatorTest`

You can check paper library with  `PaperCheckServiceImplTest` or web page

You can add paper library with  `PaperCollectServiceImplTest` or web page

You can batch add paper library with  `ImportPaperServiceTest` or web page

- PaperDataCreatorTest#reset() 清空所有的文章库数据、检测数据（paper_xxx、check_xxx）

- PaperDataCreatorTest#testCreateImportData() 配合 ImportPaperServiceTest
  ImportPaperServiceTest#initTestData()可批量造数据，批量造数据默认创建8w条，可以调整下数量。

## TODO

- [ ] 分词优化
- [ ] 优化性能。`doPragraphCheck`执行段落比对时，大量数据嵌套循环会耗费大量性能
- [ ] ES查找相似文章准确度存在问题，可能是ES分词问题。另外，能否只传递ES的分词结果，而不是传递整个段落，提高性能
- [ ] 使用guava时，如果不配置rocketMq的配置，会报错