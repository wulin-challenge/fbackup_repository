package com.bjhy.fbackup.client.statics;

import javax.sql.DataSource;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * 狱政的 NamedParameterJdbcTemplate
 * @author wubo
 */
public class YzNamedParameterJdbcTemplate extends NamedParameterJdbcTemplate{

	public YzNamedParameterJdbcTemplate(DataSource dataSource) {
		super(dataSource);
	}
	
	public YzNamedParameterJdbcTemplate(YzJdbcTemplate yzJdbcTemplate) {
		super(yzJdbcTemplate);
	}
	
}
