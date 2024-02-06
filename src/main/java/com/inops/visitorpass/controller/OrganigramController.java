package com.inops.visitorpass.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.primefaces.PrimeFaces;
import org.primefaces.component.organigram.OrganigramHelper;
import org.primefaces.event.organigram.OrganigramNodeCollapseEvent;
import org.primefaces.event.organigram.OrganigramNodeDragDropEvent;
import org.primefaces.event.organigram.OrganigramNodeExpandEvent;
import org.primefaces.event.organigram.OrganigramNodeSelectEvent;
import org.primefaces.model.DefaultOrganigramNode;
import org.primefaces.model.OrganigramNode;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.inops.visitorpass.entity.Department;
import com.inops.visitorpass.entity.Employee;
import com.inops.visitorpass.entity.EmployeeNode;
import com.inops.visitorpass.entity.Organigram;
import com.inops.visitorpass.service.IDepartment;
import com.inops.visitorpass.service.IEmployeeNode;
import com.inops.visitorpass.service.IOrganigram;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Getter
@Setter
@Log4j2
@Component("organigramController")
@Scope("session")
@RequiredArgsConstructor
public class OrganigramController {

	private final ApplicationContext ctx;
	private final IDepartment departmentService;
	private final IOrganigram organigramService;
	private final IEmployeeNode employeeNodeService;

	private OrganigramNode rootNode;
	private OrganigramNode selection;

	private boolean zoom = false;
	private String style = "width: 800px";
	private int leafNodeConnectorHeight = 0;
	private boolean autoScrollToSelection = false;

	private Organigram selectedOrganigram;

	private String department;
	private String reportingManaget;
	private String[] reporties;

	private String type;
	private String employeeName;
	private List<Employee> employees;
	private List<Employee> selectedEmployees;
	private List<Employee> reportiesEmployees;
	private List<Department> departments;

	private List<Organigram> organigrams;

	@PostConstruct
	public void init() {
		departments = departmentService.findAll().get();
		employees = ((Optional<List<Employee>>) ctx.getBean("getEmployees")).get();
		loadData();
		/*
		 * selection = new DefaultOrganigramNode(null, "Ridvan Agar", null);
		 * 
		 * OrganigramNode softwareDevelopment = addDivision(rootNode,
		 * "Software Development", "Ridvan Agar");
		 * 
		 * OrganigramNode teamJavaEE = addDivision(softwareDevelopment, "Team JavaEE");
		 * addDivision(teamJavaEE, "JSF", "Thomas Andraschko"); addDivision(teamJavaEE,
		 * "Backend", "Marie Louise");
		 * 
		 * OrganigramNode teamMobile = addDivision(softwareDevelopment, "Team Mobile");
		 * addDivision(teamMobile, "Android", "Andy Ruby"); addDivision(teamMobile,
		 * "iOS", "Stevan Jobs");
		 * 
		 * addDivision(rootNode, "Managed Services", "Thorsten Schultze",
		 * "Sandra Becker");
		 * 
		 * OrganigramNode marketing = addDivision(rootNode, "Marketing");
		 * addDivision(marketing, "Social Media", "Ali Mente", "Lisa Boehm");
		 * addDivision(marketing, "Press", "Michael Gmeiner", "Hans Peter");
		 * 
		 * addDivision(rootNode, "Management", "Hassan El Manfalouty");
		 */
	}

	public void openNew() {
		this.selectedOrganigram = new Organigram();
	}

	public void onDepartmentChange() {
		if (department != null && !"".equals(department)) {
			selectedEmployees = employees.stream().filter(emp -> emp.getDepartment().getId().equals(department))
					.collect(Collectors.toList());
		} else {
			selectedEmployees = new ArrayList<>();
		}
	}

	public void onManagerChange() {
		if (reportingManaget != null && !"".equals(reportingManaget)) {
			reportiesEmployees = employees.stream().filter(emp -> emp.getDepartment().getId().equals(department)
					&& !emp.getEmployeeId().equals(reportingManaget)).collect(Collectors.toList());
		} else {
			reportiesEmployees = new ArrayList<>();
		}
	}

	public void saveOnganization() {
		List<EmployeeNode> employeeNodes = new ArrayList<>();
		Employee manager = selectedEmployees.stream().filter(emp -> emp.getEmployeeId().equals(reportingManaget))
				.findAny().orElse(null);
		Organigram parent = new Organigram(0l, manager, "Manager", department, employeeNodes);

		Organigram parentSaved = organigramService.save(parent);

		employeeNodes = reportiesEmployees.stream()
				.filter(employ -> Arrays.asList(reporties).contains(employ.getEmployeeId()))
				.collect(Collectors.toList()).stream()
				.map(employee -> new EmployeeNode(0l, employee, "Reporter", department, parentSaved))
				.collect(Collectors.toList());
		parentSaved.setEmployeeNodes(employeeNodes);

		organigramService.save(parentSaved);
		loadData();
		PrimeFaces.current().executeScript("PF('manageProductDialog').hide()");
		PrimeFaces.current().ajax().update(":form:organigram", ":form:messages");

	}

