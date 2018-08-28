package com.pepper.web.helper;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @Auther: pei.nie
 * @Date:2018/8/24
 * @Description:
 */
@Component
public class EsHelper {


    private static final Logger logger = LoggerFactory.getLogger(EsHelper.class);

    @Autowired
    private TransportClient client;


    /* ================================  索引相关的方法 =====================================*/


    /**
     * 判断索引是否存在
     *
     * @param index
     * @return
     */
    public boolean isIndexExist(String index) {
        IndicesExistsResponse inExistsResponse = client
                .admin()
                .indices()
                .exists(new IndicesExistsRequest(index))
                .actionGet();
        logger.info("index exist？" + inExistsResponse.isExists());
        return inExistsResponse.isExists();
    }

    /**
     * 创建索引--未指定分片数和备份数
     *
     * @param index
     * @return
     */
    public boolean addIndex(String index) {
        if (this.isIndexExist(index)) {
            logger.info("Index has exited!");
            return false;
        }
        CreateIndexResponse indexresponse = client
                .admin()
                .indices()
                .prepareCreate(index)
                .execute()
                .actionGet();
        logger.info("create index success？" + indexresponse.isAcknowledged());
        return indexresponse.isAcknowledged();
    }

    /**
     * 为结构化索引增加映射
     *
     * @param index
     * @param type
     * @param mappings
     * @return
     */
    public boolean addMappings(String index, String type, String mappings) {
        PutMappingRequest mappingRequest = Requests.putMappingRequest(index).type(type).source(mappings);
        PutMappingResponse mappingResponse = client
                .admin()
                .indices()
                .putMapping(mappingRequest)
                .actionGet();
        logger.info("add mappings success？" + mappingResponse.isAcknowledged());
        return mappingResponse.isAcknowledged();
    }


    /**
     * 删除索引
     *
     * @param index
     * @return
     */
    public boolean deleteIndex(String index) {
        if (!isIndexExist(index)) {
            logger.info("Index is not exits!");
            return false;
        }
        DeleteIndexResponse delResponse = client
                .admin()
                .indices()
                .prepareDelete(index)
                .execute()
                .actionGet();
        logger.info("delete index success？" + delResponse.isAcknowledged());
        return delResponse.isAcknowledged();
    }


    /* ================================  文档相关的方法 =====================================*/


    /**
     * 添加文档(指定id)
     *
     * @param jsonObject 要增加的数据
     * @param index      索引，类似数据库
     * @param type       类型，类似表
     * @param id         数据ID
     * @return
     */
    public String addDoc(JSONObject jsonObject, String index, String type, String id) {
        IndexResponse response = client
                .prepareIndex(index, type, id)
                .setSource(jsonObject)
                .get();
        logger.info("addData response status:{},id:{}", response.status().getStatus(), response.getId());
        return response.getId();
    }

    /**
     * 添加文档
     *
     * @param jsonObject 要增加的数据
     * @param index      索引，类似数据库
     * @param type       类型，类似表
     * @return
     */
    public String addDoc(JSONObject jsonObject, String index, String type) {
        return addDoc(jsonObject, index, type, UUID.randomUUID().toString().replaceAll("-", "").toUpperCase());
    }

    /**
     * 通过id删除文档
     *
     * @param index 索引，类似数据库
     * @param type  类型，类似表
     * @param id    数据ID
     */
    public boolean delDoc(String index, String type, String id) {
        DeleteResponse response = client
                .prepareDelete(index, type, id)
                .get();
        logger.info("deleteDocById response status:{},id:{}", response.status().getStatus(), response.getId());
        return response.getResult().equals(DocWriteResponse.Result.DELETED);
    }

    /**
     * 通过id更新文档
     *
     * @param jsonObject 要增加的数据
     * @param index      索引，类似数据库
     * @param type       类型，类似表
     * @param id         数据ID
     * @return
     */
    public boolean updateDoc(JSONObject jsonObject, String index, String type, String id) {
        UpdateRequest updateRequest = new UpdateRequest().index(index).type(type).id(id).doc(jsonObject);
        UpdateResponse updateResponse = client
                .prepareUpdate(index, type, id)
                .setDoc(jsonObject)
                .get();
        return updateResponse.getResult().equals(DocWriteResponse.Result.UPDATED);

    }

    /**
     * 通过id获取文档
     *
     * @param index  索引，类似数据库
     * @param type   类型，类似表
     * @param id     数据ID
     * @param fields 需要显示的字段，逗号分隔（缺省为全部字段）
     * @return
     */

    public Map<String, Object> getDoc(String index, String type, String id, String fields) {
        GetRequestBuilder getRequestBuilder = client.prepareGet(index, type, id);
        if (StringUtils.isNotEmpty(fields)) {
            getRequestBuilder.setFetchSource(fields.split(","), null);
        }
        GetResponse getResponse = getRequestBuilder.execute().actionGet();
        return getResponse.getSource();
    }





    /* ================================  复合查询的方法 =====================================*/

    /**
     * @param index
     * @param type
     * @param queryBuilder
     * @return
     */
    public List<Map<String, Object>> queryDoc(String index, String type, QueryBuilder queryBuilder) {
        List<Map<String, Object>> sourceList = new ArrayList<Map<String, Object>>();
        SearchResponse response = client
                .prepareSearch(index)
                .setTypes(type)
                .setQuery(queryBuilder)
                .execute()
                .actionGet();
        SearchHits searchHits = response.getHits();
        for (SearchHit searchHit : searchHits) {
            logger.info("score={},source=[{}]", searchHit.getScore(), searchHit.getSourceAsString());
            sourceList.add(searchHit.getSource());
        }
        return sourceList;
    }
}

