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
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import org.apache.commons.lang.RandomStringUtils;
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
 * Utils class use to store secure cookie. It is secure on :
 * <ul>
 * <li>Cookie Confidentiality</li>
 * <li>Replay Attacks</li>
 * <li>Volume Attacks</li>
 * </ul>
 * 
 * @author jruchaud
 */
public class SecureCookie {

    protected String SSL_SESSION_ID = "SSL_SESSION_ID";
    
    /** Secret key use in encryt cookie value */
    protected String secret;
    
    /** Enabled encrypt cookie value */
    protected boolean encrypt;
    
    /** Use ssl session id in verifed key */
    protected boolean ssl;
    
    public SecureCookie() {
        this.secret = generateSecret();
        this.encrypt = true;
        this.ssl = true;
    }

    public SecureCookie(boolean encrypt, boolean ssl) {
        this.secret = generateSecret();
        this.encrypt = encrypt;
        this.ssl = ssl;
    }
    
    public SecureCookie(String secret, boolean encrypt, boolean ssl) {
        this.secret = secret;
        this.encrypt = encrypt;
        this.ssl = ssl;
    }
    
    /**
     * Generate a new secret key.
     * 
     * @return secret key;
     */
    protected String generateSecret() {
        return RandomStringUtils.random(31, true, true);
    }

    /**
     * Create a new cookie secured.
     * 
     * @param name cookie name
     * @param value cookie value
     * @param username username
     * @param expiry max age
     * @return wrapper a cookie
     */
    public SecureCookieEntity create(String name, String value, String username, int expiry) {
        SecureCookieEntity wrapper = new SecureCookieEntity(name, value, username, expiry);
        return wrapper;
    }

    /**
     * Wrap the cookie to get value.
     * 
     * @param cookie cookie
     * @return wrapper a cookie
     */
    public SecureCookieEntity wrap(Cookie cookie) {
        SecureCookieEntity wrapper = new SecureCookieEntity(cookie);
        return wrapper;
    }

    /**
     * Wrapper on cookie to add manage secure value.
     */
    public class SecureCookieEntity {
        
        protected Cookie cookie;

        public SecureCookieEntity(Cookie cookie) {
            this.cookie = cookie;
        }

        public SecureCookieEntity(String name, String value, String username, int expiry) {
            String secureValue = getSecureValue(value, username, expiry);
            this.cookie = new Cookie(name, secureValue);
        }
        
        public void setValue(String value, String username, int expiry) {
            String secureValue = getSecureValue(value, username, expiry);
            this.cookie.setValue(secureValue);
            this.cookie.setMaxAge(expiry);
        }

        public String getValue() {
            String cookieValue = cookie.getValue();
            String value = getUnsecureValue(cookieValue);
            return value;
        }

        public void setComment(String purpose) {
            cookie.setComment(purpose);
        }

        public String getComment() {
            return cookie.getComment();
        }

        public void setDomain(String domain) {
            cookie.setDomain(domain);
        }

        public String getDomain() {
            return cookie.getDomain();
        }

        public int getMaxAge() {
            return cookie.getMaxAge();
        }

        public void setPath(String uri) {
            cookie.setPath(uri);
        }

        public String getPath() {
            return cookie.getPath();
        }

        public void setSecure(boolean flag) {
            cookie.setSecure(flag);
        }

        public boolean getSecure() {
            return cookie.getSecure();
        }

        public String getName() {
            return cookie.getName();
        }

        public int getVersion() {
            return cookie.getVersion();
        }

        public void setVersion(int version) {
            cookie.setVersion(version);
        }

        public void setHttpOnly(boolean isHttpOnly) {
            cookie.setHttpOnly(isHttpOnly);
        }

        public boolean isHttpOnly() {
            return cookie.isHttpOnly();
        }
    }

    public boolean isEncrypt() {
        return encrypt;
    }

    public void setEncrypt(boolean encrypt) {
        this.encrypt = encrypt;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }
    
    /**
     * Get a secure value.
     * value = username|expiration time|(value)k|HMAC(username|expiration time|value|SSL session key, k) 
     * where k = HMAC(user|expire, sk)
     * and sk is server's secret key
     * (value)k is the result an cryptographic function
     * 
     * @param value value
     * @param username username
     * @param expiry expiry
     * @return secure value
     */
    protected String getSecureValue(String value, String username, int expiry) {
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
    protected String getUnsecureValue(String secureValue) {
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

            byte[]   keyBytes = key.getBytes();
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