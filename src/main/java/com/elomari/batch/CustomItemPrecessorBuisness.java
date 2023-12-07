package com.elomari.batch;

import com.elomari.Service.CompteService;
import com.elomari.entity.Compte;
import com.elomari.entity.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomItemPrecessorBuisness implements ItemProcessor {

    Logger logger = LoggerFactory.getLogger(StepSkipListener.class);

    @Autowired
    private CompteService compteService;

    @Override
    public Transaction process(Object obj) {
        Transaction transaction=(Transaction) obj;
        Compte compte=transaction.getCompte();
        compte.setSolde(compte.getSolde()-transaction.getMontant());
        logger.info(String.format("compte %s est updated", compte.toString()));
        return transaction;
    }
}
