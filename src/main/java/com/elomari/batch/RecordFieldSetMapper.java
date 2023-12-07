package com.elomari.batch;

import com.elomari.dto.TransactionDto;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.stereotype.Component;

@Component
public class RecordFieldSetMapper implements FieldSetMapper<TransactionDto> {

    @Override
    public TransactionDto mapFieldSet(FieldSet fieldSet){
        long compteId= fieldSet.readLong("compteId");
        long trasactionId= fieldSet.readLong("transactionId");
        String transactionDate=fieldSet.readString("transactionDate");
        String debitDate=fieldSet.readString("transactionDate");
        long montant = fieldSet.readLong("montant");


        return new TransactionDto(trasactionId,montant,transactionDate,debitDate,compteId);
    }

}
