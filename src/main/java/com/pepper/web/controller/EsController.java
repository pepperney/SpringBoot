package com.pepper.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pepper.common.util.RandomUtil;
import com.pepper.web.helper.EsHelper;
import com.pepper.web.model.CommonResp;
import com.pepper.web.model.entity.Order;
import com.pepper.web.model.entity.UserInfo;
import com.pepper.web.model.vo.QueryOrderVo;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Maps;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @Auther: pei.nie
 * @Date:2018/8/24
 * @Description:es的接口测试类，
 */
@RestController
@RequestMapping("/es")
public class EsController {

    private static final Logger logger = LoggerFactory.getLogger(EsController.class);

    /**
     * index=products,type=order的映射信息
     */
    private static final String MAPPINGS = "{\"order\":{\"properties\":{\"providerPhone\":{\"type\":\"string\"},\"address\":{\"type\":\"string\"},\"orderTime\":{\"type\":\"date\"},\"orderId\":{\"type\":\"long\"},\"provider\":{\"type\":\"string\"},\"seqNum\":{\"type\":\"string\"},\"goodsPrice\":{\"type\":\"double\"},\"discount\":{\"type\":\"double\"},\"orderStatus\":{\"type\":\"integer\"},\"providerCompany\":{\"type\":\"string\"},\"goodsName\":{\"type\":\"string\"},\"customer\":{\"type\":\"nested\",\"include_in_parent\":\"true\",\"properties\":{\"sex\":{\"type\":\"string\"},\"mobile\":{\"type\":\"string\"},\"userName\":{\"type\":\"string\"},\"userId\":{\"type\":\"long\"},\"age\":{\"type\":\"integer\"}}}}}}";

    @Autowired
    EsHelper esHelper;

    /* ================================  索引相关的方法 =====================================*/

    /**
     * 创建索引
     *
     * @param index
     * @return
     */
    @GetMapping(value = "/addIndex/{index}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Object> addIndex(@PathVariable("index") String index) {
        boolean success = esHelper.addIndex(index);
        return CommonResp.returnOKResult(Maps.newHashMap("success", success));
    }

    /**
     * 为索引增加mappings
     *
     * @param index
     * @param type
     * @return
     */
    @GetMapping(value = "/addMapping/{index}/{type}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Object> deleteIndex(@PathVariable("index") String index, @PathVariable("type") String type) {
        boolean success = esHelper.addMappings(index, type, MAPPINGS);
        return CommonResp.returnOKResult(Maps.newHashMap("success", success));
    }

    /**
     * 删除索引
     *
     * @param index
     * @return
     */
    @GetMapping(value = "/delIndex/{index}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Object> deleteIndex(@PathVariable("index") String index) {
        boolean success = esHelper.deleteIndex(index);
        return CommonResp.returnOKResult(Maps.newHashMap("success", success));
    }


    /* ================================  文档相关的方法 =====================================*/

    /**
     * 添加文档
     *
     * @param index
     * @param type
     * @return
     */
    @GetMapping(value = "/addDoc/{index}/{type}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Object> addData(@PathVariable("index") String index, @PathVariable("type") String type) {
        for (int i = 0; i < 100; i++) {
            UserInfo customer = new UserInfo();
            customer.setUserId(i);
            customer.setUserName(RandomUtil.getRandomString(4));
            customer.setAge(20 + RandomUtil.getRandomInt(20));
            customer.setMobile("155" + RandomUtil.getRandomString(8, "0123456789"));
            customer.setSex(RandomUtil.getRandomInt(2) + "");
            Order order = new Order();
            order.setOrderId((long) i);
            order.setSeqNum(UUID.randomUUID().toString().replaceAll("-", ""));
            order.setOrderTime(new Date());
            order.setOrderStatus(RandomUtil.getRandomInt(5));
            order.setAddress("广东省深圳市福田区深南大道1" + RandomUtil.getRandomString(3, "0123456789") + "号");
            order.setCost(90.00 + i);
            order.setCustomer(customer);
            order.setGoodsName("aircondition" + i);
            order.setGoodsPrice(100.00 + i);
            order.setDiscount(i);
            order.setProvider(RandomUtil.getRandomString(5));
            order.setProviderPhone("180" + RandomUtil.getRandomString(8, "0123456789"));
            order.setProviderCompany(RandomUtil.getRandomString(4) + "_company");
            esHelper.addDoc(JSONObject.parseObject(JSONObject.toJSONString(order)), index, type, String.valueOf(i));
        }
        return CommonResp.returnOK();
    }


