package com.zdx.search;


import cn.hutool.core.util.ObjUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;



@Slf4j
public class EsSearchTemplateImpl implements SearchTemplate{

    @Autowired
    private RestHighLevelClient client;
    @Override
    public Boolean createIndex(String index) {
        if (isIndexExist(index)) {
            log.error("索引不存在");
            return false;
        }
        CreateIndexRequest request = new CreateIndexRequest(index);
        request.settings(Settings.builder()
                .put("index.analysis.analyzer.default.type", "ik_max_word")
        );
        try {
            CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
            return response.isAcknowledged();
        } catch (IOException e) {
            log.error("创建索引出现错误：{}", e.getMessage());
            return false;
        }
    }

    @Override
    public Boolean deleteIndex(String index) {
        if (isIndexExist(index)) {
            log.error("索引不存在");
            return false;
        }
        DeleteIndexRequest request = new DeleteIndexRequest(index);
        try {
            AcknowledgedResponse response = client.indices().delete(request, RequestOptions.DEFAULT);
            return response.isAcknowledged();
        } catch (IOException e) {
            log.error("删除索引异常：{}", e.getMessage());
            return false;
        }
    }

    @Override
    public Boolean isIndexExist(String index) {
        GetIndexRequest request = new GetIndexRequest(index);
        try {
            return client.indices().exists(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("查询索引出错：{}", e.getMessage());
            return false;
        }
    }

    @Override
    public void insertDoc(Object object, String index, String id) {
        IndexRequest request = new IndexRequest();
        request.index(index).id(id);
        request.source(JSON.toJSONString(object), XContentType.JSON);
        try {
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            log.info("新增文档成功：{}", response.status().getStatus());
            response.getId();
        } catch (IOException e) {
            log.error("查询文档异常：{}", e.getMessage());
        }
    }

    @Override
    public void deleteDoc(String index, String id) {
        DeleteRequest request = new DeleteRequest();
        request.index(index).id(id);
        try {
            DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
            log.info("删除文档成功：{}", response.status().getStatus());
        } catch (IOException e) {
            log.error("删除文档异常：{}", e.getMessage());
        }
    }

    @Override
    public void updateDoc(Object data, String index, String id) {
        UpdateRequest request = new UpdateRequest();
        request.index(index).id(id);
        request.doc(JSON.toJSONString(data), XContentType.JSON);
        try {
            UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
            log.info("更新文档成功：{}", response.status().getStatus());
        } catch (IOException e) {
            log.error("更新文档异常：{}", e.getMessage());
        }
    }

    @Override
    public <T> T getDocById(String index, String id, Class<T> clazz) {
        if (!existsDocById(index, id)) {
            return null;
        }
        GetRequest request = new GetRequest();
        request.index(index).id(id);
        try {
            GetResponse response = client.get(request, RequestOptions.DEFAULT);
            return JSON.parseObject(response.getSourceAsString(), clazz);
        } catch (IOException e) {
            log.error("查询文档异常：{}", e.getMessage());
        }
        return null;
    }

    @Override
    public Boolean existsDocById(String index, String id) {
        GetRequest request = new GetRequest();
        request.index(index).id(id);
        try {
            return client.exists(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("文档判断异常：{}", e.getMessage());
            return false;
        }
    }

    @Override
    public Boolean bulkDoc(String index, BulkRequest request) {
        try {
            BulkResponse response = client.bulk(request, RequestOptions.DEFAULT);
            log.info("批量数据：{}", response.status().getStatus());
            return true;
        } catch (IOException e) {
            log.error("批量数据异常：{}", e.getMessage());
            return false;
        }
    }

    @Override
    public <T> List<T> searchAll(String index, Class<T> clazz) {
        SearchRequest request = new SearchRequest();
        request.indices(index);
        MatchAllQueryBuilder query = QueryBuilders.matchAllQuery();
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(query);
        request.source(builder);
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            SearchHits hits = response.getHits();
            List<T> objs = Lists.newArrayList();
            for (SearchHit hit : hits) {
                objs.add(JSON.parseObject(hit.getSourceAsString(), clazz));
            }
            return objs;
        } catch (IOException e) {
            log.error("查询文档异常：{}", e.getMessage());
        }
        return null;
    }

    @Override
    public <T> List<T> searchQuery(String index, SearchSourceBuilder query, Class<T> clazz, String... highlightField) {
        SearchRequest request = new SearchRequest();
        request.indices(index);
        request.source(getSearchRequest(query,0, 0, null, highlightField, null));
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            return getResponseList(response, highlightField, clazz);
        } catch (IOException e) {
            log.error("查询文档异常：{}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public <T> IPage<T> searchDoc(String index, SearchSourceBuilder query, Integer limit, Integer page, SortBuilder[] sortField, String[] highlightField,Class<T> clazz,String... fields) {
        SearchRequest request = new SearchRequest();
        request.indices(index);
        request.source(getSearchRequest(query, limit, page, sortField, highlightField, fields));
        try {
            IPage<T> iPage = new Page<>(page, limit);
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            List<T> objs = getResponseList(response, highlightField, clazz);
            SearchHits hits = response.getHits();
            iPage.setRecords(objs);
            iPage.setSize(hits.getTotalHits().value);
            iPage.setTotal(hits.getTotalHits().value);
            return iPage;
        } catch (IOException e) {
            log.error("查询文档异常：{}", e.getMessage(), e);
            return null;
        }
    }

    private SearchSourceBuilder getSearchRequest(SearchSourceBuilder query, Integer limit, Integer page, SortBuilder[] sortField, String[] highlightField, String[] fields) {
        if (ObjUtil.isNotNull(sortField)) {
            for (SortBuilder sortBuilder : sortField) {
                query.sort(sortBuilder);
            }
        }
        if (ObjUtil.isNotNull(highlightField) && highlightField.length != 0) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            for (String s : highlightField) {
                highlightBuilder.requireFieldMatch(false)
                        .field(new HighlightBuilder.Field(s))
                        .preTags("<span style='color:red;'>")
                        .postTags("</span>");
            }

            query.highlighter(highlightBuilder);
        }
        if (ObjUtil.isNotNull(fields)) {
            for (String field : fields) {
                query.fetchField(field);
            }
        }
        if (limit != 0 && page != 0) {
            query.size(limit);
            query.from((page -1) * limit);
        }
        return query;
    }

    private <T> List<T> getResponseList(SearchResponse response, String[] highlightField, Class<T> clazz) {
        List<T> objs = new ArrayList<>();
        SearchHits hits = response.getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> source = hit.getSourceAsMap();
            if (ObjUtil.isNotNull(highlightField) && highlightField.length != 0) {
                Map<String, Object> highlightMap = Maps.newHashMap();
                for (String s : highlightField) {
                    if (hit.getHighlightFields().containsKey(s)) {
                        highlightMap.put(s, hit.getHighlightFields().get(s).fragments()[0].string());
                    }
                }
                source.put("highlight", highlightMap);
            }
            String json = JSON.toJSONString(source);
            T obj = JSON.parseObject(json, clazz);
            objs.add(obj);
        }
        return objs;
    }
}
