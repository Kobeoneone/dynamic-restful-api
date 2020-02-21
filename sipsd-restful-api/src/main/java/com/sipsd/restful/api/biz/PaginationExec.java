package com.sipsd.restful.api.biz;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Auther: gaoqiang
 * @Date: 2020-02-12 19:31
 * @Description:
 */
public class PaginationExec<T> implements Serializable
{
    private List<T> data;

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
