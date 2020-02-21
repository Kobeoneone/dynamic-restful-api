package com.sipsd.restful.api.mode.JDBC;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2016/8/29.
 */
public class Pagination  implements Serializable{

    /**
     * 每页大小
     */
    protected int pageSize=10;

    /**
     * 当前页,从1开始
     */
    protected int currentPage=1;
    /**
     * 排序组
     */
    protected String sortGroup;

    /**
     * 这个针对sqlserver的order字段
     */
    private String fieldName;

    /**
     * 数据库类别，用来执行sql分页的时候选用不同的数据库方式
     */
    private String dbType;


    protected final List<SortBy> sorts=new ArrayList<SortBy>();

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {

        if(pageSize<1){
            pageSize=10;
        }
        this.pageSize = pageSize;
    }

    public int getCurrentPage() {
        if(currentPage<=0){
            currentPage=1;
        }
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        if(currentPage<1){
            currentPage=1;
        }
        this.currentPage = currentPage;
    }

    public String getSortGroup() {
        return sortGroup;
    }

    public void setSortGroup(String sortGroup) {
        this.sortGroup = sortGroup;
    }

    public void addSortBy(int priority,String colName,SortType sortType) throws java.lang.IllegalArgumentException
    {
        if(colName==null||colName.trim().equals("")){
        return;
    }
        SortBy sortBy=new SortBy(colName);
        sortBy.setPriority(priority);
        sortBy.setSortType(sortType);
        sorts.add(sortBy);
    }
    public void addSortBy(final List<SortBy> list) throws java.lang.IllegalArgumentException
    {
        if(list==null||list.size()==0){
            return;
        }
        for(SortBy sortBy:list){
            addSortBy(sortBy.getPriority(),sortBy.getColName(),sortBy.getSortType());
        }
    }

    public void addSortBy(SortBy sortBy) throws java.lang.IllegalArgumentException
    {
        addSortBy(sortBy.getPriority(),sortBy.getColName(),sortBy.getSortType());
    }

    List<SortBy> getSorts() {
        Collections.sort(sorts);
        return Collections.unmodifiableList(sorts);
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public void setFieldName(String fieldName)
    {
        this.fieldName = fieldName;
    }

    public String getDbType()
    {
        return dbType;
    }

    public void setDbType(String dbType)
    {
        this.dbType = dbType;
    }
}
