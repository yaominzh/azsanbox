package com.azwe.xunke.service;

import com.azwe.xunke.common.BusinessException;
import com.azwe.xunke.model.SellerModel;

import java.util.List;


public interface SellerService {
    SellerModel create(SellerModel sellerModel);
    SellerModel get(Integer id);
    List<SellerModel> selectAll();
    SellerModel changeStatus(Integer id,Integer disabledFlag) throws BusinessException;
    Integer countAllSeller();
}

