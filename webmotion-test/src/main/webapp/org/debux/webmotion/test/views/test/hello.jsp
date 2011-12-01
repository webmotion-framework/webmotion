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
<h1>Hello ${key1}</h1>
<h1>Hello ${key2}</h1>

Message : ${cookie.flash_info_hello.value}<br/>
Message : ${flashMessages.info_hello}<br/>
<a href="/webmotion-test/deploy/test/reload">Reload</a><br/>
<a href="/webmotion-test/deploy/test/indexWithMessage">Index</a><br/>
<a href="#" onclick="test();">Template</a><br/>

<jsp:include page="/admin"/>
<jsp:include page="../calc/index.jsp" />

<div id="template">
    Empty
</div>

<script>
    function test() {
        var xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function() { 
            if(xhr.readyState == 4 && xhr.status == 200) {
                var node = document.getElementById("template");
                node.innerHTML =  xhr.responseText;
            } 
        }; 
        
        xhr.open("GET", "/webmotion-test/deploy/test/template", true);                
        xhr.send(); 
    }
</script>
