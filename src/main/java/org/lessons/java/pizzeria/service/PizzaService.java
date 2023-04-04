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
}
