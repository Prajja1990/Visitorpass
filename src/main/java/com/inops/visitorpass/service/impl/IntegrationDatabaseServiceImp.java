package com.inops.visitorpass.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.inops.visitorpass.entity.IntegrationDatabase;
import com.inops.visitorpass.repository.IntegrationDatabaseRepository;
import com.inops.visitorpass.service.IIntegrationDatabaseService;

import lombok.RequiredArgsConstructor;

@Service("integrationDatabaseService")
@RequiredArgsConstructor
public class IntegrationDatabaseServiceImp implements IIntegrationDatabaseService {

	private final IntegrationDatabaseRepository integrationDatabaseRepository;

	@Override
	public Optional<List<IntegrationDatabase>> findAll() {
		return Optional.of(integrationDatabaseRepository.findAll());
	}

	@Override
	public IntegrationDatabase save(IntegrationDatabase integrationDatabases) {
		return integrationDatabaseRepository.save(integrationDatabases);
	}

	@Override
	public void delete(IntegrationDatabase integrationDatabase) {
		integrationDatabaseRepository.delete(integrationDatabase);
	}

	@Override
	public void deleteAll(List<IntegrationDatabase> integrationDatabases) {
		integrationDatabaseRepository.deleteAll(integrationDatabases);
	}
}
