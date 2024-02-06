package com.inops.visitorpass.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.inops.visitorpass.entity.EmployeeNode;
import com.inops.visitorpass.repository.EmployeeNodeRepository;
import com.inops.visitorpass.service.IEmployeeNode;

import lombok.RequiredArgsConstructor;

@Service("employeeNodeService")
@RequiredArgsConstructor
public class EmployeeNodeService implements IEmployeeNode {

	private final EmployeeNodeRepository employeeNodeRepository;
	
	@Override
	public Optional<List<EmployeeNode>> findAll() {
		return Optional.of(employeeNodeRepository.findAll());
	}

	@Override
	public EmployeeNode save(EmployeeNode employeeNode) {
		return employeeNodeRepository.save(employeeNode);
	}

	@Override
	public void delete(EmployeeNode employeeNode) {
		employeeNodeRepository.delete(employeeNode);
	}

	@Override
	public void deleteAll(List<EmployeeNode> employeeNodes) {
		employeeNodeRepository.deleteAll(employeeNodes);
	}

}
