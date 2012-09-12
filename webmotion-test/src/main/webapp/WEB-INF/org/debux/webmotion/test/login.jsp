<!--
  #%L
  Webmotion test
  
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
    <head>
        <title>Shiro login</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body>
        <h1>Login</h1>
        <p>
            Admin account (username/password) : admin/admin<br/>
            Guest account (username/password) : guest/guest
        </p>
        <c:if test="${param.error}">
            <p style="color: firebrick">The username or password is incorrect !</p>
        </c:if>
        <form>
            <div>
                Username : <input type="text" name="username"/>
            </div>
            <div>
                Password : <input type="password" name="password"/>
            </div>
            <div>
                Remember me : <input type="checkbox" name="rememberMe" value="true"/>
            </div>
            <div>
                <input type="submit" value="login"/>
            </div>
        </form>
    </body>
</html>
