package com.example.dianping.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.dianping.common.ResultEnum;
import com.example.dianping.dao.ShopModelMapper;
import com.example.dianping.exception.BussinessException;
import com.example.dianping.model.CategoryModel;
import com.example.dianping.model.SellerModel;
import com.example.dianping.model.ShopModel;
import com.example.dianping.service.CategoryService;
import com.example.dianping.service.SellerService;
import com.example.dianping.service.ShopService;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Adward_Z
 * @date 2020/8/7
 */
@Service
public class ShopServiceImpl implements ShopService {

    @Autowired
    private ShopModelMapper mapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SellerService sellerService;

    @Autowired
    private RestHighLevelClient client;

    @Override
    @Transactional
    public ShopModel create(ShopModel shopModel) {
        shopModel.setCreatedAt(new Date());
        shopModel.setUpdatedAt(new Date());

        // 校验商家是否存在正确
        SellerModel sellerModel = sellerService.get(shopModel.getSellerId());
        if (sellerModel == null){
            throw new BussinessException(ResultEnum.PARAMETER_VALIDATION_ERROR.getCode(), "商户不存在");
        }

        if (sellerModel.getDisabledFlag().intValue() == 1){
            throw new BussinessException(ResultEnum.PARAMETER_VALIDATION_ERROR.getCode(), "商户已禁用");
        }

        // 校验类目是否存在正确
        CategoryModel categoryModel = categoryService.get(shopModel.getCategordId());
        if (categoryModel == null){
            throw new BussinessException(ResultEnum.PARAMETER_VALIDATION_ERROR.getCode(), "类目不存在");
        }

        mapper.insertSelective(shopModel);
        return get(shopModel.getId());
    }

    @Override
    public ShopModel get(Integer id) {
        ShopModel shopModel = mapper.selectByPrimaryKey(id);
        if (shopModel == null){
            return null;
        }
        shopModel.setSellerModel(sellerService.get(shopModel.getSellerId()));
        shopModel.setCategoryModel(categoryService.get(shopModel.getCategordId()));

        return shopModel;
    }

    @Override
    public List<ShopModel> selectAll() {
        List<ShopModel> shopModelList = mapper.selectAll();
        shopModelList.forEach(shopModel -> {
            shopModel.setSellerModel(sellerService.get(shopModel.getSellerId()));
            shopModel.setCategoryModel(categoryService.get(shopModel.getCategordId()));

        });

        return shopModelList;
    }

    @Override
    public Integer countAllShop() {
        return mapper.countAllShop();
    }

    @Override
    public List<ShopModel> recommend(BigDecimal longitude, BigDecimal latitude) {
        List<ShopModel> shopModelList = mapper.recommend(longitude, latitude);
        shopModelList.forEach(shopModel -> {
            shopModel.setSellerModel(sellerService.get(shopModel.getSellerId()));
            shopModel.setCategoryModel(categoryService.get(shopModel.getCategordId()));
        });
        return shopModelList;
    }

    @Override
    public List<ShopModel> search(BigDecimal longitude, BigDecimal latitude, String keyword, Integer orderBy,
                                  Integer categoryId, String tags) {
        List<ShopModel> shopModelList = mapper.search(longitude, latitude, keyword, orderBy, categoryId, tags);
        shopModelList.forEach(shopModel -> {
            shopModel.setCategoryModel(categoryService.get(shopModel.getCategordId()));
            shopModel.setSellerModel(sellerService.get(shopModel.getSellerId()));
        });
        return shopModelList;
    }

    @Override
    public List<Map<String, Object>> searchGroupByTags(String keyword, Integer categoryId, String tags) {
        return mapper.searchGroupByTags(keyword, categoryId, tags);
    }

