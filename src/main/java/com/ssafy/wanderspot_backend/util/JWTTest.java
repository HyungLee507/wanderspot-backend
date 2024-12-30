package com.ssafy.wanderspot_backend.util;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import io.jsonwebtoken.security.Keys;

public class JWTTest {

	public static void main(String[] args) throws InterruptedException {
		String key = "ssafy-screte-key-20241101-ssafy-screte-key-20241101-ssafy-screte-key-20241118";
		SecretKey secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));

		Map<String, String> headers = new HashMap<>();
		headers.put("typ", "jwt");

//		String token = Jwts.builder().header().add(headers).and().subject("ssafy-token").claim("userId", "kimssafy")
//				.issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + 5000)).signWith(secretKey)
//				.compact();
//
//		System.out.println(token);
//		Thread.sleep(5000);
//
//		Jws<Claims> jwtClaims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
//		System.out.println(jwtClaims.getHeader());
//		System.out.println(jwtClaims.getPayload());

	}

}
