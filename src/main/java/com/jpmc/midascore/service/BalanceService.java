package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BalanceService {

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<Balance> getBalance(Long userId) {
        UserRecord user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ResponseEntity<Balance>(new Balance(0.0f), HttpStatus.OK);
        }
        return new ResponseEntity<Balance>(new Balance(user.getBalance()), HttpStatus.OK);
    }
}
