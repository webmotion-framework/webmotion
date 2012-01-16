<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<fmt:setBundle basename="bundle.wikimotion"/>

<!DOCTYPE html>
<html lang="${language}">
    <head>
        <title>${site_name}</title>

        <link rel="icon" type="image/png" href="<c:url value="/img/favicon.png"/>">
        <link rel="shortcut icon" type="image/x-icon" href="<c:url value="/img/favicon.ico"/>">
        
        <!-- Le HTML5 shim, for IE6-8 support of HTML elements -->
        <!--[if lt IE 9]>
          <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
        <![endif]-->

        <!-- Le styles -->
        <link href="http://twitter.github.com/bootstrap/1.3.0/bootstrap.min.css" rel="stylesheet">
        <style type="text/css">
            /* Override some defaults */
            html, body {
                background-color: #eee;
            }
            body {
                padding-top: 40px; /* 40px to make the container go all the way to the bottom of the topbar */
            }
            .content {
                background-color: #fff;
                padding: 20px;
                margin: 0 -20px; /* negative indent the amount of the padding to maintain the grid system */
                -webkit-border-radius: 0 0 6px 6px;
                   -moz-border-radius: 0 0 6px 6px;
                        border-radius: 0 0 6px 6px;
                -webkit-box-shadow: 0 1px 2px rgba(0,0,0,.15);
                   -moz-box-shadow: 0 1px 2px rgba(0,0,0,.15);
                        box-shadow: 0 1px 2px rgba(0,0,0,.15);
              }
        </style>

        <script type="text/javascript" src="<c:url value="/js/prototype.js"/>"></script>
    </head>

    <body>

        <div class="topbar">
            <div class="fill">
                <div class="container">
                    <a class="brand" href="<c:url value="/"/>">${site_name}</a>

                    <jsp:include page="/header?sub=include" />
                    
                    <ul class="nav secondary-nav">
                        <c:forEach items="${languages}" var="lang">
                            <li><a href="<c:url value="/?language=${lang}"/>">${lang}</a></li>
                        </c:forEach>
                        
                        <c:if test="${current_user == null}">
                            <li><a href="<c:url value="/login"/>"><fmt:message key="wiki.login"/></a></li>
                        </c:if>
                        <c:if test="${current_user != null}">
                            <li><a href="<c:url value="/logout"/>"><fmt:message key="wiki.logout"/>${current_user.name}</a></li>
                        </c:if>
                    </ul>
                </div> 
            </div>
        </div>

        <div class="container">
            <div class="content">
                <jsp:include page="${layout_url}" />
            </div>
        </div>
            
        <jsp:include page="/footer?sub=include" />

    </body>
</html>
