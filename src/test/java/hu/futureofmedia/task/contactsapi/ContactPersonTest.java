package hu.futureofmedia.task.contactsapi;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.google.gson.Gson;

import hu.futureofmedia.task.contactsapi.controllers.ContactPersonController;
import hu.futureofmedia.task.contactsapi.dto.ContactPersonDTO;
import hu.futureofmedia.task.contactsapi.dto.SimplifiedContactPersonDTO;
import hu.futureofmedia.task.contactsapi.entities.Company;
import hu.futureofmedia.task.contactsapi.entities.ContactPerson;
import hu.futureofmedia.task.contactsapi.services.ContactPersonService;

@RunWith(SpringRunner.class)
@WebMvcTest(ContactPersonController.class)
public class ContactPersonTest {

	@MockBean
	ContactPersonService contactService;

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ModelMapper modelMapper;

	private List<SimplifiedContactPersonDTO> createInitList() {
		List<SimplifiedContactPersonDTO> resultList = new ArrayList<>();
		for (Integer i = 1; i <= 5; i++) {
			SimplifiedContactPersonDTO contactPerson = new SimplifiedContactPersonDTO("Teszt Név " + i, i.toString(),
					"test@test.com", "+36 1 000 0000");
			resultList.add(contactPerson);
		}
		return resultList;
	}

	@Test
	public void createContactTest() throws Exception {
		Company company = new Company(1L, "company");
		ContactPersonDTO contact = createContact(company);
		Gson gson = new Gson();
		ContactPerson entity = modelMapper.map(contact, ContactPerson.class);
		entity.setCompany(company);
		entity.setId(1L);
		when(contactService.createContactPerson(contact)).thenReturn(entity);
		mvc.perform(post("/contacts").contentType("application/json").content(gson.toJson(contact).toString()))
				.andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
				.andExpect(jsonPath("$.firstName").value(contact.getFirstName()))
				.andExpect(jsonPath("$.lastName").value(contact.getLastName()))
				.andExpect(jsonPath("$.email").value(contact.getEmail()))
				.andExpect(jsonPath("$.phoneNumber").value(contact.getPhoneNumber()))
				.andExpect(jsonPath("$.comment").value(contact.getComment()))
				.andExpect(jsonPath("$.createdAt").value(contact.getCreatedAt()))
				.andExpect(jsonPath("$.lastModified").value(contact.getLastModified()))
				.andExpect(jsonPath("$.company.id").value(company.getId()))
				.andExpect(jsonPath("$.company.name").value(company.getName()));
	}

	@Test
	public void getContacts() throws Exception {
		Company company = new Company(1L, "company");
		List<SimplifiedContactPersonDTO> testContacts = createInitList();

		when(contactService.getActiveContacts(null, null, null, null, null, 0)).thenReturn(testContacts);
		mvc.perform(get("/contacts?page-number=1")).andDo(MockMvcResultHandlers.print())
				.andExpect(jsonPath("$", hasSize(5)));

		mvc.perform(get("/contacts")).andExpect(status().is4xxClientError());

		ContactPersonDTO contact = createContact(company);
		when(contactService.getDetailedContactPerson(1L)).thenReturn(contact);
		mvc.perform(get("/contacts/1").contentType("application/json")).andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk()).andExpect(jsonPath("$.firstName").value(contact.getFirstName()))
				.andExpect(jsonPath("$.lastName").value(contact.getLastName()))
				.andExpect(jsonPath("$.email").value(contact.getEmail()))
				.andExpect(jsonPath("$.phoneNumber").value(contact.getPhoneNumber()))
				.andExpect(jsonPath("$.comment").value(contact.getComment()))
				.andExpect(jsonPath("$.createdAt").value(contact.getCreatedAt()))
				.andExpect(jsonPath("$.lastModified").value(contact.getLastModified()))
				.andExpect(jsonPath("$.companyName").value(company.getName()));
	}

	@Test
	private void deleteContact() throws Exception {
		Company company = new Company(1L, "company");
		ContactPersonDTO contact = createContact(company);
		ContactPerson entity = modelMapper.map(contact, ContactPerson.class);
		entity.setCompany(company);
		entity.setId(1L);
		when(contactService.deleteContactPersonById(1L)).thenReturn(entity);
		mvc.perform(get("/contacts/1").contentType("application/json")).andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk()).andExpect(jsonPath("$.firstName").value(contact.getFirstName()))
				.andExpect(jsonPath("$.lastName").value(contact.getLastName()))
				.andExpect(jsonPath("$.email").value(contact.getEmail()))
				.andExpect(jsonPath("$.phoneNumber").value(contact.getPhoneNumber()))
				.andExpect(jsonPath("$.comment").value(contact.getComment()))
				.andExpect(jsonPath("$.createdAt").value(contact.getCreatedAt()))
				.andExpect(jsonPath("$.lastModified").value(contact.getLastModified()))
				.andExpect(jsonPath("$.company.id").value(company.getId()))
				.andExpect(jsonPath("$.company.name").value(company.getName()));
	}

	private ContactPersonDTO createContact(Company company) {
		ContactPersonDTO contact = new ContactPersonDTO();
		contact.setFirstName("John");
		contact.setLastName("Doe");
		contact.setComment("Teszt");
		contact.setCompanyName(company.getName());
		contact.setEmail("test@test.com");
		contact.setPhoneNumber("+36 30 123 4567");
		return contact;
	}
}
