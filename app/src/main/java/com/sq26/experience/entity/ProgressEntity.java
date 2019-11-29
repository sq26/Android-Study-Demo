package com.sq26.experience.entity;

//下载进度的实体类
public class ProgressEntity {
    //当前已下载大小
    private Long current;
    //全部大小
    private Long total;

    public Long getCurrent() {
        return current;
    }

    public void setCurrent(Long current) {
        this.current = current;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
