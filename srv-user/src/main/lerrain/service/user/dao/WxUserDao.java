package lerrain.service.user.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    public Integer countWxUser(WxUser user) {
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder("select count(*) from t_wx_user where is_deleted='N' ");
        addParams(user, sql, params);
        return jdbc.queryForObject(sql.toString(), params.toArray(), Integer.class);
    }

    private void addParams(WxUser user, StringBuilder sql, List<Object> params) {
        if (!StringUtils.isNullOrEmpty(user.getMobile())) {
            sql.append("mobile = ? ");
            params.add(user.getMobile());
        }
        if (!StringUtils.isNullOrEmpty(user.getName())) {
            sql.append("name = ? ");
            params.add(user.getName());
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
                return wxUser;
            }

        });
    }

}
