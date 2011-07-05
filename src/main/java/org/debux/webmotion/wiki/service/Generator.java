/*
 * #%L
 * Webmotion in wiki
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
package org.debux.webmotion.wiki.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.nuiton.jrst.JRST;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julien
 */
public enum Generator {
    
    HTML {
        public String generate(File file) throws IOException {
            String content = getContent(file);
            return content;
        }
    },
    
    RST {
        public String generate(File file) throws IOException {
            String content = getContent(file);
            String result = "Can't generate HTML from RST";
            try {
                result = JRST.generate(JRST.TYPE_HTML_INNER_BODY, content);
            } catch (Exception ex) {
                log.error("Error during parsing file", ex);
            }
            
            return result;
        }
    },
    
    TEX {
        public String generate(File file) throws IOException {
            Runtime runtime = Runtime.getRuntime();
            String[] command = {"/bin/sh", "-c", "tth -u -r <" + file.getAbsolutePath()};
            Process process = runtime.exec(command);
            
            String result = IOUtils.toString(process.getInputStream());
            return result;
        }
    };
    
    private static final Logger log = LoggerFactory.getLogger(Generator.class);
    
    public abstract String generate(File file) throws IOException;
    
    protected String getContent(File file) throws IOException {
        String content = IOUtils.toString(new FileInputStream(file));
        return content;
    }
}