    @Override
    public Map<String, Object> searchES(BigDecimal longitude, BigDecimal latitude, String keyword, Integer orderBy, Integer categoryId, String tags) throws IOException {
        Map<String, Object> result = new HashMap<>();

        Request request = new Request("GET", "/shop/_search");

        // 构建请求
        JSONObject jsonRequestObj = new JSONObject();
        // 构建source部分
        jsonRequestObj.put("_source", "*");

        // 构建自定义距离字段
        jsonRequestObj.put("script_fields", new JSONObject());
        jsonRequestObj.getJSONObject("script_fields").put("distance", new JSONObject());
        jsonRequestObj.getJSONObject("script_fields").getJSONObject("distance").put("script", new JSONObject());
        jsonRequestObj.getJSONObject("script_fields").getJSONObject("distance").getJSONObject("script").put(
                "source", "haversin(lat, lon, doc['location'].lat, doc['location'].lon)"
        );
        jsonRequestObj.getJSONObject("script_fields").getJSONObject("distance").getJSONObject("script").put(
                "lang", "expression"
        );
        jsonRequestObj.getJSONObject("script_fields").getJSONObject("distance").getJSONObject("script").put(
                "params", new JSONObject()
        );
        jsonRequestObj.getJSONObject("script_fields").getJSONObject("distance").getJSONObject("script").
                getJSONObject("params").put("lat", latitude);
        jsonRequestObj.getJSONObject("script_fields").getJSONObject("distance").getJSONObject("script").
                getJSONObject("params").put("lon", longitude);

        // 构建query字段
        Map<String, Object> cixingMap = analyzeCategoryKeyword(keyword);
        boolean isAffectFilter = true;

        jsonRequestObj.put("query", new JSONObject());

        // 构建function_score
        jsonRequestObj.getJSONObject("query").put("function_score", new JSONObject());

        JSONObject functionScoreObject = jsonRequestObj.getJSONObject("query").getJSONObject("function_score");
        functionScoreObject.put("query", new JSONObject());
        functionScoreObject.getJSONObject("query").put("bool", new JSONObject());
        functionScoreObject.getJSONObject("query").getJSONObject("bool").put("must", new JSONArray());
        functionScoreObject.getJSONObject("query").getJSONObject("bool").getJSONArray("must").add(new JSONObject());

        // 构建match query
        int queryIndex = 0;
        JSONObject functionScoreQuery = functionScoreObject.getJSONObject("query");

        if (cixingMap.keySet().size() > 0 && isAffectFilter){
            // TODO 分词函数识别器(11-5)
        }else {

        }
        functionScoreQuery.getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex).put("match", new JSONObject());
        functionScoreQuery.getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex).
                getJSONObject("match").put("name", new JSONObject());
        functionScoreQuery.getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex).
                getJSONObject("match").getJSONObject("name").put("query", keyword);
        functionScoreQuery.getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex).
                getJSONObject("match").getJSONObject("name").put("boost", 0.1);

        queryIndex++;
        functionScoreQuery.getJSONObject("bool").getJSONArray("must").add(new JSONObject());
        functionScoreQuery.getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex).put("term", new JSONObject());
        functionScoreQuery.getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex).getJSONObject("term").put("seller_disabled_flag", 0);

        if (tags != null){
            queryIndex++;
            functionScoreQuery.getJSONObject("bool").getJSONArray("must").add(new JSONObject());
            functionScoreQuery.getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex).put("term", new JSONObject());
            functionScoreQuery.getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex).getJSONObject("term").put("tags", tags);

        }

        if (categoryId != null){
            queryIndex++;
            functionScoreQuery.getJSONObject("bool").getJSONArray("must").add(new JSONObject());
            functionScoreQuery.getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex).put("term", new JSONObject());
            functionScoreQuery.getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex).getJSONObject("term").put("categord_id", categoryId);

        }

        // 构建functions
        functionScoreObject.put("functions", new JSONArray());
        int functionIndex = 0;
        JSONArray functionScoreFunctions = functionScoreObject.getJSONArray("functions");
        if (orderBy == null){
            functionScoreFunctions.add(new JSONObject());
            functionScoreFunctions.getJSONObject(functionIndex).put("gauss", new JSONObject());
            functionScoreFunctions.getJSONObject(functionIndex).getJSONObject("gauss").put("location", new JSONObject());
            functionScoreFunctions.getJSONObject(functionIndex).getJSONObject("gauss").getJSONObject("location").
                                   put("origin", latitude.toString() + "," + longitude.toString());
            functionScoreFunctions.getJSONObject(functionIndex).getJSONObject("gauss").getJSONObject("location").
                                   put("scale", "100km");
            functionScoreFunctions.getJSONObject(functionIndex).getJSONObject("gauss").getJSONObject("location").
                                   put("offset", "0km");
            functionScoreFunctions.getJSONObject(functionIndex).getJSONObject("gauss").getJSONObject("location").
                                   put("decay", "0.5");
            functionScoreFunctions.getJSONObject(functionIndex).put("weight", "9");
            functionIndex++;

            functionScoreFunctions.add(new JSONObject());
            functionScoreFunctions.getJSONObject(functionIndex).put("field_value_factor", new JSONObject());
            functionScoreFunctions.getJSONObject(functionIndex).getJSONObject("field_value_factor").put("field", "remark_score");
            functionScoreFunctions.getJSONObject(functionIndex).put("weight", "0.2");
            functionIndex++;

            functionScoreFunctions.add(new JSONObject());
            functionScoreFunctions.getJSONObject(functionIndex).put("field_value_factor", new JSONObject());
            functionScoreFunctions.getJSONObject(functionIndex).getJSONObject("field_value_factor").put("field", "seller_remark_score");
            functionScoreFunctions.getJSONObject(functionIndex).put("weight", "0.1");


            functionScoreObject.put("score_mode", "sum");
            functionScoreObject.put("boost_mode", "sum");

        }else {
            functionScoreFunctions.add(new JSONObject());
            functionScoreFunctions.getJSONObject(0).put("field_value_factor", new JSONObject());
            functionScoreFunctions.getJSONObject(0).getJSONObject("field_value_factor").put("field", "price_per_man");
            functionScoreFunctions.getJSONObject(0).put("weight", "1");

            functionScoreObject.put("score_mode", "sum");
            functionScoreObject.put("boost_mode", "replace");
        }


        // 构建排序字段
        jsonRequestObj.put("sort", new JSONArray());
        jsonRequestObj.getJSONArray("sort").add(new JSONObject());
        jsonRequestObj.getJSONArray("sort").getJSONObject(0).put("_score", new JSONObject());
        if (orderBy == null){
            jsonRequestObj.getJSONArray("sort").getJSONObject(0).getJSONObject("_score").put("order", "desc");
        }else {
            jsonRequestObj.getJSONArray("sort").getJSONObject(0).getJSONObject("_score").put("order", "asc");
        }

        // 聚合字段
        jsonRequestObj.put("aggs", new JSONObject());
        jsonRequestObj.getJSONObject("aggs").put("group_by_tags", new JSONObject());
        jsonRequestObj.getJSONObject("aggs").getJSONObject("group_by_tags").put("terms", new JSONObject());
        jsonRequestObj.getJSONObject("aggs").getJSONObject("group_by_tags").getJSONObject("terms").put("field", "tags");


        String reqJson = jsonRequestObj.toJSONString();

        System.out.println(reqJson);

        request.setJsonEntity(reqJson);
        Response response = client.getLowLevelClient().performRequest(request);
        String responseStr = EntityUtils.toString(response.getEntity());

        System.out.println(responseStr);

        JSONObject jsonObject = JSONObject.parseObject(responseStr);
        JSONArray jsonArray = jsonObject.getJSONObject("hits").getJSONArray("hits");

        List<ShopModel> shopModelList = new ArrayList<>();

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            Integer id = new Integer(object.get("_id").toString());
            BigDecimal distance = new BigDecimal(
                    object.getJSONObject("fields").getJSONArray("distance").get(0).toString()
            );
            ShopModel shopModel = get(id);
            shopModel.setDistance(distance.multiply(new BigDecimal(1000).setScale(0, BigDecimal.ROUND_CEILING)).intValue());
            shopModelList.add(shopModel);
        }

        List<Map> tagList = new ArrayList<>();
        JSONArray tagsJsonArray = jsonObject.getJSONObject("aggregations").getJSONObject("group_by_tags").getJSONArray("buckets");
        for (int i = 0; i < tagsJsonArray.size(); i++) {
            JSONObject jsonObj = tagsJsonArray.getJSONObject(i);
            Map<String, Object> tagMap = new HashMap<>();
            tagMap.put("tags", jsonObj.getString("key"));
            tagMap.put("num", jsonObj.getString("doc_count"));
            tagList.add(tagMap);
        }

        result.put("tags", tagList);
        result.put("shop", shopModelList);
        return result;
    }

    // 构造分词函数识别器

    private Map<String, Object> analyzeCategoryKeyword(String keyword) throws IOException {
        Map<String, Object> res = new HashMap<>();

        Request request = new Request("GET", "/shop/_analyze");
        request.setJsonEntity("{" + " \"field\": \"name\"," + "\"text\":\""+keyword+"\"\n" + "}");
        Response response = client.getLowLevelClient().performRequest(request);
        String reponseStr = EntityUtils.toString(request.getEntity());
        JSONObject jsonObject = JSONObject.parseObject(reponseStr);
        JSONArray jsonArray = jsonObject.getJSONArray("tokens");
        for (int i = 0; i < jsonArray.size(); i++) {
            String token = jsonArray.getJSONObject(i).getString("tokens");
            Integer categoryId = getCategoryIdByToken(token);
            if (categoryId != null){    res.put(token, categoryId); }
        }
        return res;
    }

    private Integer getCategoryIdByToken(String token){
        for (Integer key : categoryWorkMap.keySet()) {
            List<String> tokenList = categoryWorkMap.get(key);
            if (tokenList.contains(token)){ return key; }
        }
        return null;
    }

    private Map<Integer, List<String>> categoryWorkMap = new HashMap<>();

    @PostConstruct
    public void init(){
        categoryWorkMap.put(1, new ArrayList<>());
        categoryWorkMap.put(2, new ArrayList<>());

        categoryWorkMap.get(1).add("吃饭");
        categoryWorkMap.get(1).add("下午茶");

        categoryWorkMap.get(2).add("休息");
        categoryWorkMap.get(2).add("睡觉");
        categoryWorkMap.get(2).add("住宿");
    }
}
