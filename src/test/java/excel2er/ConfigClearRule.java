package excel2er;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import excel2er.ui.ConfigUtil;

public class ConfigClearRule implements TestRule{
    @Override
    public Statement apply(final Statement base,final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                if (description.getAnnotation(ConfigNotClear.class) == null) {
                	ConfigUtil.clear();
                }
                base.evaluate();
            }
        };
    }
}