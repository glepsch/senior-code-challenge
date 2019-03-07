package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());
        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Creating employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        return employee;
    }

    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }

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
				if (e.getDirectReports() != null && e.getDirectReports().size() > 0) {
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
