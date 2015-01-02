<!--
  #%L
  Webmotion test
  
  $Id$
  $HeadURL$
  %%
  Copyright (C) 2011 - 2015 Debux
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<html>
    <head>
        <title>Shiro menu</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body>
        <h1>Menu <shiro:principal/></h1>
        <div>
            <a href="<c:url value="./auth/admin/index"/>">Admin part</a>
        </div>
        <div>
            <a href="<c:url value="./auth/guest/index"/>">Guest part</a>
        </div>
        <shiro:user>
            <div>
                <a href="<c:url value="./logout"/>">Logout</a>
            </div>
        </shiro:user>
    </body>
</html>
