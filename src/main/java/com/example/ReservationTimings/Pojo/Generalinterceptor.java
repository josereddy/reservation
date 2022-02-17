package com.example.ReservationTimings.Pojo;

import com.example.ReservationTimings.Services.CrudServices;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

public class Generalinterceptor implements HandlerInterceptor {
    private static final Logger log = LogManager.getLogger(Generalinterceptor.class.getName());

    private CrudServices cr_service;
public Generalinterceptor(CrudServices cr)
{
    log.info("General INTERCEPTOR: CALLED");
    cr_service =cr;

}

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.info("General Interceptor: Entered into Pre-handler method");
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);
        log.debug("GET Interceptor: Exited from Pre-handler method");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("GET Interceptor: Entered into post-handler method");
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
        long startTime = (Long)request.getAttribute("startTime");
        long endTime = System.currentTimeMillis();
        long executeTime = endTime-startTime;
        List data =new ArrayList();
        data.add(executeTime);
        data.add(request.getMethod());
        data.add(request.getRequestURI());
        cr_service.add_interceptor_data(data);
        log.debug("GET Interceptor: Exited from Post-handler  method");
    }


}