	private void loadData() {
		rootNode = new DefaultOrganigramNode("root", "Inops", null);
		rootNode.setCollapsible(false);
		rootNode.setDroppable(true);

		List<EmployeeNode> employeeNodes = employeeNodeService.findAll().get();
		organigrams = organigramService.findAll().get();
		
		  Map<String, List<EmployeeNode> > nodesByDepartment = employeeNodes.stream()
	                .collect(Collectors.groupingBy(EmployeeNode::getDepartment));
		  
		  nodesByDepartment.forEach((k,v)->{
			  Map<Organigram, List<EmployeeNode> > nodesByManager =	  v.stream()
              .collect(Collectors.groupingBy(EmployeeNode::getOrganigramChild));
			  
		  });
			/*
			 * departments.forEach(dep -> {
			 * 
			 * OrganigramNode development = addDivision(rootNode, dep.getDepartmentName());
			 * 
			 * List<EmployeeNode> nodes = employeeNodes.stream() .filter(node ->
			 * node.getDepartment().equals(dep.getDepartmentName())).collect(Collectors.
			 * toList());
			 * 
			 * 
			 * 
			 * 
			 * 
			 * List<Organigram> orgsManager = organigrams.stream().filter(org ->
			 * org.getDepartment().equals(dep.getId())) .collect(Collectors.toList());
			 * 
			 * // OrganigramNode m = addDivision(development, );
			 * 
			 * });
			 */

		organigrams.forEach(org -> {

			OrganigramNode development = addDivision(rootNode, org.getDepartment());

			addDivision(development, org.getParentEmployee().getEmployeeName(),
					org.getEmployeeNodes().stream().map(emp -> emp.getChildEmployee().getEmployeeName())
							.collect(Collectors.toList()).toArray(new String[0]));
		});
	}

	protected OrganigramNode addDivision(OrganigramNode parent, String name, String... employees) {
		OrganigramNode divisionNode = new DefaultOrganigramNode("division", name, parent);
		divisionNode.setDroppable(true);
		divisionNode.setDraggable(true);
		divisionNode.setSelectable(true);

		if (employees != null) {
			for (String employee : employees) {
				OrganigramNode employeeNode = new DefaultOrganigramNode("employee", employee, divisionNode);
				employeeNode.setDraggable(true);
				employeeNode.setSelectable(true);
			}
		}

		return divisionNode;
	}

	public void nodeDragDropListener(OrganigramNodeDragDropEvent event) {
		FacesMessage message = new FacesMessage();
		message.setSummary("Node '" + event.getOrganigramNode().getData() + "' moved from "
				+ event.getSourceOrganigramNode().getData() + " to '" + event.getTargetOrganigramNode().getData()
				+ "'");
		message.setSeverity(FacesMessage.SEVERITY_INFO);

		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public void nodeSelectListener(OrganigramNodeSelectEvent event) {
		FacesMessage message = new FacesMessage();
		message.setSummary("Node '" + event.getOrganigramNode().getData() + "' selected.");
		message.setSeverity(FacesMessage.SEVERITY_INFO);

		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public void nodeCollapseListener(OrganigramNodeCollapseEvent event) {
		FacesMessage message = new FacesMessage();
		message.setSummary("Node '" + event.getOrganigramNode().getData() + "' collapsed.");
		message.setSeverity(FacesMessage.SEVERITY_INFO);

		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public void nodeExpandListener(OrganigramNodeExpandEvent event) {
		FacesMessage message = new FacesMessage();
		message.setSummary("Node '" + event.getOrganigramNode().getData() + "' expanded.");
		message.setSeverity(FacesMessage.SEVERITY_INFO);

		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public void removeDivision() {
		// re-evaluate selection - might be a differenct object instance if viewstate
		// serialization is enabled
		OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);
		setMessage(currentSelection.getData() + " will entfernt werden.", FacesMessage.SEVERITY_INFO);
	}

	public void removeEmployee() {
		// re-evaluate selection - might be a differenct object instance if viewstate
		// serialization is enabled
		OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);
		currentSelection.getParent().getChildren().remove(currentSelection);
	}

	public void addEmployee() {
		// re-evaluate selection - might be a differenct object instance if viewstate
		// serialization is enabled
		OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);

		OrganigramNode employee = new DefaultOrganigramNode("employee", employeeName, currentSelection);
		employee.setDraggable(true);
		employee.setSelectable(true);
	}

	private void setMessage(String msg, FacesMessage.Severity severity) {
		FacesMessage message = new FacesMessage();
		message.setSummary(msg);
		message.setSeverity(severity);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

}
