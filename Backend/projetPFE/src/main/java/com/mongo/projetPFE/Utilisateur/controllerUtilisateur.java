package com.mongo.projetPFE.Utilisateur;

import com.mongo.projetPFE.DTO.AuthentificationDTO;
import com.mongo.projetPFE.produit.Product;
import com.mongo.projetPFE.securite.JwtService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.mongo.projetPFE.Utilisateur.TypeDeRole.utilisateur;

@Slf4j
@RestController
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor

public class controllerUtilisateur {
    private UtilisateurService utilisateurService;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;

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
    public void inscription(@RequestBody Utilisateur utilisateur) {
        log.info("inscription");
        this.utilisateurService.inscription(utilisateur);
    }
    @PostMapping("/activation")
    public void activation(@RequestBody Map<String,String> activation) {

        this.utilisateurService.activation(activation);
    }

    @PostMapping("/modifier-mot-de-passe")
    public void modifierMotDePasse(@RequestBody Map<String,String> activation) {

        this.utilisateurService.modifierMotDePasse(activation);
    }

    @PostMapping("/nouveau-mot-de-passe")
    public void nouveauMotDePasse(@RequestBody Map<String,String> activation) {

        this.utilisateurService.nouveauMotDePasse(activation);
    }
    @PostMapping("/connexion")
    public Map<String,String> connexion (@RequestBody AuthentificationDTO authentificationDTO){
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authentificationDTO.username(), authentificationDTO.password())
        );
        if(authenticate.isAuthenticated()){
          return this.jwtService.generate(authentificationDTO.username());
        }
        return null;

    }
    @PostMapping("/deconnexion")
    public void deconnexion() {

        this.jwtService.deconnexion();
    }
}
