package org.lessons.java.pizzeria.controller;

import jakarta.validation.Valid;
import org.lessons.java.pizzeria.model.Pizza;
import org.lessons.java.pizzeria.service.PizzaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/pizzas")
public class PizzaController {
    //    @Autowired
//    private PizzaRepository pizzaRepository;
    @Autowired
    private PizzaService pizzaService;

    @GetMapping
    public String index(Model model, @RequestParam(name = "q") Optional<String> keyword) {
        List<Pizza> pizzas;
        if (keyword.isEmpty()) {
            pizzas = pizzaService.getAllPizzas(keyword);
        } else {
            pizzas = pizzaService.getFilteredPizzas(keyword.get());
            model.addAttribute("keyword", keyword.get());
        }
        model.addAttribute("list", pizzas);

        return "/pizzas/index";
    }

    @GetMapping("/{pizzaId}")
    public String show(@PathVariable("pizzaId") Integer id, Model model) {
        try {
            Pizza pizza = pizzaService.getById(id);
            model.addAttribute("pizza", pizza);
            return "pizzas/show";

        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pizza with id " + id + " not found");

        }
//        Optional<Pizza> result = pizzaRepository.findById(id);
//        if (result.isPresent()) {
//            model.addAttribute("pizza", result.get());
//            return "pizzas/show";
//        } else {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pizza with id " + id + " not found");
//        }

    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("pizza", new Pizza());
        return "/pizzas/create";
    }

    @PostMapping("/create")
    public String doCreate(@Valid @ModelAttribute("pizza") Pizza formPizza, BindingResult bindingResult) {
        //Validazione
        boolean hasErrors = bindingResult.hasErrors();
        //custom name validation
        if (!pizzaService.validName(formPizza)) {
            //aggiungo un errore al binding result
            bindingResult.addError(new FieldError("pizza", "name", formPizza.getName(), false, null, null, "Esiste già una pizza con questo nome"));
            hasErrors = true;
        }
        if (hasErrors) {
            //ritorno la view con il form
            return "pizzas/create";
        }
        //se non ci sono errori lo persisto
        pizzaService.createPizza(formPizza);
        return "redirect:/pizzas";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        try {
            Pizza pizza = pizzaService.getById(id);
            model.addAttribute("pizza", pizza);
            return "/pizzas/edit";
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "pizza with id " + id + " not found");
        }
    }

    @PostMapping("/edit/{id}")
    public String doEdit(@PathVariable Integer id, @Valid @ModelAttribute("pizza") Pizza formPizza, BindingResult bindingResult) {
        //Validazioni
        if (!pizzaService.validName(formPizza)) {
            //aggiungo un errore al binding result
            bindingResult.addError(new FieldError("pizza", "name", formPizza.getName(), false, null, null, "Esiste già una pizza con questo nome"));
        }
        if (bindingResult.hasErrors()) {
            //ricreo la view precompilata
            return "/pizzas/edit";
        }
        //persisto la pizza
        try {
            Pizza updatePizza = pizzaService.updatePizza(formPizza, id);
            return "redirect:/pizzas/" + Integer.toString(updatePizza.getId());
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "pizza with id " + id + " not found");
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            boolean success = pizzaService.deleteById(id);
            if (success) {
                redirectAttributes.addFlashAttribute("message", "Pizza with id " + id + " deleted");

            } else {
                redirectAttributes.addFlashAttribute("message", "Unable to deleted the pizza with id " + id);

//                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to delete the pizza");
            }
        } catch (Exception e) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            redirectAttributes.addFlashAttribute("message", "Pizza with id " + id + " not found");


        }
        return "redirect:/pizzas";

    }

}
