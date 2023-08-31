package com.example.undertowdemo;


import com.example.undertowdemo.controller.HelloServlet;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.jsp.HackInstanceManager;
import io.undertow.jsp.JspServletBuilder;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import org.apache.jasper.deploy.JspPropertyGroup;
import org.apache.jasper.deploy.TagLibraryInfo;

import java.io.File;
import java.util.HashMap;

public class Application  {

    public static void main(String[] args) throws Exception {

        File base = new File("src/main/webapp");
        FileResourceManager fileResourceManager = new FileResourceManager(base);


        DeploymentInfo servletBuilder = Servlets.deployment()
                .setClassLoader(Application.class.getClassLoader())
                .setContextPath("/")
                .setDeploymentName("demo.war")
                // 添加静态/JSP等资源的访问管理器
                .setResourceManager(fileResourceManager)
                .addServlets(
                        Servlets.servlet("MyServlet", HelloServlet.class).addMapping("/hello"),
                        JspServletBuilder.createServlet("JspServlet", "*.jsp")
                );

        JspServletBuilder.setupDeployment(servletBuilder, new HashMap<String, JspPropertyGroup>(), new HashMap<String, TagLibraryInfo>(), new HackInstanceManager());

        DeploymentManager manager = Servlets.defaultContainer().addDeployment(servletBuilder);
        manager.deploy();
        PathHandler path = Handlers.path(Handlers.redirect("/demo")).addPrefixPath("/demo", manager.start());

        Undertow server = Undertow.builder()
                .addHttpListener(8081, "localhost")
                .setHandler(path)
                .build();
        server.start();
    }



}
