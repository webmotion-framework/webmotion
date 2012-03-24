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
 * 
 * @author jruchaud
 */
public class SecureCookie {

    protected String SSL_SESSION_ID = "SSL_SESSION_ID";
    
    protected String secret = "0123456789abcdef0123456789abcdef";
    
    protected boolean encrypt;
    protected boolean ssl;

    protected Map<String, Cookie> cookies;

    /**
     * Default contructor use in test.
     */
    protected SecureCookie() {
    }
    
    public SecureCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        this.cookies = new HashMap<String, Cookie>(cookies.length);
        for (Cookie cookie : cookies) {
            String name = cookie.getName();
            this.cookies.put(name, cookie);
        }
    }
    
    public void setSecret(String secret) {
        this.secret = secret;
    }

    public boolean isEncrypt() {
        return encrypt;
    }

    public void setEncrypt(boolean encrypt) {
        this.encrypt = encrypt;
    }

    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }
    
    public void setCookie(String name, String value, String username, long expire, 
            String path, String domain, boolean secure, boolean httponly) {
        
        String secureValue = getSecureValue(value, username, expire);
        
        Cookie cookie = new Cookie(name, secureValue);
        cookie.setPath(path);
        cookie.setDomain(domain);
        cookie.setSecure(secure);
        cookie.setHttpOnly(httponly);
    }
    
    public String getCookie(String name, boolean deleteIfInvalid) {
        Cookie cookie = cookies.get(name);
        if (cookie == null) {
            return null;
        }
        
        String cookieValue = cookie.getValue();
        String value = getUnsecureValue(cookieValue);
        
        if (deleteIfInvalid && value == null) {
            deleteCookie(name, cookie.getPath(), cookie.getDomain(), 
                    cookie.getSecure(), cookie.isHttpOnly());
        }
        
        return value;
    }
    
    public void deleteCookie(String name, String path, String domain, boolean secure, boolean httponly) {
        
    }
    
    protected String getSecureValue(String value, String username, long expire) {
        String encryptValue;
        String key = hashSha1(username + expire, secret);
        if (encrypt) {
            encryptValue = encryptRijndael(value, key);
        } else {
            encryptValue = new String(Base64.encode(value.getBytes()));
        }
        
        String verifKey;
        String sessionId = System.getProperty(SSL_SESSION_ID);
        if (ssl && (sessionId != null || !sessionId.isEmpty())) {
            verifKey = hashSha1(username + expire + value + sessionId, key);
        } else {
            verifKey = hashSha1(username + expire + value, key);
        }
        
        return username + "|" +
               expire + "|" +
               encryptValue  + "|" +
               verifKey;
    }
    
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
        if (ssl && (sessionId != null || !sessionId.isEmpty())) {
            dataVerifKey = hashSha1(username + expire + data + sessionId, key);
        } else {
            dataVerifKey = hashSha1(username + expire + data, key);
        }
        
        if (verifKey.endsWith(dataVerifKey)) {
            return data;
        }

        return null;
    }
    
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