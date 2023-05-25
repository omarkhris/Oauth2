package com.security.oauth2.service;

import com.security.oauth2.document.UserCred;
import com.security.oauth2.dto.UserCredIdDTO;
import com.security.oauth2.repository.UserRepository;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Service
public class UserManager implements UserDetailsManager {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ModelMapper modelMapper;


    @Override
    public void createUser(UserDetails user) {
        ((UserCred) user).setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save((UserCred) user);
    }

    @Override
    public void updateUser(UserDetails user) {
        UserCred existingUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        existingUser.setAuthorities(user.getAuthorities());
        userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(String username) {
        if(userRepository.existsByUsername(username)){
            userRepository.delete(userRepository.findByUsername(username).get());
        }
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
        String username = currentAuthentication.getName();

        UserCred existingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(oldPassword, existingUser.getPassword())) {
            throw new BadCredentialsException("Invalid old password");
        }

        existingUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(existingUser);
    }

    @Override
    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException(
                        MessageFormat.format("username {0} not found", username)
                ));
    }


    public UserCredIdDTO getUserById(String id){
        UserCred user = userRepository.findById(id).get();
        UserCredIdDTO dto = modelMapper.map(user, UserCredIdDTO.class);
        return dto;
    }


}
