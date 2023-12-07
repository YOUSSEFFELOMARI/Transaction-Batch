package com.elomari.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Component
@XStreamAlias("transaction")
public class TransactionDto {
    @XStreamAsAttribute
    private long transactionId;
    private long montant;
    private String transactionDate;
    private String debitDate;
    private long CompteId;
}
