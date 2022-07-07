package co.nmb.systemsdevelopmentspecialist.services;

import co.nmb.systemsdevelopmentspecialist.models.AuthorisedUser;
import co.nmb.systemsdevelopmentspecialist.models.User;
import co.nmb.systemsdevelopmentspecialist.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class AuthorisedUserService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user=userRepository.findByUsername(username);

        if(user==null){

            throw new UsernameNotFoundException("No such user");
        }

        return new AuthorisedUser(user);
    }
}
