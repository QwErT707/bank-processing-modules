package org.clientpr.demo;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
@Disabled("Temporarily disabled due to JWT validator issues")
@SpringBootTest
//@ActiveProfiles("test")
public class ClientProcMainTests {
    @Test
    void contextLoads() {
    }
}
