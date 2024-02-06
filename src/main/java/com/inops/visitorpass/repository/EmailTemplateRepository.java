package com.inops.visitorpass.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inops.visitorpass.entity.EmailTemplate;

public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long> {

	
}
