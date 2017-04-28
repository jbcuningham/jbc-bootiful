package com.example;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {

    List<Client> findByLastNameStartsWithIgnoreCase(String lastName);

}

