package com.sipsd.restful.api.mode.JDBC;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2016/8/31.
 */
public class PaginationResult<T> extends Pagination implements Serializable{

    private int totalCount;
    private int totalPage;

    private List<T> data;


    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }


    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }


    public int getTotalPage() {
        return totalPage;
    }

    public void setData(final List<T> data){
        if(data==null){
            this.data=new ArrayList<T>();
        }else {
            this.data=data;
        }
    }
    public List<T> getData() {
        return Collections.unmodifiableList(data);
    }
}
