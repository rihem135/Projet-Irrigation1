package com.mongo.projetPFE.support;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@RestController
public class ControllerSupport {

    supportRepository supportRepository;
    @PostMapping("/support")
    public ResponseEntity<String> support(@RequestBody support support) {
        String email = support.getEmail();
        if (!isValidEmail(email)) {
            return ResponseEntity.badRequest().body("Votre email est invalide");
        }

        String telephone = support.getTelephone();
        if (!isValidPhoneNumber(telephone)) {
            return ResponseEntity.badRequest().body("Votre numéro est invalide");
        }



            supportRepository.save(support);
                return ResponseEntity.ok("Message récu");

    }

    private boolean isValidEmail(String email) {
        return  email.contains("@") && email.contains(".");
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        // Vérifiez si la longueur du numéro est de 8 caractères et s'il est composé de chiffres uniquement
        return phoneNumber.length() == 8 && phoneNumber.matches("[0-9]+");
    }
}
