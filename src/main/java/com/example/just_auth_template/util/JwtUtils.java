package com.example.just_auth_template.util;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

public class JwtUtils {
//    public static final long EXPIRE = 1000 * 60 * 60 * 24; //一天
    public static final long EXPIRE = 1000 * 60; //60秒
    public static final String APP_SECRET = "zkdlh8Ycvxmwd9sDxzc8czxcascX9";

    public static String getJwtToken(  String obj) {
        String JwtToken = Jwts.builder()
                .setHeaderParam("type", "JWT")
                .setHeaderParam("alg", "HS256")
                .setSubject("guli-user")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))

                .claim("nickname", obj)
                .signWith(SignatureAlgorithm.HS256, APP_SECRET)
                .compact();
        return JwtToken;
    }



    /**
     * 判断token是否存在与有效
     *
     * @param jwtToken
     * @return
     */
    public static boolean checkToken(String jwtToken) {
        if (StringUtils.isEmpty(jwtToken)) return false;
        try {
            Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }

    /**
     * 判断token是否存在与有效
     *
     * @param request
     * @return
     */

    public static boolean checkToken(HttpServletRequest request) {
        try {
            String jwtToken = request.getHeader("token");
            if (StringUtils.isEmpty(jwtToken)) return false;
            Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * 根据token获取id
     *
     * @param request
     * @return
     */

    public static String getMemberIdByJwtToken(HttpServletRequest request) {
        String jwtToken = request.getHeader("token");
        if (StringUtils.isEmpty(jwtToken)) return "";
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        Claims claims = claimsJws.getBody();
        return (String) claims.get("id");
    }

    /**
     *   固定串加第一次生成的token当key存到redis,token当value
     *   aop每次拦截,从redis获取value(也就是token),token如果过期就重新生成
     *
     * @param args
     */
    public static void main(String[] args) {
        String jwtToken = getJwtToken( "清风");
//        eyJ0eXBlIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJzdWIiOiJndWxpLXVzZXIiLCJpYXQiOjE2NjM3NDA5MDcsImV4cCI6MTY2Mzc0MDk2NywiaWQiOiIxIiwibmlja25hbWUiOiLmuIXpo44ifQ.A745w4cCCL3-XrDewg8B9lwdPfMjAuNkEwwGfNWIYv4
        System.out.println(jwtToken);
//        boolean b = checkToken("eyJ0eXBlIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJzdWIiOiJndWxpLXVzZXIiLCJpYXQiOjE2NjM3NDEwNjksImV4cCI6MTY2Mzc0MTEyOSwiaWQiOiIxIiwibmlja25hbWUiOiLmuIXpo44ifQ.Y6EI_1oDHq0ZM6yf5Je8ujDVe0MOAD_-QsCCADxVN38");
        boolean b = checkToken(jwtToken);
        System.out.println(b);

        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        Claims claims = claimsJws.getBody();
        claims.setExpiration(new Date(System.currentTimeMillis() + EXPIRE));
        System.out.println(claims.get("id"));
        System.out.println(claims.get("nickname"));
    }


}
