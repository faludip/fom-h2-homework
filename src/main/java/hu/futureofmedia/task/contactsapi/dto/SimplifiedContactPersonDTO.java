package hu.futureofmedia.task.contactsapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimplifiedContactPersonDTO {
	
	private String name;
	private String companyName;
	private String email;
	private String phoneNumber;

}
