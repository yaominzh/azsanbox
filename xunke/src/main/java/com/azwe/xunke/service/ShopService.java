package com.azwe.xunke.service;

import com.azwe.xunke.common.BusinessException;
import com.azwe.xunke.model.ShopModel;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by AZ 2020-04-23
 */

public interface ShopService {
    ShopModel create(ShopModel shopModel) throws BusinessException;
    ShopModel get(Integer id);
    List<ShopModel> selectAll();
    List<ShopModel> recommend(BigDecimal longitude,BigDecimal latitude);

    List<Map<String,Object>> searchGroupByTags(String keyword,Integer categoryId,String tags);

    Integer countAllShop();

    List<ShopModel> search(BigDecimal longitude,BigDecimal latitude,
                           String keyword,Integer orderby,Integer categoryId,String tags);
}
