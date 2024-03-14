package com.inops.visitorpass.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import org.primefaces.PrimeFaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.inops.visitorpass.domain.Entitlements;
import com.inops.visitorpass.domain.ResetPassword;
import com.inops.visitorpass.entity.Department;
import com.inops.visitorpass.entity.Employee;
import com.inops.visitorpass.entity.MenuItemEntity;
import com.inops.visitorpass.entity.RoleEntitlement;
import com.inops.visitorpass.entity.User;
import com.inops.visitorpass.model.Role;
import com.inops.visitorpass.service.IEntitlement;
import com.inops.visitorpass.service.IMenuCategory;
import com.inops.visitorpass.service.IUserService;
import com.inops.visitorpass.service.impl.UserServiceImpl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Getter
@Setter
@Log4j2
@Component("userController")
@Scope("session")
@RequiredArgsConstructor
public class UserController {

	private final IUserService userService;
	private final IMenuCategory menuCategoryService;
	private final IEntitlement entitlementService;

	@Autowired
	ApplicationContext ctx;

	private String emailId;
	private String oldPassword;
	private String newPassword;

	private String[] selectedMenuCategories;
	private List<SelectItem> appMenuCategories;

	private Entitlements roleEntitlement;
	private List<Entitlements> roleEntitlements;
	private List<Entitlements> selectedRoleEntitlements;

	private List<RoleEntitlement> dbRoleEntitlements;

	private User selectedUser;
	private List<User> users;
	private List<User> selectedUsers;

	private List<Employee> employees;
	private String employeeId;
	private long entitlementId;
	private String roleName;

	@PostConstruct
	public void init() {
		selectedMenuCategories = new String[100];
		users = userService.findAll().get();
		dbRoleEntitlements = entitlementService.findAll().get();
		dbRoleEntitlements.forEach(entitlement -> {
			entitlement.getMenuItem().removeIf(menu -> !menu.getMenuCategory().isCustom());
		});
		employees = ((Optional<List<Employee>>) ctx.getBean("getEmployees")).get();
		rollMenue();
		getRoles();
	}

	public void openNew() {
		this.roleEntitlement = new Entitlements();
		this.selectedUser = new User();
	}

	public void resetPassword() {
		try {
			boolean isReset = userService.resetPassword(new ResetPassword(emailId, oldPassword, newPassword));
			if (isReset)
				addMessage(FacesMessage.SEVERITY_INFO, "Info Message", "Password changed successfully for: " + emailId);
			else
				addMessage(FacesMessage.SEVERITY_ERROR, "Error Message", "Old Password not matching for : " + emailId);
		} catch (Exception e) {
			addMessage(FacesMessage.SEVERITY_ERROR, "Error Message", "Failed to reset password for : " + emailId);
			log.error("exception at the time of resetPassword {}", e);
		}
		cleanUp();
	}

	private void rollMenue() {
		appMenuCategories = new ArrayList<>();
		menuCategoryService.findAll().get().forEach(menuCategori -> {
			if (menuCategori.isCustom()) {
				SelectItemGroup categories = new SelectItemGroup(menuCategori.getLabel());
				List<SelectItem> arr = menuCategori.getMenuItem().stream().filter(menu->menu.isEnable())
						.map(menu -> new SelectItem(menu.getLabel(), menu.getLabel())).collect(Collectors.toList());
				SelectItem[] objectArray = arr.toArray(new SelectItem[0]);
				categories.setSelectItems(objectArray);
				appMenuCategories.add(categories);
			}
		});
	}

	public void saveRole() {
		try {

			Set<MenuItemEntity> menuEntity = new HashSet<>();
			menuCategoryService.findAll().get().forEach(menuItem -> {
				menuItem.getMenuItem().forEach(items -> {
					String menuItemValue = Arrays.stream(roleEntitlement.getSelectedMenuCategories())
							.filter(role -> role.equals(items.getLabel())).findAny().orElse(null);
					if (menuItemValue != null) {
						menuEntity.add(items);
					}
				});
			});
			RoleEntitlement role = new RoleEntitlement(roleEntitlement.getEntitlementId(),
					roleEntitlement.getEntitlementName(), menuEntity);

			RoleEntitlement roleEnt = entitlementService.create(role).get();
			if (roleEntitlement.getEntitlementId() == 0l) {
				roleEntitlement.setEntitlementId(roleEnt.getEntitlementRoleId());
				this.roleEntitlements.add(roleEntitlement);
			} else {
				Entitlements entitlement = roleEntitlements.stream()
						.filter(ent -> ent.getEntitlementId() == roleEntitlement.getEntitlementId()).findAny()
						.orElse(null);
				entitlement.setEntitlementName(roleEntitlement.getEntitlementName());
				entitlement.setSelectedMenuCategories(roleEntitlement.getSelectedMenuCategories());
			}
			addMessage(FacesMessage.SEVERITY_INFO, "Info Message", "Entitlement Added successfully");

		} catch (Exception e) {
			addMessage(FacesMessage.SEVERITY_ERROR, "Error Message", e.getMessage());
		}
	}