    /**
     * 通过id删除数据
     *
     * @param index
     * @param type
     * @param id
     * @return
     */
    @GetMapping(value = "/delDoc/{index}/{type}/{id}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Object> deleteDocById(@PathVariable("index") String index, @PathVariable("type") String type, @PathVariable("id") String id) {
        boolean success = esHelper.delDoc(index, type, id);
        return CommonResp.returnOKResult(Maps.newHashMap("success", success));
    }

    /**
     * 通过id更新数据
     *
     * @param index
     * @param type
     * @param id
     * @return
     */
    @GetMapping(value = "/updateDoc/{index}/{type}/{id}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Object> updateDocById(@PathVariable("index") String index, @PathVariable("type") String type, @PathVariable("id") String id) {
        Order order = new Order();
        order.setOrderStatus(5);
        boolean success = esHelper.updateDoc(JSONObject.parseObject(JSONObject.toJSONString(order)), index, type, id);
        return CommonResp.returnOKResult(Maps.newHashMap("success", success));
    }

    /**
     * 通过id获取数据
     *
     * @param index
     * @param type
     * @param id
     * @return
     */
    @GetMapping(value = "/getDoc/{index}/{type}/{id}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Object> getDoc(@PathVariable("index") String index, @PathVariable("type") String type, @PathVariable("id") String id) {
        Map<String, Object> map = esHelper.getDoc(index, type, id, null);
        return CommonResp.returnOKResult(JSON.toJSONString(map));
    }

    /* ================================  复合查询的方法 =====================================*/

    /**
     * @See https://blog.csdn.net/gxl0805/article/details/72871680
     * @See https://segmentfault.com/a/1190000011881302
     * @param vo
     * @return
     */
    @PostMapping(value = "/queryDoc/{index}/{type}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Object> queryDoc(@RequestBody QueryOrderVo vo) {
        QueryBuilder queryBuilder = null;
        List<Map<String, Object>> list = new ArrayList<>();
        //1. boolQuery布尔查询,或should，相等must。不等must_not
        if(!ObjectUtils.isEmpty(vo.getOrderId())){
            queryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("orderId",vo.getOrderId()));
            list = esHelper.queryDoc(vo.getIndex(), vo.getType(), queryBuilder);
            logger.info("boolQuery查询获取到的结果：{}",JSON.toJSON(esHelper.queryDoc(vo.getIndex(), vo.getType(), queryBuilder)));
            logger.info("----------------------------------------------------------");
        }
        //2. termQuery分词精确查询，查询orderStatus 分词后包含指定orderStatus的term的文档
        if (!ObjectUtils.isEmpty(vo.getOrderStatus())) {
            queryBuilder = QueryBuilders.termQuery("orderStatus", vo.getOrderStatus());
            logger.info("termQuery查询获取到的结果：{}",JSON.toJSON(esHelper.queryDoc(vo.getIndex(), vo.getType(), queryBuilder)));
        }

        //3. termsQuery多term查询，查询goodsName包含指定goodsName用逗号分隔后的任何一个或多个的文档
        if (StringUtils.isNotEmpty(vo.getGoodsName())) {
            queryBuilder = QueryBuilders.termsQuery("goodsName", vo.getGoodsName().split(","));
            logger.info("termsQuery查询获取到的结果：{}",JSON.toJSON(esHelper.queryDoc(vo.getIndex(), vo.getType(), queryBuilder)));
        }
        //4. rangeQuery,范围查询，查询价格大于0小于等于指定价格的文档
        if(!ObjectUtils.isEmpty(vo.getGoodsPrice())){
            queryBuilder = QueryBuilders
                    .rangeQuery("goodsPrice")
                    .lt(vo.getGoodsPrice())
                    .gt(0.00)
                    .includeLower(false)//包括下界
                    .includeUpper(true);//包括上界
            logger.info("rangeQuery查询获取到的结果：{}",JSON.toJSON(esHelper.queryDoc(vo.getIndex(), vo.getType(), queryBuilder)));
        }
        list = esHelper.queryDoc(vo.getIndex(), vo.getType(), queryBuilder);
        return CommonResp.returnOKResult(JSON.toJSONString(list));
    }

}
