package com.dacs2.config;

import com.dacs2.model.UserDtls;
import com.dacs2.repository.UserRepository;
import com.dacs2.service.UserService;
import com.dacs2.util.AppConstant;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthFailureHandlerImpl extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        String  email = request.getParameter("username");

        UserDtls userDtls = userRepository.findByEmailAndConfirmed(email, true);

        if (userDtls != null) {
            if (userDtls.getIsEnable()) {

                if (userDtls.getAccountNonLocked()) {

                    if (userDtls.getFailedAttempt() < AppConstant.ATTEMPT_TIME) {
                        exception = new LockedException("Tài khoản hoặc mật khẩu không đúng!");
                        userService.increaseFailedAttempt(userDtls);
                    } else {
                        userService.userAccountLock(userDtls);
                        exception = new LockedException("Tài khoản của bạn đã bị khóa! || Đăng nhập thất bại!");
                    }

                } else {

                    if (userService.unlockAccountTimeExpired(userDtls)) {
                        exception = new LockedException("Tài khoản của bạn đã mở khóa! || Hãy thử đăng nhập lại!");
                    } else {
                        exception = new LockedException("Tài khoản của bạn đã bị khóa! || Hãy đăng nhập lại sau!");
                    }
                }

            } else {
                exception = new LockedException("Tài khoản của bạn không hoạt động!");
            }
        } else {
            exception = new LockedException("Tài khoản không tồn tại!");
        }

        super.setDefaultFailureUrl("/dang-nhap?error");
        super.onAuthenticationFailure(request, response, exception);
    }
}
