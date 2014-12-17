<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!--
  #%L
  WebMotion website
  $Id:$
  $HeadURL:$
  %%
  Copyright (C) 2011 - 2014 Debux
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
<div style="padding: 400px 0">
    <h1 class="error">
        Oups, il semble que la page ne soit pas disponible.
    </h1>

    <c:url value="/main" var="main_url"/>
    <a href="${main_url}">Retourner sur la page d'accueil</a>
</div>
