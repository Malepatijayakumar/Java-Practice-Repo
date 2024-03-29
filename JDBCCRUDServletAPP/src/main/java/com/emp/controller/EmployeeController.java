package com.emp.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Objects;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.emp.Util.Constants;
import com.emp.dto.Employee;
import com.emp.service.IEmployeeService;
import com.emp.servicefactory.EmployeeServiceFactory;

@WebServlet("/controller/*")
public class EmployeeController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Long id = null;
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			doProcess(request,response);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			doProcess(request,response);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void doProcess(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException, NumberFormatException, ServletException {
		IEmployeeService employeeService = EmployeeServiceFactory.getEmployeeService();
		String pathInfo = request.getPathInfo();
		if (pathInfo.endsWith(Constants.add)) {
			Employee emp = new Employee(request.getParameter("name"), Integer.parseInt(request.getParameter("age")),
					request.getParameter("address"), Double.parseDouble(request.getParameter("salary")));
			addEmployee(emp,employeeService,response);
		} else if (pathInfo.endsWith(Constants.search)) {
			searchEmployee(Long.parseLong(request.getParameter("empId")),employeeService,response);
		} else if (pathInfo.endsWith(Constants.edit)) {
			id = Long.parseLong(request.getParameter("empId"));
			getEmployee(id,employeeService,response);
		} else if (pathInfo.endsWith(Constants.delete)) {
			deleteEmployee(Long.parseLong(request.getParameter("empId")),employeeService,response,request);
		}else if(pathInfo.endsWith(Constants.update)) {
			Employee emp = new Employee(request.getParameter("name"), Integer.parseInt(request.getParameter("age")),
					request.getParameter("address"), Double.parseDouble(request.getParameter("salary")));
			emp.setId(id);
			updateEmployee(emp,employeeService,response);
		}
	}
	
	public void updateEmployee(Employee employee, IEmployeeService employeeService, HttpServletResponse response) throws IOException, SQLException {
		PrintWriter out = response.getWriter();
		if(Objects.nonNull(employee)) {
			String output = employeeService.updateEmployee(employee);
			if(Constants.SUCCESS.equalsIgnoreCase(output)) {			
				out.println("<center><form style='color:green' align='center'><h1 >Successfully Updated Employee</h1></form></center>");
			}else {
				out.println("<center><form style='color:red' align='center'><h1 >Updating Employee as been failed,please try Again</h1></form></center>");
			}
		}
	}

	public void addEmployee(Employee emp , IEmployeeService employeeService, HttpServletResponse response) throws SQLException, IOException {
		String output = employeeService.addEmployee(emp);
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		if(Constants.SUCCESS.equalsIgnoreCase(output)) {			
			out.println("<form style='color:green' align='center'><h1 >Successfully Added Employee</h1></form>");
		}else {
			out.println("<form style='color:red' align='center'><h1 >Adding Employee as been failed,please try Again</h1></form>");
		}
		out.close();
	}
	
	public void searchEmployee(Long id , IEmployeeService employeeService,HttpServletResponse response) throws IOException, SQLException {
		Employee emp = employeeService.searchEmployee(id);
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		if(Objects.nonNull(emp)) {
			out.println("<center>");
			out.println("<br/><br/>");
			out.println("<h1>Employee Details</h1>");
			out.println("<table border=5px>");
			out.println("<tr><th>Name</th><td>"+emp.getName()+"</td></tr>");
			out.println("<tr><th>Age</th><td>"+emp.getAge()+"</td></tr>");
			out.println("<tr><th>Address</th><td>"+emp.getAddress()+"</td></tr>");
			out.println("<tr><th>Salary</th><td>"+emp.getSalary()+"</td></tr>");
			out.println("</table>");
			out.println("</center>");
		}else {
			out.println("<center><h1 style='color:red'>Sorry did not found Employee with provided Id, please try again</h1></center>");
		}
		out.close();
	}
	
	public void getEmployee(Long id, IEmployeeService employeeService, HttpServletResponse response)
			throws IOException, SQLException {
		PrintWriter out = response.getWriter();
		Employee emp = employeeService.searchEmployee(id);
		response.setContentType("text/html");
		if (Objects.nonNull(emp)) {
			out.println("<form method=\"get\" action='./controller/update'>");
			out.println("<center><h1>Employee Details</h1><table border=5px><tr><td>ID</td><td>" + emp.getId()
					+ "</td></tr>");
			out.println("<tr><th>Name</th><td><input type='text' name='name' value=" + emp.getName() + "></input></td></tr>");
			out.println("<tr><th>Age</th><td><input type='Number' name='age' value=" + emp.getAge() + "></input></td></tr>");
			out.println("<tr><th>Address</th><td><input type='text' name='address' value=" + emp.getAddress() + "></input></td></tr>");
			out.println("<tr><th>Salary</th><td><input type='Number' name='salary' value=" + emp.getSalary() + "></input></td></tr>");
			out.println("</table>");
			out.println("<input style='margin-top:10px' type='submit' value='Submit' />");
			out.println("</center>");
			out.println("</form>");
		}else {
			out.println("<center><h1 style='color:red'>Sorry did not found Employee with provided Id, please try again</h1></center>");
		}
		out.close();
	}
	
	/**
	 * @param id
	 * @param employeeService
	 * @param response
	 * @param request
	 * @throws IOException
	 * @throws ServletException
	 */
	/* Shifted static Response to HTML pages , and to fetch response we are using RequestDispatcher*/
	public void deleteEmployee(Long id , IEmployeeService employeeService,HttpServletResponse response , HttpServletRequest request) throws IOException, ServletException { PrintWriter out = response.getWriter();
		RequestDispatcher reqDis = null;
		response.setContentType("text/html");
		if(Objects.nonNull(id)) {
			String output = employeeService.deleteEmployee(id);
			if(Objects.nonNull(output) && Constants.SUCCESS.equalsIgnoreCase(output)) {
				reqDis = request.getRequestDispatcher("../deleteSuccess.html");
				reqDis.forward(request,response);
			}else {
				reqDis = request.getRequestDispatcher("../deleteFailure.html");
				reqDis.forward(request,response);
			}
		}
		out.close();
	}
}
