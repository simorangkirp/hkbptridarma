package church_player_agent.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("*");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // SPA routes
        registry.addViewController("/")
                .setViewName("forward:/index.html");

        registry.addViewController("/dashboard")
                .setViewName("forward:/index.html");

        registry.addViewController("/search")
                .setViewName("forward:/index.html");

        registry.addViewController("/lyrics")
                .setViewName("forward:/index.html");

        registry.addViewController("/library")
                .setViewName("forward:/index.html");

        registry.addViewController("/stage")
                .setViewName("forward:/index.html");

        registry.addViewController("/settings")
                .setViewName("forward:/index.html");
    }
}