package hu.futureofmedia.task.contactsapi.dto;

import java.util.Date;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;


@Data
public class ContactPersonDTO {

	@NotEmpty(message = "Last name must be not empty!")
	private String firstName;

	@NotEmpty(message = "Last name must be not empty!")
	private String lastName;

	@NotNull
	@Email(message = "The email address is invalid.")
	private String email;

	private String phoneNumber;
	private String comment;
	private Date createdAt;
	private Date lastModified;

	@NotNull
	private String companyName;

}
