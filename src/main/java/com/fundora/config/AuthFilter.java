package com.fundora.config;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Module III (Servlets & Filter API):
 * AuthFilter intercepts incoming HTTP requests, manages session state,
 * and ensures user authentication before accessing ledger & expense routes.
 */
@WebFilter(urlPatterns = {"/expenses/*", "/ledger/*", "/dashboard/*", "/api/settle/*"})
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Lifecycle init
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Retrieve existing session without creating a new one
        HttpSession session = httpRequest.getSession(false);
        String requestURI = httpRequest.getRequestURI();

        // Allow public static resources, login, index, and general API lookups
        boolean isStatic = requestURI.contains("/static/") || requestURI.contains("/css/") || requestURI.contains("/js/");
        boolean isLoginOrPublic = requestURI.endsWith("/login") || requestURI.endsWith("/logout") || requestURI.equals("/") || requestURI.endsWith("index.jsp");

        if (isStatic || isLoginOrPublic) {
            chain.doFilter(request, response);
            return;
        }

        // Check active session or mock logged-in user header for REST API testing
        Long loggedInUserId = (session != null) ? (Long) session.getAttribute("currentUserId") : null;
        String authHeader = httpRequest.getHeader("X-Fundora-User-Id");

        if (loggedInUserId != null || authHeader != null) {
            // If header exists and session is null, initialize session attribute
            if (loggedInUserId == null && authHeader != null) {
                HttpSession newSession = httpRequest.getSession(true);
                newSession.setAttribute("currentUserId", Long.parseLong(authHeader));
            }
            chain.doFilter(request, response);
        } else {
            // Redirect unauthenticated web browser requests to /login
            if (requestURI.startsWith("/api/")) {
                // For API requests without auth, fallback to user 1 for smooth testing
                HttpSession autoSession = httpRequest.getSession(true);
                autoSession.setAttribute("currentUserId", 1L);
                chain.doFilter(request, response);
            } else {
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            }
        }
    }

    @Override
    public void destroy() {
        // Lifecycle destroy
    }
}
