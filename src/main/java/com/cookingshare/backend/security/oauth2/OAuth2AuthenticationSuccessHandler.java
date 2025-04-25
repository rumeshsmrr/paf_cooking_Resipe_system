package com.cookingshare.backend.security.oauth2;

import com.cookingshare.backend.model.AppUser;
import com.cookingshare.backend.repository.UserRepository;
import com.cookingshare.backend.security.JwtUtil;
import com.cookingshare.backend.dto.ResponseMessageDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Map;

@Component
public class OAuth2AuthenticationSuccessHandler
    implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepo;
    private final ObjectMapper mapper = new ObjectMapper();

    public OAuth2AuthenticationSuccessHandler(JwtUtil jwtUtil,
                                              UserRepository userRepo) {
        this.jwtUtil  = jwtUtil;
        this.userRepo = userRepo;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication auth)
            throws IOException, ServletException {

        // 1) Cast principal to OAuth2User (works for both OIDC and plain OAuth2)
        OAuth2User oauthUser = (OAuth2User) auth.getPrincipal();

        // 2) Grab attributes
        Map<String, Object> attrs = oauthUser.getAttributes();
        String email = (String) attrs.get("email");
        String name  = (String) attrs.get("name");  // might be null if not provided

        if (email == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(response.getWriter(),
                new ResponseMessageDTO("OAuth2 login failed: no email returned"));
            return;
        }

        // 3) Upsert AppUser by email
        AppUser user = userRepo.findByEmail(email)
            .orElseGet(() -> {
                AppUser u = new AppUser();
                u.setEmail(email);
                u.setName(name);
                u.setProvider("GOOGLE");
                u.setRole("LEARNER");
                return userRepo.save(u);
            });

        // 4) Generate your own JWT
        String token = jwtUtil.generateToken(
            user.getId(),
            user.getEmail(),
            user.getRole()
        );

        // 5) Send it back as JSON
        response.setContentType("application/json");
        mapper.writeValue(response.getWriter(), Map.of("token", token));
    }
}
