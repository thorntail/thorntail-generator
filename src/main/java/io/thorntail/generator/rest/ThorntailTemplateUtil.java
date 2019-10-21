package io.thorntail.generator.rest;

import org.thymeleaf.context.IExpressionContext;

import java.util.List;

public class ThorntailTemplateUtil {
    private final IExpressionContext ctx;

    public ThorntailTemplateUtil(IExpressionContext ctx) {
        this.ctx = ctx;
    }

    public boolean hasDependency(String string) {
        List<String> dependencies = (List<String>) ctx.getVariable("dependencies");
        for (String dependency : dependencies) {
            if (dependency.contains(string)) {
                return true;
            }
        }
        return false;
    }
}
