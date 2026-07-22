package com.devpedrogo.redesolidaria.config;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;


@Component
public class TokenProvider {

    @Value("${jwt.expiration}")
    private long expirationTime;

    @Value("${jwt.key}")
    private String key;

    //gerar um token
    public String gerarToken(Authentication authentication){
        UserDetails user = (UserDetails) authentication.getPrincipal();

        return buildToken(user.getUsername());
    }

    private String buildToken(String username){
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSingingKey())
                .compact();
    }

    private SecretKey getSingingKey(){
        return Keys.hmacShaKeyFor(key.getBytes());

        // GARANTE QUE FUNCIONE IGUAL EM QUALQUER SERVIDOR (WINDOWS/LINUX)
        // return Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
    }

    //validar um token
    public Boolean isTokenValid(String token){
        try{
            getClaims(token);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    private Claims getClaims(String token){
        //validar assinatura
        //validar expiracao
        return Jwts.parser()
            .verifyWith(getSingingKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    //extrair informações do token
    public String getUsername(String token){
        return getClaims(token).getSubject();
    }
}

