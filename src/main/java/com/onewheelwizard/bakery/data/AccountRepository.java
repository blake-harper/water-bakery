package com.onewheelwizard.bakery.data;

import com.onewheelwizard.bakery.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUsername(String username);

    @Transactional
    Long deleteByUsername(String username);
}
