package com.inops.visitorpass.service.job;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.inops.visitorpass.entity.Integration;
import com.inops.visitorpass.entity.ScheduledTask;
import com.inops.visitorpass.service.IIntegrationService;
import com.inops.visitorpass.util.DataSourceConfiguration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Getter
@Setter
@Log4j2
@Service("integrationComputation")
@RequiredArgsConstructor
public class IntegrationComputation implements IJob {

	private final IIntegrationService integrationService;
	private final DataSourceConfiguration dataSourceConfiguration;
	private final RestTemplate restTemplate;

	private List<Integration> integrations;

	@PostConstruct
	public void init() {

	}

	@Override
	public void execute(Date from, Date to, ScheduledTask task) {

		integrations = integrationService.findAll().get();

		integrations.forEach(integratoin -> {
			JdbcTemplate template = dataSourceConfiguration.setDataSource(integratoin);
			List<Map<String, Object>> list = template.queryForList(integratoin.getSourceIntegrationQuery());
			insertEntity(template, list, integratoin);
		});

	}

	public void insertEntity(JdbcTemplate template, List<Map<String, Object>> data, Integration integration) {
		// Construct the SQL query with dynamic column names
		String columns = integration.getDestinationFields();/// String.join(", ", data.keySet());
		String sql = String.format(integration.getDestinationIntegrationQuery(), columns);

		// Execute the insert query with the dynamic data
		if (integration.getSourceApi() != null) {
			restTemplate.exchange(integration.getSourceApi(), HttpMethod.GET, null,
					new ParameterizedTypeReference<List<Object>>() {
					}).getBody();
		}

		template.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {

				Map<String, Object> map = data.get(i);
				int index = 1;
				for (Map.Entry<String, Object> entry : map.entrySet()) {

					if (entry.getValue() instanceof String) {
						preparedStatement.setString(index, (String) entry.getValue());
					}
					if (entry.getValue() instanceof Long) {
						preparedStatement.setLong(index, (Long) entry.getValue());
					}
					if (entry.getValue() instanceof Integer) {
						preparedStatement.setInt(index, (Integer) entry.getValue());
					}

					if (entry.getValue() instanceof java.sql.Date) {
						preparedStatement.setDate(index, (java.sql.Date) entry.getValue());
					}

					index++;
				}

			}

			@Override
			public int getBatchSize() {
				return data.size();
			}
		});
	}

}
