/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;
import com.mycompany.moxxdesignsdbconnection.entitys.User;
import exceptions.DuplicateException;
import exceptions.IncompleteDataException;
import java.util.List;
import java.util.Optional;
import repository.IUserRepository;

/**
 *
 * @author USER
 */
public class UserService {

    private final IUserRepository userRepository;

    public UserService(IUserRepository repo) {
        this.userRepository = repo;
    }

    public User registerNewUser(User user) throws DuplicateException, IncompleteDataException {
        if (invalidUser(user)) {
            throw new IncompleteDataException("El usuario debe tener un rol, nombre de usuario y contraseña válidos.");
        }

        Optional<User> existingUser = userRepository.findByUserName(user.getUserName());
        if (existingUser.isPresent()) {
            throw new DuplicateException("Ya existe un usuario con el nombre de usuario: " + user.getUserName());
        }

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    private boolean invalidUser(User user) {
        if (user.getRole() == null || user.getRole().trim().isEmpty() ||
            user.getUserName() == null || user.getUserName().trim().isEmpty() ||
            user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return true;
        }
        return false;
    }
}
