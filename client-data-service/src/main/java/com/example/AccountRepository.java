package com.example;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by jonathancuningham on 4/24/17.
 */
public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByClientId(String filterText);
}
