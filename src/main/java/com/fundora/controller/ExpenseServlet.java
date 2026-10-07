package com.fundora.controller;

import com.fundora.dto.ExpenseRequestDTO;
import com.fundora.dto.LedgerSummaryDTO;
import com.fundora.model.Group;
import com.fundora.model.SplitType;
import com.fundora.model.User;
import com.fundora.repository.GroupRepository;
import com.fundora.repository.UserRepository;
import com.fundora.service.LedgerService;
import com.fundora.service.SavingsGoalService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Module III (Servlets, JSP & MVC Pattern):
 * ExpenseServlet serves dynamic server-side rendered JSP views,
 * handles HTTP GET/POST form submissions, and interacts with Session state.
 */
@WebServlet(name = "ExpenseServlet", urlPatterns = {"/dashboard", "/expenses", "/ledger"})
public class ExpenseServlet extends HttpServlet {

    private LedgerService ledgerService;
    private GroupRepository groupRepository;
    private UserRepository userRepository;
    private SavingsGoalService savingsGoalService;

    @Override
    public void init() throws ServletException {
        super.init();
        WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
        this.ledgerService = ctx.getBean(LedgerService.class);
        this.groupRepository = ctx.getBean(GroupRepository.class);
        this.userRepository = ctx.getBean(UserRepository.class);
        this.savingsGoalService = ctx.getBean(SavingsGoalService.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Long currentUserId = (Long) session.getAttribute("currentUserId");
        if (currentUserId == null) {
            currentUserId = 1L; // Fallback to Aarav Sharma
            session.setAttribute("currentUserId", currentUserId);
        }

        User currentUser = userRepository.findById(currentUserId).orElse(null);
        request.setAttribute("currentUser", currentUser);

        String path = request.getServletPath();
        String groupIdParam = request.getParameter("groupId");
        Long groupId = (groupIdParam != null && !groupIdParam.isEmpty()) ? Long.parseLong(groupIdParam) : 1L;

        List<Group> userGroups = groupRepository.findAll();
        request.setAttribute("groups", userGroups);
        request.setAttribute("selectedGroupId", groupId);

        if ("/ledger".equals(path) || (groupIdParam != null && !groupIdParam.isEmpty())) {
            LedgerSummaryDTO ledgerSummary = ledgerService.getGroupLedger(groupId);
            request.setAttribute("ledger", ledgerSummary);
            request.getRequestDispatcher("/WEB-INF/jsp/group-ledger.jsp").forward(request, response);
        } else {
            // Dashboard view
            LedgerSummaryDTO defaultLedger = ledgerService.getGroupLedger(groupId);
            request.setAttribute("ledger", defaultLedger);
            request.setAttribute("savingsGoals", savingsGoalService.getAllGoals());
            request.getRequestDispatcher("/WEB-INF/jsp/dashboard.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Long currentUserId = (Long) session.getAttribute("currentUserId");
        if (currentUserId == null) currentUserId = 1L;

        String action = request.getParameter("action");

        if ("addExpense".equals(action)) {
            Long groupId = Long.parseLong(request.getParameter("groupId"));
            String title = request.getParameter("title");
            BigDecimal amount = new BigDecimal(request.getParameter("amount"));
            String category = request.getParameter("category");
            String splitTypeStr = request.getParameter("splitType");

            ExpenseRequestDTO dto = new ExpenseRequestDTO();
            dto.setGroupId(groupId);
            dto.setPaidByUserId(currentUserId);
            dto.setTitle(title);
            dto.setAmount(amount);
            dto.setCategory(category);
            dto.setSplitType(splitTypeStr != null ? SplitType.valueOf(splitTypeStr) : SplitType.EQUAL);

            ledgerService.addExpense(dto);
            response.sendRedirect(request.getContextPath() + "/dashboard?groupId=" + groupId);
        } else {
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }
}
