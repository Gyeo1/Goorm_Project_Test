package com.Auth.deploy.Config.Security;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;



//Exception 처리를 위한 엔트리 포인트이다.
@Component
@Slf4j
public class JwtEntryPoint implements AuthenticationEntryPoint {
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.error("Error! Unauthorized error: {}", authException.getMessage());
        log.info(request.toString());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized"); //SC_UNAUTHORIZED는 401에러이다.
    }
}
