/*
 * #%L
 * Webmotion server
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

import org.apache.commons.fileupload.ProgressListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The listener is created for multi-part form to send the progress. It is 
 * stored in session.
 * 
 * @see org.apache.commons.fileupload.FileUpload
 * @author julien
 */
public class FileProgressListener implements ProgressListener {

    private static final Logger log = LoggerFactory.getLogger(FileProgressListener.class);
    
    /** Attribute name where the listener is stored */
    public static String SESSION_ATTRIBUTE_NAME = "fileProgressListener";
    
    protected long bytesRead;
    protected long contentLength;
    protected int items;

    @Override
    public void update(long bytesRead, long contentLength, int items) {
        this.bytesRead = bytesRead;
        this.contentLength = contentLength;
        this.items = items;
        
        log.info("FileProgressListener : " 
                + bytesRead + " bytesRead, " 
                + contentLength + " contentLength, " 
                + items + " items");
    }

    public long getBytesRead() {
        return bytesRead;
    }

    public long getContentLength() {
        return contentLength;
    }

    public int getItems() {
        return items;
    }
    
}
