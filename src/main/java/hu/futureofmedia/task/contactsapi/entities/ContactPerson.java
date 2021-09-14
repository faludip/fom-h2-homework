package hu.futureofmedia.task.contactsapi.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ContactPerson {

	@GeneratedValue
	@Id
	private Long id;
	
	@NotEmpty(message = "First name must be not empty!")
	private String firstName;
	
	@NotEmpty(message = "Last name must be not empty!")
	private String lastName;

	@Column(unique = true)
	@NotEmpty
	@NotEmpty(message = "Last email must be not empty!")
	@Email(message = "The email address is invalid.")
	private String email;
	
	@Column(unique = true)
	private String phoneNumber;

	@OneToOne
	private Company company;

	@Column(columnDefinition = "TEXT")
	private String comment;

	@Enumerated(EnumType.STRING)
	private Status status;

	private Date createdAt;
	private Date lastModified;

	public ContactPerson(Long id, String firstName, String lastName, String email, String phoneNumber, Company company,
			String comment, Status status) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.company = company;
		this.comment = comment;
		this.status = status;
		this.createdAt = new Date();
	}

}
