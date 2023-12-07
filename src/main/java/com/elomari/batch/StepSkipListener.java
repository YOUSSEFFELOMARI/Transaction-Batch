package com.elomari.batch;

import com.elomari.dto.TransactionDto;
import com.elomari.entity.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component("StepSkipListener")
public class StepSkipListener implements SkipListener<TransactionDto, Transaction> {
    Logger logger = LoggerFactory.getLogger(StepSkipListener.class);
    @Override
    public void onSkipInRead(Throwable throwable) {
        logger.info("A failure on read {} ", throwable.getMessage());
    }
    @Override
    public void onSkipInWrite(Transaction item, Throwable throwable) {
        logger.info("A failure on write {} , {}", throwable.getMessage(), item);
    }
    @Override
    public void onSkipInProcess(TransactionDto transaction, Throwable throwable) {
        try {
            logger.info("Item {} was skipped due to the exception {}",
                    new ObjectMapper().writeValueAsString(transaction),
                    throwable.getMessage());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}