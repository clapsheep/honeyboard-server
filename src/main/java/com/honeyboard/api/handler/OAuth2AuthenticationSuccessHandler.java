package com.honeyboard.api.handler;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.honeyboard.api.jwt.model.service.JwtService;
import com.honeyboard.api.user.model.User;
import com.honeyboard.api.user.model.service.UserService;
import com.honeyboard.api.util.CookieUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	
	private final JwtService jwtService;
    private final CookieUtil cookieUtil;
    private final UserService userService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        Boolean isNewUser = oAuth2User.getAttribute("isNewUser");
        
        if (isNewUser) {
            String temporaryToken = jwtService.generateTemporaryToken(email);
            response.setHeader("Authorization", "Bearer " + temporaryToken);
            response.setStatus(HttpStatus.FOUND.value());
            return;
        }
        
        User user = userService.getUserByEmail(email);
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        
        cookieUtil.addCookie(response, "access_token", accessToken, 
                (int) (jwtService.getAccessTokenExpire() / 1000));
        
        cookieUtil.addCookie(response, "refresh_token", refreshToken, 
                (int) (jwtService.getRefreshTokenExpire() / 1000));
            
        response.setStatus(HttpStatus.OK.value());
    }

}
