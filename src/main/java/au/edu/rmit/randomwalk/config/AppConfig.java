package au.edu.rmit.randomwalk.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author forrest0402
 * @Description
 * @date 2/5/2018
 */
@Configuration
@ComponentScan(basePackages = {"au.edu.rmit.randomwalk"})
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class AppConfig {

}

