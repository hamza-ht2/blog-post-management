package com.example.post_management.services;

import com.example.post_management.models.User;
import com.example.post_management.models.enums.Role;
import com.example.post_management.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public User register(User user){
        if (userRepository.existsByEmail(user.getEmail())){
            throw new RuntimeException("email already in use");
        }
        if (userRepository.existsByUsername(user.getUsername())){
            throw new RuntimeException("username already taken");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        return userRepository.save(user);
    }
    public User getUserById(Long id){
        return userRepository.findById(id).orElseThrow(()-> new RuntimeException("user not found with id :"+id));
    }
    public User getUserByUsername(String username){
        return userRepository.findUserByUsername(username).orElseThrow(()-> new RuntimeException("user not found with username :"+username));
    }
    public User getUserByEmail(String email){
        return userRepository.findUserByEmail(email).orElseThrow(()-> new RuntimeException("user not found with email : "+email));
    }
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
    public List<User> getUsersByRole(Role role){
        return userRepository.findUsersByRole(role);
    }
    public List<User> getUsersByEnabled(boolean enabled){
        return userRepository.findUsersByEnabled(enabled);
    }
    public List<User> getUsersByCountry(String country){
        return userRepository.findUsersByCountry(country);
    }
    public List<User> getUsersByEnabledAndRole(boolean enabled, Role role){
        return userRepository.findUsersByEnabledAndRole(enabled, role);
    }
    public User updateProfile(Long userId, User user){
        User existing = getUserById(userId);
        existing.setFirstName(user.getFirstName());
        existing.setLastName(user.getLastName());
        existing.setUsername(user.getUsername());
        existing.setBio(user.getBio());
        existing.setWebsiteUrl(user.getWebsiteUrl());
        existing.setCountry(user.getCountry());
        return userRepository.save(existing);
    }
    public User updatePassword(Long userId, String oldPassword, String newPassword){
        User user = getUserById(userId);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())){
            throw new RuntimeException("password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }
    public void follow(Long followerId, Long followingId){
        if(followerId.equals(followingId)){
            throw new RuntimeException("you cannot follow yourself");
        }
        User follower = getUserById(followerId);
        User following = getUserById(followingId);
        if(follower.getFollowing().contains(following)){
            throw new RuntimeException("Already following this user");
        }
        follower.getFollowing().add(following);
        userRepository.save(follower);
    }
    public void unfollow(Long followerId, Long followingId){
        User follower = getUserById(followerId);
        User following  = getUserById(followingId);
        if(!follower.getFollowing().contains(following)){
            throw new RuntimeException("you're not following this user");
        }
        follower.getFollowing().remove(following);
        userRepository.save(follower);
    }
    public List<User> getFollowers(Long userId){
        return getUserById(userId).getFollowers();
    }
    public List<User> getFollowings(Long userId){
        return getUserById(userId).getFollowing();
    }

    public User updatePhotoProfile(Long userId, String newProfile){
        User user = getUserById(userId);
        user.setPhotoProfile(newProfile);
        return userRepository.save(user);
    }
    public User enableUser(Long userId){
        User user = getUserById(userId);
        user.setEnabled(true);
        return userRepository.save(user);
    }
    public User disableUser(Long userId){
        User user = getUserById(userId);
        user.setEnabled(false);
        return userRepository.save(user);
    }
    public User changeRole(Long userId, Role role){
        User user = getUserById(userId);
        user.setRole(role);
        return userRepository.save(user);
    }
    public void deleteUser(Long userId){
        if(!userRepository.existsUserById(userId)){
            throw new RuntimeException("user don't exist");
        }
        userRepository.deleteById(userId);
    }
}
