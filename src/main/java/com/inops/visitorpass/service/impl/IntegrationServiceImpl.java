package com.inops.visitorpass.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.inops.visitorpass.entity.Integration;
import com.inops.visitorpass.repository.IntegrationRepository;
import com.inops.visitorpass.service.IIntegrationService;

import lombok.RequiredArgsConstructor;

@Service("integrationService")
@RequiredArgsConstructor
public class IntegrationServiceImpl implements IIntegrationService {

	private final IntegrationRepository integrationRepository;

	@Override
	public Optional<List<Integration>> findAll() {

		return Optional.of(integrationRepository.findAll());
	}

	@Override
	public Integration save(Integration integration) {
		return integrationRepository.save(integration);
	}

	@Override
	public void delete(Integration integration) {
		integrationRepository.delete(integration);
	}

	@Override
	public void deleteAll(List<Integration> integrations) {
		integrationRepository.deleteAll(integrations);
	}

}
