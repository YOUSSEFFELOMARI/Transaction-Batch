package com.elomari.repository;

import com.elomari.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}