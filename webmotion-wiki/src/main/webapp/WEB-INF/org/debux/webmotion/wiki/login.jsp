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
    <form id="wiki_login" class="form-horizontal" action="<c:url value="login"/>" method="POST">
        <c:if test="${param['error.login'] == 'username'}" var="error_username">
            <c:set var="error_username_class" value="error"/>
        </c:if>
        <c:if test="${param['error.login'] == 'password'}" var="error_password">
            <c:set var="error_password_class" value="error"/>
        </c:if>
        
        <div class="control-group ${error_username_class}">
            <label class="control-label" for="username"><fmt:message key="wiki.username"/></label>
            <div class="controls">
                <input class="username" id="username" name="username" size="20" type="text">
                <c:if test="${error_username}">
                    <span class="help-inline"><fmt:message key="wiki.username.error"/></span>
                </c:if>
            </div>
        </div>
            
        <div class="control-group ${error_password_class}">
            <label class="control-label" for="password"><fmt:message key="wiki.password"/></label>
            <div class="controls">
                <input class="password" id="password" name="password" size="20" type="password">
                <c:if test="${error_password}">
                    <span class="help-inline"><fmt:message key="wiki.password.error"/></span>
                </c:if>
            </div>
        </div>
            
        <div id="wiki_action" class="form-actions">
            <a class="btn btn-primary" href="#" onclick="$('#wiki_login').submit()"><fmt:message key="wiki.login"/></a>
        </div>
    </form>
</div>
