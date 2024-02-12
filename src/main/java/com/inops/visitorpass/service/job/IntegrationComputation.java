package com.inops.visitorpass.service.job;

import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.inops.visitorpass.entity.ScheduledTask;
import com.inops.visitorpass.service.IIntegrationDatabaseService;
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
public class IntegrationComputation implements IJob{
	
	private final IIntegrationService integrationService;
	private final IIntegrationDatabaseService integrationDatabaseService;
	private final DataSourceConfiguration dataSourceConfiguration;
	
	@PostConstruct
	public void init() {
		
	}

	@Override
	public void execute(Date from, Date to, ScheduledTask task) {
		
		JdbcTemplate template = dataSourceConfiguration.setDataSource(null);
		
		
	}

}
