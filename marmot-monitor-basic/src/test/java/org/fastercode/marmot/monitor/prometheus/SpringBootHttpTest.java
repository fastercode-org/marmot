package org.fastercode.marmot.monitor.prometheus;

import io.prometheus.client.CollectorRegistry;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * @author huyaolong
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {SpringBootHttpTest.App.class})
@AutoConfigureMockMvc
public class SpringBootHttpTest {
    @SpringBootApplication
    public static class App {
        public static void main(String[] args) {
            SpringApplication.run(App.class, args);
            new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(2000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.gc();
                }
            }).start();
        }
    }

    public static class PrometheusMyServlet extends io.prometheus.client.exporter.MetricsServlet {
        public PrometheusMyServlet(CollectorRegistry registry) {
            super(registry);
        }
    }

    public static class CodahaleServlet extends com.codahale.metrics.servlets.MetricsServlet {
    }

    @Configuration
    public static class CustomServletConfig {
        @Bean
        public ServletRegistrationBean prometheusMyServlet() {
            CollectorRegistry registry = new CollectorRegistry();
            registry.register(new GcCollector());

            ServletRegistrationBean bean = new ServletRegistrationBean(
                    new PrometheusMyServlet(registry),
                    "/test/prometheusMyServlet"
            );
            bean.setLoadOnStartup(1);
            return bean;
        }

        @Bean
        public ServletRegistrationBean codahaleServlet() {
            ServletRegistrationBean bean = new ServletRegistrationBean(
                    new CodahaleServlet(),
                    "/test/codahaleServlet"
            );
            bean.setLoadOnStartup(1);
            return bean;
        }
    }

    @Autowired
    protected MockMvc mvc;

    @Test
    @SneakyThrows
    public void test() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/test/prometheusMyServlet").params(params);
        MvcResult result = mvc.perform(builder).andReturn();
        System.out.println(result.getResponse().getContentAsString());
    }
}
