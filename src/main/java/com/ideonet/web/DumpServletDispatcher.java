package com.ideonet.web;

import com.ideonet.beans.util.ApplicationContextUtils;
import com.ideonet.web.method.model.HandlerMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class DumpServletDispatcher extends HttpServlet {

    private HandleMapping handleMapping = new HandleMapping();
    private HandleMethodAdapter handleMethodAdapter = new HandleMethodAdapter();

    @Override
    public final void init() {
        ApplicationContextUtils.refresh();
        handleMapping.init();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        HandlerMethod handleMethod = handleMapping.getHandler(uri);
        handleMethodAdapter.handle(req, resp, handleMethod);
    }
}
