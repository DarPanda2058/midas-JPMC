package com.jpmc.midascore.controller;

import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.service.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class BalanceController {

    @Autowired
    private BalanceService balanceService;

    @GetMapping("/balance")
    public ResponseEntity<Balance> getBalance(@RequestParam("userId") Long userId) {
        return balanceService.getBalance(userId);
    }
}
