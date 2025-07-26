package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    private static final String INCENTIVE_SERVICE_URL = "http://localhost:8080/incentive";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionRepository transactionRepository;


    public void processTransaction(Transaction transaction) {
        UserRecord sender = userRepository.findById(transaction.getSenderId()).orElse(null);
        UserRecord recipient = userRepository.findById(transaction.getRecipientId()).orElse(null);

        logger.info("Processing transaction: {}", transaction);

        boolean isValid = validateTransaction(sender, recipient, transaction.getAmount());

        TransactionRecord transactionRecord = new TransactionRecord(
                sender,
                recipient,
                transaction.getAmount(),
                isValid
        );



        if(isValid){

            Object Incentive = processIncentive(transaction);

            transactionRepository.save(transactionRecord);
            sender.setBalance(sender.getBalance() - transaction.getAmount());
            recipient.setBalance(recipient.getBalance() + transaction.getAmount());
            userRepository.save(sender);
            userRepository.save(recipient);

            logger.info("Transaction processed successfully: {} -> {} of amount {}", sender.getName(), recipient.getName(), transaction.getAmount());
        } else {
            logger.info("Transaction is invalid and will not be processed: {} -> {} of amount {}", sender.getName(), recipient.getName(), transaction.getAmount());
        }
        logger.info("waldorf's final amount: {}", userRepository.findByName("wilbur").map(UserRecord::getBalance).orElse(0.0f));
    }

    private Object processIncentive(Transaction transaction) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            Object incentive = restTemplate.postForObject(INCENTIVE_SERVICE_URL, transaction, Object.class);
            logger.info("Incentive received: {}", incentive);
            return incentive;
        } catch (Exception e) {
            logger.error("Failed to process incentive for transaction: {}", transaction, e);
            return null;
        }
    }

    public boolean validateTransaction(UserRecord sender, UserRecord receiver, Float amount) {
        if (sender == null || receiver == null) {
            logger.error("Sender or receiver cannot be found.");
            return false;
        }
        if(amount <= 0) {
            logger.error("Transaction amount must be greater than zero.");
            return false;
        }
        if(sender.getBalance() < amount) {
            logger.error("{} does not have enough balance to complete the transaction.",sender.getName());
            return false;
        }
        return true;

    }
}
