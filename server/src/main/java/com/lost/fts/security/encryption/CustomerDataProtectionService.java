package com.lost.fts.security.encryption;

import org.springframework.stereotype.Service;
import com.lost.fts.customer.infrastructure.persistence.CustomerEntity;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerDataProtectionService {

    private final FieldEncryptionService encryptionService;

    public void encryptSensitiveFields(CustomerEntity customer) {

        // customer.setPanEncrypted(
        // encryptionService.encrypt(customer.getPan())
        // );

        // customer.setPhoneEncrypted(
        // encryptionService.encrypt(customer.getPhone())
        // );

        customer.setEmailEncrypted(
                encryptionService.encrypt(customer.getEmail()));
    }

    public void decryptSensitiveFields(CustomerEntity customer) {

        // customer.setPan(
        // encryptionService.decrypt(customer.getPanEncrypted())
        // );

        // customer.setPhone(
        // encryptionService.decrypt(customer.getPhoneEncrypted())
        // );

        customer.setEmail(
                encryptionService.decrypt(customer.getEmailEncrypted()));
    }
}
