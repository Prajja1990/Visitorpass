package com.inops.visitorpass.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.inops.visitorpass.entity.EMail;
import com.inops.visitorpass.entity.EmailTemplate;
import com.inops.visitorpass.repository.EmailTemplateRepository;
import com.inops.visitorpass.service.IEmail;
import com.inops.visitorpass.service.IEmailTemplate;

import lombok.RequiredArgsConstructor;

@Service("emailTemplateService")
@RequiredArgsConstructor
public class EmailTemplateService implements IEmailTemplate {

	private final EmailTemplateRepository emailTemplateRepository;

	@Override
	public Optional<List<EmailTemplate>> findAll() {
		return Optional.of(emailTemplateRepository.findAll());
	}

	@Override
	public EmailTemplate save(EmailTemplate emailTemplate) {
		return emailTemplateRepository.save(emailTemplate);
	}

	@Override
	public void delete(EmailTemplate emailTemplate) {
		emailTemplateRepository.delete(emailTemplate);
	}

	@Override
	public void deleteAll(List<EmailTemplate> smailTemplates) {
		emailTemplateRepository.deleteAll(smailTemplates);
	}

}
