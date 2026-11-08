package com.lost.fts.transaction.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lost.fts.transaction.api.dto.TransactionCreateRequest;
import com.lost.fts.transaction.api.dto.TransactionResponse;
import com.lost.fts.transaction.application.TransactionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> ingestTransaction( @Valid @RequestBody TransactionCreateRequest request) {

        var result = transactionService.ingest(request);

        if (result.alreadyExisted()) {
            return ResponseEntity.ok(result.transaction());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(result.transaction());
    }

    @GetMapping("/{transactionRef}")
    public ResponseEntity<TransactionResponse> getTransaction(@PathVariable String transactionRef) {

        return ResponseEntity.ok(transactionService.getByTransactionRef(transactionRef));
    }
}
