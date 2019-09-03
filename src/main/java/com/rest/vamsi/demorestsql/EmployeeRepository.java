package com.rest.vamsi.demorestsql;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

	void cretira();

}
