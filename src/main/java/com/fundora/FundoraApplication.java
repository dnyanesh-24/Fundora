package com.fundora;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * Module III & VI: Main Application Entry Point
 * @ServletComponentScan enables dynamic discovery of @WebServlet (ExpenseServlet)
 * and @WebFilter (AuthFilter) components in embedded Tomcat container.
 */
@SpringBootApplication
@ServletComponentScan
public class FundoraApplication {

    public static void main(String[] args) {
        SpringApplication.run(FundoraApplication.class, args);
        System.out.println("==========================================================");
        System.out.println("🚀 FUNDORA SOCIAL FINTECH PROTOTYPE RUNNING ON http://localhost:8080");
        System.out.println("   - Web UI Dashboard: http://localhost:8080/dashboard");
        System.out.println("   - H2 SQL Console:   http://localhost:8080/h2-console");
        System.out.println("   - REST API Base:    http://localhost:8080/api/groups/1/ledger");
        System.out.println("==========================================================");
    }
}
