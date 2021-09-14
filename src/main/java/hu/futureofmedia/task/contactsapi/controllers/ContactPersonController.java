package hu.futureofmedia.task.contactsapi.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.google.i18n.phonenumbers.NumberParseException;

import hu.futureofmedia.task.contactsapi.dto.ContactPersonDTO;
import hu.futureofmedia.task.contactsapi.dto.SimplifiedContactPersonDTO;
import hu.futureofmedia.task.contactsapi.entities.ContactPerson;
import hu.futureofmedia.task.contactsapi.exceptions.ContactPersonNotFoundException;
import hu.futureofmedia.task.contactsapi.services.ContactPersonService;

/**
 * @author faludi.peter A Kapcsolattart� m�veletek�rt felelel�s oszt�ly
 */
@RestController
public class ContactPersonController {

	@Autowired
	private ContactPersonService contactService;

	/**
	 * Az akt�v kapcsolattart�kat list�z� f�ggv�ny.Az al�bbi param�terek
	 * opcion�lisak, sz�r�sre vannak haszn�lva, csak az akt�v st�tusz�
	 * kapcsolattart�kat jelen�ti meg, n�v szerint ABC sorrendben �s egyszerre
	 * mindig t�zet lehet�s�g van oldalsz�mon�nt �jabb 10 kapcsolatot lek�rni.
	 * Kapcsolatonk�nt megjelen�tett adatok: Teljes n�v, C�g neve (hozz�rendelt
	 * c�ghez tartoz� n�v),E-mail c�m,Telefonsz�m
	 * 
	 * @param firstName   a kapcsolattart� keresztneve
	 * @param lastName    a kapcsolattart� vezet�kneve
	 * @param email       a kapcsolattart� emailc�me
	 * @param phoneNumber a kapcsolattart� telefonsz�ma
	 * @param comment     a kapcsolattart�hoz tartoz� megjegyz�s
	 * @param pageNumber  aktu�lis, megtekintett oldal
	 * @return oldalsz�m szerint 10 akt�v st�tusz� kapcsolattart� list�ja
	 */
	@GetMapping("/contacts")
	public ResponseEntity<List<SimplifiedContactPersonDTO>> listContacts(
			@RequestParam(value = "first-name", required = false) String firstName,
			@RequestParam(value = "last-name", required = false) String lastName,
			@RequestParam(value = "email", required = false) String email,
			@RequestParam(value = "phone-number", required = false) String phoneNumber,
			@RequestParam(value = "comment", required = false) String comment,
			@RequestParam(value = "page-number") int pageNumber) {
		List<SimplifiedContactPersonDTO> result = contactService.getActiveContacts(firstName, lastName, email,
				phoneNumber, comment, pageNumber - 1);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	/**
	 * Az alkalmaz�s t�rl�si k�r�st gener�l. A rendszer megkeresi a kiv�lasztott
	 * kapcsolattart�t �s a st�tusz�t t�r�ltre �ll�tja.
	 * 
	 * @param id A kapcsolattart� azonos�t�ja.
	 * @return A kapcsolat tart� adataival t�r vissza.
	 */
	@DeleteMapping("/contacts/{id}")
	public ResponseEntity<ContactPerson> deleteContactById(@PathVariable(name = "id") Long id) {
		try {
			return new ResponseEntity<>(contactService.deleteContactPersonById(id), HttpStatus.OK);
		} catch (ContactPersonNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}

	}

	/**
	 * A felhaszn�l� m�dos�tani szeretn� a kapcsolattart� adatait, az erre szolg�l�
	 * f�ggv�ny.
	 * 
	 * @param id  A kapcsolattart� azonos�t�ja.
	 * @param dto a kapcsolattart� adatainak t�rols�ra szolgal� objektum, tartalma :
	 *            Vezet�kn�v, sz�veges beviteli mez�; Keresztn�v, sz�veges beviteli
	 *            mez�; C�g, kiv�laszt�s leg�rd�l� men�vel; E-mail c�m, sz�veges
	 *            beviteli mez�; Telefonsz�m, sz�veges beviteli mez�; Megjegyz�s,
	 *            sz�veges beviteli mez�, t�bbsoros
	 * @return Az �j kapcsolattart� adatai.
	 */
	@PostMapping("/contacts/{id}")
	public ResponseEntity<ContactPerson> deleteContactById(@PathVariable(name = "id") Long id,
			@RequestBody ContactPersonDTO dto) {
		try {
			return new ResponseEntity<>(contactService.updateContactPerson(id, dto), HttpStatus.OK);
		} catch (ContactPersonNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}

	}

	/**
	 * Az alkalmaz�s lek�rdezi a kiv�lasztott kapcsolattartoz�hoz tartoz� r�szletek
	 * �s megjelen�tiegy tetsz�leges elrendez�sben. A megjelen�tett
	 * adatok:Vezet�kn�v,Keresztn�v,C�g neve,E-mail
	 * c�m,Telefonsz�m,Megjegyz�s,L�trehoz�s ideje,Utols� m�dos�t�s ideje
	 * 
	 * @param id A kapcsolattart� azonos�t�ja.
	 * @return A kapcsolattart� adatai
	 */
	@GetMapping("/contacts/{id}")
	public ResponseEntity<ContactPersonDTO> findDetailedContact(@PathVariable(name = "id") Long id) {
		try {
			return new ResponseEntity<>(contactService.getDetailedContactPerson(id), HttpStatus.OK);
		} catch (ContactPersonNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	/**
	 * A felhaszn�l� l�tre szeretne hozni egy �j kapcsolattart�t, az erre szolg�l�
	 * f�ggv�ny.
	 * 
	 * @param dto a kapcsolattart� adatainak t�rols�ra szolgal� objektum, tartalma :
	 *            Vezet�kn�v, sz�veges beviteli mez�; Keresztn�v, sz�veges beviteli
	 *            mez�; C�g, kiv�laszt�s leg�rd�l� men�vel; E-mail c�m, sz�veges
	 *            beviteli mez�; Telefonsz�m, sz�veges beviteli mez�; Megjegyz�s,
	 *            sz�veges beviteli mez�, t�bbsoros
	 * @return Az �j kapcsolattart� adatai.
	 */
	@PostMapping("/contacts")
	public ResponseEntity<ContactPerson> createNewContactPerson(@RequestBody ContactPersonDTO dto) {
		ContactPerson entity;
		try {
			entity = contactService.createContactPerson(dto);
			return new ResponseEntity<>(entity, HttpStatus.OK);
		} catch (NumberParseException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}

	}

}
