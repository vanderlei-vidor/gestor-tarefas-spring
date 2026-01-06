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
			if (repository.count() == 0) {
				Usuario admin = new Usuario();
				admin.setNome("Vanderlei");
				admin.setEmail("admin@teste.com");
				admin.setSenha(encoder.encode("123")); // Aqui a senha é trancada corretamente
				repository.save(admin);
				System.out.println("✅ Usuário admin@teste.com criado com sucesso!");
			}
		};
	}
}