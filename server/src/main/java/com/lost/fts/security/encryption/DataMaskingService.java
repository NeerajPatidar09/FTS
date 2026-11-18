package com.lost.fts.security.encryption;

import org.springframework.stereotype.Service;

@Service
public class DataMaskingService {

    public String maskPan(String pan) {

        if (pan == null || pan.length() < 5) {
            return "*****";
        }

        return "XXXXX"
            + pan.substring(5, pan.length() - 1)
            + "X";
    }

    public String maskPhone(String phone) {

        if (phone == null || phone.length() < 4) {
            return "********";
        }

        return "******" +
            phone.substring(phone.length() - 4);
    }

    public String maskEmail(String email) {

        if (email == null || !email.contains("@")) {
            return "********";
        }

        String[] parts = email.split("@", 2);

        String username = parts[0];

        String visible = username.substring(
            0,
            Math.min(2, username.length())
        );

        return visible + "*****@" + parts[1];
    }
}