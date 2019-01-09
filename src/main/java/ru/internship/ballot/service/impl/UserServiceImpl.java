package ru.internship.ballot.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.internship.ballot.AuthorizedUser;
import ru.internship.ballot.model.User;
import ru.internship.ballot.model.Vote;
import ru.internship.ballot.repository.UserRepository;
import ru.internship.ballot.service.UserService;
import ru.internship.ballot.util.UserUtil;
import ru.internship.ballot.util.exception.NotFoundException;

import java.util.Optional;

@Service("userService")
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(User user) {
        Assert.notNull(user, "user must not be null");
        return userRepository.save(user);
    }

    @Override
    public AuthorizedUser loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.getByEmailWithVotes(email.toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("User " + email + " is not found"));

        Optional<Vote> todayVote = UserUtil.getTodayVote(user);

        AuthorizedUser aUser = new AuthorizedUser(user);
        aUser.getUserTo().setTodayVote(todayVote);

        return aUser;
    }

    @Override
    public User getByEmail(String email) throws NotFoundException {
        Assert.notNull(email, "email must not be null");
        return userRepository.getByEmail(email).orElseThrow(() -> new NotFoundException("email=" + email));
    }

    @Override
    public User getByEmailWithVotes(String email) throws NotFoundException {
        Assert.notNull(email, "email must not be null");
        return userRepository.getByEmail(email).orElseThrow(() -> new NotFoundException("email=" + email));
    }
}
