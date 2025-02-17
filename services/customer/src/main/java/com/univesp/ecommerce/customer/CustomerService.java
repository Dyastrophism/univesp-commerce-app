package com.univesp.ecommerce.customer;

import com.univesp.ecommerce.exception.CustomerNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public String createCustomer(CustomerRequest customerRequest) {
        var customer = customerRepository.save(customerMapper.toCustomer(customerRequest));
        return customer.getId();
    }

    public void updateCustomer(CustomerRequest customerRequest) {
        var customer = customerRepository.findById(customerRequest.id())
                .orElseThrow(() -> new CustomerNotFoundException(
                        String.format("Cannot update customer:: No customer found with the provided ID:: %s", customerRequest.id())
                ));
        mergeCustomer(customer, customerRequest);
        customerRepository.save(customer);
    }

    public List<CustomerResponse> findAllCustomers() {
        return customerRepository.findAll().stream()
                .map(customerMapper::fromCustomer)
                .toList();
    }

    public Boolean existsById(String customerId) {
        return customerRepository.existsById(customerId);
    }

    public CustomerResponse findById(String customerId) {
        return customerRepository.findById(customerId)
                .map(customerMapper::fromCustomer)
                .orElseThrow(() -> new CustomerNotFoundException(
                        String.format("No customer found with the provided ID:: %s", customerId)
                ));
    }

    public void deleteCustomer(String customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException(
                    String.format("Cannot delete customer:: No customer found with the provided ID:: %s", customerId)
            );
        }
        customerRepository.deleteById(customerId);
    }

    private void mergeCustomer(Customer customer, CustomerRequest customerRequest) {
        if (StringUtils.isNotBlank(customerRequest.firstName())) {
            customer.setFirstName(customerRequest.firstName());
        }
        if (StringUtils.isNotBlank(customerRequest.lastName())) {
            customer.setLastName(customerRequest.lastName());
        }
        if (StringUtils.isNotBlank(customerRequest.email())) {
            customer.setEmail(customerRequest.email());
        }
        if (customerRequest.address() != null) {
            customer.setAddress(customerRequest.address());
        }
    }

}
