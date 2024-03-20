package com.mongo.projetPFE.Utilisateur;

import com.mongo.projetPFE.DTO.AuthentificationDTO;
import com.mongo.projetPFE.produit.Product;
import com.mongo.projetPFE.securite.JwtService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

import static com.mongo.projetPFE.Utilisateur.TypeDeRole.utilisateur;

@Slf4j
@RestController
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor

public class controllerUtilisateur {
    private UtilisateurService utilisateurService;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;
    private UtilisateurRepository utilisateurRepository;
    private final UserDetailsService userDetailsService;





    /*  @PostMapping("/tuto")
      public ResponseEntity<String> save(@RequestBody Utilisateur utilisateur) {
        log.info("inscription");
          return ResponseEntity.ok(utilisateurService.save(utilisateur).getId());

      @PostMapping("/autorised")
      public void inscription(){
          log.info("inscription");
      }
  }*/
    @PostMapping("/mounir")
    public ResponseEntity<String> inscription(@RequestBody Utilisateur utilisateur) {
        String email = utilisateur.getEmail();
        if (!email.contains("@") || !email.contains(".")) {
            return ResponseEntity.badRequest().body("Votre mail est invalide");
        }


        Optional<Utilisateur> optionalUtilisateur= this.utilisateurRepository.findByEmail(utilisateur.getEmail());
        if (optionalUtilisateur.isPresent()){
            return ResponseEntity.badRequest().body("Votre email existe déjà");
        }

        try {
            utilisateurService.inscription(utilisateur);
            return ResponseEntity.ok("Inscription réussie");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erreur lors de l'inscription : " + e.getMessage());
        }
    }
    @PostMapping("/activation")
    public ResponseEntity<String> activation(@RequestBody Map<String, String> activation) {
        try {
            utilisateurService.activation(activation);
            return ResponseEntity.ok("Activation réussie veuillez vous-connecter");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/modifier-mot-de-passe")
    public ResponseEntity<String> modifierMotDePasse(@RequestBody Map<String,String> activation) {

        try {
            this.utilisateurService.modifierMotDePasse(activation);
            return ResponseEntity.ok("utilisateur reconnue");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @PostMapping("/nouveau-mot-de-passe")
    public ResponseEntity<String> nouveauMotDePasse(@RequestBody Map<String,String> activation) {


        try {
            this.utilisateurService.nouveauMotDePasse(activation);
            return ResponseEntity.ok("mot de passe changé avec succes");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
   /* @PostMapping("/connexion")
    public Map<String,String> connexion (@RequestBody AuthentificationDTO authentificationDTO){
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authentificationDTO.username(), authentificationDTO.password())
        );
        if(authenticate.isAuthenticated()){
          return this.jwtService.generate(authentificationDTO.username());
        }
        return null;

    }  */

    @PostMapping("/connexion")
    public ResponseEntity<?> connexion(@RequestBody AuthentificationDTO authentificationDTO) {
        try {
            // Vérifiez d'abord si l'utilisateur existe
            try {
                userDetailsService.loadUserByUsername(authentificationDTO.username());
            } catch (UsernameNotFoundException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Adresse e-mail incorrecte");
            }

            // Tentez ensuite d'authentifier l'utilisateur
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authentificationDTO.username(), authentificationDTO.password())
            );

            if(authenticate.isAuthenticated()){
                return ResponseEntity.ok(this.jwtService.generate(authentificationDTO.username()));
            } else {
                // Normalement, vous n'arriverez pas ici car une exception sera lancée si l'authentification échoue
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Échec de l'authentification");
            }
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Mot de passe incorrect");
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Problème d'authentification");
        }
    }




    @PostMapping("/deconnexion")
    public void deconnexion() {

        this.jwtService.deconnexion();
    }
}
