package com.dacs2.service;

import com.dacs2.model.UserDtls;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {

    public UserDtls saveUser(UserDtls user);

    public UserDtls getUserByEmail(String email);

    public Page<UserDtls> getUsers(Integer pageNumber, Integer pageSize, String role);

    public UserDtls updateAccountStatus(Integer id, Boolean status);

    public void increaseFailedAttempt(UserDtls user);

    public void userAccountLock(UserDtls user);

    public boolean unlockAccountTimeExpired(UserDtls user);

    public void resetAttempt(int userId);

    void updateUserResetToken(String email, String resetToken);

    public UserDtls getUserByToken(String token);

    public UserDtls updateUser(UserDtls user);

    public UserDtls updateUserProfile(UserDtls user, MultipartFile img) throws IOException;

    public Page<UserDtls> searchUsers(Integer pageNumber, Integer pageSize, String role, String search);

    public UserDtls saveAmin(UserDtls user);

    public List<UserDtls> getAllAdmin();

    public Boolean existsEmail(String email);

    void updateConfirmEmailToken(String email, String confirmToken);

    public UserDtls addUser(UserDtls user);

    public UserDtls confirmEmail(String confirmToken);

}
