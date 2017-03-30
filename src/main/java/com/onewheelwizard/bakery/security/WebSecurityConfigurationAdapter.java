package com.onewheelwizard.bakery.security;

import com.onewheelwizard.bakery.model.Account;
import com.onewheelwizard.bakery.model.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
class WebSecurityConfigurationAdapter extends GlobalAuthenticationConfigurerAdapter {

    @Autowired
    AccountRepository accountRepository;

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService());
    }

    @Bean
    UserDetailsService userDetailsService() {
        return new UserDetailsService() {

            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return accountRepository.findByUsername(username)
                        .map(account-> new User(account.getUsername(), account.getPassword(), true, true, true, true,
                            AuthorityUtils.createAuthorityList("USER")))
                        .orElseThrow(() -> new UsernameNotFoundException(username));

            }

        };
    }
}
