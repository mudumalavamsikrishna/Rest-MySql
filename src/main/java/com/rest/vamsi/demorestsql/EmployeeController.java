package com.rest.vamsi.demorestsql;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class EmployeeController {
	@Autowired
	EmployeeRepository employeeRepository;
	@Autowired
	EmployeeResourceAssembler assembler;

	@GetMapping(path = "/employees", produces = MediaTypes.HAL_JSON_VALUE)
	public Resources<Resource<Employee>> getAllEmployee() {

		// return employeeRepository.findAll();

		/*
		 * List<Resource<Employee>>
		 * employees=employeeRepository.findAll().stream().map(employee -> new
		 * Resource<>(employee,
		 * linkTo(methodOn(EmployeeController.class).getEmployeeById(employee.getId())).
		 * withSelfRel(),
		 * linkTo(methodOn(EmployeeController.class).getAllEmployee()).withRel(
		 * "employees"))) .collect(Collectors.toList());
		 */

		List<Resource<Employee>> employees = employeeRepository.findAll().stream().map(assembler::toResource)
				.collect(Collectors.toList());

		return new Resources<>(employees, linkTo(methodOn(EmployeeController.class).getAllEmployee()).withSelfRel());
	}

	@PostMapping(path = "/employees",produces=MediaTypes.HAL_JSON_VALUE)
	public ResponseEntity<?> createEmployee(@RequestBody Employee employee) throws URISyntaxException {

		// return employeeRepository.save(employee);

		Resource<Employee> resource = assembler.toResource(employeeRepository.save(employee));

		return ResponseEntity.created(new URI(resource.getId().expand().getHref())).body(resource);
	}

	@GetMapping(path = "/employees/{id}", produces = MediaTypes.HAL_JSON_VALUE)
	public Resource<Employee> getEmployeeById(@PathVariable(value = "id") int id) {

		/*
		 * Employee employee = employeeRepository.findById(id).orElseThrow(() -> new
		 * EmployeeNotFoundException(id));
		 * 
		 * return new Resource<>(employee,
		 * linkTo(methodOn(EmployeeController.class).getEmployeeById(id)).withSelfRel(),
		 * linkTo(methodOn(EmployeeController.class).getAllEmployee()).withRel(
		 * "employees"));
		 * 
		 */
		Employee employee = employeeRepository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));

		return assembler.toResource(employee);

	}

	@PutMapping(path="/employees/{id}",produces=MediaTypes.HAL_JSON_VALUE)
	public ResponseEntity<?> updateEmployee(@RequestBody Employee newemployee, @PathVariable int id) throws URISyntaxException {

		// Optional<Employee> e = employeeRepository.findById(id);

		Employee updatedEmployee= employeeRepository.findById(id).map(employee -> {
			employee.setName(newemployee.getName());
			employee.setSalary(newemployee.getSalary());
			return employeeRepository.save(employee);
		}).orElseGet(() -> {
			newemployee.setId(id);
			return employeeRepository.save(newemployee);
		});
		
		Resource<Employee> resource=assembler.toResource(updatedEmployee);
		
		return ResponseEntity.created(new URI(resource.getId().expand().getHref())).body(resource);
	}

	@DeleteMapping("/employees/{id}")
	public ResponseEntity<?> deleteEmployee(@PathVariable int id) {
		employeeRepository.deleteById(id);
		
		return ResponseEntity.noContent().build();
	}

}
