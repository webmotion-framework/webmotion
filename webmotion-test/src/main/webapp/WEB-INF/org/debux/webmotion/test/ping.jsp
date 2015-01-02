<%--
  #%L
  WebMotion test
  $Id:$
  $HeadURL:$
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
  --%>
<!DOCTYPE html>
<html>
    <head>
        <title>Ping</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        
        <script>
            var connection;
            var output;
            
            function init() {
                var url = window.location.toString().replace("http", "ws").replace("index", "ws");
                connection = new WebSocket(url);
                output = document.getElementById("output");
            
                connection.onopen = function () {
                };

                connection.onclose = function () {
                };

                connection.onerror = function (error) {
                    console.log(error);
                };

                connection.onmessage = function (e) {
                    var message = JSON.parse(e.data);
                    console.log(message);
                    alert(message.result);
                };
            }
            
            function submitPing() {
                var form = document.getElementById("ping");
                var message = form.elements['message'].value;
                ping(message);
            }
            
            function ping(message) {
                var event = {
                    method : "ping",
                    params : {
                        message : message
                    }
                }
                connection.send(JSON.stringify(event));
            }
        </script>
    </head>
    <body onload="init()">
        <h1>Ping</h1>
        
        <form id="ping" onsubmit="submitPing(); return false;">
            Message <input type="text" name="message"/> 
            <input type="submit" value="Ping"/>
        </form>
        
    </body>
</html>
