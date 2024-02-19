package com.inops.visitorpass.util;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;

import com.inops.visitorpass.entity.Integration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
//@AllArgsConstructor
@Service("dataSourceConfiguration")
public class DataSourceConfiguration {

    public JdbcTemplate setDataSource(Integration integration) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(integration.getDriverClassName());
        dataSource.setUrl(integration.getUrl());
        dataSource.setUsername(integration.getUsername());
        dataSource.setPassword(integration.getPassword());
        return new JdbcTemplate(dataSource);
    }

    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
