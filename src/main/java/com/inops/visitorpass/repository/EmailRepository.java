package com.inops.visitorpass.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inops.visitorpass.entity.EMail;

public interface EmailRepository extends JpaRepository<EMail, Long> {

	
}
