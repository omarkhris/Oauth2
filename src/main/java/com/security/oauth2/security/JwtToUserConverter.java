package com.security.oauth2.security;



import com.security.oauth2.document.UserCred;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class JwtToUserConverter implements Converter<Jwt, UsernamePasswordAuthenticationToken> {
    @Override
    public UsernamePasswordAuthenticationToken convert(Jwt source) {
        UserCred user = new UserCred();
        user.setId(source.getSubject());
        return new UsernamePasswordAuthenticationToken(user, source, Collections.EMPTY_LIST);
    }
}
