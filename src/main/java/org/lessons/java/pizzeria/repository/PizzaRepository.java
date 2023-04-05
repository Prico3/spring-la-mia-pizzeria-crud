package org.lessons.java.pizzeria.repository;

import org.lessons.java.pizzeria.model.Pizza;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PizzaRepository extends JpaRepository<Pizza, Integer> {
    public List<Pizza> findByNameContainingIgnoreCase(String name);

    public boolean existsByNameAndIdNot(String name, Integer id);

    public boolean existsByName(String name);

}