package com.elomari.mapper;

import com.elomari.Service.CompteService;
import com.elomari.dto.TransactionDto;
import com.elomari.entity.Transaction;
import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
public class TransactionMapper {

    private static CompteService compteService;


    public static Transaction toEntity(TransactionDto transactionDto){
        return Transaction.builder()
                .transactionId(transactionDto.getTransactionId())
                .transactionDate(DateFormatter.mapFormattedDateToDate(transactionDto.getTransactionDate()))
                .montant(transactionDto.getMontant())
                .debitDate(DateFormatter.mapFormattedDateToDate(transactionDto.getDebitDate()))
                .compte(compteService.find(transactionDto.getCompteId()))
//                .compte(null)
                .build();
    }

    public TransactionDto toDto(Transaction transaction){
        return TransactionDto.builder()
                .transactionId(transaction.getTransactionId())
                .transactionDate(DateFormatter.mapDateToFormattedDate(transaction.getTransactionDate()))
                .montant(transaction.getMontant())
                .debitDate(DateFormatter.mapDateToFormattedDate(transaction.getDebitDate()))
                .CompteId(transaction.getCompte().getCompteId())
                .build();
    }

}
