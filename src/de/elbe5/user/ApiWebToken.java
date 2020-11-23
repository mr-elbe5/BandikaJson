package de.elbe5.user;

import de.elbe5.application.Application;
import de.elbe5.base.json.JwtSerializer;
import de.elbe5.base.log.Log;
import de.elbe5.servlet.ResponseException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.orgjson.io.OrgJsonDeserializer;
import org.json.JSONObject;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class ApiWebToken {

    private static final String ISSUER = "Elbe5.de";

    public static JSONObject getTokenObject(UserData user, String loginDuration) {
        JSONObject object=new JSONObject();
        object.put("token",getToken(user, loginDuration));
        object.put("id", user.getId());
        object.put("name", user.getName());
        return object;
    }

    @SuppressWarnings("unchecked")
    public static String getToken(UserData user, String loginDuration){
        assert(user!=null);
        LocalDateTime now = Application.getCurrentTime();
        LocalDateTime expiration;
        switch (loginDuration){
            case "day" : expiration = now.plusDays(1);
                break;
            case "week" : expiration = now.plusWeeks(1);
                break;
            case "month" : expiration = now.plusMonths(1);
                break;
            case "year" : expiration = now.plusHours(1);
                break;
            default: expiration = now.plusHours(1);
                break;
        }
        byte[] secretBytes = Decoders.BASE64.decode(Application.getEncodedSecretKey());
        SecretKey key = new SecretKeySpec(secretBytes, SignatureAlgorithm.HS256.getJcaName());
        Log.log(Encoders.BASE64.encode(key.getEncoded()));
        JwtBuilder builder = Jwts.builder()
                .setSubject(Application.getConfiguration().getApplicationName())
                .setId(Integer.toString(user.getId()))
                .setIssuedAt(Timestamp.valueOf(now))
                .setIssuer(ISSUER)
                .setExpiration(Timestamp.valueOf(expiration))
                .claim("name",user.getName())
                .signWith(key)
                .serializeToJsonWith(new JwtSerializer());
        return builder.compact();
    }

    public static UserData verifyToken(String token) {
        Jws<Claims> jws;
        byte[] secretBytes = Decoders.BASE64.decode(Application.getEncodedSecretKey());
        SecretKey key = new SecretKeySpec(secretBytes, SignatureAlgorithm.HS256.getJcaName());
        try{
            jws = Jwts.parserBuilder()
                    .deserializeJsonWith(new OrgJsonDeserializer())
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
        }
        catch (ExpiredJwtException ex){
            throw new ResponseException(HttpServletResponse.SC_UNAUTHORIZED);
        }
        catch (JwtException ex){
            throw new ResponseException(HttpServletResponse.SC_BAD_REQUEST);
        }
        if (!jws.getBody().getIssuer().equals(ISSUER)){
            throw new ResponseException(HttpServletResponse.SC_UNAUTHORIZED);
        }
        int id=Integer.parseInt(jws.getBody().getId());
        if (id==0){
            throw new ResponseException(HttpServletResponse.SC_UNAUTHORIZED);
        }
        UserData data = Application.getUsers().getUser(id);
        if (data==null){
            throw new ResponseException(HttpServletResponse.SC_UNAUTHORIZED);
        }
        return data;
    }

}
