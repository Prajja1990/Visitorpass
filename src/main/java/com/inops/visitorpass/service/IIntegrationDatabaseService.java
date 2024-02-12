package com.inops.visitorpass.service;

import java.util.List;
import java.util.Optional;

import com.inops.visitorpass.entity.IntegrationDatabase;

public interface IIntegrationDatabaseService {

	Optional<List<IntegrationDatabase>> findAll();

	IntegrationDatabase save(IntegrationDatabase integrationDatabases);

	void delete(IntegrationDatabase integrationDatabase);

	void deleteAll(List<IntegrationDatabase> integrationDatabases);
}
