package com.dekraChallenge.dekra_challenge.aspect;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.dekraChallenge.dekra_challenge.application.ProductService;
import com.dekraChallenge.dekra_challenge.domain.model.Product;
import com.dekraChallenge.dekra_challenge.domain.port.out.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Verifies that {@link ExecutionTimeAspect} advises application/adapter beans and logs only
 * non-sensitive timing metadata.
 */
@SpringBootTest(properties = "app.demo-data.enabled=false")
class ExecutionTimeAspectTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ApplicationContext applicationContext;

    @MockitoBean
    private ProductRepository productRepository;

    private ListAppender<ILoggingEvent> appender;
    private Logger aspectLogger;

    @BeforeEach
    void setUp() {
        aspectLogger = (Logger) LoggerFactory.getLogger(ExecutionTimeAspect.class);
        appender = new ListAppender<>();
        appender.start();
        aspectLogger.addAppender(appender);
        aspectLogger.setLevel(Level.INFO);
    }

    @AfterEach
    void tearDown() {
        aspectLogger.detachAppender(appender);
    }

    @Test
    void aspect_is_registered_as_a_spring_bean() {
        assertThat(applicationContext.getBeansOfType(ExecutionTimeAspect.class))
                .as("ExecutionTimeAspect must be a single Spring-managed @Component")
                .hasSize(1);
        assertThat(applicationContext.getBean(ExecutionTimeAspect.class)).isNotNull();
    }

    @Test
    void should_log_execution_time_for_application_method() {
        when(productRepository.findAll())
                .thenReturn(List.of(new Product(1L, "Teclado", "Mecánico", new BigDecimal("100.00"))));

        productService.list();

        List<ILoggingEvent> logs = appender.list;
        assertThat(logs).isNotEmpty();
        ILoggingEvent event = logs.stream()
                .filter(e -> e.getFormattedMessage().contains("ProductService.list"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No timing log for ProductService.list"));

        assertThat(event.getLevel()).isEqualTo(Level.INFO);
        assertThat(event.getFormattedMessage())
                .contains("ProductService.list")
                .contains("executed in")
                .contains("ms")
                .contains("status=OK");
    }

    @Test
    void should_log_failed_status_when_method_throws() {
        when(productRepository.findById(99L)).thenReturn(java.util.Optional.empty());

        try {
            productService.getById(99L);
        } catch (RuntimeException ignored) {
            // expected ProductNotFoundException
        }

        ILoggingEvent event = appender.list.stream()
                .filter(e -> e.getFormattedMessage().contains("ProductService.getById"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No timing log for ProductService.getById"));

        assertThat(event.getFormattedMessage()).contains("status=FAILED");
    }

    @Test
    void should_not_log_sensitive_data() {
        when(productRepository.findAll()).thenReturn(List.of());
        productService.list();

        String allMessages = appender.list.stream()
                .map(ILoggingEvent::getFormattedMessage)
                .reduce("", (a, b) -> a + " | " + b)
                .toLowerCase();

        assertThat(allMessages)
                .doesNotContain("password")
                .doesNotContain("token")
                .doesNotContain("authorization")
                .doesNotContain("bearer")
                .doesNotContain("secret");
    }
}
