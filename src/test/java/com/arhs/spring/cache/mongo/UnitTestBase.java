/**
 * MIT License
 *
 * Copyright (c) 2016 ARHS Group
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.arhs.spring.cache.mongo;

import org.junit.After;
import org.junit.Assert;
import org.springframework.boot.test.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Unit test base.
 *
 * @arhs ARHS Spikeseed
 */
public class UnitTestBase {

    protected AnnotationConfigApplicationContext context;

    @After
    public void close() {
        if (context != null) {
            context.close();
        }
    }

    protected void assertBeanExists(Class<?> bean) {
        Assert.assertNotNull("The bean does not exist in the context.", context.containsBean(bean.getName()));
    }

    protected AnnotationConfigApplicationContext load(Class<?>[] configs, String... environment) {
        // Creates a instance of the "AnnotationConfigApplicationContext" class that represents
        // the application context.
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();

        // Adds environment.
        EnvironmentTestUtils.addEnvironment(applicationContext, environment);

        // Registers the configuration class and auto-configuration classes.
        applicationContext.register(TestConfiguration.class);
        applicationContext.register(configs);
        applicationContext.refresh();

        return applicationContext;
    }

}
