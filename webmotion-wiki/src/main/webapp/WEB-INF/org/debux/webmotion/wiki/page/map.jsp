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
<!DOCTYPE html>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="bundle.wikimotion"/>

<div id="main_content">
    <ul>
    <c:forEach var="map" items="${map}" >
        <c:if test="${map.key != null}">
            <li>${map.key}</li>
            <ul>
            <c:forEach var="page" items="${map.value}" >
                <li><a href="<c:url value="${url}/${map.key}/${page}"/>">${page}</a></li>
            </c:forEach>
            </ul>
        </c:if>
        <c:if test="${map.key == null}">
            <c:forEach var="page" items="${map.value}" >
                <li><a href="<c:url value="${url}/${page}"/>">${page}</a></li>
            </c:forEach>
        </c:if>
    </c:forEach>
    </ul>
</div>
