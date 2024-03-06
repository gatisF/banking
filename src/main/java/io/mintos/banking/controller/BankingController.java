package io.mintos.banking.controller;

import io.mintos.banking.records.AccountRecord;
import io.mintos.banking.records.TransactionsRecord;
import io.mintos.banking.records.TransfareRecord;
import io.mintos.banking.services.BankingService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/bank")
public class BankingController {

    private final BankingService service;

    public BankingController(final BankingService service) { this.service = Objects.requireNonNull(service); }

    @GetMapping("/clients/{personalCode}/accounts")
    public ResponseEntity<List<AccountRecord>> getAccountsByClientId(@PathVariable @NotBlank String personalCode) {
        if (service.clientExists(personalCode)) {
            return ResponseEntity.ok(service.getAccountsByClient(personalCode));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/accounts/{accountNumber}/transactions")
    public ResponseEntity<List<TransactionsRecord>> getTransactionHistory(@PathVariable String accountNumber,
                                                                          @RequestParam(required = false) Integer offset,
                                                                          @RequestParam(required = false) Integer limit) {
        List<TransactionsRecord> transactionHistory = service.getTransactionHistory(accountNumber, offset, limit);
        return  ResponseEntity.ok(transactionHistory);
    }

    @PostMapping("/accounts/transfer")
    public ResponseEntity<Void> transferFunds(@RequestBody TransfareRecord transfareRecord) {
        service.transferFunds(transfareRecord);
        return ResponseEntity.ok().build();
    }
}
