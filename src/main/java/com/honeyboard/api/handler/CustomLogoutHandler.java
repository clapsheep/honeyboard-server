package com.honeyboard.api.handler;

import com.honeyboard.api.jwt.model.service.JwtService;
import com.honeyboard.api.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomLogoutHandler implements LogoutHandler {

    private static final String ACCESS_TOKEN = "access_token";
    private static final String REFRESH_TOKEN = "refresh_token";

    private final JwtService jwtService;
    private final CookieUtil cookieUtil;

    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {
        log.debug("로그아웃 처리 시작");

        try {
            // 쿠키에서 리프레시 토큰 추출
            Cookie[] cookies = request.getCookies();
            System.out.println("쿠키" + cookies + "쿠키");
            if (cookies != null) {
                log.debug("쿠키 확인 중...");
                for (Cookie cookie : cookies) {
                    if (REFRESH_TOKEN.equals(cookie.getName())) {
                        String refreshToken = cookie.getValue();
                        String userEmail = jwtService.extractUserEmail(refreshToken);
                        log.info("사용자 로그아웃 처리 중: {}", userEmail);

                        // Redis에서 리프레시 토큰 삭제
                        jwtService.invalidateRefreshToken(userEmail);
                        log.debug("Redis에서 리프레시 토큰 삭제 완료");
                        break;
                    }
                }
            } else {
                log.warn("요청에서 쿠키를 찾을 수 없습니다");
            }

            // 쿠키 삭제
            log.debug("액세스 토큰 쿠키 삭제 중...");
            cookieUtil.deleteCookie(response, ACCESS_TOKEN);
            log.debug("리프레시 토큰 쿠키 삭제 중...");
            cookieUtil.deleteCookie(response, REFRESH_TOKEN);

            // SecurityContext 초기화
            log.debug("보안 컨텍스트 초기화");
            SecurityContextHolder.clearContext();

            // 로그아웃 성공 응답
            response.setStatus(HttpServletResponse.SC_OK);
            log.info("로그아웃 처리 완료");

        } catch (Exception e) {
            log.error("로그아웃 처리 중 오류 발생: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
