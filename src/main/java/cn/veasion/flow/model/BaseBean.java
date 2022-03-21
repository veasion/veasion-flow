package cn.veasion.flow.model;

import java.io.Serializable;
import java.util.Date;

/**
 * BaseBean
 *
 * @author luozhuowei
 * @date 2020/10/18
 */
public class BaseBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Integer isDeleted;
    private Date createTime;
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
