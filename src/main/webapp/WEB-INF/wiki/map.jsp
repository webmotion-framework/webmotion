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
<html>

    <head>
        <meta charset="utf-8">
        <title>WikiMotion</title>
        <link rel="icon" type="image/png" href="/wikimotion/img/collaboration.png" />
                
        <script type="text/javascript" src="/wikimotion/js/prototype.js"></script>
        
        <link rel="stylesheet" href="/wikimotion/css/classic.css" type="text/css"  media="screen">
        <link rel='stylesheet' href='http://fonts.googleapis.com/css?family=Droid+Sans:regular,bold&v1' type='text/css'>
    </head>

    <body>

        <div id="header">
            <div class="logo">WikiMotion</div>
            <div class="nav">
                <jsp:include page="/deploy/include/menu_header" />
            </div>
        </div>

        <div id="main">
            <div id="main_content">
                <c:if test="${requestScope.action == 'display'}">
                    <h1>Site map</h1>
                </c:if>
                <c:if test="${requestScope.action == 'media'}">
                    <h1>Media map</h1>
                </c:if>
                <ul>
                <c:forEach var="map" items="${requestScope.map}" >
                    <c:if test="${map.key != null}">
                        <li>${map.key}</li>
                        <ul>
                        <c:forEach var="page" items="${map.value}" >
                            <li><a href="/wikimotion/deploy/${requestScope.action}/${map.key}/${page}">${page}</a></li>
                        </c:forEach>
                        </ul>
                    </c:if>
                    <c:if test="${map.key == null}">
                        <c:forEach var="page" items="${map.value}" >
                            <li><a href="/wikimotion/deploy/${requestScope.action}/${page}">${page}</a></li>
                        </c:forEach>
                    </c:if>
                </c:forEach>
                </ul>
            </div>
        </div>

        <div id="footer">
            <div class="nav">
                <jsp:include page="/deploy/include/menu_footer" />
            </div>
            <div>Powerd by WikiMotion and WebMotion</div>
        </div>

    </body>

</html>
