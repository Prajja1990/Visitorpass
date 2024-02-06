package com.inops.visitorpass.service.job;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;

import javax.annotation.PostConstruct;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.inops.visitorpass.entity.Employee;
import com.inops.visitorpass.entity.LeaveBalance;
import com.inops.visitorpass.entity.LeaveBalanceHistory;
import com.inops.visitorpass.entity.LeaveTypeEntity;
import com.inops.visitorpass.entity.ScheduledTask;
import com.inops.visitorpass.service.ILeaveBalance;
import com.inops.visitorpass.service.ILeaveBalanceHistory;
import com.inops.visitorpass.service.ILeaveType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Getter
@Setter
@Log4j2
@Service("leaveComputation")
@RequiredArgsConstructor
public class LeaveComputation implements IJob {

	private final ApplicationContext ctx;
	private final ILeaveType leaveTypeService;
	private final ILeaveBalanceHistory leaveBalanceHistoryService;
	private final ILeaveBalance leaveBalanceService;

	private List<LeaveTypeEntity> leaveTypes;
	private List<Employee> employees;

	@PostConstruct
	public void init() {
		leaveTypes = leaveTypeService.findAll().get();
		employees = ((Optional<List<Employee>>) ctx.getBean("getEmployees")).get();
	}

	@Override
	public void execute(Date from, Date to, ScheduledTask task) {
		employees.forEach(employee -> {

			leaveTypes.forEach(leaveType -> {
				LeaveBalance previousLeaveBalance = null;
				Optional<LeaveBalance> leaveBalance = leaveBalanceService.findByEmployeeAndLeaveType(employee,
						leaveType);
				if (leaveBalance.isPresent())
					previousLeaveBalance = leaveBalance.get();
				LeaveBalanceHistory leaveBalanceHistory = null;
				// Gender and marital status check
				boolean genderAndMarital = booleanFunction.test(employee.getGender(), leaveType.getApplicableGender())
						&& booleanFunction.test(employee.getMaritalStatus(), leaveType.getApplicableMaritalStatus());

				// For employees with gender and marital status
				if (genderAndMarital || leaveType.getApplicableGender().equals("All")
						|| leaveType.getApplicableMaritalStatus().equals("All")) {

					if (previousLeaveBalance == null) {
						// new balance
						Set<LeaveBalanceHistory> leaveBalanceHistorys = new HashSet<>();
						previousLeaveBalance = new LeaveBalance(0l, employee, leaveType, new Date(), 0d,
								leaveType.getLeaveCredit(), 0d, leaveType.getLeaveCredit(), 0d, 0d,
								leaveBalanceHistorys);

						leaveBalanceHistory = new LeaveBalanceHistory(0l, LocalDate.now().getMonth().getValue(),
								LocalDate.now().getYear(), new Date(), 0d, leaveType.getLeaveCredit(), 0d,
								leaveType.getLeaveCredit(), 0d, 0d, previousLeaveBalance);
						leaveBalanceHistorys.add(leaveBalanceHistory);
					}

					switch (leaveType.getLeaveCreditDuration()) {
					case "Monthly":

						leaveBalanceHistory = previousLeaveBalance.getLeaveBalanceHistorys().stream()
								.filter(history -> history.getMonth() == LocalDate.now().getMonthValue()).findAny()
								.orElse(null);
						String[] array = { "Monthly", "Quarterly", "Half yearly" };
						List<String> restType = Arrays.asList(array);

						if (leaveBalanceHistory == null) {
							previousLeaveBalance.setCarryForwardBalance(0);
							previousLeaveBalance.setPreviousBalance(previousLeaveBalance.getClosingBalance());
							previousLeaveBalance.setClosingBalance(
									previousLeaveBalance.getClosingBalance() + leaveType.getLeaveCredit());
							previousLeaveBalance.setCreditDate(new Date());
							previousLeaveBalance.setEarnedBalance(leaveType.getLeaveCredit());

							leaveBalanceHistory = new LeaveBalanceHistory(0l, LocalDate.now().getMonth().getValue(),
									LocalDate.now().getYear(), new Date(), previousLeaveBalance.getClosingBalance(),
									leaveType.getLeaveCredit(), 0d,
									previousLeaveBalance.getClosingBalance() + leaveType.getLeaveCredit(), 0d, 0d,
									previousLeaveBalance);
							previousLeaveBalance.getLeaveBalanceHistorys().add(leaveBalanceHistory);

						} else

						if (leaveType.isReset() && leaveType.getResetOn() == LocalDate.now().getDayOfMonth()
								&& (restType.contains(leaveType.getResetType()))) {
							// Reset

							previousLeaveBalance.setCarryForwardBalance(0);
							previousLeaveBalance.setClosingBalance(leaveType.getLeaveCredit());
							previousLeaveBalance.setCreditDate(new Date());
							previousLeaveBalance.setEarnedBalance(leaveType.getLeaveCredit());
							previousLeaveBalance.setRedeemedBalance(0);

							leaveBalanceHistory = new LeaveBalanceHistory(0l, LocalDate.now().getMonth().getValue(),
									LocalDate.now().getYear(), new Date(), 0d, leaveType.getLeaveCredit(), 0d,
									leaveType.getLeaveCredit(), 0d, 0d, previousLeaveBalance);
							previousLeaveBalance.getLeaveBalanceHistorys().add(leaveBalanceHistory);

						} else if (leaveType.isCarryForward()
								&& leaveType.getResetMonth() == LocalDate.now().getMonth().getValue()) {
							// carry forward
							double carryForward = (previousLeaveBalance.getCarryForwardBalance() > leaveType
									.getMaxAccumulationCount()) ? leaveType.getMaxAccumulationCount()
											: leaveType.getLeaveCredit() + previousLeaveBalance.getClosingBalance();
							previousLeaveBalance.getClosingBalance();

							previousLeaveBalance.setCarryForwardBalance(previousLeaveBalance.getClosingBalance());
							previousLeaveBalance.setClosingBalance(carryForward);
							previousLeaveBalance.setCreditDate(new Date());
							previousLeaveBalance.setEarnedBalance(leaveType.getLeaveCredit());
							previousLeaveBalance.setRedeemedBalance(0);

							leaveBalanceHistory = new LeaveBalanceHistory(0l, LocalDate.now().getMonthValue(),
									LocalDate.now().getYear(), new Date(), previousLeaveBalance.getClosingBalance(),
									leaveType.getLeaveCredit(), 0d, carryForward,
									previousLeaveBalance.getClosingBalance(), 0d, previousLeaveBalance);

						}

						break;
					case "Yearly":

						if (leaveType.isReset() && leaveType.getResetMonth() == LocalDate.now().getMonth().getValue()) {
							// Reset

							previousLeaveBalance.setCarryForwardBalance(0);
							previousLeaveBalance.setClosingBalance(leaveType.getLeaveCredit());
							previousLeaveBalance.setCreditDate(new Date());
							previousLeaveBalance.setEarnedBalance(leaveType.getLeaveCredit());
							previousLeaveBalance.setRedeemedBalance(0);

							leaveBalanceHistory = new LeaveBalanceHistory(0l, LocalDate.now().getMonthValue(),
									LocalDate.now().getYear(), new Date(), 0d, leaveType.getLeaveCredit(), 0d,
									leaveType.getLeaveCredit(), 0d, 0d, previousLeaveBalance);
							previousLeaveBalance.getLeaveBalanceHistorys().add(leaveBalanceHistory);

						} else if (leaveType.isCarryForward()
								&& leaveType.getResetMonth() == LocalDate.now().getMonth().getValue()) {
							// carry forward
							double carryForward = (previousLeaveBalance.getCarryForwardBalance() > leaveType
									.getMaxAccumulationCount()) ? leaveType.getMaxAccumulationCount()
											: leaveType.getLeaveCredit() + previousLeaveBalance.getClosingBalance();
							previousLeaveBalance.getClosingBalance();

							previousLeaveBalance.setCarryForwardBalance(previousLeaveBalance.getClosingBalance());
							previousLeaveBalance.setClosingBalance(carryForward);
							previousLeaveBalance.setCreditDate(new Date());
							previousLeaveBalance.setEarnedBalance(leaveType.getLeaveCredit());
							previousLeaveBalance.setRedeemedBalance(0);

							leaveBalanceHistory = new LeaveBalanceHistory(0l, LocalDate.now().getMonthValue(),
									LocalDate.now().getYear(), new Date(), previousLeaveBalance.getClosingBalance(),
									leaveType.getLeaveCredit(), 0d, carryForward,
									previousLeaveBalance.getClosingBalance(), 0d, previousLeaveBalance);

						}

						break;

					default:
						break;
					}
					if (leaveBalanceHistory != null)
						leaveBalanceHistoryService.save(leaveBalanceHistory);
				}
			});

		});
	}

	BiPredicate<String, String> booleanFunction = (e, l) -> e.equals(l);

}
