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

//        SearchRequest searchRequest = new SearchRequest("shop");
//        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//        sourceBuilder.query(QueryBuilders.matchQuery("name", keyword));
//        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
//        searchRequest.source(sourceBuilder);
//
//        List<Integer> shopIdList = new ArrayList<>();
//        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
//        SearchHit[] hits = searchResponse.getHits().getHits();
//        for (SearchHit hit : hits) {
//            shopIdList.add(new Integer(hit.getSourceAsMap().get("id").toString()));
//        }
        Request request = new Request("GET", "/shop/_search");
        String reqJson = "{\n" +
                "  \"explain\": true, \n" +
                "  \"_source\": \"*\",\n" +
                "  \"script_fields\": {\n" +
                "    \"distance\": {\n" +
                "      \"script\": {\n" +
                "        \"source\": \"haversin(lat, lon, doc['location'].lat, doc['location'].lon)\",\n" +
                "        \"lang\": \"expression\",\n" +
                "        \"params\": {\"lat\": " + latitude.toString() + ", \"lon\": " + longitude.toString() + "\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  \"query\": {\n" +
                "    \"function_score\": {\n" +
                "      \"query\": {\n" +
                "        \"bool\": {\n" +
                "          \"must\": [\n" +
                "            {\"match\": {\"name\": {\"query\": \"" + keyword +"\", \"boost\": 0.1}}},\n" +
                "            {\"term\": {\"seller_disabled_flag\": 0}}\n" +
                "          ]\n" +
                "        }\n" +
                "      },\n" +
                "      \"functions\": [\n" +
                "        {\n" +
                "          \"gauss\": {\n" +
                "            \"location\": {\n" +
                "              \"origin\": \"" + latitude.toString() + "," + longitude.toString() + "\",\n" +
                "              \"scale\": \"100km\",\n" +
                "              \"offset\": \"0km\",\n" +
                "              \"decay\": 0.5\n" +
                "            }\n" +
                "          },\n" +
                "          \"weight\": 9\n" +
                "        },\n" +
                "        {\"field_value_factor\": {\n" +
                "          \"field\": \"remark_score\"\n" +
                "        },\n" +
                "          \"weight\": 0.2\n" +
                "        },\n" +
                "        {\"field_value_factor\": {\n" +
                "          \"field\": \"seller_remark_score\"\n" +
                "        },\n" +
                "          \"weight\": 0.1\n" +
                "        }\n" +
                "      ],\n" +
                "      \"score_mode\": \"sum\",\n" +
                "      \"boost_mode\": \"sum\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"sort\": [\n" +
                "    {\n" +
                "      \"_score\": {\n" +
                "        \"order\": \"desc\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";
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
            Integer id = new Integer(jsonObject.get("_id").toString());
            BigDecimal distance = new BigDecimal(
                    jsonObject.getJSONObject("fields").getJSONArray("distance").get(0).toString()
            );
            ShopModel shopModel = get(id);
            shopModel.setDistance(distance.multiply(new BigDecimal(1000).setScale(0, BigDecimal.ROUND_CEILING)).intValue());
            shopModelList.add(shopModel);
        }

//         = shopIdList.stream().map(id -> {
//            return get(id);
//        }).collect(Collectors.toList());

        result.put("shop", shopModelList);
        return result;
    }
}
