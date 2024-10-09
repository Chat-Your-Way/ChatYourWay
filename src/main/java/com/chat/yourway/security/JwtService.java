package com.chat.yourway.security;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.chat.yourway.config.security.SecurityJwtProperties;
import com.chat.yourway.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

  private final SecurityJwtProperties jwtProperties;

  public String extractEmail(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public String generateAccessToken(UserDetails userDetails) {
    return generateAccessTokenBuild(new HashMap<>(), userDetails);
  }

  public String generateRefreshToken(UserDetails userDetails) {
    return generateRefreshTokenBuild(new HashMap<>(), userDetails);
  }

  public String extractToken(HttpServletRequest request) {
    String token = request.getHeader(AUTHORIZATION);

    if (token == null) {
      token = request.getParameter(AUTHORIZATION);
    }

    final String tokenType = jwtProperties.getTokenType();
    final String tokenTypePrefix = tokenType + " ";

    if (isNotValidTokenType(token, tokenTypePrefix)) {
      log.warn("Invalid token type, token type should be [{}]", tokenType);
      throw new InvalidTokenException("Invalid token type, token type should be [" + tokenType + "]");
    }
    return token.substring(tokenTypePrefix.length());
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    return extractEmail(token).equals(userDetails.getUsername()) && !isTokenExpired(token);
  }

  private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    return claimsResolver.apply(extractAllClaims(token));
  }

  private String generateAccessTokenBuild(Map<String, Object> extraClaims, UserDetails userDetails) {
    return buildToken(extraClaims, userDetails, jwtProperties.getAccessExpiration().toMillis());
  }

  private String generateRefreshTokenBuild(Map<String, Object> extraClaims, UserDetails userDetails) {
    return buildToken(extraClaims, userDetails, jwtProperties.getRefreshExpiration().toMillis());
  }

  private boolean isNotValidTokenType(String token, String tokenTypePrefix) {
    return token == null || !token.startsWith(tokenTypePrefix);
  }

  private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
    return Jwts.builder()
        .setClaims(extraClaims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private Key getSigningKey() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecretKey()));
  }
}