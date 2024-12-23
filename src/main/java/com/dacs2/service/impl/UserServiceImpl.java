package com.dacs2.service.impl;

import com.dacs2.model.UserDtls;
import com.dacs2.repository.UserRepository;
import com.dacs2.service.UserService;
import com.dacs2.util.AppConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    String imgPath = System.getProperty("user.dir") + File.separator
            + "src" + File.separator + "main" + File.separator + "resources"
            + File.separator + "static" + File.separator + "img";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDtls saveUser(UserDtls user) {
        user.setRole("ROLE_USER");
        user.setIsEnable(true);
        user.setAccountNonLocked(true);
        user.setFailedAttempt(0);
        String encodePassword =  passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
        return userRepository.save(user);
    }

    @Override
    public UserDtls confirmEmail(String confirmToken) {
        UserDtls user = userRepository.findByConfirmToken(confirmToken);
        user.setConfirmed(true);
        return userRepository.save(user);
    }

    @Override
    public UserDtls addUser(UserDtls user) {
        UserDtls findUser = userRepository.findByEmail(user.getEmail());
        if (ObjectUtils.isEmpty(findUser)) {
            user.setProfileImage("default.png");
            user.setRole("ROLE_USER");
            user.setIsEnable(true);
            user.setAccountNonLocked(true);
            user.setFailedAttempt(0);
            String encodePassword =  passwordEncoder.encode(user.getPassword());
            user.setPassword(encodePassword);
            user.setConfirmed(false);
            return userRepository.save(user);
        }
        return findUser;
    }

    @Override
    public UserDtls getUserByEmail(String email) {
        return userRepository.findByEmailAndConfirmed(email, true);
    }

    @Override
    public Page<UserDtls> getUsers(Integer pageNumber, Integer pageSize, String role) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        return userRepository.findByRole(pageable, role);
    }

    @Override
    public UserDtls updateAccountStatus(Integer id, Boolean status) {
        Optional<UserDtls> findByuser = userRepository.findById(id);

        if (findByuser.isPresent()) {
            UserDtls userDtls = findByuser.get();
            userDtls.setIsEnable(status);
            userRepository.save(userDtls);
            return userDtls;
        }
        return null;
    }

    @Override
    public void increaseFailedAttempt(UserDtls user) {
        int attempt = user.getFailedAttempt() + 1;
        user.setFailedAttempt(attempt);
        userRepository.save(user);
    }

    @Override
    public void userAccountLock(UserDtls user) {
        user.setAccountNonLocked(false);
        user.setLockTime(new Date());
        userRepository.save(user);
    }

    @Override
    public boolean unlockAccountTimeExpired(UserDtls user) {
        long lockTime = user.getLockTime().getTime();
        long unlockTime = lockTime + AppConstant.UNLOCK_DURATION_TIME;

        long currentTime = System.currentTimeMillis();

        if (currentTime > unlockTime) {
            user.setAccountNonLocked(true);
            user.setFailedAttempt(0);
            user.setLockTime(null);
            userRepository.save(user);
            return true;
        }

        return false;
    }

    @Override
    public void resetAttempt(int userId) {

    }

    @Override
    public void updateUserResetToken(String email, String resetToken) {
        UserDtls user = userRepository.findByEmailAndConfirmed(email, true);
        user.setResetToken(resetToken);
        userRepository.save(user);
    }

    @Override
    public void updateConfirmEmailToken(String email, String confirmToken) {
        UserDtls user = userRepository.findByEmailAndConfirmed(email, false);
        if (!ObjectUtils.isEmpty(user)) {
            user.setConfirmToken(confirmToken);
            userRepository.save(user);
        }
    }

    @Override
    public UserDtls getUserByToken(String token) {
        return userRepository.findByResetToken(token);
    }

    @Override
    public UserDtls updateUser(UserDtls user) {
        return userRepository.save(user);
    }

    @Override
    public UserDtls updateUserProfile(UserDtls user, MultipartFile img) throws IOException {

        UserDtls dbUser = userRepository.findById(user.getId()).get();

        if (!img.isEmpty()) {
            dbUser.setProfileImage(img.getOriginalFilename());
        }

        if (!ObjectUtils.isEmpty(dbUser)) {
            dbUser.setName(user.getName());
            dbUser.setMobileNumber(user.getMobileNumber());
            dbUser.setAddress(user.getAddress());
            dbUser.setCity(user.getCity());
            dbUser.setPrefecture(user.getPrefecture());
            dbUser.setWard(user.getWard());
            dbUser = userRepository.save(dbUser);
        }

        if (!img.isEmpty()) {

            Path path = Paths.get(imgPath + File.separator + "profile_img" + File.separator + img.getOriginalFilename());

            Files.copy(img.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        }

        return dbUser;
    }

    public static Integer convertToNumber(String str) {
        try {
            return Integer.valueOf(str);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public Page<UserDtls> searchUsers(Integer pageNumber, Integer pageSize, String role, String search) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));

        return userRepository.findByRoleAndKeyword(
                pageable, role, convertToNumber(search), search);
    }

    @Override
    public UserDtls saveAmin(UserDtls user) {
        user.setRole("ROLE_ADMIN");
        user.setIsEnable(true);
        user.setAccountNonLocked(true);
        user.setFailedAttempt(0);
        String encodePassword =  passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
        return userRepository.save(user);
    }

    @Override
    public List<UserDtls> getAllAdmin() {
        return userRepository.findByRole("ROLE_ADMIN");
    }

    @Override
    public Boolean existsEmail(String email) {
        if (ObjectUtils.isEmpty(userRepository.findByEmailAndConfirmed(email, true))) {
            return false;
        }
        return true;
    }

    @Override
    public UserDtls getFirstAdmin() {
        return userRepository.getFirstAdmin();
    }
}
