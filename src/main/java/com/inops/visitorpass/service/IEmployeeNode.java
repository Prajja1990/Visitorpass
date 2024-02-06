package com.inops.visitorpass.service;

import java.util.List;
import java.util.Optional;

import com.inops.visitorpass.entity.EmployeeNode;

public interface IEmployeeNode {

	Optional<List<EmployeeNode>> findAll();
	
	EmployeeNode save(EmployeeNode employeeNode);

	void delete(EmployeeNode employeeNode);

	void deleteAll(List<EmployeeNode> employeeNodes);
}
