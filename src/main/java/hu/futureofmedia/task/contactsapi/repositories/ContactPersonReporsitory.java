package hu.futureofmedia.task.contactsapi.repositories;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import hu.futureofmedia.task.contactsapi.entities.ContactPerson;
import hu.futureofmedia.task.contactsapi.entities.Status;

@Repository
public interface ContactPersonReporsitory extends JpaRepository<ContactPerson, Long> {
	
	List<ContactPerson> findAll();
	
	@Query("SELECT c from ContactPerson c where c.status = 'ACTIVE' " +
            "AND (:firstName is null or c.firstName = :firstName) " +
            "AND (:lastName is null or c.lastName = :lastName) " +
            "AND (:email is null or c.email = :email) " +
            "AND (:phoneNumber is null or c.phoneNumber = :phoneNumber) " +
            "AND (:comment is null or c.comment = :comment)")
    Page<ContactPerson> findAllActive(
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("email") String email,
            @Param("phoneNumber") String phoneNumber,
            @Param("comment") String description,
            Pageable pageable);
	
	ContactPerson getById(Long id);
	
}
