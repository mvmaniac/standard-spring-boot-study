package io.devfactory.enrollment.service;

import io.devfactory.enrollment.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class EnrollmentService {

  private final EnrollmentRepository enrollmentRepository;

}
