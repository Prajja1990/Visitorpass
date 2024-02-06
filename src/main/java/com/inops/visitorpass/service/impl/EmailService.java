package com.inops.visitorpass.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.inops.visitorpass.entity.EMail;
import com.inops.visitorpass.repository.EmailRepository;
import com.inops.visitorpass.service.IEmail;

import lombok.RequiredArgsConstructor;

@Service("emailService")
@RequiredArgsConstructor
public class EmailService implements IEmail {

	private final EmailRepository emailRepository;

	@Override
	public EMail save(EMail eMail) {
		return emailRepository.save(eMail);
	}

	@Override
	public void delete(EMail eMail) {
		emailRepository.delete(eMail);
	}

	@Override
	public void deleteAll(List<EMail> eMail) {
		emailRepository.deleteAll(eMail);
	}

	@Override
	public Optional<List<EMail>> findAll() {
		return Optional.of(emailRepository.findAll());
	}

}