	private void getRoles() {
		roleEntitlements = dbRoleEntitlements
				.stream().map(
						entitel -> new Entitlements(entitel.getEntitlementRoleId(), entitel.getEntitlementName(),
								entitel.getMenuItem().stream().map(MenuItemEntity::getLabel)
										.collect(Collectors.toList()).toArray(new String[0])))
				.collect(Collectors.toList());
	}

	public void deleteRole() {

		entitlementService.delete(roleEntitlement.getEntitlementId());
		this.roleEntitlements.remove(this.roleEntitlement);
		if (this.selectedRoleEntitlements != null) {
			this.selectedRoleEntitlements.remove(this.roleEntitlement);
		}
		this.roleEntitlement = null;
		PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
		addMessage(FacesMessage.SEVERITY_INFO, "Info Message", "Entitlemnt deleted successfully");
	}

	public void deleteRoles() {

		entitlementService.deleteAll(
				selectedRoleEntitlements.stream().map(ent -> ent.getEntitlementId()).collect(Collectors.toList()));
		this.roleEntitlements.removeAll(this.selectedRoleEntitlements);
		this.selectedRoleEntitlements = null;
		addMessage(FacesMessage.SEVERITY_INFO, "Info Message", "Entitlemnt deleted successfully");
		PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
		PrimeFaces.current().executeScript("PF('dtProducts').clearFilters()");
	}

	public String getDeleteRolesButtonMessage() {
		if (hasSelectedRoles()) {
			int size = this.selectedRoleEntitlements.size();
			return size > 1 ? size + " Entitlemnt selected" : "1 Entitlemnt selected";
		}

		return "Delete";
	}

	public boolean hasSelectedRoles() {
		return this.selectedRoleEntitlements != null && !this.selectedRoleEntitlements.isEmpty();
	}

	public void saveUser() {
		try {
			selectedUser.setRole(Role.findByValue(roleName));
			if (this.selectedUser.getId() == null) {
				selectedUser.setEmployee(employees.stream().filter(emp -> emp.getEmployeeId().equals(employeeId))
						.findAny().orElse(null));
				selectedUser.setRoleEntitlement(dbRoleEntitlements.stream()
						.filter(role -> role.getEntitlementRoleId() == entitlementId).findAny().orElse(null));
				userService.saveUser(selectedUser);
				this.users.add(selectedUser);
				addMessage(FacesMessage.SEVERITY_INFO, "Info Message", "User Added successfully");

			} else {
				User user = users.stream().filter(usr -> usr.getId() == selectedUser.getId()).findAny().orElse(null);
				if (user == null) {
					user = new User();
					user.setEmployee(employees.stream().filter(emp -> emp.getEmployeeId().equals(employeeId)).findAny()
							.orElse(null));
					user.setRoleEntitlement(dbRoleEntitlements.stream()
							.filter(role -> role.getEntitlementRoleId().equals(entitlementId)).findAny().orElse(null));
					userService.saveUser(user);
				} else {
					user.setEmployee(employees.stream().filter(emp -> emp.getEmployeeId().equals(employeeId)).findAny()
							.orElse(null));
					user.setRoleEntitlement(dbRoleEntitlements.stream()
							.filter(role -> role.getEntitlementRoleId() == entitlementId).findAny().orElse(null));
					user.setEmail(selectedUser.getEmail());
					user.setFirstName(selectedUser.getFirstName());
					user.setLastName(selectedUser.getLastName());
					user.setPassword(selectedUser.getPassword());
					user.setMobile(selectedUser.getMobile());
					userService.saveUser(user);
				}
				addMessage(FacesMessage.SEVERITY_INFO, "Info Message", "User updated successfully");
			}
		} catch (Exception e) {
			addMessage(FacesMessage.SEVERITY_ERROR, "Error Message", e.getMessage());
		}
	}

	public void deleteUser() {

		userService.delete(selectedUser);
		this.users.remove(this.selectedUser);
		if (this.selectedUser != null) {
			this.selectedUsers.remove(this.selectedUser);
		}
		this.selectedUser = null;
		PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
		addMessage(FacesMessage.SEVERITY_INFO, "Info Message", "User deleted successfully");
	}

	public void deleteUsers() {
		userService.deleteAll(this.selectedUsers);
		this.users.removeAll(this.selectedUsers);
		this.selectedUsers = null;
		addMessage(FacesMessage.SEVERITY_INFO, "Info Message", "Users deleted successfully");
		PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
		PrimeFaces.current().executeScript("PF('dtProducts').clearFilters()");
	}

	public String getDeleteUsersButtonMessage() {
		if (hasSelectedUsers()) {
			int size = this.selectedUsers.size();
			return size > 1 ? size + " Users selected" : "1 User selected";
		}

		return "Delete";
	}

	public boolean hasSelectedUsers() {
		return this.selectedUsers != null && !this.selectedUsers.isEmpty();
	}

	public void getFirstAndLastName() {
		Employee employee = employees.stream().filter(emp -> emp.getEmployeeId().equals(employeeId)).findAny()
				.orElse(null);
		selectedUser.setFirstName(employee.getEmployeeName());
	}

	private void cleanUp() {
		setEmailId(null);
		setNewPassword(null);
		setOldPassword(null);
	}

	public void addMessage(FacesMessage.Severity severity, String summary, String detail) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
	}

}
