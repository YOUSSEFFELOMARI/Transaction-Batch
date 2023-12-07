package com.elomari.batch;

import com.elomari.Service.CompteService;
import com.elomari.dto.TransactionDto;
import com.elomari.entity.Compte;
import com.elomari.entity.Transaction;
import com.elomari.mapper.DateFormatter;
import com.elomari.repository.CompteRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
@Component
public class CustomItemProcessor implements ItemProcessor {
    Logger logger = LoggerFactory.getLogger(StepSkipListener.class);

    @Autowired
    private CompteService compteService;
    @Override
    public Transaction process(Object obj){
        TransactionDto transactionDto=(TransactionDto) obj;
        Compte compte=compteService.find(transactionDto.getCompteId());

        Transaction transaction=new Transaction(transactionDto.getTransactionId(),
                transactionDto.getMontant(),null,null,null);
        transaction.setTransactionDate(DateFormatter.mapFormattedDateToDate(transactionDto.getTransactionDate()));
        transaction.setDebitDate(LocalDateTime.now());
        transaction.setCompte(compte);

        logger.info(String.format("processing Transaction %s", transactionDto));
        return transaction;
    }

}
