package cn.my.spring;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ClassText {

    List<String> packageNames = new ArrayList<String>();

    public static void main(String[] args) {
        // 当前类(class)所在的包目录
        System.out.println(ClassText.class.getResource("").getFile());
        // class path根目录
        System.out.println(ClassText.class.getResource("/" + "cn/my/spring").getFile());

        // TestMain.class在<bin>/testpackage包中
        // 1.properties  在bin目录（class根目录）
        System.out.println(ClassText.class.getResource("/properties").getFile());

        // TestMain.class在<bin>/testpackage包中
        // 2.properties  在<bin>/testpackage包中
        System.out.println(ClassText.class.getResource("properties"));

        // TestMain.class在<bin>/testpackage包中
        // 3.properties  在<bin>/testpackage.subpackage包中
        //System.out.println(ClassText.class.getResource("subpackage/3.properties"));
        ClassText text = new ClassText();
        System.out.println(text.getClass().getResource("/cn/my").getPath());
        System.out.println(text.getClass().getResource("cn/my"));
        System.out.println(text.getClass().getClassLoader().getResource("/cn/my"));
        System.out.println(text.getClass().getClassLoader().getResource("cn/my").getPath());
        new ClassText().scanPackage("cn.my.spring");


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

    private String replaceTo(String path) {
        return path.replaceAll("\\.", "/");
    }


}
