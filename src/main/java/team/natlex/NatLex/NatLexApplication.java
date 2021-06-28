package team.natlex.NatLex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude= {UserDetailsServiceAutoConfiguration.class})
//@SpringBootApplication
public class NatLexApplication {

	public static void main(String[] args) {
		SpringApplication.run(NatLexApplication.class, args);
	}
}