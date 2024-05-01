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


MoreLikeThisQuery

> 提前执行PaperDataCreatorTest#testInitData()

```http request
POST paper-paragraph/_search
{
  "query": {
    "bool": {
      "must":{"more_like_this": {
      "fields": [
        "content"
      ],
      "like": [
        """ 我们的研究项目主要探索高科技材料在太阳能电池中的应用。首先，通过对肿瘤癌细胞进行研究、分析，发现了一种具有潜在抗肿瘤活性化合物质的存在，这种化合物质十分特殊，且具有显著特点。通过进行系统的实验研究和临床前评估，我们验证了该化合物的抗肿瘤效果良好，并探讨了其作用机制、运行原理。最终，实验结果揭示了：该化合物能够选择性地抑制肿瘤细胞的增殖并诱导其凋亡，但对正常细胞的影响不大，对普通系统的影响也非常有限。所以，该发现为抗肿瘤药物的研发提供了新的候选药物方向，增加了攻克肿瘤问题的可能性。今后，我们正进行进一步的研究探索，评估该化合物在人体内的安全性和有效性，加速研究进度，早日产出研究成果，为推动高质量发展注入新动能。 """
      ],
      "analyzer": "ik_smart",
      "min_doc_freq": 5,
      "max_doc_freq": 1000,

      "min_term_freq": 2,
      "minimum_should_match":"50%",
      "min_word_length":"2",
      "boost":"1"
    }},
      "must_not": [
        // 这里可以排除掉同一文档
        {"match":{"paperNo":"test:9"}}
      ]
    }
    
  }
}

```

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


### 调整

- EsPaperCoreServiceImpl#findSimilarPaperParagraph中`moreLikeThisQuery`相关参数、分词器、停用词需根据具体数据进行调整。
- TextUtil中分词需针对数据特性进行调整，停用词补充。


## TODO

- [ ] 分词优化
- [ ] 优化性能。`doPragraphCheck`执行段落比对时，大量数据嵌套循环会耗费大量性能
- [ ] ES查找相似文章准确度存在问题，可能是ES分词问题。另外，能否只传递ES的分词结果，而不是传递整个段落，提高性能
- [ ] 使用guava时，如果不配置rocketMq的配置，会报错