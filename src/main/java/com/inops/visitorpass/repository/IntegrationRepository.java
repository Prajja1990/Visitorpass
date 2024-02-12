package com.inops.visitorpass.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inops.visitorpass.entity.Integration;

public interface IntegrationRepository extends JpaRepository<Integration, Long> {

}
