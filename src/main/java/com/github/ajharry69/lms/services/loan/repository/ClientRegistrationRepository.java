package com.github.ajharry69.lms.services.loan.repository;

import com.github.ajharry69.lms.services.loan.model.ClientRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRegistrationRepository extends JpaRepository<ClientRegistration, Long> {
    Optional<ClientRegistration> findByUrl(String url);
}