package com.elomari.batch;

import com.elomari.Service.TransactionService;
import com.elomari.entity.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomItemWriter implements ItemWriter<Transaction> {

    Logger logger = LoggerFactory.getLogger(StepSkipListener.class);


    @Autowired
    private TransactionService transactionService;

    @Override
    public void write(Chunk<? extends Transaction> transactions) throws Exception {
        transactions.forEach( tr->{
            logger.info(String.format("writing Transaction %s", tr.getTransactionId()+" "+tr.getCompte().getCompteId()+" "+tr.getMontant()+" "));
            transactionService.create(tr);
        });
    }
}
