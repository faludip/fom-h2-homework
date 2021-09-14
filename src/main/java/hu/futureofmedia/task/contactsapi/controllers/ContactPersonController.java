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
 * @author faludi.peter A Kapcsolattartó mûveletekért felelelõs osztály
 */
@RestController
public class ContactPersonController {

	@Autowired
	private ContactPersonService contactService;

	/**
	 * Az aktív kapcsolattartókat listázó függvény.Az alábbi paraméterek
	 * opcionálisak, szûrésre vannak használva, csak az aktív státuszú
	 * kapcsolattartókat jeleníti meg, név szerint ABC sorrendben és egyszerre
	 * mindig tízet lehetõség van oldalszámonént újabb 10 kapcsolatot lekérni.
	 * Kapcsolatonként megjelenített adatok: Teljes név, Cég neve (hozzárendelt
	 * céghez tartozó név),E-mail cím,Telefonszám
	 * 
	 * @param firstName   a kapcsolattartó keresztneve
	 * @param lastName    a kapcsolattartó vezetékneve
	 * @param email       a kapcsolattartó emailcíme
	 * @param phoneNumber a kapcsolattartó telefonszáma
	 * @param comment     a kapcsolattartóhoz tartozó megjegyzés
	 * @param pageNumber  aktuális, megtekintett oldal
	 * @return oldalszám szerint 10 aktív státuszú kapcsolattartó listája
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
	 * Az alkalmazás törlési kérést generál. A rendszer megkeresi a kiválasztott
	 * kapcsolattartót és a státuszát töröltre állítja.
	 * 
	 * @param id A kapcsolattartó azonosítója.
	 * @return A kapcsolat tartó adataival tér vissza.
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
	 * A felhasználó módosítani szeretné a kapcsolattartó adatait, az erre szolgáló
	 * függvény.
	 * 
	 * @param id  A kapcsolattartó azonosítója.
	 * @param dto a kapcsolattartó adatainak tárolsára szolgaló objektum, tartalma :
	 *            Vezetéknév, szöveges beviteli mezõ; Keresztnév, szöveges beviteli
	 *            mezõ; Cég, kiválasztás legördülõ menüvel; E-mail cím, szöveges
	 *            beviteli mezõ; Telefonszám, szöveges beviteli mezõ; Megjegyzés,
	 *            szöveges beviteli mezõ, többsoros
	 * @return Az új kapcsolattartó adatai.
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
	 * Az alkalmazás lekérdezi a kiválasztott kapcsolattartozóhoz tartozó részletek
	 * és megjelenítiegy tetszõleges elrendezésben. A megjelenített
	 * adatok:Vezetéknév,Keresztnév,Cég neve,E-mail
	 * cím,Telefonszám,Megjegyzés,Létrehozás ideje,Utolsó módosítás ideje
	 * 
	 * @param id A kapcsolattartó azonosítója.
	 * @return A kapcsolattartó adatai
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
	 * A felhasználó létre szeretne hozni egy új kapcsolattartót, az erre szolgáló
	 * függvény.
	 * 
	 * @param dto a kapcsolattartó adatainak tárolsára szolgaló objektum, tartalma :
	 *            Vezetéknév, szöveges beviteli mezõ; Keresztnév, szöveges beviteli
	 *            mezõ; Cég, kiválasztás legördülõ menüvel; E-mail cím, szöveges
	 *            beviteli mezõ; Telefonszám, szöveges beviteli mezõ; Megjegyzés,
	 *            szöveges beviteli mezõ, többsoros
	 * @return Az új kapcsolattartó adatai.
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
