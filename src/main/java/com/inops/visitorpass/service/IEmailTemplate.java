package com.inops.visitorpass.service;

import java.util.List;
import java.util.Optional;

import com.inops.visitorpass.entity.EmailTemplate;

public interface IEmailTemplate {

	Optional<List<EmailTemplate>> findAll();

	EmailTemplate save(EmailTemplate emailTemplate);

	void delete(EmailTemplate emailTemplate);

	void deleteAll(List<EmailTemplate> smailTemplates);
}
