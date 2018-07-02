package lerrain.service.customer;

import java.util.Date;

/**
 * 渠道用户关联数据
 */
public class ChannelUserR {

    Integer channelType;   // 渠道类型（1-微信小程序）
    String channelUserId;  //
    Long accountId;   // accountId

    Date createTime;    // 创建时间
    Date updateTime;    // 修改时间

    public Integer getChannelType() {
        return channelType;
    }

    public void setChannelType(Integer channelType) {
        this.channelType = channelType;
    }

    public String getChannelUserId() {
        return channelUserId;
    }

    public void setChannelUserId(String channelUserId) {
        this.channelUserId = channelUserId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
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
