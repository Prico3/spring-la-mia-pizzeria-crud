package org.lessons.java.pizzeria.service;

import org.lessons.java.pizzeria.model.Pizza;
import org.lessons.java.pizzeria.repository.PizzaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PizzaService {
    @Autowired
    PizzaRepository pizzaRepository;

    public Pizza createPizza(Pizza formPizza) {
        Pizza pizzaToPersist = new Pizza();
        pizzaToPersist.setName(formPizza.getName());
        pizzaToPersist.setPrice(formPizza.getPrice());
        pizzaToPersist.setDescription(formPizza.getDescription());
        pizzaToPersist.setCratedAt(LocalDateTime.now());
        return pizzaRepository.save(pizzaToPersist);
    }

    public Pizza updatePizza(Pizza formPizza, Integer id) throws RuntimeException {
        Pizza pizzaToUpdate = getById(id);
        pizzaToUpdate.setName(formPizza.getName());
        pizzaToUpdate.setDescription(formPizza.getDescription());
        pizzaToUpdate.setPrice(formPizza.getPrice());
        return pizzaRepository.save(pizzaToUpdate);
    }

    public List<Pizza> getAllPizzas(Optional<String> keyword) {

        return pizzaRepository.findAll(Sort.by("name"));

    }

    public List<Pizza> getFilteredPizzas(String keyword) {
        return pizzaRepository.findByNameContainingIgnoreCase(keyword);
    }

    public Pizza getById(Integer id) throws RuntimeException {
        Optional<Pizza> result = pizzaRepository.findById(id);
        if (result.isPresent()) {
            return result.get();
        } else {
            throw new RuntimeException("Pizza with id " + id + " not found");
        }
    }

    public boolean deleteById(Integer id) {
        pizzaRepository.findById(id).orElseThrow(() -> new RuntimeException(Integer.toString(id)));
        try {
            pizzaRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validName(Pizza pizzaToValidate) {
        //verifico se su sul DB esistte una pizza con lo stesso nome di pizzaToValidate
        if (pizzaToValidate.getName() == null) {
            return !pizzaRepository.existsByName(pizzaToValidate.getName());
        }
        return !pizzaRepository.existsByNameAndIdNot(pizzaToValidate.getName(), pizzaToValidate.getId());
    }
}
