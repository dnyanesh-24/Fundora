package com.fundora.controller;

import com.fundora.model.User;
import com.fundora.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Module III (Servlets, HTTP Session & Authentication Management):
 * LoginServlet handles user sign-in, multi-persona quick switching (Students, Hostel Manager),
 * session creation/invalidation, and view dispatching to login.jsp.
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login", "/logout"})
public class LoginServlet extends HttpServlet {

    private UserRepository userRepository;

    @Override
    public void init() throws ServletException {
        super.init();
        WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
        this.userRepository = ctx.getBean(UserRepository.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String path = request.getServletPath();

        if ("/logout".equals(path)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate(); // Destroy session
            }
            response.sendRedirect(request.getContextPath() + "/login?loggedOut=true");
            return;
        }

        // Render login page with list of available personas for instant demo login
        List<User> demoUsers = userRepository.findAll();
        request.setAttribute("demoUsers", demoUsers);
        request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String quickUserId = request.getParameter("quickUserId");
        String emailOrPhone = request.getParameter("emailOrPhone");

        User selectedUser = null;

        if (quickUserId != null && !quickUserId.trim().isEmpty()) {
            // Quick-switch 1-click persona login
            Long userId = Long.parseLong(quickUserId);
            selectedUser = userRepository.findById(userId).orElse(null);
        } else if (emailOrPhone != null && !emailOrPhone.trim().isEmpty()) {
            // Standard form lookup by email or phone
            Optional<User> byEmail = userRepository.findByEmail(emailOrPhone.trim().toLowerCase());
            if (byEmail.isPresent()) {
                selectedUser = byEmail.get();
            } else {
                Optional<User> byPhone = userRepository.findByPhoneNumber(emailOrPhone.trim());
                if (byPhone.isPresent()) {
                    selectedUser = byPhone.get();
                }
            }
        }

        if (selectedUser != null) {
            // Create authenticated session
            HttpSession session = request.getSession(true);
            session.setAttribute("currentUserId", selectedUser.getId());
            session.setAttribute("currentUser", selectedUser);
            session.setAttribute("userName", selectedUser.getName());
            session.setAttribute("userRole", selectedUser.getRole());
            
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } else {
            // Authentication failure
            request.setAttribute("errorMessage", "User not found. Please select a valid profile or check credentials.");
            List<User> demoUsers = userRepository.findAll();
            request.setAttribute("demoUsers", demoUsers);
            request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
        }
    }
}
