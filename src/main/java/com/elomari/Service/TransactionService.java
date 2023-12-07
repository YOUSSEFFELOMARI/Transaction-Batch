package com.elomari.Service;

import com.elomari.entity.Transaction;
import com.elomari.repository.TransactionRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService {


    private final TransactionRepository transactionRepository;
    
    public void create(Transaction transaction){
        if (transactionRepository.existsById(transaction.getTransactionId()))
            throw new EntityExistsException("transaction already stored in database - ID : "+transaction.getTransactionId()) {};
        transactionRepository.save(transaction);
    }

    public Transaction update(Transaction transaction){
        if (!transactionRepository.existsById(transaction.getTransactionId()))
            throw new EntityExistsException("No transaction in database With - ID : "+transaction.getTransactionId()) {};
        return transactionRepository.save(transaction);
    }

    public Transaction find(long transactionid){
        return transactionRepository.findById(transactionid).orElseThrow(()->
                new EntityNotFoundException("No transaction in database With - ID : "+transactionid){});
    }
}
