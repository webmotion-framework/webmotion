<!--
  #%L
  Webmotion in test
  
  $Id$
  $HeadURL$
  %%
  Copyright (C) 2011 Debux
  %%
  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as 
  published by the Free Software Foundation, either version 3 of the 
  License, or (at your option) any later version.
  
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Lesser Public License for more details.
  
  You should have received a copy of the GNU General Lesser Public 
  License along with this program.  If not, see
  <http://www.gnu.org/licenses/lgpl-3.0.html>.
  #L%
  -->
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="bundle.wikimotion"/>

<div id="main_content">
    <form id="wiki_upload" class="form-horizontal" action="<c:url value="${url}"/>" method="POST" enctype="multipart/form-data">
        <input type="hidden" name="sub" value="upload" />
        
        <div class="control-group">
            <label class="control-label" for="file"><fmt:message key="wiki.file"/></label>
            <div class="controls">
                <input class="file" id="file" name="file" type="file">
            </div>
        </div>
        
        <div id="wiki_action" class="form-actions">
            <a class="btn btn-primary" href="#" onclick="$('#wiki_upload').submit();"><fmt:message key="wiki.upload"/></a>
        </div>
    </form>
</div>
