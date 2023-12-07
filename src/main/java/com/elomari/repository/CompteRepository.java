package com.elomari.repository;

import com.elomari.entity.Compte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface CompteRepository extends JpaRepository<Compte, Long> {
}