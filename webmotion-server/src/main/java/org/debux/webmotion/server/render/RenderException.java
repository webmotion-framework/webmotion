/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.debux.webmotion.server.render;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.call.HttpContext.ErrorData;
import org.debux.webmotion.server.mapping.Mapping;

/**
 * Basic render execption to user.
 * 
 * @author julien
 */
public class RenderException extends Render {

    @Override
    public void create(Mapping mapping, Call call) throws IOException, ServletException {
        HttpContext context = call.getContext();
        HttpServletRequest request = context.getRequest();
        HttpServletResponse response = context.getResponse();
        
        // Retrieve the three possible error attributes, some may be null
        ErrorData errorData = context.getErrorData();
        Integer code = errorData.getStatusCode();
        String message = errorData.getMessage();
        Class<?> type = errorData.getExceptionType();
        Throwable throwable = errorData.getException();
        String uri = errorData.getRequestUri();
        
        if (uri == null) {
            uri = request.getRequestURI(); // in case there's no URI given
        }
        
        // The error reason is either the status code or exception type
        String reason = code != null ? "Error " + code.toString() : type.toString();
        
        response.setContentType("text/html");
        
        PrintWriter out = context.getOut();
        out.println("<html>");
        out.println("<head>");
        
        out.println("<title>" + reason + "</title>");
        out.println("<style media=\"screen, projection\">");
        out.println("html { background: #e6e6e6;}" + "body { margin: 0; }" + "div { margin: 0 auto; width: 98%; }" + "#content { background: #fff; border: 2px solid #ccc; width: auto;margin-top: 1em; padding: 0em 1em 1em; }" + "h1 { margin: 1.083em 0 0; color: #333; }" + "#about { text-align: center; }");
        out.println("</style>");
        
        out.println("</head>");
        out.println("<body>");
        
        out.println("<div>");
        
        out.println("<div id=\"content\">");
        out.println("<h1> Oops! " + reason + "</h1>");
        out.println("<h2>" + message + "</h2>");
        out.println("<pre>");
        if (throwable != null) {
            throwable.printStackTrace(out);
        }
        out.println("</pre>");
        out.println("<hr>");
        out.println("<i>Error accessing " + uri + "</i>");
        out.println("</div>");
        
        out.println("<div id=\"about\">");
        out.println("WebMotion");
        out.println("</div>");
        
        out.println("</div>");
        
        out.println("</body></html>");
    }
    
}
