package com.inops.visitorpass.service.job;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

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

	public void insertEntity(JdbcTemplate template, List<Map<String, Object>> list, Integration integration) {
		// Construct the SQL query with dynamic column names
		// String columns = String.join(", ", data.keySet());
		String values = integration.getDestinationFields().trim();
		/*
		 * for (Map<String, Object> data : list) { values = ":" +String.join(", :",
		 * data.keySet()); }
		 */
		String sql = String.format(integration.getDestinationIntegrationQuery(), values);

		// Execute the insert query with the dynamic data
		if (!values.isEmpty())
			template.update(sql,list.get(0));
	}

}
