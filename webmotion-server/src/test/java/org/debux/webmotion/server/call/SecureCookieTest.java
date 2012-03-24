/*
 * #%L
 * Webmotion server
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Debux
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

import org.apache.commons.lang.StringUtils;
import org.debux.webmotion.server.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

/**
 * Test on SecureCookie .
 * 
 * @author julien
 */
public class SecureCookieTest {
   
    private static final Logger log = LoggerFactory.getLogger(WebMotionUtilsTest.class);

    @Test
    public void testHashSha1() {
        SecureCookie cookie = new SecureCookie();
        String result = cookie.hashSha1("test", "test");
        log.info("result : " + result);
        AssertJUnit.assertNotNull(result);
    }
    
    @Test
    public void testRijndael() {
        String key = "0123456789abcdef0123456789abcdef";
        String value = "test";
        SecureCookie cookie = new SecureCookie();
        
        String encryptValue = cookie.encryptRijndael("test", key);
        log.info("encryptValue : " + encryptValue);
        AssertJUnit.assertNotNull(encryptValue);
        
        String decryptValue = cookie.decryptRijndael(encryptValue, key);
        log.info("decryptValue : " + decryptValue);
        AssertJUnit.assertEquals(value, decryptValue);
    }
    
    @Test
    public void testSecureValue() {
        String value = "test";
        SecureCookie cookie = new SecureCookie();
        cookie.setEncrypt(false);
        
        String secureValue = cookie.getSecureValue(value, "username", -1);
        log.info("secureValue : " + secureValue);
        AssertJUnit.assertNotNull(secureValue);
        
        String unsecureValue = cookie.getUnsecureValue(secureValue);
        log.info("unsecureValue : " + unsecureValue);
        AssertJUnit.assertEquals(value, unsecureValue);
    }
    
    @Test
    public void testEncryptSecureValue() {
        String value = "test";
        SecureCookie cookie = new SecureCookie();
        cookie.setEncrypt(true);
        
        String secureValue = cookie.getSecureValue(value, "username", -1);
        log.info("secureValue : " + secureValue);
        AssertJUnit.assertNotNull(secureValue);
        
        String unsecureValue = cookie.getUnsecureValue(secureValue);
        log.info("unsecureValue : " + unsecureValue);
        AssertJUnit.assertEquals(value, unsecureValue);
    }
    
    @Test
    public void testInvalidKey() {
        String value = "test";
        SecureCookie cookie = new SecureCookie();
        cookie.setEncrypt(true);
        
        String secureValue = cookie.getSecureValue(value, "username", -1);
        log.info("secureValue : " + secureValue);
        AssertJUnit.assertNotNull(secureValue);
        
        // Invalid key
        String[] split = secureValue.split("\\|");
        split[3] = "invalid key";
        secureValue = StringUtils.join(split, "|");
        
        String unsecureValue = cookie.getUnsecureValue(secureValue);
        log.info("unsecureValue : " + unsecureValue);
        AssertJUnit.assertNull(unsecureValue);
    }

    @Test
    public void testInvalidValue() {
        String value = "test";
        SecureCookie cookie = new SecureCookie();
        cookie.setEncrypt(true);
        
        String secureValue = cookie.getSecureValue(value, "username", -1);
        log.info("secureValue : " + secureValue);
        AssertJUnit.assertNotNull(secureValue);
        
        // Invalid value
        String[] split = secureValue.split("\\|");
        split[2] = "Ij7J7G33H5xE9K5vaTiEypPnjJPuDdZ0C9QyvcIj/ZI=";
        secureValue = StringUtils.join(split, "|");
        
        String unsecureValue = cookie.getUnsecureValue(secureValue);
        log.info("unsecureValue : " + unsecureValue);
        AssertJUnit.assertNull(unsecureValue);
    }

}
