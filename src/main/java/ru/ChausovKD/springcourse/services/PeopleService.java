package ru.ChausovKD.springcourse.services;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ChausovKD.springcourse.models.Book;
import ru.ChausovKD.springcourse.models.Person;
import ru.ChausovKD.springcourse.repositories.PeopleRepository;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PeopleService {
    private final PeopleRepository peopleRepository;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    public List<Person> findAll() {
        return peopleRepository.findAll();
    }

    public Person findOne(int idPerson) {
        Optional<Person> foundPerson = peopleRepository.findById(idPerson);
        return foundPerson.orElse(null);
    }

    @Transactional
    public void save(Person person) {
        peopleRepository.save(person);
    }

    @Transactional
    public void update(int idPerson, Person updatedPerson) {
        updatedPerson.setIdPerson(idPerson);
        peopleRepository.save(updatedPerson);
    }

    @Transactional
    public void delete(int idPerson) {
        peopleRepository.deleteById(idPerson);
    }

    @Transactional
    public List<Book> getBooksByPersonId(int idPerson) {
        Optional<Person> person = peopleRepository.findById(idPerson);
        if (person.isPresent()) {
            Hibernate.initialize(person.get().getBooks());
            person.get().getBooks().forEach(book -> {
                long diffInMillies = Math.abs(book.getTakenAt().getTime() - new Date().getTime());
                if (diffInMillies > 864000000) { // 10 суток = 864000000
                    book.setExpired(true); // срок истёк
                }
            });
            return person.get().getBooks();
        } else {
            return Collections.emptyList();
        }
    }

    public List<Person> findByFullName(String fullName) {
        return peopleRepository.findByFullName(fullName);
    }
}
