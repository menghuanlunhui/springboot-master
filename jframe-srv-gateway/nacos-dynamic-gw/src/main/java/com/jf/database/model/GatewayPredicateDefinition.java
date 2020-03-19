package com.jf.database.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description: 路由断言定义模型
 * User: admin
 * Date: 2018-11-26
 * Time: 13:38
 */
public class GatewayPredicateDefinition {

    //断言对应的Name
    private String name;

    //配置的断言规则
    private Map<String, String> args = new LinkedHashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getArgs() {
        return args;
    }

    public void setArgs(Map<String, String> args) {
        this.args = args;
    }
}
