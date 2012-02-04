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

<div id="wiki">
    <c:if test="${current_user != null}">
        <div id="wiki_action" style="text-align: right">
            <c:if test="${nameSpace == null}">
                <a class="btn btn-primary" href="<c:url value="/media?action=attach"/>"><fmt:message key="wiki.attach"/></a>
            </c:if>
            <c:if test="${nameSpace != null}">
                <a class="btn btn-primary" href="<c:url value="/media/${nameSpace}?action=attach"/>"><fmt:message key="wiki.attach"/></a>
            </c:if>
            <a class="btn btn-primary" href="<c:url value="${url}?action=edit"/>"><fmt:message key="wiki.edit"/></a>
        </div>
    </c:if>

    <c:if test="${!param.toc}">
        <jsp:include page="${url}?sub=include" />
    </c:if>
    
    <c:if test="${param.toc}">
        <div id="wiki_toc">
            <h2><fmt:message key="wiki.toc"/></h2>
            <div id="generated-toc" class="generate_from_h1 generate_for_wiki_content"></div>
        </div>

        <div id="wiki_content">
            <jsp:include page="${url}?sub=include" />
        </div>
    </c:if>
</div>
