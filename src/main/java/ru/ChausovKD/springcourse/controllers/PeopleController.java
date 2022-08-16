package ru.ChausovKD.springcourse.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.ChausovKD.springcourse.models.Person;
import ru.ChausovKD.springcourse.services.PeopleService;
import ru.ChausovKD.springcourse.util.PersonValidator;

import javax.validation.Valid;

@Controller
@RequestMapping("/people")
public class PeopleController {
    private final PeopleService peopleService;
    private final PersonValidator personValidator;

    @Autowired
    public PeopleController(PeopleService peopleService, PersonValidator personValidator) {
        this.peopleService = peopleService;
        this.personValidator = personValidator;
    }

    @GetMapping()
    public String index(Model model){
        model.addAttribute("people", peopleService.findAll());
        return "people/index";
    }

    @GetMapping("/new")
    public String newPerson(@ModelAttribute("person") Person person){
        return "people/new";
    }

    @PostMapping()
    public String create(@ModelAttribute("person") @Valid Person person,
                         BindingResult bindingResult) {

        personValidator.validate(person, bindingResult);

        if(bindingResult.hasErrors()) {
            return "people/new";
        }
        peopleService.save(person);
        return "redirect:/people";
    }

    @GetMapping("/{idPerson}")
    public String show(@PathVariable("idPerson") int idPerson,
                       Model model){
        model.addAttribute("person", peopleService.findOne(idPerson));
        model.addAttribute("books", peopleService.getBooksByPersonId(idPerson));
        return "people/show";
    }

    @DeleteMapping("/{idPerson}")
    public String delete(@PathVariable("idPerson") int idPerson){
        peopleService.delete(idPerson);
        return "redirect:/people";
    }

    @GetMapping("/{idPerson}/edit")
    public String edit(@PathVariable("idPerson") int idPerson,
                       Model model){
        model.addAttribute("person", peopleService.findOne(idPerson));
        return "people/edit";
    }

    @PatchMapping("/{idPerson}")
    public String update(@ModelAttribute("person") @Valid Person person,
                         BindingResult bindingResult,
                         @PathVariable("idPerson") int idPerson) {

        personValidator.validate(person, bindingResult);

        if(bindingResult.hasErrors()){
            return "people/edit";
        }
        peopleService.update(idPerson, person);
        return "redirect:/people";
    }
}

