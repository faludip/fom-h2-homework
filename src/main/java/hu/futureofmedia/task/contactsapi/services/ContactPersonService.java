package hu.futureofmedia.task.contactsapi.services;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import hu.futureofmedia.task.contactsapi.dto.ContactPersonDTO;
import hu.futureofmedia.task.contactsapi.dto.SimplifiedContactPersonDTO;
import hu.futureofmedia.task.contactsapi.entities.Company;
import hu.futureofmedia.task.contactsapi.entities.ContactPerson;
import hu.futureofmedia.task.contactsapi.entities.Status;
import hu.futureofmedia.task.contactsapi.exceptions.ContactPersonNotFoundException;
import hu.futureofmedia.task.contactsapi.repositories.CompanyRepository;
import hu.futureofmedia.task.contactsapi.repositories.ContactPersonReporsitory;

@Service
public class ContactPersonService {

	private CompanyRepository companyRepository;
	private ContactPersonReporsitory contactRepo;
	private static PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	public void setContactRepo(ContactPersonReporsitory contactRepo) {
		this.contactRepo = contactRepo;
	}

	@Autowired
	public void setCompanyRepository(CompanyRepository companyRepository) {
		this.companyRepository = companyRepository;
	}

	public List<ContactPerson> getContacts() {
		return contactRepo.findAll();
	}

	public List<SimplifiedContactPersonDTO> getActiveContacts(String firstName, String lastName, String email,
			String phoneNumber, String comment, int pageNumber) {
		Pageable page = PageRequest.of(pageNumber, 10,
				Sort.by(Sort.Order.asc("firstName"), Sort.Order.asc("lastName")));
		return contactRepo.findAllActive(firstName, lastName, email, phoneNumber, comment, page).getContent().stream()
				.map(this::transformContactEntityToTableView).collect(Collectors.toList());
	}

	public ContactPerson deleteContactPersonById(Long id) throws ContactPersonNotFoundException {
		throwNotFoundContactException(id);
		ContactPerson contactPerson = contactRepo.findById(id).get();
		contactPerson.setStatus(Status.DELETED);
		contactRepo.save(contactPerson);
		return contactPerson;
	}

	public ContactPersonDTO getDetailedContactPerson(Long id) throws ContactPersonNotFoundException {
		throwNotFoundContactException(id);
		return transformContactEntityToDetailedView(contactRepo.findById(id).get());
	}

	public ContactPerson createContactPerson(ContactPersonDTO dto) throws NumberParseException {
		ContactPerson entity = transformContactDTOToEntity(dto);
		isValidPhoneNumber(dto.getPhoneNumber());
		entity.setStatus(Status.ACTIVE);
		entity.setCreatedAt(new Date());
		contactRepo.save(entity);
		return entity;
	}

	public ContactPerson updateContactPerson(Long id, ContactPersonDTO dto)
			throws NumberParseException, ContactPersonNotFoundException {
		throwNotFoundContactException(id);
		ContactPerson oldEntity = contactRepo.findById(id).get();
		ContactPerson entity = transformContactDTOToEntity(dto);
		isValidPhoneNumber(dto.getPhoneNumber());
		entity.setLastModified(new Date());
		entity.setId(id);
		entity.setCreatedAt(oldEntity.getCreatedAt());
		entity.setStatus(oldEntity.getStatus());
		contactRepo.save(entity);
		return entity;
	}

	private SimplifiedContactPersonDTO transformContactEntityToTableView(ContactPerson entity) {
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		SimplifiedContactPersonDTO dto = modelMapper.map(entity, SimplifiedContactPersonDTO.class);
		dto.setName(entity.getFirstName() + " " + entity.getLastName());
		dto.setCompanyName(entity.getCompany().getName());
		return dto;
	}

	private ContactPersonDTO transformContactEntityToDetailedView(ContactPerson entity) {
		ContactPersonDTO dto = modelMapper.map(entity, ContactPersonDTO.class);
		dto.setCompanyName(entity.getCompany().getName());
		return dto;
	}

	private ContactPerson transformContactDTOToEntity(ContactPersonDTO dto) {
		ContactPerson entity = modelMapper.map(dto, ContactPerson.class);
		entity.setCompany(companyRepository.findByName(dto.getCompanyName()));
		return entity;
	}

	private boolean isValidPhoneNumber(String phoneNumber) throws NumberParseException {
		Phonenumber.PhoneNumber phone = phoneNumberUtil.parse(phoneNumber, "HU");
		return phoneNumberUtil.isValidNumberForRegion(phone, "HU");
	}

	private void throwNotFoundContactException(Long id) throws ContactPersonNotFoundException {
		if (contactRepo.findById(id).get() == null)
			throw new ContactPersonNotFoundException("Contact person was not found with this ID");
	}

	@PostConstruct
	private void init() {
		for (Integer i = 1; i <= 20; i++) {
			Company company = new Company(new Long(i), "company" + i.toString());
			ContactPerson contactPerson = new ContactPerson(null, (100 - i) + "First" + i, "Last" + i,
					i + "asd@gmail.com", "asd" + i, company, null, Status.ACTIVE);
			companyRepository.save(company);
			contactRepo.save(contactPerson);

		}
	}

}
