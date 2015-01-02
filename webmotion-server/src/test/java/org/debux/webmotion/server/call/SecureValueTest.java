/*
 * #%L
 * Webmotion server
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2015 Debux
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

import org.debux.webmotion.server.tools.ReflectionUtilsTest;
import org.debux.webmotion.server.tools.HttpUtils;
import org.apache.commons.lang3.StringUtils;
import org.debux.webmotion.server.*;
import org.debux.webmotion.server.call.CookieManager.SecureValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

/**
 * Test on SecureValue.
 * 
 * @author julien
 */
public class SecureValueTest {
   
    private static final Logger log = LoggerFactory.getLogger(ReflectionUtilsTest.class);

    public static String secret = HttpUtils.generateSecret();
    
    @Test
    public void testHashSha1() {
        SecureValue secured = new SecureValue(secret, "username", true, true);
        String result = secured.hashSha1("test", "test");
        log.debug("result : " + result);
        AssertJUnit.assertNotNull(result);
    }
    
    @Test
    public void testRijndael() {
        String key = "0123456789abcdef0123456789abcdef";
        String value = "test";
        SecureValue secured = new SecureValue(secret, "username", true, true);
        
        String encryptValue = secured.encryptRijndael("test", key);
        log.debug("encryptValue : " + encryptValue);
        AssertJUnit.assertNotNull(encryptValue);
        
        String decryptValue = secured.decryptRijndael(encryptValue, key);
        log.debug("decryptValue : " + decryptValue);
        AssertJUnit.assertEquals(value, decryptValue);
    }
    
    @Test
    public void testSecureValue() {
        String value = "test";
        SecureValue secured = new SecureValue(secret, "username", false, true);
        
        String secureValue = secured.getSecureValue(value, -1);
        log.debug("secureValue : " + secureValue);
        AssertJUnit.assertNotNull(secureValue);
        
        String unsecureValue = secured.getUnsecureValue(secureValue);
        log.debug("unsecureValue : " + unsecureValue);
        AssertJUnit.assertEquals(value, unsecureValue);
    }
    
    @Test
    public void testEncryptSecureValue() {
        String value = "test";
        SecureValue secured = new SecureValue(secret, "username", true, true);
        
        String secureValue = secured.getSecureValue(value, -1);
        log.debug("secureValue : " + secureValue);
        AssertJUnit.assertNotNull(secureValue);
        
        String unsecureValue = secured.getUnsecureValue(secureValue);
        log.debug("unsecureValue : " + unsecureValue);
        AssertJUnit.assertEquals(value, unsecureValue);
    }
    
    @Test
    public void testInvalidKey() {
        String value = "test";
        SecureValue secured = new SecureValue(secret, "username", true, true);
        
        String secureValue = secured.getSecureValue(value, -1);
        log.debug("secureValue : " + secureValue);
        AssertJUnit.assertNotNull(secureValue);
        
        // Invalid key
        String[] split = secureValue.split("\\|");
        split[3] = "invalid key";
        secureValue = StringUtils.join(split, "|");
        
        String unsecureValue = secured.getUnsecureValue(secureValue);
        log.debug("unsecureValue : " + unsecureValue);
        AssertJUnit.assertNull(unsecureValue);
    }

    @Test
    public void testInvalidValue() {
        String value = "test";
        SecureValue secured = new SecureValue(secret, "username", true, true);
        
        String secureValue = secured.getSecureValue(value, -1);
        log.debug("secureValue : " + secureValue);
        AssertJUnit.assertNotNull(secureValue);
        
        // Invalid value
        String[] split = secureValue.split("\\|");
        split[2] = "Ij7J7G33H5xE9K5vaTiEypPnjJPuDdZ0C9QyvcIj/ZI=";
        secureValue = StringUtils.join(split, "|");
        
        String unsecureValue = secured.getUnsecureValue(secureValue);
        log.debug("unsecureValue : " + unsecureValue);
        AssertJUnit.assertNull(unsecureValue);
    }

}
