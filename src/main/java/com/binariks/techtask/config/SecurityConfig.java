package com.binariks.techtask.config;

import com.binariks.techtask.exception.OAuthExc;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@EnableMethodSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests().antMatchers("/**").permitAll()
                .and()
                .oauth2Login()
                .failureHandler((request, response, exception) -> {
//                            request.getSession().setAttribute("error.message", exception.getMessage());
                            response.sendError(HttpStatus.FORBIDDEN.value(), exception.getMessage());
                        });
    }

    @Bean
    public OAuth2UserService<OidcUserRequest, OidcUser> oAuth2UserService() {
        OidcUserService oidcUserService = new OidcUserService();
        return userRequest -> {
            String email = (String) userRequest.getIdToken().getClaims().get("email");
            OidcUser user = oidcUserService.loadUser(userRequest);
            if (email.contains("@lcdut.com.ua")) {
                return user;
            }
//            throw new OAuthExc("Wrong domain");
            throw new OAuth2AuthenticationException(new OAuth2Error("invalid_token", "Not in the Team", ""));
        };
    }
}