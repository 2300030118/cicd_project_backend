package klu.service;

import klu.model.User;
import klu.model.SubscriptionType;
import klu.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for User-related operations.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Create a new user with default subscription if not provided.
     */
    public User createUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (user.getSubscriptionType() == null) {
            user.setSubscriptionType(SubscriptionType.FREE);
        }
        return userRepository.save(user);
    }

    /**
     * Authenticate a user by email and password.
     */
    public User authenticateUser(String email, String password) {
        if (email == null || password == null) {
            throw new IllegalArgumentException("Email and password are required");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid password");
        }

        return user;
    }

    /**
     * Get user by ID.
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Update user details.
     */
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (userDetails.getName() != null) user.setName(userDetails.getName());
        if (userDetails.getEmail() != null) user.setEmail(userDetails.getEmail());
        if (userDetails.getPassword() != null) user.setPassword(userDetails.getPassword());
        if (userDetails.getSubscriptionType() != null) {
            user.setSubscriptionType(userDetails.getSubscriptionType());
        }

        return userRepository.save(user);
    }

    /**
     * Update subscription type safely.
     */
    public User updateSubscription(Long id, SubscriptionType subscriptionType) {
        if (subscriptionType == null) {
            throw new IllegalArgumentException("Subscription type is required");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setSubscriptionType(subscriptionType);
        return userRepository.save(user);
    }

    /**
     * Cancel subscription (set to FREE).
     */
    public void cancelSubscription(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setSubscriptionType(SubscriptionType.FREE);
        userRepository.save(user);
    }

    /**
     * Check if an email is available.
     */
    public boolean isEmailAvailable(String email) {
        if (email == null || email.isBlank()) return false;
        return !userRepository.existsByEmail(email);
    }

    /**
     * Get all users.
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Delete a user by ID.
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found");
        }
        userRepository.deleteById(id);
    }
}
