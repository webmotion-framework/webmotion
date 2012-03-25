/*
 * #%L
 * Webmotion in action
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Debux
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.debux.webmotion.server.call;

import java.security.Security;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.engines.RijndaelEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.paddings.ZeroBytePadding;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Base64;
import org.debux.webmotion.server.WebMotionException;

/**
 * Use in http context to manage cookie. The cookie can be secured.
 * It is secure on :
 * <ul>
 * <li>Confidentiality</li>
 * <li>Replay Attacks</li>
 * <li>Volume Attacks</li>
 * </ul>
 * 
 * @author jruchaud
 */
public class CookieManger {

    /** Enabled secure cookie value */
    protected SecureValue secured;
    
    /** Current http context */
    protected HttpContext context;

    /** Cookies in request */
    protected Map<String, Cookie> cookies;
    
    /**
     * Create basic cookie manager not secured.
     * @param context http context
     */
    public CookieManger(HttpContext context) {
        this.context = context;
        HttpServletRequest request = context.getRequest();
        
        this.cookies = new HashMap<String, Cookie>();
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            String name = cookie.getName();
            this.cookies.put(name, cookie);
        }
    }
    
    /**
     * Create secured cookie manager for a user.
     * @param context http context
     * @param username username used to create a verified key
     * @param encrypt if value is encrypt
     * @param ssl if ssl session id is used in verified key
     */
    public CookieManger(HttpContext context, String username, boolean encrypt, boolean ssl) {
        this(context);
        
        ServerContext serverContext = context.getServerContext();
        String secret = serverContext.getSecret();
        
        secured = new SecureValue(secret, username, encrypt, ssl);
    }
    
    /**
     * Create a new cookie, it is not store in response until call add method.
     * @param name name
     * @param value value
     * @return cookie
     */
    public CookieEntity create(String name, String value) {
        CookieEntity cookieEntity = new CookieEntity(name, value);
        return cookieEntity;
    }
    
    /**
     * Store cookie in response.
     * @param cookieEntity cookie
     */
    public void add(CookieEntity cookieEntity) {
        Cookie cookie = cookieEntity.toCookie();
        HttpServletResponse response = context.getResponse();
        response.addCookie(cookie);
        
        String name = cookie.getName();
        cookies.put(name, cookie);
    }
    
    /**
     * Get a cookie.
     * @param name name
     * @return cookie.
     */
    public CookieEntity get(String name) {
        Cookie cookie = cookies.get(name);
        if (cookie != null) {
            CookieEntity cookieEntity = new CookieEntity(cookie);
            return cookieEntity;
        }
        return null;
    }
    
    /**
     * Remove a cookie.
     * @param name name
     */
    public void remove(String name) {
        CookieEntity cookieEntity = get(name);
        if (cookieEntity != null) {
            cookieEntity.setMaxAge(0);
            cookieEntity.setValue(null);
            add(cookieEntity);
        }
    }
    
    /**
     * Wrapper represents a cookie.
     */
    public class CookieEntity {

        protected String name;
        protected String value;
        protected String path;
        protected String domain;
        protected String comment;
        protected int maxAge;
        protected boolean secure;
        
        public CookieEntity(String name, String value) {
            this.name = name;
            this.value = value;
        }
        
        public CookieEntity(Cookie cookie) {
            name = cookie.getName();
            path = cookie.getPath();
            domain = cookie.getDomain();
            comment = cookie.getComment();
            maxAge = cookie.getMaxAge();
            secure = cookie.getSecure();
            
            if (secured != null) {
                String secureValue = cookie.getValue();
                value = secured.getUnsecureValue(secureValue);
                
            } else {
                value = cookie.getValue();
            }
        }
        
        public Cookie toCookie() {
            String cookieValue = null;
            if (secured != null && value != null) {
                cookieValue = secured.getSecureValue(value, maxAge);
            } else {
                cookieValue = value;
            }
            
            Cookie cookie = new Cookie(name, cookieValue);
            cookie.setPath(path);
            cookie.setDomain(domain);
            cookie.setComment(comment);
            cookie.setMaxAge(maxAge);
            cookie.setSecure(secure);
            return cookie;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public int getMaxAge() {
            return maxAge;
        }

        public void setMaxAge(int maxAge) {
            this.maxAge = maxAge;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public boolean isSecure() {
            return secure;
        }

        public void setSecure(boolean secure) {
            this.secure = secure;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
    
    /**
     * Utils class use to store secure cookie. It is secure on :
     * <ul>
     * <li>Confidentiality</li>
     * <li>Replay Attacks</li>
     * <li>Volume Attacks</li>
     * </ul>
     * 
     * @author jruchaud
     */
    public static class SecureValue {

        /** Property name contains ssl session id */
        protected String SSL_SESSION_ID = "SSL_SESSION_ID";

        /** Secret key use in encryt cookie value */
        protected String secret;

        /** Enabled encrypt cookie value */
        protected boolean encrypt;

        /** Use ssl session id in verifed key */
        protected boolean ssl;

        /** Use as key */
        protected String username;

        public SecureValue(String secret, String username, boolean encrypt, boolean ssl) {
            this.secret = secret;
            this.username = username;
            this.encrypt = encrypt;
            this.ssl = ssl;
        }

        public boolean isEncrypt() {
            return encrypt;
        }

        public String getSecret() {
            return secret;
        }

        public boolean isSsl() {
            return ssl;
        }

        public String getUsername() {
            return username;
        }

        /**
         * Get a secure value.
         * value = username|expiration time|(value)k|HMAC(username|expiration time|value|SSL session key, k) 
         * where k = HMAC(user|expire, sk)
         * and sk is server's secret key
         * (value)k is the result an cryptographic function
         * 
         * @param value value
         * @param expiry expiry
         * @return secure value
         */
        public String getSecureValue(String value, int expiry) {
            long expire = -1;
            if (expiry > 0) {
                expire = System.currentTimeMillis() + expiry;
            }

            String encryptValue;
            String key = hashSha1(username + expire, secret);
            if (encrypt) {
                encryptValue = encryptRijndael(value, key);
            } else {
                encryptValue = new String(Base64.encode(value.getBytes()));
            }

            String verifKey;
            String sessionId = System.getProperty(SSL_SESSION_ID);
            if (ssl && sessionId != null && !sessionId.isEmpty()) {
                verifKey = hashSha1(username + expire + value + sessionId, key);
            } else {
                verifKey = hashSha1(username + expire + value, key);
            }

            return username + "|" +
                   expire + "|" +
                   encryptValue  + "|" +
                   verifKey;
        }

        /**
         * Get value if it is valid otherwise null.
         * 
         * @param secureValue secure value
         * @return value
         */
        public String getUnsecureValue(String secureValue) {
            if (secureValue == null) {
                return null;
            }

            String[] split = secureValue.split("\\|");
            if (split.length < 4) {
                return null;
            }

            long expire = Long.parseLong(split[1]);
            if (expire != -1 && expire < System.currentTimeMillis()) {
                return null;
            }

            String username = split[0];
            String value = split[2];
            String verifKey = split[3];

            String key = hashSha1(username + expire, secret);
            String data;
            if (encrypt) {
                data = decryptRijndael(value, key);
            } else {
                data = new String(Base64.decode(value));
            }

            String dataVerifKey;
            String sessionId = System.getProperty(SSL_SESSION_ID);
            if (ssl && sessionId != null && !sessionId.isEmpty()) {
                dataVerifKey = hashSha1(username + expire + data + sessionId, key);
            } else {
                dataVerifKey = hashSha1(username + expire + data, key);
            }

            if (verifKey.endsWith(dataVerifKey)) {
                return data;
            }

            return null;
        }

        /**
         * Encrypt value with key.
         * 
         * @param value value
         * @param key key
         * @return encrypt value
         */
        protected String encryptRijndael(String value, String key) {
            try {
                BlockCipher engine = new RijndaelEngine(256);
                BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(engine), new ZeroBytePadding());

                byte[] keyBytes = key.getBytes();
                cipher.init(true, new KeyParameter(keyBytes));

                byte[] input = value.getBytes();
                byte[] cipherText = new byte[cipher.getOutputSize(input.length)];

                int cipherLength = cipher.processBytes(input, 0, input.length, cipherText, 0);
                cipher.doFinal(cipherText, cipherLength);

                String result = new String(Base64.encode(cipherText));
                return result;

            } catch (Exception e) {
                throw new WebMotionException("Encrypt failed", e);
            }
        }

        /**
         * Decrypt value with key
         * 
         * @param value value
         * @param key key
         * @return  decrypt value
         */
        protected String decryptRijndael(String value, String key) {
            try {
                BlockCipher engine = new RijndaelEngine(256);
                BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(engine), new ZeroBytePadding());

                byte[] keyBytes = key.getBytes();
                cipher.init(false, new KeyParameter(keyBytes));

                byte[] output = Base64.decode(value.getBytes());
                byte[] cipherText = new byte[cipher.getOutputSize(output.length)];

                int cipherLength = cipher.processBytes(output, 0, output.length, cipherText, 0);
                int outputLength = cipher.doFinal(cipherText, cipherLength);
                outputLength += cipherLength;

                byte[] resultBytes = cipherText;
                if (outputLength != output.length) {
                    resultBytes = new byte[outputLength];
                    System.arraycopy(
                            cipherText, 0,
                            resultBytes, 0,
                            outputLength
                        );
                }

                String result = new String(resultBytes);
                return result;

            } catch (Exception e) {
                throw new WebMotionException("Encrypt failed", e);
            }
        }

        /**
         * Hash value with key.
         * 
         * @param value value
         * @param key key
         * @return hash
         */
        protected String hashSha1(String value, String key) {
            try {
                Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

                byte[] keyBytes = key.getBytes();
                SecretKey secretKey = new SecretKeySpec(keyBytes, "HMac-SHA1");

                Mac mac = Mac.getInstance("HMac-SHA1", "BC");
                mac.init(secretKey);
                mac.reset();

                byte[] input = value.getBytes();
                mac.update(input, 0, input.length);
                byte[] out = mac.doFinal();

                String result = new String(Base64.encode(out));
                return result;

            } catch (Exception e) {
                throw new WebMotionException("Hash failed", e);
            }
        }
    }
}
