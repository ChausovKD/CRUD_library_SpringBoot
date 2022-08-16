package ru.ChausovKD.springcourse.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.ChausovKD.springcourse.models.Book;
import ru.ChausovKD.springcourse.models.Person;
import ru.ChausovKD.springcourse.services.BookService;
import ru.ChausovKD.springcourse.services.PeopleService;

import javax.validation.Valid;

@Controller
@RequestMapping("/books")
public class BookController {
    private final PeopleService peopleService;
    private final BookService bookService;

    @Autowired
    public BookController(PeopleService peopleService, BookService bookService) {
        this.peopleService = peopleService;
        this.bookService = bookService;
    }

    @GetMapping()
    public String index(@RequestParam(value = "page", required = false) Integer page,
                        @RequestParam(value = "books_per_page", required = false) Integer booksPerPage,
                        @RequestParam(value = "sort_by_year", required = false) boolean sortByYear,
                        Model model) {
        if (page == null || booksPerPage == null) {
            model.addAttribute("books", bookService.findAll(sortByYear));
        } else {
            model.addAttribute("books", bookService.findAllWithPagination(page, booksPerPage, sortByYear));
        }
        return "books/index";
    }

    @PostMapping()
    public String create(@ModelAttribute("book") @Valid Book book,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "books/new";
        }
        bookService.save(book);
        return "redirect:/books";
    }

    @GetMapping("/new")
    public String newBook(@ModelAttribute("book") Book book) {
        return "books/new";
    }

    @GetMapping("/{idBook}")
    public String show(@PathVariable("idBook") int idBook,
                       Model model,
                       @ModelAttribute("person") Person person) {
        model.addAttribute("book", bookService.findOne(idBook));

        Person bookOwner = bookService.findOwnerBook(idBook);

        if (bookOwner != null) {
            model.addAttribute("owner", bookOwner);
        } else {
            model.addAttribute("people", peopleService.findAll());
        }
        return "books/show";
    }

    @GetMapping("/{idBook}/edit")
    public String edit(Model model, @PathVariable("idBook") int idBook) {
        model.addAttribute("book", bookService.findOne(idBook));
        return "books/edit";
    }

    @PatchMapping("/{idBook}")
    public String update(@ModelAttribute("book") @Valid Book book,
                         BindingResult bindingResult,
                         @PathVariable("idBook") int idBook) {

        if (bindingResult.hasErrors()) {
            return "books/edit";
        }
        bookService.update(idBook, book);
        return "redirect:/books";
    }

    @DeleteMapping("/{idBook}")
    public String delete(@PathVariable("idBook") int idBook) {
        bookService.delete(idBook);
        return "redirect:/books";
    }

    @PatchMapping("/{idBook}/assign")
    public String assign(@PathVariable("idBook") int idBook,
                         @ModelAttribute("person") Person selectedPerson) {
        bookService.assign(idBook, selectedPerson);
        return "redirect:/books/" + idBook;
    }

    @PatchMapping("/{idBook}/release")
    public String release(@PathVariable("idBook") int idBook) {
        bookService.release(idBook);
        return "redirect:/books/" + idBook;
    }

    @GetMapping("/search")
    public String searchPage() {
        return "books/search";
    }

    @PostMapping("/search")
    public String makeSearch(Model model, @RequestParam("query") String query) {
        model.addAttribute("books", bookService.searchByTitle(query));
        return "/books/search";
    }
}
