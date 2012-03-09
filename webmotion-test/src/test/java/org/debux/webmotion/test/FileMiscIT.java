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
package org.debux.webmotion.test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

/**
 * Test misc file.
 * 
 * @author julien
 */
public class FileMiscIT extends AbstractIT {

    private static final Logger log = LoggerFactory.getLogger(FileMiscIT.class);
    
    @Test
    public void file() throws IOException, URISyntaxException {
        String url = getAbsoluteUrl("download");
        HttpPost request = new HttpPost(url);
        
        MultipartEntity entity = new MultipartEntity();
        
        URI path = getClass().getClassLoader().getResource("Outlook.png").toURI();
        File file = new File(path);
        entity.addPart("file", new FileBody(file));
        
        request.setEntity(entity);
        
        String result = execute(request);
        AssertJUnit.assertTrue(result.contains("Outlook.png is uploaded"));
    }
    
}
