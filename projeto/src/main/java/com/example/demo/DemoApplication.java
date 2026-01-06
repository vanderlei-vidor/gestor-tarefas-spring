package com.example.demo;

import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	CommandLineRunner init(UsuarioRepository repository, PasswordEncoder encoder) {
		return args -> {
			// Buscamos o admin pelo e-mail
			var adminOpt = repository.findByEmail("admin@teste.com");

			if (adminOpt.isPresent()) {
				// SE ELE JÁ EXISTE: Vamos garantir que a senha seja criptografada agora
				Usuario adminExistente = adminOpt.get();
				adminExistente.setSenha(encoder.encode("123")); // Sobrescreve com BCrypt
				repository.save(adminExistente);
				System.out.println("✅ Senha do admin@teste.com atualizada para BCrypt!");
			} else {
				// SE NÃO EXISTE: Cria do zero (como você já fazia)
				Usuario admin = new Usuario();
				admin.setNome("Vanderlei");
				admin.setEmail("admin@teste.com");
				admin.setSenha(encoder.encode("123"));
				repository.save(admin);
				System.out.println("✅ Usuário admin@teste.com criado do zero!");
			}
		};
	}
}