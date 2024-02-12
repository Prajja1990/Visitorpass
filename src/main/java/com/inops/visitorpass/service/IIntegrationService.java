package com.inops.visitorpass.service;

import java.util.List;
import java.util.Optional;

import com.inops.visitorpass.entity.Integration;

public interface IIntegrationService {

	Optional<List<Integration>> findAll();

	Integration save(Integration integration);

	void delete(Integration integration);

	void deleteAll(List<Integration> integrations);
}
