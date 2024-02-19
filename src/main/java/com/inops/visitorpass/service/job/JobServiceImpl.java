package com.inops.visitorpass.service.job;

import org.springframework.stereotype.Service;

import com.inops.visitorpass.service.ICompute;

import lombok.RequiredArgsConstructor;

@Service("jobService")
@RequiredArgsConstructor
public class JobServiceImpl implements IJobService {

	private final ICompute computeService;
	private final IJob leaveComputation, reportComputation, integrationComputation;

	@Override
	public IJob getJob(String serviceName) {
		IJob jobService = null;
		switch (serviceName) {
		case "Muster":
			jobService = (fromDate, toDate, task) -> {
				computeService.createMusterForAll(fromDate);
			};
			break;

		case "Compute":
			jobService = (fromDate, toDate, task) -> {
				computeService.computeAll(fromDate);
			};
			break;
		case "Leave":
			return leaveComputation;
		// break;
		case "Report":
			return reportComputation;
		// break;
		case "Integration":
			return integrationComputation;

		default:
			break;
		}

		return jobService;
	}

}
