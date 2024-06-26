package com.kcs.community.repository.user;

import com.kcs.community.entity.RoleType;
import com.kcs.community.entity.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getLong("user_id"))
                .email(rs.getString("email"))
                .password(rs.getString("password"))
                .nickname(rs.getString("nickname"))
                .profileUrl(rs.getString("profile_url"))
                .role(RoleType.valueOf(rs.getString("role")))
                .build();
    }
}
