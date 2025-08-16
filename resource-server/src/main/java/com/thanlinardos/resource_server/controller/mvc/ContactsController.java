package com.thanlinardos.resource_server.controller.mvc;

import com.thanlinardos.resource_server.model.info.Contact;
import com.thanlinardos.resource_server.service.ContactService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@Slf4j
public class ContactsController {

    public static final String CONTACT = "contact";
    private final ContactService contactService;

    @Autowired
    public ContactsController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping("/contacts")
    public String displayContactPage(Model model) {
        model.addAttribute(CONTACT, new Contact());
        return CONTACT;
    }

    @PostMapping(value = "/saveMsg")
    public String saveMessage(@Valid @ModelAttribute("contact") Contact contact, Errors errors) {
        if(errors.hasErrors()){
            log.error("Contact form validation failed due to : {}", errors);
            return CONTACT;
        }
        contactService.saveMessageDetails(contact);
        contactService.setCounter(contactService.getCounter()+1);
        log.info("Number of times the Contact form is submitted : {}", contactService.getCounter());
        return "redirect:/contacts";
    }
}
