package com.mongo.projetPFE.Utilisateur;

import com.mongo.projetPFE.Utilisateur.Validation.Validation;
import com.mongo.projetPFE.Utilisateur.Validation.ValidationService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor

public class UtilisateurService implements UserDetailsService {
    private UtilisateurRepository utilisateurRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private ValidationService validationService;


    public void inscription(Utilisateur utilisateur) {
        if (!utilisateur.getEmail().contains("@")) {
            throw new RuntimeException("Votre mail est invalide");
        }
        if (!utilisateur.getEmail().contains(".")) {
            throw new RuntimeException("Votre mail est invalide");
        }
       Optional<Utilisateur> optionalUtilisateur= this.utilisateurRepository.findByEmail(utilisateur.getEmail());
       if (optionalUtilisateur.isPresent()){
           throw new RuntimeException("votre email existe déja");
       }

        String mdpCrypte = this.passwordEncoder.encode(utilisateur.getPassword());
        utilisateur.setPassword(mdpCrypte);

        Role roleUtilisateur = new Role();
        roleUtilisateur.setLibelle(TypeDeRole.utilisateur);
        utilisateur.setRole(roleUtilisateur);
        utilisateur = this.utilisateurRepository.save(utilisateur);
        this.validationService.enregistrer(utilisateur);
    }

    public void activation(Map<String, String> activation) {
        Validation validation = this.validationService.LireEnFonctionDuCode(activation.get("code"));
        if (Instant.now().isAfter(validation.getExpiration())) {
            throw new RuntimeException("votre code est expiré");
        }

        Utilisateur utilisateurActiver =this.utilisateurRepository.findById(validation.getUtilisateur().getId()).orElseThrow(()->new RuntimeException("Utilisateur Inconnu"));
        utilisateurActiver.setActif(true);
        this.utilisateurRepository.save(utilisateurActiver);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      return  this.utilisateurRepository.findByEmail(username).orElseThrow(()-> new UsernameNotFoundException("Aucun utilisateur ne correspond a cet identifiant"));

    }

    public void modifierMotDePasse(Map<String, String> parametres) {
        Utilisateur utilisateur= (Utilisateur) this.loadUserByUsername(parametres.get("email"));
                this.validationService.enregistrer(utilisateur);
    }

    public void nouveauMotDePasse(Map<String, String> parametres) {
        Utilisateur utilisateur= (Utilisateur) this.loadUserByUsername(parametres.get("email"));
       final Validation validation= validationService.LireEnFonctionDuCode(parametres.get("code"));

         if (validation.getUtilisateur().getEmail().equals(utilisateur.getEmail())){
        String mdpCrypte = this.passwordEncoder.encode(parametres.get("password"));
        utilisateur.setPassword(mdpCrypte);}
         this.utilisateurRepository.save(utilisateur);
    }
}


