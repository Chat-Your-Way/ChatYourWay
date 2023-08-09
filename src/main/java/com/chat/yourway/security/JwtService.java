package com.chat.yourway.security;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.chat.yourway.exception.ServiceException;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {

  @Value("${security.jwt.token-type}")
  private String tokenType;
  @Value("${security.jwt.secret-key}")
  private String secretKey;

  @Value("${security.jwt.expiration}")
  private long jwtExpiration;

  @Value("${security.jwt.refresh-token.expiration}")
  private long refreshExpiration;

  public String extractEmail(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public String generateAccessToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    return buildToken(extraClaims, userDetails, jwtExpiration);
  }

  public String generateAccessToken(UserDetails userDetails) {
    return generateAccessToken(new HashMap<>(), userDetails);
  }

  public String generateRefreshToken(UserDetails userDetails) {
    return buildToken(new HashMap<>(), userDetails, refreshExpiration);
  }

  public String extractToken(HttpServletRequest request) {
    final String authHeader = request.getHeader(AUTHORIZATION);
    final String tokenTypePrefix = tokenType + " ";

    if (isNotValidTokenType(authHeader, tokenTypePrefix)) {
      throw new ServiceException(UNAUTHORIZED, "Invalid token type");
    }
    return authHeader.substring(tokenTypePrefix.length());
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String usernameEmail = userDetails.getUsername();
    final String email = extractEmail(token);
    return email.equals(usernameEmail) && !isTokenExpired(token);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private boolean isNotValidTokenType(String authHeader, String tokenTypePrefix) {
    return authHeader == null || !authHeader.startsWith(tokenTypePrefix);
  }

  private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails,
      long expiration) {
    return Jwts
        .builder()
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
    return Jwts
        .parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private Key getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
