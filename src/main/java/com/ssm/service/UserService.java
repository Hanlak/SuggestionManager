package com.ssm.service;

import com.ssm.dto.RegisterDTO;
import com.ssm.entity.Role;
import com.ssm.entity.User;
import com.ssm.exception.*;
import com.ssm.mapper.IUserMapper;
import com.ssm.repository.UserRepository;
import com.ssm.util.SSMUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    IUserMapper iUserMapper;

    @Autowired
    SSMMailService ssmMailService;
    @Autowired
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${mail_sender_user}")
    private String mailSenderUser;

    public UserService(UserRepository userRepository, IUserMapper iUserMapper, SSMMailService ssmMailService, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.iUserMapper = iUserMapper;
        this.ssmMailService = ssmMailService;
        this.passwordEncoder = passwordEncoder;
    }


    public User saveUser(RegisterDTO registerDTO) throws DataAccessException, UserAlreadyExistsException {
        checkUserOrEmailAlreadyExists(registerDTO);
        User user = new User(registerDTO.getName(), registerDTO.getUsername(),
                registerDTO.getEmail(),
                passwordEncoder.encode(registerDTO.getPassword()), singletonList(new Role("ROLE_USER")));
        return userRepository.save(user);
    }

    public void forgotPassword(String username, String email)
            throws UserNotFoundException, EmailMismatchException, EmailSendingException, PasswordUpdateException {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UserNotFoundException("We couldn't find your account. Please enter an existing username."));

        if (!user.getEmail().equals(email)) {
            throw new EmailMismatchException("The provided email does not match the registered email for this account.");
        }

        String randomPassword = SSMUtil.randomPassword();
        String isTheMailSent = ssmMailService.toSendEMail(email, mailSenderUser, "Suggestion Manager password!", randomPassword);

        if (!isTheMailSent.equals("OK")) {
            throw new EmailSendingException("Encountered an issue while sending a password to your email. Please try again.");
        }

        int isChangeAffected = userRepository.saveNewPasswordUsingEmail(passwordEncoder.encode(randomPassword), email);

        if (isChangeAffected <= 0) {
            throw new PasswordUpdateException("System Error: Trouble while saving your password. Please try again.");
        }
    }

    public RegisterDTO accountInformation(String username) throws UserNotFoundException {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UserNotFoundException("User Session Might have Expired. Please Login again."));
        return iUserMapper.toDto(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUserName(username);
        if (!user.isPresent()) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new org.springframework.security.core.userdetails.User(user.get().getUserName(), user.get().getPassword(), mapRolesToAuthorities(user.get().getRoles()));

    }

    public void updateFullName(String fullName, String username) throws UserNotFoundException, DataAccessException {
        User user = userRepository.findByUserName(username).orElseThrow(() -> new UserNotFoundException("User Not Found,Might be Session Issue; please login"));
        if (!fullName.equals(user.getFullName())) {
            user.setFullName(fullName);
            userRepository.save(user);
        }
    }

    public void updateEmail(String email, String username) throws UserNotFoundException, DataAccessException, EmailAlreadyExistsException {
        User user = userRepository.findByUserName(username).orElseThrow(() -> new UserNotFoundException("User Not Found,Might be Session Issue; please login"));
        if (!email.equals(user.getEmail())) {
            User emailCheck = userRepository.findByEmail(email).orElse(null);
            if (ObjectUtils.isEmpty(emailCheck)) {
                user.setEmail(email);
                userRepository.save(user);
            }else{
                throw new EmailAlreadyExistsException("The given mail already registered with another account");
            }
        }
    }

    public void updatePass(String currentPass, String newPass, String username) throws UserNotFoundException, DataAccessException, PasswordUpdateException {
        User user = userRepository.findByUserName(username).orElseThrow(() -> new UserNotFoundException("User Not Found,Might be Session Issue; please login"));
        if (!passwordEncoder.matches(currentPass,user.getPassword())) {
            throw new PasswordUpdateException("Current Password is wrong,please use forgot password to create a new password and update here");
        } else if (currentPass.equals(newPass)) {
            throw new PasswordUpdateException("Password should not be same as old password");
        } else {
            user.setPassword(passwordEncoder.encode(newPass));
            userRepository.save(user);
        }
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }

    private void checkUserOrEmailAlreadyExists(RegisterDTO registerDTO) throws UserAlreadyExistsException {
        Optional<User> user = userRepository.findByUserName(registerDTO.getUsername());
        if (user.isPresent())
            throw new UserAlreadyExistsException("username already taken. please try with unique name");
        Optional<User> emailUser = userRepository.findByEmail(registerDTO.getEmail());
        if (emailUser.isPresent())
            throw new UserAlreadyExistsException("email is already registered.Please try another email");
    }


}
