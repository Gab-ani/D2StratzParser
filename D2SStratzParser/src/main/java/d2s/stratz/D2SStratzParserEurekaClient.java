package d2s.stratz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Lazy;

import com.netflix.discovery.EurekaClient;

@EnableDiscoveryClient  
@SpringBootApplication
public class D2SStratzParserEurekaClient { 

	public static void main(String[] args) {
		SpringApplication.run(D2SStratzParserEurekaClient.class, args);
	}

}
