package com.rest.vamsi.demorestsql;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

@Component
public class EmployeeResourceAssembler implements ResourceAssembler<Employee, Resource<Employee>> {

	@Override
	public Resource<Employee> toResource(Employee employee) {
		return new Resource<>(employee,
				linkTo(methodOn(EmployeeController.class).getEmployeeById(employee.getId())).withSelfRel(),
				linkTo(methodOn(EmployeeController.class).getAllEmployee()).withRel("employees"));

	}

}