# Content Checker

A study project of simple content checker for paper.

## Usage

You can use this project to check the similarity of the content of the paper.

This project use `More like this query` of Elasticsearch to find the similar paragraphs.And, it use `HanLp` and `Cosine Similarity` to calculate the similarity.

### MySQL

> I tried to use simhash to find the paragraph which may be similar to the paper content that you input, but I haven't found a good way to implement it.Four-segment stored simhash does not detect potentially similar paragraphs quickly'. So, some tables have redundant simhash fields

#### Init table

use file `schema-mysql.sql` to init table   

### Elasticsearch


#### Install


You can [install Elasticsearch by docker](https://www.elastic.co/guide/en/elasticsearch/reference/current/docker.html)

Install [Ik Analyzer](https://github.com/medcl/elasticsearch-analysis-ik#ik-analysis-for-elasticsearch) to segment Chinese words 


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

### Run the web server

run the main method in `com.eva.check.web.ContentCheckerWebApplication`

## Development

You can init paper library with  `PaperDataCreatorTest`

You can check paper library with  `PaperCheckServiceImplTest` or web page