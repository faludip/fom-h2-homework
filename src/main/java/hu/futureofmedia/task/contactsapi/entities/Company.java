package hu.futureofmedia.task.contactsapi.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "Company")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Company {
	
	@Id
	private Long id;
	private String name;
}
