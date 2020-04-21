package com.azwe.xunke.request;

import javax.validation.constraints.NotBlank;

/**
 * Created by AZ 2020-04-21
 */

public class SellerCreateReq {
    @NotBlank(message = "商户名不能为空")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
