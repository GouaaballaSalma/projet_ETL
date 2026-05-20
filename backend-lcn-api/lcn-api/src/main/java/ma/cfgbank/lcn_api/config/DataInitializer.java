package ma.cfgbank.lcn_api.config;

import lombok.RequiredArgsConstructor;
import ma.cfgbank.lcn_api.entity.Utilisateur;
import ma.cfgbank.lcn_api.model.RoleEnum;
import ma.cfgbank.lcn_api.repository.UtilisateurRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (utilisateurRepository.count() == 0) {
            Utilisateur admin = Utilisateur.builder()
                    .nomComplet("Admin CFG Bank (Moa)")
                    .email("admin@cfgbank.ma")
                    .motDePasse(passwordEncoder.encode("admin123"))
                    .role(RoleEnum.ROLE_ADMIN)
                    .build();

            utilisateurRepository.save(admin);
            System.out.println(" Administrateur de test créé avec succès : admin@cfgbank.ma / admin123");
        }
    }
}
