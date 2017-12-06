package cn.my.spring;


import cn.my.spring.annotation.MyController;
import cn.my.spring.annotation.MyQuatifier;
import cn.my.spring.annotation.MyRequestMapping;
import cn.my.spring.annotation.MyService;
import cn.my.spring.controller.UserController;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyDispatcherServlet extends HttpServlet {

    List<String> packageNames = new ArrayList<String>();
    Map<String, Object> instanceMap = new HashMap<String, Object>();
    Map<String, Object> handlerMap = new HashMap<String, Object>();

    public void init(ServletConfig config) {
        //扫描包
        scanPackage("cn.my.spring");

        try {
            //查找文件并实例化
            filterAndInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //拼装处理链
        handlerMap();
        try {
            //依赖注入
            ioc();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    //实例化所有对象
    private void filterAndInstance() throws Exception {
        if (packageNames.size() <= 0) {
            return;
        }
        for (String name : packageNames) {
            Class<?> cName = Class.forName(name.replace(".class", "").trim());
            if (cName.isAnnotationPresent(MyController.class)) {
                Object instance = cName.newInstance();
                MyController controller = cName.getAnnotation(MyController.class);
                String key = controller.value();
                System.out.println("key===" + key);
                instanceMap.put(key, instance);
            } else if (cName.isAnnotationPresent(MyService.class)) {
                Object instance = cName.newInstance();
                MyService service = cName.getAnnotation(MyService.class);
                String key = service.value();
                System.out.println("key===" + key);
                instanceMap.put(key, instance);
            }
        }
    }

    private void handlerMap() {
        if (instanceMap.size() <= 0) {
            return;
        }

        for (Map.Entry<String, Object> entry : instanceMap.entrySet()) {
            if (entry.getValue().getClass().isAnnotationPresent(MyController.class)) {
                MyController myController = entry.getValue().getClass().getAnnotation(MyController.class);
                String ctValue = myController.value();
                Method[] methods = entry.getValue().getClass().getMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(MyRequestMapping.class)) {
                        MyRequestMapping rm = method.getAnnotation(MyRequestMapping.class);
                        String rmValue = "/" + ctValue + "/" + rm.value();
                        System.out.println("handlerMap:key=" + rmValue);
                        handlerMap.put(rmValue, method);
                    }
                }
            }
        }
    }

    private void ioc() throws IllegalAccessException {
        if (instanceMap.isEmpty()) return;

        for (Map.Entry<String, Object> entry : instanceMap.entrySet()) {
            Field fields[] = entry.getValue().getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(MyQuatifier.class)) {
                    MyQuatifier myQuatifier = field.getAnnotation(MyQuatifier.class);
                    String value = myQuatifier.value();
                    System.out.println("ioc:key:" + entry.getValue());
                    field.set(entry.getValue(), instanceMap.get(value));
                }
            }
        }
    }

    private void scanPackage(String pk) {
        URL url = this.getClass().getClassLoader().getResource(replaceTo(pk));
        String pathFile = url.getFile();
        File file = new File(pathFile);
        String fileList[] = file.list();
        for (String path : fileList) {
            File searchFile = new File(pathFile + "/" + path);
            if (searchFile.isDirectory()) {
                scanPackage(pk + "." + searchFile.getName());
            } else {
                packageNames.add(pk + "." + searchFile.getName());
                System.out.println(pk + "." + searchFile.getName());
            }
        }
    }

    private String replaceTo(String pk) {
        return pk.replaceAll("\\.", "/");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String url = req.getRequestURI();
        String context = req.getContextPath();
        String path = url.replace(context, "");
        Method method = (Method) handlerMap.get(path);
        System.out.println("dopost:path:" + path);
        UserController controller = (UserController) instanceMap.get(path.split("/")[1]);
        try {
            method.invoke(controller, new Object[]{req, resp, null});
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

}



