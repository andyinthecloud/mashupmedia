package org.mashupmedia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.tiles3.TilesConfigurer;

//@Configuration
//@ComponentScan(basePackages = "org.mashupmedia.controller")
public class TilesApplicationConfiguration implements WebMvcConfigurer {

    @Bean
    public TilesConfigurer tilesConfigurer() {
        TilesConfigurer tilesConfigurer = new TilesConfigurer();
        tilesConfigurer.setDefinitions(
                new String[] { "/WEB-INF/tiles.xml" });
        tilesConfigurer.setCheckRefresh(true);
        return tilesConfigurer;
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.tiles();
        registry.jsp()
                .prefix("/WEB-INF/jsp/")
                .suffix(".jsp");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/images/**", "/jquery/**", "/jquery-ui/**", "/jquery-mobile/**", "/jquery-plugins/**", "/media/**", "/scripts/**", "/themes/**")
                .addResourceLocations("/images/", "/jquery/", "/jquery-ui/", "/jquery-mobile/", "/jquery-plugins/", "/media/", "/scripts/", "/themes/");
    }
}
