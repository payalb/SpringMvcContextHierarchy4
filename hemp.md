The WebApplicationInitializer class that we’ve seen earlier is a general-purpose interface. It turns out that Spring provides a few more specific implementations, including an abstract class called AbstractContextLoaderInitializer.

Its job, as the name implies, is to create a ContextLoaderListener and register it with the servlet container.

We only have to tell it how to build the root context:

public class AnnotationsBasedApplicationInitializer 
  extends AbstractContextLoaderInitializer {
  
    @Override
    protected WebApplicationContext createRootApplicationContext() {
        AnnotationConfigWebApplicationContext rootContext
          = new AnnotationConfigWebApplicationContext();
        rootContext.register(RootApplicationConfig.class);
        return rootContext;
    }
}
Here we can see that we no longer need to register the ContextLoaderListener, which saves us from a little bit of boilerplate code.

Note also the use of the register method that is specific to AnnotationConfigWebApplicationContext instead of the more generic setConfigLocations: by invoking it, we can register individual @Configuration annotated classes with the context, thus avoiding package scanning.


DispatcherServlet is typically declared in web.xml with a name and a mapping:


<servlet>
    <servlet-name>normal-webapp</servlet-name>
    <servlet-class>
        org.springframework.web.servlet.DispatcherServlet
    </servlet-class>
    <load-on-startup>1</load-on-startup>
</servlet>
<servlet-mapping>
    <servlet-name>normal-webapp</servlet-name>
    <url-pattern>/api/*</url-pattern>
</servlet-mapping>
If not otherwise specified, the name of the servlet is used to determine the XML file to load. In our example, we’ll use the file WEB-INF/normal-webapp-servlet.xml.

We can also specify one or more paths to XML files, in a similar fashion to ContextLoaderListener:

<servlet>
    ...
    <init-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>/WEB-INF/normal/*.xml</param-value>
    </init-param>
</servlet>
3.2. Using web.xml and a Java Application Context
When we want to use a different type of context we proceed like with ContextLoaderListener, again. That is, we specify a contextClass parameter along with a suitable contextConfigLocation:

<servlet>
    <servlet-name>normal-webapp-annotations</servlet-name>
    <servlet-class>
        org.springframework.web.servlet.DispatcherServlet
    </servlet-class>
    <init-param>
        <param-name>contextClass</param-name>
        <param-value>
            org.springframework.web.context.support.AnnotationConfigWebApplicationContext
        </param-value>
    </init-param>
    <init-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>com.baeldung.contexts.config.NormalWebAppConfig</param-value>
    </init-param>
    <load-on-startup>1</load-on-startup>
</servlet>

Note: when we extend AbstractDispatcherServletInitializer (see section 3.4), we register both a root web application context and a single dispatcher servlet.

So, if we want more than one servlet, we need multiple AbstractDispatcherServletInitializer implementations. However, we can only define one root context, or the application won’t start.

Fortunately, the createRootApplicationContext method can return null. Thus, we can have one AbstractContextLoaderInitializer and many AbstractDispatcherServletInitializer implementations that don’t create a root context. In such a scenario, it is advisable to order the initializers with @Order explicitly.

Also, note that AbstractDispatcherServletInitializer registers the servlet under a given name (dispatcher) and, of course, we cannot have multiple servlets with the same name. So, we need to override getServletName:

@Override
protected String getServletName() {
    return "another-dispatcher";
}

We’ll define an AbstractContextLoaderInitializer to load the root context:

1
2
3
4
5
6
7
@Override
protected WebApplicationContext createRootApplicationContext() {
    AnnotationConfigWebApplicationContext rootContext
      = new AnnotationConfigWebApplicationContext();
    rootContext.register(RootApplicationConfig.class);
    return rootContext;
}
Then, we need to create the two servlets, thus we’ll define two subclasses of AbstractDispatcherServletInitializer. First, the “normal” one:

1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
@Override
protected WebApplicationContext createServletApplicationContext() {
    AnnotationConfigWebApplicationContext normalWebAppContext
      = new AnnotationConfigWebApplicationContext();
    normalWebAppContext.register(NormalWebAppConfig.class);
    return normalWebAppContext;
}
 
@Override
protected String[] getServletMappings() {
    return new String[] { "/api/*" };
}
 
@Override
protected String getServletName() {
    return "normal-dispatcher";
}
Then, the “secure” one, which loads a different context and is mapped to a different path:

1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
@Override
protected WebApplicationContext createServletApplicationContext() {
    AnnotationConfigWebApplicationContext secureWebAppContext
      = new AnnotationConfigWebApplicationContext();
    secureWebAppContext.register(SecureWebAppConfig.class);
    return secureWebAppContext;
}
 
@Override
protected String[] getServletMappings() {
    return new String[] { "/s/api/*" };
}
 
@Override
protected String getServletName() {
    return "secure-dispatcher";
}
And we’re done! We’ve just applied what we touched in previous sections.

We can do the same with web.xml, again just by combining the pieces we’ve discussed so far.

Define a root application context:

1
2
3
4
5
<listener>
    <listener-class>
        org.springframework.web.context.ContextLoaderListener
    </listener-class>
</listener>
A “normal” dispatcher context:

1
2
3
4
5
6
7
8
9
10
11
<servlet>
    <servlet-name>normal-webapp</servlet-name>
    <servlet-class>
        org.springframework.web.servlet.DispatcherServlet
    </servlet-class>
    <load-on-startup>1</load-on-startup>
</servlet>
<servlet-mapping>
    <servlet-name>normal-webapp</servlet-name>
    <url-pattern>/api/*</url-pattern>
</servlet-mapping>
And, finally, a “secure” context:

1
2
3
4
5
6
7
8
9
10
11
<servlet>
    <servlet-name>secure-webapp</servlet-name>
    <servlet-class>
        org.springframework.web.servlet.DispatcherServlet
    </servlet-class>
    <load-on-startup>1</load-on-startup>
</servlet>
<servlet-mapping>
    <servlet-name>secure-webapp</servlet-name>
    <url-pattern>/s/api/*</url-pattern>
</servlet-mapping>