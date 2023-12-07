package com.elomari.Service;

import com.elomari.entity.Compte;
import com.elomari.repository.CompteRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompteService {


    private final CompteRepository compteRepository;

    public Compte create(Compte compte){
        if (compteRepository.existsById(compte.getCompteId()))
            throw new EntityExistsException("compte already stored in database - ID : "+compte.getCompteId()) {};
        return compteRepository.save(compte);
    }

    public Compte update(Compte compte){
        if (!compteRepository.existsById(compte.getCompteId()))
            throw new EntityExistsException("No compte in database With - ID : "+compte.getCompteId()) {};
        return compteRepository.save(compte);
    }

    public Compte find(long id){
        return compteRepository.findById(id).orElseThrow(()->
                new EntityNotFoundException("No compte in database With - ID : "+id){});
    }

}
