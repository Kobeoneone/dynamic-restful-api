package com.sipsd.restful.api.mode.JDBC;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/8/31.
 */
public class SortBy implements Comparable<SortBy> ,Serializable{

    private SortType sortType;
    /**
     * 排序优先级，正整形，从1开始，1最大
     */
    private int priority;
    /**
     * 排序的列名
     */
    private String colName;

    public SortBy(String colName) throws java.lang.IllegalArgumentException
    {
        this(colName,1);
    }

    public SortBy(String colName,int priority) throws java.lang.IllegalArgumentException
    {
        this(colName,priority,SortType.ASC);
    }

    public SortBy(String colName,int priority,SortType sortType) throws java.lang.IllegalArgumentException
    {
        if(colName==null||colName.trim().equals("")){
            throw new java.lang.IllegalArgumentException("colName must have real value");
        }
        if(priority<1){
            throw new java.lang.IllegalArgumentException("priority must greater than 1");
        }
        if(sortType==null){
            throw new java.lang.IllegalArgumentException("sortType can not be null");
        }

        this.colName=colName;
        this.priority=priority;
        this.sortType=SortType.ASC;
    }

    public SortType getSortType() {
        return sortType;
    }

    public void setSortType(SortType sortType) throws java.lang.IllegalArgumentException
    {
        if(sortType==null){
            throw new java.lang.IllegalArgumentException("sortType can not be null");
        }
        this.sortType = sortType;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) throws java.lang.IllegalArgumentException
    {
        if(priority<1){
            throw new java.lang.IllegalArgumentException("priority must greater than 1");
        }
        this.priority = priority;
    }

    public String getColName() {
        return colName;
    }

    public int compareTo(SortBy o) {
        return this.priority-o.getPriority();
    }
}
