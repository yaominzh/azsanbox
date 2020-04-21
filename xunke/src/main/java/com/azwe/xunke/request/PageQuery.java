package com.azwe.xunke.request;

/**
 * Created by AZ 2020-04-21
 */

public class PageQuery {
    private Integer page = 1;

    private Integer size = 20;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
