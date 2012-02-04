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

<div id="wiki" style="text-align: center">
    <div id="wiki_message" class="alert alert-block">
        <h1><fmt:message key="wiki.page.not.found"/></h1>
    
        <form id="wiki_create" action="<c:url value="${url}"/>" method="GET">
            <input type="hidden" name="sub" value="create" />
            <div id="wiki_action">
                <select id="select" name="type">
                    <option value="html">HTML</option>
                    <option value="rst">Rst</option>
                    <option value="tex">LaTex</option>
                </select>

                <a class="btn btn-primary" href="#" onclick="$('#wiki_create').submit();"><fmt:message key="wiki.create"/></a>
            </div>
        </form>
    </div>
</div>
