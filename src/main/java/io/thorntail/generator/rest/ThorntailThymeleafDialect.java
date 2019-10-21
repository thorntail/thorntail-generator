package io.thorntail.generator.rest;

import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;

import java.util.Collections;
import java.util.Set;

public class ThorntailThymeleafDialect implements IExpressionObjectDialect {
    @Override
    public IExpressionObjectFactory getExpressionObjectFactory() {
        return new IExpressionObjectFactory() {
            @Override
            public Set<String> getAllExpressionObjectNames() {
                return Collections.singleton("thorntail");
            }

            @Override
            public Object buildObject(IExpressionContext context, String expressionObjectName) {
                return new ThorntailTemplateUtil(context);
            }

            @Override
            public boolean isCacheable(String expressionObjectName) {
                return false;
            }
        };
    }

    @Override
    public String getName() {
        return "thorntail";
    }
}
