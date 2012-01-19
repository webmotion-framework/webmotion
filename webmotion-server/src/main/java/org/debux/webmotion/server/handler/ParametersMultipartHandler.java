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
package org.debux.webmotion.server.handler;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.ArrayUtils;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.WebMotionHandler;
import org.debux.webmotion.server.WebMotionException;
import org.debux.webmotion.server.WebMotionServerContext;
import org.debux.webmotion.server.call.FileProgressListener;
import org.debux.webmotion.server.call.UploadFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extract parameter in request, when it is a multipart request. Use apache 
 * FileUpload to process.
 * 
 * @author julien
 */
public class ParametersMultipartHandler implements WebMotionHandler {

    private static final Logger log = LoggerFactory.getLogger(ParametersMultipartHandler.class);

    @Override
    public void init(Mapping mapping, WebMotionServerContext context) {
        // do nothing
    }

    @Override
    public void handle(Mapping mapping, Call call) {
        HttpContext context = call.getContext();
        HttpServletRequest request = context.getRequest();

        Map<String, Object> extractParameters = new HashMap<String, Object>();
        call.setExtractParameters(extractParameters);

        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if(isMultipart) {
            FileItemFactory fileItemFactory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(fileItemFactory);

            HttpSession session = request.getSession();
            if(session != null) {
                log.info("Set file upload listener");
                FileProgressListener listener = new FileProgressListener();
                upload.setProgressListener(listener);
                session.setAttribute(FileProgressListener.SESSION_ATTRIBUTE_NAME, listener);
                call.setFileUploadRequest(true);
            } else {
                log.warn("No session for file upload listener");
            }

            try {
                List<DiskFileItem> items = upload.parseRequest(request);
                for (DiskFileItem item : items) {
                    String fieldName = item.getFieldName();

                    if (item.isFormField()) {
                        String fieldValue = item.getString();

                        String[] values = (String[]) extractParameters.get(fieldName);
                        if(values == null) {
                            values = new String[] {fieldValue};
                            extractParameters.put(fieldName, values);

                        } else {
                            values = (String[]) ArrayUtils.add(values, fieldValue);
                            extractParameters.put(fieldName, values);
                        }

                    } else {
                        UploadFile uploadFile = new UploadFile();
                        
                        File file = item.getStoreLocation();
                        uploadFile.setFile(file);
                        
                        String fileName = item.getName();
                        uploadFile.setName(fileName);

                        long fileSize = item.getSize();
                        uploadFile.setSize(fileSize);
                        
                        String fileType = item.getContentType();
                        uploadFile.setContentType(fileType);
                        
                        extractParameters.put(fieldName, uploadFile);
                    }
                }
            } catch (FileUploadException fue) {
                throw new WebMotionException("Error during upload file on server", fue);
            }

        } else {
            Map<String, String[]> parameters = context.getParameters();
            extractParameters.putAll(parameters);
        }
    }
}
