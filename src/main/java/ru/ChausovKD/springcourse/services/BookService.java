package ru.ChausovKD.springcourse.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ChausovKD.springcourse.models.Book;
import ru.ChausovKD.springcourse.models.Person;
import ru.ChausovKD.springcourse.repositories.BooksRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BookService {
    private final BooksRepository booksRepository;

    @Autowired
    public BookService(BooksRepository booksRepository) {
        this.booksRepository = booksRepository;
    }

    public List<Book> findAll(boolean sortByYear) {
        if (sortByYear) {
            return booksRepository.findAll(Sort.by("yearOfWriting"));
        }
        return booksRepository.findAll();
    }

    public List<Book> findAllWithPagination(int page, int booksPerPage, boolean sortByYear) {
        if (sortByYear) {
            return booksRepository.findAll(PageRequest.of(page, booksPerPage, Sort.by("yearOfWriting"))).getContent();
        }
        return booksRepository.findAll(PageRequest.of(page, booksPerPage)).getContent();
    }

    public Book findOne(int idBook) {
        Optional<Book> foundBook = booksRepository.findById(idBook);
        return foundBook.orElse(null);
    }

    @Transactional
    public List<Book> searchByTitle(String query) {
        return booksRepository.findBookByTitleStartingWith(query);
    }

    @Transactional
    public void save(Book book) {
        booksRepository.save(book);
    }

    @Transactional
    public void update(int idBook, Book updatedBook) {
        Book bookToBeUpdated = booksRepository.findById(idBook).get();
        updatedBook.setIdBook(idBook);
        updatedBook.setOwner(bookToBeUpdated.getOwner());

        booksRepository.save(updatedBook);
    }

    @Transactional
    public void delete(int idBook) {
        booksRepository.deleteById(idBook);
    }

    public Person findOwnerBook(int idBook) {
//        Optional<Book> book = booksRepository.findById(idBook);
////        Hibernate.initialize(book.get().getOwner());
//        return Optional.ofNullable(book.get().getOwner());
        return booksRepository.findById(idBook).map(Book::getOwner).orElse(null);
    }

    @Transactional
    public void release(int idBook) {
        booksRepository.findById(idBook).ifPresent(
                book -> {
                    book.setOwner(null);
                    book.setTakenAt(null);
                });
    }

    @Transactional
    public void assign(int idBook, Person selectedPerson) {
        booksRepository.findById(idBook).ifPresent(
                book -> {
                    book.setOwner(selectedPerson);
                    book.setTakenAt(new Date());
                });
    }
}
