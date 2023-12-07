package com.elomari.Service;

import com.elomari.dto.TransactionDto;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.PathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.HashMap;
import java.util.Map;

@Profile("spring")
public class App {
    @Scheduled
    public static void main(String[] args) throws Exception {
        StaxEventItemReader<TransactionDto> reader=new StaxEventItemReader<>();
        reader.setResource(new PathResource("C:/Users/eloma/OneDrive/Desktop/Youssef_ELOMARI_Batch/src/main/resources/input/Transaction.xml"));
        reader.setFragmentRootElementName("transaction");
        XStreamMarshaller xStreamMarshaller=new XStreamMarshaller();
        Map<String,String> map=new HashMap<>();
        map.put("transactionDto","com.elomari.dto.TransactionDto");
        xStreamMarshaller.setAliases(map);
        reader.setUnmarshaller(xStreamMarshaller);

        System.out.println(reader);
    }
}
