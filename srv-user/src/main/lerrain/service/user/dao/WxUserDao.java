package lerrain.service.user.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import lerrain.service.common.ServiceTools;
import lerrain.service.user.WxUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.mysql.jdbc.StringUtils;

@Repository
public class WxUserDao {

    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    ServiceTools tools;

    public Integer countWxUser(WxUser user) {
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder("select count(*) from t_wx_user where is_deleted='N' ");
        addParams(user, sql, params);
        return jdbc.queryForObject(sql.toString(), params.toArray(), Integer.class);
    }

    private void addParams(WxUser user, StringBuilder sql, List<Object> params) {
        if (!StringUtils.isNullOrEmpty(user.getMobile())) {
            sql.append("and mobile = ? ");
            params.add(user.getMobile());
        }
        if (!StringUtils.isNullOrEmpty(user.getName())) {
            sql.append("and name = ? ");
            params.add(user.getName());
        }
        if (user.getOpenId() != null) {
            sql.append("and open_id = ? ");
            params.add(user.getOpenId());
        }
        if (user.getUserId() != null) {
            sql.append("and user_id = ? ");
            params.add(user.getUserId());
        }
        if (user.getStart() != null && user.getLimit() != null) {
            sql.append(" limit ?,?");
            params.add(user.getStart());
            params.add(user.getLimit());
        }

    }

    public List<WxUser> listWxUser(WxUser user) {
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "select id,open_id, name, mobile, user_id,auth_status,remark from t_wx_user where is_deleted='N' ");
        addParams(user, sql, params);
        return jdbc.query(sql.toString(), params.toArray(), new RowMapper<WxUser>() {
            @Override
            public WxUser mapRow(ResultSet rs, int rowNum) throws SQLException {
                WxUser wxUser = new WxUser();
                wxUser.setId(rs.getLong("id"));
                wxUser.setOpenId(rs.getString("open_id"));
                wxUser.setMobile(rs.getString("mobile"));
                wxUser.setName(rs.getString("name"));
                wxUser.setRemark(rs.getString("remark"));
                wxUser.setAuthStatus(rs.getString("auth_status"));
                wxUser.setUserId(rs.getLong("user_id"));
                return wxUser;
            }

        });
    }

    public void insert(WxUser user) {
        String sql = "INSERT INTO `t_wx_user` (`id`, `open_id`, `name`, `mobile`, `user_id`, `auth_status`, `remark`, `is_deleted`, `creator`, `gmt_created`, `modifier`, `gmt_modified`) VALUES (?, ?, ?, ?, ?, ?, ?, 'N', 'system', NOW(), 'system', NOW())";
        jdbc.update(sql, tools.nextId("wxUser"), user.getOpenId(), user.getName(), user.getMobile(), user.getUserId(),
                user.getAuthStatus(), user.getRemark());
    }

    public void updateByOpenId(WxUser user) {
        StringBuilder sql = new StringBuilder("update t_wx_user set gmt_modified=NOW()");
        List<Object> list = new ArrayList<>();
        if (!StringUtils.isNullOrEmpty(user.getName())) {
            sql.append(",name=?");
            list.add(user.getName());
        }
        if (!StringUtils.isNullOrEmpty(user.getMobile())) {
            sql.append(",mobile=?");
            list.add(user.getMobile());
        }
        if (!StringUtils.isNullOrEmpty(user.getRemark())) {
            sql.append(",remark=?");
            list.add(user.getRemark());
        }
        if (!StringUtils.isNullOrEmpty(user.getAuthStatus())) {
            sql.append(",auth_status=?");
            list.add(user.getAuthStatus());
        }
        sql.append(" where open_id=?");
        list.add(user.getOpenId());
        jdbc.update(sql.toString(), list.toArray());
    }
}
