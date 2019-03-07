package com.mindex.challenge.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.ReportingStructureService;

@Service
public class ReportingStructureServiceImpl implements ReportingStructureService {
	
	private static final Logger LOG = LoggerFactory.getLogger(ReportingStructureServiceImpl.class);
	
	@Autowired
    private EmployeeRepository employeeRepository;

	@Override
	public ReportingStructure reportingStructure(String id) {
		LOG.debug("Creating reporting structure with id [{}]", id);
		
		Employee employee = employeeRepository.findByEmployeeId(id);
		
		if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }
		
		// determine total number of reports
		Integer numberOfReports = numberOfReports(employee);
		
		return new ReportingStructure(employee, numberOfReports);
	}
	
	private Integer numberOfReports(Employee employee) {
		Integer reports = 0;
		// generate list of direct reports
		List<Employee> drList = employee.getDirectReports();

		if (drList != null && drList.size() > 0) {
			// iterate direct report list
			for (Employee dr : drList) {
				// get the direct report's employee record
				Employee e = employeeRepository.findByEmployeeId(dr.getEmployeeId());

				// check the direct report for their direct reports
				List<Employee> drList2 = e.getDirectReports();
				if (drList2 != null && drList2.size() > 0) {
					// add the direct report's direct reports
					dr.setDirectReports(e.getDirectReports());

					// add count of direct report's direct reports
					reports += numberOfReports(e);
				}

				reports++;
			}

		}

		return reports;
	}

}
